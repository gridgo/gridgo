package io.gridgo.bean;

public enum BType {

	NULL, BOOLEAN, CHAR, BYTE, SHORT, INTEGER, FLOAT, LONG, DOUBLE, STRING, RAW, OBJECT, ARRAY;

	public static BType forName(String name) {
		for (BType value : values()) {
			if (value.name().equalsIgnoreCase(name)) {
				return value;
			}
		}
		return null;
	}
}
