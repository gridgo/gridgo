package io.gridgo.utils.exception;

public class ThreadingException extends RuntimeException {

	private static final long serialVersionUID = 2985464912066462611L;

	public ThreadingException() {
		super();
	}

	public ThreadingException(String message) {
		super(message);
	}

	public ThreadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ThreadingException(Throwable cause) {
		super(cause);
	}
}
