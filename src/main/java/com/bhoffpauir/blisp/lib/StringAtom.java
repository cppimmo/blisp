package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

/**
 * 
 */
public class StringAtom extends Atom<String> {
	public StringAtom() {
		super(new String());
	}
	
	public StringAtom(String value) {
		super(value);
	}
	
	@Override
	public Pattern getRegexPattern() {
		return Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
	}

	@Override
	public String toString() {
		if (extendedPrint) {
			return "String: " + value;
		} else {
			return value;
		}
	}
}
