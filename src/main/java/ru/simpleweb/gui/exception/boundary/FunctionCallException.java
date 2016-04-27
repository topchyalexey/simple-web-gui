package ru.simpleweb.gui.exception.boundary;

public class FunctionCallException extends RuntimeException {
	private static final long serialVersionUID = 8247194504019849224L;

	public FunctionCallException(Exception e) {
		super(e);
	}
}
