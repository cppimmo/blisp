package com.bhoffpauir.blisp.lib.exceptions;

public class LispRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public LispRuntimeException(String message) {
		super(message);
	}
}
