package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;
/**
 * In LISP, atoms are scalar values. blisp uses a hybrid approach to atoms using a base
 * Atom class and generics to handle basic types, while subclassing Atom for more complex
 * or specific types (e.g., SymbolAtom).
 * 
 * @param <T> The underlying value of this atom.
 */
public abstract class Atom<T> {
    protected final T value;
    protected static boolean extendedPrint = false;
    
    public Atom(T value) {
        this.value = value;
    }
    
    public T getValue() {
    	return value;
    }
    
    public abstract Pattern getRegexPattern();

    @Override
    public abstract String toString();
    /**
     * Toggle the extra information for atom printing.
     * @return The old value of the extended print setting.
     */
    public static boolean toggleExtenedPrint() {
    	boolean oldValue = extendedPrint;
    	extendedPrint = !extendedPrint;
    	return oldValue;
    }
    /**
     * Enable/disable the extra information for atom printing.
     * @param value True = enabled, false = disabled.
     * @return The old value of the extended print setting.
     */
    public static boolean setExtendedPrint(boolean value) {
    	boolean oldValue = extendedPrint;
    	extendedPrint = value;
    	return oldValue;
    }
}
