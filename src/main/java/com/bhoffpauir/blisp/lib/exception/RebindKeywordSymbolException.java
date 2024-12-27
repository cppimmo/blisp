package com.bhoffpauir.blisp.lib.exception;

public class RebindKeywordSymbolException extends LispRuntimeException {
	private static final long serialVersionUID = 3715552004708752462L;

	public RebindKeywordSymbolException(String symbol) {
		super("Can't rebind keyword symbol: " + symbol);
	}
}
