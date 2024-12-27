package com.bhoffpauir.blisp.lib;

/**
 * 
 */
public record Token(TokenType type, String lexeme, Object literal, SourceLocation location) {
	/**
	 * 
	 * @param type
	 * @param lexeme
	 * @return
	 */
	public static Token of(TokenType type, String lexeme) {
		return new Token(type, lexeme, null, SourceLocation.of(0, 0));
	}
	
	/**
	 * 
	 * @param type
	 * @param lexeme
	 * @param literal
	 * @return
	 */
	public static Token of(TokenType type, String lexeme, Object literal) {
		return new Token(type, lexeme, literal, SourceLocation.of(0, 0));
	}
	
	/**
	 * 
	 * @param type
	 * @param lexeme
	 * @param literal
	 * @param location
	 * @return
	 */
	public static Token of(TokenType type, String lexeme, Object literal, SourceLocation location) {
		return new Token(type, lexeme, literal, location);
	}
	
	@Override
	public String toString() {
		return String.format("%s %s %s", type, lexeme, literal);
	}
}
