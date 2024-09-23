package com.bhoffpauir.blisp.lib.exceptions;
/**
 * 
 */
public class UnboundSymbolException extends LispRuntimeException {
	public UnboundSymbolException(String symbol) {
		super("Undefined symbol: " + symbol);
	}
}
