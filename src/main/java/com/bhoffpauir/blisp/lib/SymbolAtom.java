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
public class SymbolAtom extends Atom<String> implements Comparable<SymbolAtom> {
	//! Symbol atom for nil.
	public static final SymbolAtom nil = new SymbolAtom("nil");
	
	public SymbolAtom(String value) {
		super(value);
	}
	/**
	 * Test equality of symbols using case-insensitivity.
	 * 
	 * @param atom The SymbolAtom to compare to.
	 * @return True if the symbol values match, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		// Comparison is case-insensitive
		if (obj instanceof SymbolAtom) {
			SymbolAtom symAtom = (SymbolAtom) obj;
			return value.equalsIgnoreCase(symAtom.value);
		} else {
			throw new IllegalArgumentException("obj must by a SymbolAtom");
		}
	}
	
	@Override
	public int compareTo(SymbolAtom obj) {
		return value.compareTo(obj.value);
	}
	
	@Override
	public Pattern getRegexPattern() {	
		return Pattern.compile("[λa-zA-Z!$%&*/:<=>?^_~+\\-.][a-zA-Z0-9!$%&*/:<=>?^_~+\\-.]*");
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
