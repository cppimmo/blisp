package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

/**
 * Atom representation for symbols.
 * 
 * The rules for valid symbols are as follows:
 * - Alphabetic characters: Both uppercase and lowercase letters (A-Z, a-z) are valid.
 * - Digits: Numbers (0-9) are allowed, but the symbol cannot start with a digit (to differentiate from numbers).
 * - Special characters: Some special characters like !, $, %, &amp;, *, /, :, &lt;, =, &gt;, ?, ^, ~, _,
 *     -, +, and . can appear in a symbol name.
 * - Not allowed: Symbols cannot contain whitespace or certain punctuation characters like
 *     parentheses (), double quotes ", semicolons ;, backticks `, commas ,, and vertical bars |. 
 */
public class SymbolAtom extends Atom<String> {
	public SymbolAtom(String value) {
		super(value);
	}

	@Override
	public Pattern getRegexPattern() {	
		return Pattern.compile("[a-zA-Z!$%&*/:<=>?^_~+\\-.][a-zA-Z0-9!$%&*/:<=>?^_~+\\-.]*");
	}
	
	@Override
	public String toString() {
		if (extendedPrint) {
			return "Symbol: " + value;
		} else {
			return value;
		}
	}
}
