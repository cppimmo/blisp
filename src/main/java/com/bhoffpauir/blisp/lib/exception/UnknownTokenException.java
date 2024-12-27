package com.bhoffpauir.blisp.lib.exception;
/**
 * Runtime exception thrown when the tokenizer encounters input that it cannot discern.
 */
public class UnknownTokenException extends LispRuntimeException {
	private static final long serialVersionUID = -3663686810381362076L;
	public static final int MAX_TOKEN_LENGTH = 24 - 3;
	
	public UnknownTokenException(String previousToken) {
		super(String.format("Unknown token after: %s...", modifyToken(previousToken)));
	}
	// Limit the size of the token string
	private static String modifyToken(String token) {
		int maxTokenLength = Math.min(token.length(), MAX_TOKEN_LENGTH);
		return token.substring(0, maxTokenLength);
	}
}
