package io.gridgo.bean.exceptions;

public class InvalidTypeException extends RuntimeException {

	private static final long serialVersionUID = 2985464912066462611L;

	public InvalidTypeException() {
		super();
	}

	public InvalidTypeException(String message) {
		super(message);
	}

	public InvalidTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidTypeException(Throwable cause) {
		super(cause);
	}
}
