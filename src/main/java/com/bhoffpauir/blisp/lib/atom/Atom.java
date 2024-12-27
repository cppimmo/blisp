package com.bhoffpauir.blisp.lib.atom;

import java.util.regex.Pattern;

import com.bhoffpauir.blisp.lib.EvalState;

/**
 * In LISP, atoms are scalar values. blisp uses a hybrid approach to atoms, 
 * utilizing a base {@code Atom} class with generics to handle basic types 
 * while subclassing {@code Atom} for more complex or specific types (e.g., {@code SymbolAtom}).
 * This class represents a generic atom in the blisp language.
 *
 * @param <T> The underlying value type of this atom.
 */
public abstract class Atom<T> {
    /**
     * The value held by this atom, of type {@code T}.
     */
    protected final T value;
    protected final EvalState state;

    /**
     * A flag indicating whether extended information should be printed 
     * when representing the atom as a string. Default is {@code false}.
     */
    protected static boolean extendedPrint = false;

    /**
     * Constructs an {@code Atom} with the specified value. The atom state is defaulted 
     * to {@code EvalState.UNQUOTED}.
     *
     * @param value The initial value of this atom.
     */
    public Atom(T value) {
        this.value = value;
        this.state = EvalState.UNQUOTED;
    }
    
    /**
     * Constructs an {@code Atom} with the specified value and state.
     * 
     * @param value The initial value of this atom.
     * @param state The state of this atom.
     */
    public Atom(T value, EvalState state) {
    	this.value = value;
    	this.state = state;
    }
    
    /**
     * Returns the value of this atom.
     *
     * @return The value held by this atom.
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns a regular expression {@code Pattern} that matches the format 
     * of valid values for this atom type.
     *
     * @return A regex pattern for validating values.
     */
    public abstract Pattern getRegexPattern();

    /**
     * Provides a string representation of this atom. The format may vary 
     * depending on the {@code extendedPrint} flag.
     *
     * @return The string representation of this atom.
     */
    @Override
    public abstract String toString();

    /**
     * Toggles the extended print mode, which determines whether extra information 
     * is included when printing atoms.
     *
     * @return The previous state of the {@code extendedPrint} flag.
     */
    public static boolean toggleExtenedPrint() {
        boolean oldValue = extendedPrint;
        extendedPrint = !extendedPrint;
        return oldValue;
    }

    /**
     * Sets the extended print mode for atom printing, enabling or disabling extra information.
     *
     * @param value {@code true} to enable extended printing, {@code false} to disable it.
     * @return The previous state of the {@code extendedPrint} flag.
     */
    public static boolean setExtendedPrint(boolean value) {
        boolean oldValue = extendedPrint;
        extendedPrint = value;
        return oldValue;
    }
}
