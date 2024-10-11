package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;
/**
 * Atom representation for strings. In blisp, the java.lang.String class is used for the
 * value of strings. 
 */
public class StringAtom extends Atom<String> implements Comparable<StringAtom> {
	public StringAtom() {
		super(new String());
	}
	// TODO: Implement comparator/comparable interface.
	public StringAtom(String value) {
		super(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringAtom) {
			StringAtom strAtom = (StringAtom) obj;
			return value.equals(strAtom.value);
		} else
			throw new IllegalArgumentException("obj must be a StringAtom");
	}
	
	@Override
	public int compareTo(StringAtom obj) {
		return value.compareTo(obj.value);
	}
	
	@Override
	public Pattern getRegexPattern() {
		return Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
	}

	@Override
	public String toString() {
		final var quoted = '"' + value + '"';
		if (extendedPrint) {
			return "String: " + quoted;
		} else {
			return quoted;
		}
	}
}
