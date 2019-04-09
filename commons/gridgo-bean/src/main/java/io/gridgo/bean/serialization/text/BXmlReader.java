package io.gridgo.bean.serialization.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.udojava.evalex.Expression;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BType;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.exceptions.NameAttributeNotFoundException;
import io.gridgo.bean.exceptions.UnresolvableXmlRefException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.utils.ByteArrayUtils;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.UnsupportedTypeException;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

class BXmlReader {

    private static final String REF_NAME = "refName";

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    private final BFactory factory;

    public BXmlReader(BFactory factory) {
        this.factory = factory;
    }

    public BElement parse(String xml) {
        if (xml == null)
            return null;

        try (InputStream in = new ByteArrayInputStream(xml.getBytes(Charset.forName("utf-8")))) {
            return parse(in);
        } catch (IOException e) {
            throw new BeanSerializationException("Cannot parse xml", e);
        }
    }

    public BElement parse(InputStream in) {
        if (in == null)
            return null;

        try {
            Document document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(in);
            RefManager refManager = new RefManager();
            BElement result = parse(document, refManager);
            refManager.resolve();
            return result;
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new BeanSerializationException("Cannot parse xml", e);
        }
    }

    public BElement parse(Node node, RefManager refManager) {
        if (node != null) {
            if (node instanceof Document) {
                node = node.getFirstChild();
            }
            String nodeName = node.getNodeName();
            BElement result = null;
            switch (nodeName.toLowerCase()) {
            case "object":
                result = parseObject(node, refManager);
                break;
            case "array":
                result = parseArray(node, refManager);
                break;
            default:
                result = parseValue(node, refManager);
                break;
            }
            return result;
        }
        return null;
    }

    private BValue parseValue(Node node, RefManager refManager) {
        String nodeName = node.getNodeName();
        BType type = BType.forName(nodeName);
        if (type != null) {
            String stringValue;
            Node valueAttr = node.getAttributes().getNamedItem("value");
            if (valueAttr != null) {
                stringValue = valueAttr.getNodeValue();
            } else {
                stringValue = node.getTextContent().trim();
            }

            BValue value = this.factory.newValue();

            Node refNameAttr = node.getAttributes().getNamedItem(REF_NAME);
            var builder = RefItem.builder();
            builder.type(type).content(stringValue).target(value);
            RefItem refItem = builder.build();
            if (refNameAttr != null) {
                refItem.setName(refNameAttr.getNodeValue());
                refManager.addRef(refItem);
            }
            try {
                switch (type) {
                case BOOLEAN:
                    value.setData(PrimitiveUtils.getBooleanValueFrom(stringValue));
                    break;
                case BYTE:
                    value.setData(PrimitiveUtils.getByteValueFrom(stringValue));
                    break;
                case CHAR:
                    value.setData(PrimitiveUtils.getCharValueFrom(stringValue));
                    break;
                case DOUBLE:
                    value.setData(PrimitiveUtils.getDoubleValueFrom(stringValue));
                    break;
                case FLOAT:
                    value.setData(PrimitiveUtils.getFloatValueFrom(stringValue));
                    break;
                case INTEGER:
                    value.setData(PrimitiveUtils.getIntegerValueFrom(stringValue));
                    break;
                case LONG:
                    value.setData(PrimitiveUtils.getLongValueFrom(stringValue));
                    break;
                case RAW:
                    value.setData(ByteArrayUtils.fromHex(stringValue));
                    break;
                case SHORT:
                    value.setData(PrimitiveUtils.getShortValueFrom(stringValue));
                    break;
                case STRING:
                    value.setData(stringValue);
                    break;
                case NULL:
                    break;
                default:
                    throw new InvalidTypeException("Cannot parse node with name " + nodeName + " as BValue");
                }
            } catch (NumberFormatException ex) {
                refItem.setResolved(false);
                if (refItem.getName() == null) {
                    refManager.addRef(refItem);
                }
            }
            return value;
        }
        throw new InvalidTypeException("Cannot parse node with name " + nodeName + " as BValue");
    }

