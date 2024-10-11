package com.bhoffpauir.blisp.lib.exceptions;
/**
 * Base for all blisp runtime exceptions.
 */
public class LispRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 293734693595379971L;

	public LispRuntimeException(String message) {
		super(message);
	}
}
