package io.gridgo.bean.xml;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.udojava.evalex.Expression;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BType;
import io.gridgo.bean.exceptions.UnresolvableXmlRefException;
import io.gridgo.utils.exception.UnsupportedTypeException;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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
			throw new UnresolvableXmlRefException("Circular reference found in expression: " + this.getContent()
					+ (this.getName() == null ? "" : ", refName: " + this.getName()));
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
