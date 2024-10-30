package com.bhoffpauir.blisp.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Atom representation of a list in blisp.
 * 
 * <p>In blisp, lists are represented by atoms that store a {@link java.util.List} of {@code Object} values.
 * These lists can contain various types of elements, including other atoms such as numbers, strings, symbols, 
 * or nested lists.</p>
 * 
 * <p>List atoms provide equality checking, where an empty list is considered equal to the {@link SymbolAtom#nil} 
 * symbol atom. Additionally, this class overrides the {@code toString} method to print list elements 
 * in a Lisp-style format, with optional extended printing for debug purposes.</p>
 * 
 * <p>The {@link #getRegexPattern()} method is not applicable for lists and will throw an 
 * {@link UnsupportedOperationException} if called.</p>
 * 
 * @see SymbolAtom
 * @see Atom
 */
public class ListAtom extends Atom<List<Object>> {
	/**
	 * Constructs an ListAtom with an empty list.  The empty list is equal to the nil symbol.
	 */
	public ListAtom() {
		super(new ArrayList<>());
	}
	
	/**
     * Constructs a ListAtom with the given list value.
     * 
     * @param value The list of objects to store in this atom.
     */
	public ListAtom(List<Object> value) {
		super(value);
	}
	
	/**
     * Constructs a {@code ListAtom} with the specified List value and state.
     *
     * @param value The initial value of this List atom.
     * @param state The state of this List atom.
     */
    public ListAtom(List<Object> value, EvalState state) {
    	super(value, state);
    }
	
	/**
     * Tests for equality between this list atom and another object.
     * 
     * <p>This method supports two types of comparisons:</p>
     * <ul>
     *   <li>List equality: Two {@code ListAtom} objects are considered equal if their lists 
     *   contain the same elements in the same order.</li>
     *   <li>Nil equality: An empty list is considered equal to the {@link SymbolAtom#nil} symbol.</li>
     * </ul>
     * 
     * @param atom The object to compare with this list atom. It must be either a {@code ListAtom} or a {@code SymbolAtom}.
     * @return {@code true} if the lists or empty list/nil symbol are considered equal, otherwise {@code false}.
     * @throws IllegalArgumentException if the provided object is not a {@code ListAtom} or {@code SymbolAtom}.
     */
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

	/**
     * Returns a regular expression pattern for matching list atoms.
     * 
     * <p>This method is not applicable to lists and will throw an {@link UnsupportedOperationException}.
     * </p>
     * 
     * @return This method does not return anything as it throws an exception.
     * @throws UnsupportedOperationException if this method is called.
     */
	@Override
	public Pattern getRegexPattern() {
		throw new UnsupportedOperationException();
	}

	/**
     * Returns the string representation of the list in a Lisp-style format.
     * 
     * <p>Each element of the list is converted to a string, and the elements are enclosed in parentheses, 
     * separated by commas. If {@code extendedPrint} is enabled, the string will be prefixed with "List: " 
     * for debugging purposes.</p>
     * 
     * @return A string representing the list contents.
     */
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
