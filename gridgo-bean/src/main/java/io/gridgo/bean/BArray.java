package io.gridgo.bean;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.impl.BFactory;
import io.gridgo.utils.StringUtils;
import net.minidev.json.JSONArray;

public interface BArray extends BContainer, List<BElement> {

	static BArray newDefault() {
		return BFactory.DEFAULT.newArray();
	}

	static BArray newDefault(Object data) {
		return BFactory.DEFAULT.newArray(data);
	}

	static BArray newFromSequence(Object... sequence) {
		return BFactory.DEFAULT.newArrayFromSequence(sequence);
	}

	@Override
	default BType getType() {
		return BType.ARRAY;
	}

	default BType typeOf(int index) {
		return this.get(index).getType();
	}

	default void addAny(Object obj) {
		this.add(this.getFactory().fromAny(obj));
	}

	default void addAnySequence(Object... elements) {
		for (Object object : elements) {
			this.addAny(object);
		}
	}

	default void addAnyAll(Collection<?> collection) {
		for (Object object : collection) {
			this.addAny(object);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	default List<Object> toJsonElement() {
		List<Object> list = new JSONArray();
		for (BElement entry : this) {
			list.add(entry.toJsonElement());
		}
		return list;
	}

	default String toJson() {
		return JSONArray.toJSONString(this.toJsonElement());
	}

	@Override
	default String toXml(String name) {
		StringBuilder builder = new StringBuilder();
		if (name != null) {
			builder.append("<array name=\"").append(name).append("\">");
		} else {
			builder.append("<array>");
		}
		for (BElement element : this) {
			builder.append(element.toXml());
		}
		builder.append("</array>");
		return builder.toString();
	}

	default List<Object> toList() {
		List<Object> list = new LinkedList<>();
		for (BElement entry : this) {
			if (entry instanceof BValue) {
				list.add(((BValue) entry).getData());
			} else if (entry instanceof BObject) {
				list.add(((BObject) entry).toMap());
			} else if (entry instanceof BArray) {
				list.add(((BArray) entry).toList());
			} else {
				throw new InvalidTypeException("Found unexpected BElement implementation: " + entry.getClass());
			}
		}
		return list;
	}

	default BValue getValue(int index) {
		return this.get(index).asValue();
	}

	default BArray getArray(int index) {
		return this.get(index).asArray();
	}

	default BObject getObject(int index) {
		return this.get(index).asObject();
	}

	default boolean getBoolean(int index) {
		return this.getValue(index).getBoolean();
	}

	default char getChar(int index) {
		return this.getValue(index).getChar();
	}

	default byte getByte(int index) {
		return this.getValue(index).getByte();
	}

	default short getShort(int index) {
		return this.getValue(index).getShort();
	}

	default int getInteger(int index) {
		return this.getValue(index).getInteger();
	}

	default long getLong(int index) {
		return this.getValue(index).getLong();
	}

	default float getFloat(int index) {
		return this.getValue(index).getFloat();
	}

	default double getDouble(int index) {
		return this.getValue(index).getDouble();
	}

	default String getString(int index) {
		return this.getValue(index).getString();
	}

	default byte[] getRaw(int index) {
		return this.getValue(index).getRaw();
	}

	default BValue removeValue(int index) {
		return this.remove(index).asValue();
	}

	default BObject removeObject(int index) {
		return this.remove(index).asObject();
	}

	default BArray removeArray(int index) {
		return this.remove(index).asArray();
	}

	default boolean removeBoolean(int index) {
		return this.removeValue(index).getBoolean();
	}

	default char removeChar(int index) {
		return this.removeValue(index).getChar();
	}

	default byte removeByte(int index) {
		return this.removeValue(index).getByte();
	}

	default short removeShort(int index) {
		return this.removeValue(index).getShort();
	}

	default int removeInteger(int index) {
		return this.removeValue(index).getInteger();
	}

	default long removeLong(int index) {
		return this.removeValue(index).getLong();
	}

	default float removeFloat(int index) {
		return this.removeValue(index).getFloat();
	}

	default double removeDouble(int index) {
		return this.removeValue(index).getDouble();
	}

	default String removeString(int index) {
		return this.removeValue(index).getString();
	}

	default byte[] removeRaw(int index) {
		return this.removeValue(index).getRaw();
	}

	@Override
	default void writeString(String name, int numTab, StringBuilder writer) {
		StringUtils.tabs(numTab, writer);
		if (name == null) {
			writer.append("[\n");
		} else {
			writer.append(name).append(": ARRAY = [\n");
		}
		for (int i = 0; i < this.size(); i++) {
			this.get(i).writeString("[" + i + "]", numTab + 1, writer);
			if (i < this.size() - 1) {
				writer.append(",\n");
			} else {
				writer.append("\n");
			}
		}
		StringUtils.tabs(numTab, writer);
		writer.append("]");
	}
}
