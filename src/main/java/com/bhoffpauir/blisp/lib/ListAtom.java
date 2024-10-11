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
	public boolean equals(Object atom) {
		if (atom instanceof ListAtom) {
			ListAtom lstAtom = (ListAtom) atom;
			return value.equals(lstAtom.getValue());
		} else if (atom instanceof SymbolAtom) {
			// Empty list equals nil symbol
			SymbolAtom symAtom = (SymbolAtom) atom;
			return (value.isEmpty() && symAtom.equals(SymbolAtom.nil)); 
		} else { 
			throw new IllegalArgumentException("atom must be a ListAtom/SymbolAtom");
		}
	}

	@Override
	public Pattern getRegexPattern() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (int i = 0; i < value.size(); i++) {
			var elem = value.get(i);
			sb.append((i == value.size() - 1) ? elem : elem.toString() + ", ");
		}
		sb.append(')');
		
		if (extendedPrint) {
			return "List: " + sb.toString();
		} else {
			return sb.toString();
		}
	}

}
