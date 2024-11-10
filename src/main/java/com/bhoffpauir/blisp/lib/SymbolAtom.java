package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

/**
 * Represents a symbolic atom in a blisp, adhering to specific rules for valid symbols.
 * A symbol consists of alphabetic characters, digits, and certain special characters, 
 * and is case-insensitive for comparison purposes.
 * 
 * <p>Rules for valid symbols:</p>
 * <ul>
 *   <li>Alphabetic characters: Both uppercase and lowercase letters (A-Z, a-z) are valid.</li>
 *   <li>Digits: Numbers (0-9) are allowed, but the symbol cannot start with a digit (to differentiate from numbers).</li>
 *   <li>Special characters: Symbols can include special characters like !, $, %, &amp;, *, /, :, &lt;, =, &gt;, ?, ^, ~, _, -, +, and .</li>
 *   <li>Not allowed: Symbols cannot contain whitespace or punctuation characters like parentheses (), double quotes ", semicolons ;, backticks `, commas ,, and vertical bars |.</li>
 * </ul>
 * 
 * <p>The {@code SymbolAtom} class extends {@code Atom<String>} and implements {@code Comparable<SymbolAtom>},
 * allowing symbols to be compared and stored in ordered collections.</p>
 * 
 * <p>Case-insensitive comparison is used to determine symbol equality. The class also provides a 
 * regular expression pattern for matching valid symbols.</p>
 * 
 * @see Atom
 * @see Comparable
 */
public class SymbolAtom extends Atom<String> implements Comparable<SymbolAtom> {
    /**
     * Predefined symbol atom representing {@code nil}.
     */
    public static final SymbolAtom nil = new SymbolAtom("nil");
    
    /**
     * Constructs a new {@code SymbolAtom} with the specified value.
     * 
     * @param value the string value of the symbol
     */
    public SymbolAtom(String value) {
        super(value);
    }
    
    /**
     * Constructs a {@code SymbolAtom} with the specified Symbol value and state.
     *
     * @param value The initial value of this Symbol atom.
     * @param state The state of this Symbol atom.
     */
    public SymbolAtom(String value, EvalState state) {
    	super(value, state);
    }
    
    /**
     * Tests equality of symbols, ignoring case.
     * 
     * @param obj the object to compare to, which should be a {@code SymbolAtom}
     * @return {@code true} if the symbols are equal (ignoring case), {@code false} otherwise
     * @throws IllegalArgumentException if the provided object is not a {@code SymbolAtom}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SymbolAtom) {
            SymbolAtom symAtom = (SymbolAtom) obj;
            return value.equalsIgnoreCase(symAtom.value);
        } else {
            throw new IllegalArgumentException("obj must be a SymbolAtom");
        }
    }
    
    /**
     * Compares this {@code SymbolAtom} to another symbol, using lexicographic ordering.
     * 
     * @param obj the {@code SymbolAtom} to compare to
     * @return a negative integer, zero, or a positive integer as this symbol is lexicographically less than, 
     * equal to, or greater than the specified symbol
     */
    @Override
    public int compareTo(SymbolAtom obj) {
        return value.compareTo(obj.value);
    }
    
    /**
     * Provides a regular expression pattern that matches valid symbols according to the symbol rules.
     * 
     * @return a {@code Pattern} representing the valid symbol syntax
     */
    @Override
    public Pattern getRegexPattern() {    
        return Pattern.compile("[λa-zA-Z!$%&*/:<=>?^_~+\\-.][a-zA-Z0-9!$%&*/:<=>?^_~+\\-.]*");
    }
    
    /**
     * Returns a string representation of the symbol. If extended printing is enabled, 
     * the representation will include the label "Symbol: ". Otherwise, only the symbol's value is returned.
     * 
     * @return the string representation of the symbol
     */
    @Override
    public String toString() {
        if (extendedPrint) {
            return "Symbol: " + value.toUpperCase();
        } else {
            return value.toUpperCase();
        }
    }
}
