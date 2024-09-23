package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

public class BooleanAtom extends Atom<Boolean> {
	public BooleanAtom(Boolean value) {
		super(value);
	}

	@Override
	public Pattern getRegexPattern() {
		return Pattern.compile("^(true|false)$");
	}

	@Override
	public String toString() {
		if (extendedPrint) {
			return "Boolean: " + value.toString();
		} else {
			return value.toString();
		}
	}
}
