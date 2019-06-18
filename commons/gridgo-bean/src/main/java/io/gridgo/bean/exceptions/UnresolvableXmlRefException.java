package io.gridgo.bean.exceptions;

public class UnresolvableXmlRefException extends RuntimeException {

	private static final long serialVersionUID = 2985464912066462611L;

	public UnresolvableXmlRefException() {
		super();
	}

	public UnresolvableXmlRefException(String message) {
		super(message);
	}

	public UnresolvableXmlRefException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnresolvableXmlRefException(Throwable cause) {
		super(cause);
	}
}
