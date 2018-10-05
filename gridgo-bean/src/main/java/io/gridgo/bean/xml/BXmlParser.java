package io.gridgo.bean.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BType;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.impl.BFactory;
import io.gridgo.bean.impl.BFactoryAware;
import io.gridgo.bean.xml.RefItem.RefItemBuilder;
import io.gridgo.utils.ByteArrayUtils;
import io.gridgo.utils.PrimitiveUtils;
import lombok.Setter;

public class BXmlParser implements BFactoryAware {

	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

	@Setter
	private BFactory factory;

	public BXmlParser(BFactory factory) {
		this.factory = factory;
	}

	public BXmlParser() {
		this(BFactory.DEFAULT);
	}

	public BElement parse(String xml) {
		if (xml != null) {
			try (InputStream in = new ByteArrayInputStream(xml.getBytes(Charset.forName("utf-8")))) {
				Document document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(in);
				RefManager refManager = new RefManager();
				BElement result = parse(document, refManager);
				refManager.resolve();
				return result;
			} catch (SAXException | IOException | ParserConfigurationException e) {
				throw new RuntimeException("Cannot parse xml", e);
			}
		}
		return null;
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

			Node refNameAttr = node.getAttributes().getNamedItem("refName");
			RefItemBuilder builder = RefItem.builder();
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
		Node refNameAttr = node.getAttributes().getNamedItem("refName");
		if (refNameAttr != null) {
			refManager.addRef(
					RefItem.builder().type(BType.ARRAY).name(refNameAttr.getNodeValue()).target(result).build());
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
		Node refNameAttr = node.getAttributes().getNamedItem("refName");
		if (refNameAttr != null) {
			refManager.addRef(
					RefItem.builder().type(BType.OBJECT).name(refNameAttr.getNodeValue()).target(result).build());
		}
		Node child = node.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Element.ELEMENT_NODE) {
				Node nameAttr = child.getAttributes().getNamedItem("name");
				if (nameAttr == null) {
					throw new NullPointerException(
							"Name attribute (which is required for any item in BObject's xml) cannot be found");
				}
				result.put(nameAttr.getNodeValue(), parse(child, refManager));
			}
			child = child.getNextSibling();
		}
		return result;
	}
}