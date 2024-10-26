package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

/**
 * Represents a string atom in blisp, using {@link java.lang.String} as its value.
 * In blisp, strings are enclosed in double quotes.
 * 
 * <p>The {@code StringAtom} class extends {@code Atom<String>} and implements {@code Comparable<StringAtom>}, 
 * allowing string atoms to be compared and stored in ordered collections.</p>
 * 
 * <p>The class includes functionality for equality testing, string comparison, 
 * and regular expression pattern matching for valid strings in the language.</p>
 * 
 * @see Atom
 * @see Comparable
 */
public class StringAtom extends Atom<String> implements Comparable<StringAtom> {
    /**
     * Constructs a new {@code StringAtom} with an empty string value.
     */
    public StringAtom() {
        super(new String());
    }
    
    /**
     * Constructs a new {@code StringAtom} with the specified string value.
     * 
     * @param value the string value of the atom
     */
    public StringAtom(String value) {
        super(value);
    }
    
    /**
     * Constructs a {@code StringAtom} with the specified String value and state.
     *
     * @param value The initial value of this String atom.
     * @param state The state of this String atom.
     */
    public StringAtom(String value, EvalState state) {
    	super(value, state);
    }
    
    /**
     * Tests equality of string atoms based on their value.
     * 
     * @param obj the object to compare to, which should be a {@code StringAtom}
     * @return {@code true} if the string values are equal, {@code false} otherwise
     * @throws IllegalArgumentException if the provided object is not a {@code StringAtom}
     */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringAtom) {
			StringAtom strAtom = (StringAtom) obj;
			return value.equals(strAtom.value);
		} else
			throw new IllegalArgumentException("obj must be a StringAtom");
	}
	
	/**
     * Compares this {@code StringAtom} to another {@code StringAtom}, using lexicographic ordering.
     * 
     * @param obj the {@code StringAtom} to compare to
     * @return a negative integer, zero, or a positive integer as this string atom is lexicographically 
     * less than, equal to, or greater than the specified string atom
     */
	@Override
	public int compareTo(StringAtom obj) {
		return value.compareTo(obj.value);
	}
	
	/**
     * Provides a regular expression pattern that matches valid string literals in blisp.
     * The pattern handles escaped characters within the string.
     * 
     * @return a {@code Pattern} representing the valid string syntax in blisp
     */
	@Override
	public Pattern getRegexPattern() {
		return Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
	}

	/**
     * Returns a string representation of the string atom. The string value is enclosed in double quotes.
     * If extended printing is enabled, the representation will include the label "String: ".
     * 
     * @return the string representation of the string atom
     */
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
