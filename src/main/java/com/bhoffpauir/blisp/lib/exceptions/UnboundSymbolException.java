package com.bhoffpauir.blisp.lib.exceptions;
/**
 * Runtime exception thrown when the evaluator encounters a symbol that doesn't exist in
 * the environment.
 */
public class UnboundSymbolException extends LispRuntimeException {
	private static final long serialVersionUID = -2429357825419703507L;

	public UnboundSymbolException(String symbol) {
		super("Unbound symbol: " + symbol);
	}
}
