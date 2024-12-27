package com.bhoffpauir.blisp.lib.exception;
/**
 * Runtime exception thrown when the parse cannot find a closing parenthesis.
 */
public class UnbalancedParenthesisException extends LispRuntimeException {
	private static final long serialVersionUID = 5923311031405072632L;

	public UnbalancedParenthesisException() {
		super("Unbalanced parenthesis");
	}
	
	public UnbalancedParenthesisException(String message) {
		super(message);
	}
}
