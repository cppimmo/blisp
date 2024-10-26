package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

/**
 * Represents a Boolean atom in blisp.
 * This class extends the {@code Atom} class and handles Boolean values.
 */
public class BooleanAtom extends Atom<Boolean> {
    /**
     * Constructs a {@code BooleanAtom} with the specified Boolean value.
     *
     * @param value The initial value of this Boolean atom.
     */
    public BooleanAtom(Boolean value) {
        super(value);
    }

    /**
     * Constructs a {@code BooleanAtom} with the specified Boolean value and state.
     *
     * @param value The initial value of this Boolean atom.
     * @param state The state of this Boolean atom.
     */
    public BooleanAtom(Boolean value, EvalState state) {
    	super(value, state);
    }
    
    /**
     * Checks if this Boolean atom is equal to another object.
     *
     * @param obj The object to compare with this Boolean atom.
     * @return {@code true} if the object is a {@code BooleanAtom} with the same value;
     *         {@code false} otherwise.
     * @throws IllegalArgumentException if {@code obj} is not an instance of {@code BooleanAtom}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BooleanAtom) {
            BooleanAtom boolAtom = (BooleanAtom) obj;
            return value.equals(boolAtom.value);
        } else {
            throw new IllegalArgumentException("obj must be a BooleanAtom");
        }
    }

    /**
     * Returns a regular expression {@code Pattern} that matches valid Boolean values.
     *
     * @return A regex pattern for validating Boolean values, which matches "true" or "false".
     */
    @Override
    public Pattern getRegexPattern() {
        return Pattern.compile("^(true|false)$");
    }

    /**
     * Provides a string representation of this Boolean atom. The format may vary 
     * depending on the {@code extendedPrint} flag.
     *
     * @return The string representation of this Boolean atom, prefixed with "Boolean: " 
     *         if extended printing is enabled.
     */
    @Override
    public String toString() {
        if (extendedPrint) {
            return "Boolean: " + value.toString();
        } else {
            return value.toString();
        }
    }
}
