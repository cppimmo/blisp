package com.bhoffpauir.blisp.lib;

import java.util.List;
import java.util.regex.Pattern;
/**
 * Atom representation of a list. In blisp, lists are represented by an atom with an
 * {@code Object} list value.
 */
public class ListAtom extends Atom<List<Object>> {
	public ListAtom(List<Object> value) {
		super(value);
	}

	@Override
	public Pattern getRegexPattern() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		if (extendedPrint) {
			return "List: " + value.toString();
		} else {
			return value.toString();
		}
	}

}
