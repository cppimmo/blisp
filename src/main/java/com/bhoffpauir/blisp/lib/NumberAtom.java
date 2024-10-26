package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

import com.bhoffpauir.blisp.lib.exceptions.LispRuntimeException;

/**
 * Atom representation for numbers in blisp.
 * 
 * <p>This class encapsulates numeric values, primarily represented as {@link java.lang.Double} by default. 
 * It can be expanded to support other number types by switching to a {@link java.lang.Number} base type 
 * in the future.</p>
 * 
 * <p>Number representations can handle integer and floating-point formats, including optional scientific notation.</p>
 * 
 * <p>Example valid numbers:</p>
 * <ul>
 *   <li>123</li>
 *   <li>-456.789</li>
 *   <li>3.14e+10</li>
 * </ul>
 * 
 * <p>This class also implements {@link java.lang.Comparable} to allow comparisons between number atoms, and 
 * it provides pattern matching to validate number formats.</p>
 * 
 * @see java.lang.Number
 * @see java.lang.Double
 */
// TODO: Switch NumberAtom to use Number base type, then add more constructors.
public class NumberAtom extends Atom<Number> implements Comparable<NumberAtom> {
	/**
     * Default constructor initializing the number atom to 0.0.
     */
	public NumberAtom() {
		super(0);
	}
	
	/**
     * Constructor that initializes the number atom with the given integer value.
     * 
     * @param value The numeric value to assign to this atom.
     */
	public NumberAtom(Integer value) {
		super(value);
	}
	
	/**
     * Constructs a {@code NumberAtom} with the specified integer number value and state.
     *
     * @param value The initial value of this Number atom.
     * @param state The state of this Number atom.
     */
	public NumberAtom(Integer value, EvalState state) {
		super(value, state);
	}
	
	/**
     * Constructor that initializes the number atom with the given double value.
     * 
     * @param value The numeric value to assign to this atom.
     */
	public NumberAtom(Double value) {
		super(value);
	}
	
	/**
     * Constructs a {@code NumberAtom} with the specified double number value and state.
     *
     * @param value The initial value of this Number atom.
     * @param state The state of this Number atom.
     */
    public NumberAtom(Double value, EvalState state) {
    	super(value, state);
    }
	
	/**
     * Compares this number atom with another based on their numeric values.
     * 
     * @param obj The {@link NumberAtom} to compare with.
     * @return A negative integer, zero, or a positive integer as this object is less than, 
     * equal to, or greater than the specified {@code NumberAtom}.
     */
	@Override
	public int compareTo(NumberAtom obj) {
		// TODO: This method needs to be able to compare Number of different types gracefully.
		if (value instanceof Integer) {
			return ((Integer) value).compareTo((Integer) obj.getValue());
		} else if (value instanceof Double) {
			return ((Double) value).compareTo((Double) obj.getValue());
		} else {
			throw new LispRuntimeException("Invalid number type");
		}
	}
	
	/**
     * Tests for equality between this number atom and another.
     * 
     * @param obj The object to compare to. Must be an instance of {@link NumberAtom}.
     * @return {@code true} if both objects represent the same numeric value, otherwise {@code false}.
     * @throws IllegalArgumentException if the object is not an instance of {@link NumberAtom}.
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true; // Check for reference equality
	    if (!(obj instanceof NumberAtom)) return false;
	    // TODO: This method needs to be able to compare Number of different types gracefully.
	    NumberAtom numAtom = (NumberAtom) obj;

	    // Convert both values to Double for comparison
	    Double thisValue = (value instanceof Integer) ? ((Integer) value).doubleValue() : (Double) value;
	    Double otherValue = (numAtom.value instanceof Integer) ? ((Integer) numAtom.value).doubleValue() : (Double) numAtom.value;

	    boolean retVal = thisValue.equals(otherValue);
	    return retVal;
		//throw new IllegalArgumentException("obj must be a NumberAtom");
	}
	
	/**
     * Returns the regular expression pattern to validate number formats, including integers, decimals, and scientific notation.
     * 
     * @return A {@link java.util.regex.Pattern} object for validating number strings.
     */
	@Override
	public Pattern getRegexPattern() {
		return Pattern.compile("-?\\\\d+(\\\\.\\\\d+)?([eE][+-]?\\\\d+)?");
	}

	/**
     * Returns the string representation of the number.
     * 
     * @return A string containing the numeric value, prefixed with "Number: " if {@code extendedPrint} is enabled.
     */
	@Override
	public String toString() {
		String typeName = value.getClass().getName();
		String num = switch (value) {
			case Integer i -> Integer.toString(i);
			case Double d -> Double.toString(d);
			default -> throw new LispRuntimeException("Invalid number type");
		};
		
		if (extendedPrint) {
			return String.format("%s: ", typeName) + num;
		} else {
			return num;
		}
	}
}
