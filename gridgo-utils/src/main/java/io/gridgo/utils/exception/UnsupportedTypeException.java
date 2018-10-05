package io.gridgo.utils.exception;

public class UnsupportedTypeException extends RuntimeException {

	private static final long serialVersionUID = 2985464912066462611L;

	public UnsupportedTypeException() {
		super();
	}

	public UnsupportedTypeException(String message) {
		super(message);
	}

	public UnsupportedTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedTypeException(Throwable cause) {
		super(cause);
	}
}