    private BArray parseArray(Node node, RefManager refManager) {
        BArray result = this.factory.newArray();
        Node refNameAttr = node.getAttributes().getNamedItem(REF_NAME);
        if (refNameAttr != null) {
            refManager.addRef(RefItem.builder().type(BType.ARRAY).name(refNameAttr.getNodeValue()).target(result).build());
        }
        Node child = node.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Element.ELEMENT_NODE) {
                result.add(parse(child, refManager));
            }
            child = child.getNextSibling();
        }
        return result;
    }

    private BObject parseObject(Node node, RefManager refManager) {
        BObject result = this.factory.newObject();
        Node refNameAttr = node.getAttributes().getNamedItem(REF_NAME);
        if (refNameAttr != null) {
            refManager.addRef(RefItem.builder().type(BType.OBJECT).name(refNameAttr.getNodeValue()).target(result).build());
        }
        Node child = node.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Element.ELEMENT_NODE) {
                Node nameAttr = child.getAttributes().getNamedItem("name");
                if (nameAttr == null) {
                    throw new NameAttributeNotFoundException("Name attribute (which is required for any item in BObject's xml) cannot be found");
                }
                result.put(nameAttr.getNodeValue(), parse(child, refManager));
            }
            child = child.getNextSibling();
        }
        return result;
    }
}


class RefManager {

    @Getter
    private final List<RefItem> refs = new LinkedList<>();

    void addRef(RefItem target) {
        this.refs.add(target);

    }

    void resolve() {
        Map<String, RefItem> map = new HashMap<>();
        List<RefItem> unresolvedItems = new LinkedList<>();
        for (RefItem item : this.refs) {
            if (item.getName() != null) {
                map.put(item.getName(), item);
            }
            if (!item.isResolved()) {
                unresolvedItems.add(item);
            }
        }

        while (unresolvedItems.size() > 0) {
            unresolvedItems.get(0).resolve(map);

            Iterator<RefItem> iterator = unresolvedItems.iterator();
            while (iterator.hasNext()) {
                RefItem item = iterator.next();
                if (item.isResolved()) {
                    iterator.remove();
                }
            }
        }
    }
}

@Data
@Builder
class RefItem {
    private BType type;
    private String name;
    private String content;
    private BElement target;

    @Builder.Default
    private boolean resolved = true;

    @Builder.Default
    private boolean visited = false;

    void resolve(Map<String, RefItem> refs) {
        if (this.isVisited()) {
            throw new UnresolvableXmlRefException(
                    "Circular reference found in expression: " + this.getContent() + (this.getName() == null ? "" : ", refName: " + this.getName()));
        }
        this.setVisited(true);

        Expression exp = new Expression(this.getContent());
        List<String> vars = exp.getUsedVariables();
        for (String var : vars) {
            if (!refs.containsKey(var)) {
                throw new UnresolvableXmlRefException("Cannot resolve ref: " + var);
            }
            RefItem ref = refs.get(var);
            if (!ref.isResolved()) {
                ref.resolve(refs);
            }
            exp.with(var, new BigDecimal(ref.getTarget().asValue().getDouble()));
        }
        BigDecimal result = exp.eval();
        switch (type) {
        case BYTE:
            this.getTarget().asValue().setData(result.byteValue());
            break;
        case DOUBLE:
            this.getTarget().asValue().setData(result.doubleValue());
            break;
        case FLOAT:
            this.getTarget().asValue().setData(result.floatValue());
            break;
        case INTEGER:
            this.getTarget().asValue().setData(result.intValue());
            break;
        case LONG:
            this.getTarget().asValue().setData(result.longValue());
            break;
        case SHORT:
            this.getTarget().asValue().setData(result.shortValue());
            break;
        default:
            throw new UnsupportedTypeException("Type " + type + " is unsupported");
        }
        this.setResolved(true);
    }
}