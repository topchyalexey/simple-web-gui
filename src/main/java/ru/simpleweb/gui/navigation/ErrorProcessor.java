package ru.simpleweb.gui.navigation;

import java.util.Stack;

import javax.ejb.Singleton;

import org.apache.log4j.Logger;

@Singleton(mappedName = "ErrorProcessor")
public class ErrorProcessor {

	private final static Logger log = Logger.getLogger(ErrorProcessor.class);

	private Stack<Throwable> errors = new Stack<Throwable>();

	public RuntimeException push(Throwable ex) {
		if (ex.getCause() != null) {
			errors.push(ex.getCause());
		}
		errors.push(ex);
		return new RuntimeException(ex);
	}

	public Throwable peek() {
		if (errors.isEmpty()) {
			return null;
		}
		return errors.peek();
	}

	public Throwable pop() {
		if (errors.isEmpty()) {
			return null;
		}
		if (errors.size() == 1) {
			return peek();
		}
		return errors.pop();
	}
}
