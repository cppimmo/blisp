package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

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
public class NumberAtom extends Atom<Double> implements Comparable<NumberAtom> {
	
	/**
     * Default constructor initializing the number atom to 0.0.
     */
	public NumberAtom() {
		super(0.0);
	}
	
	/**
     * Constructor that initializes the number atom with the given value.
     * 
     * @param value The numeric value to assign to this atom.
     */
	public NumberAtom(Double value) {
		super(value);
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
		return value.compareTo(obj.getValue());
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
		if (obj instanceof NumberAtom) {
			NumberAtom numAtom = (NumberAtom) obj;
			return value.equals(numAtom.value);
		} else
			throw new IllegalArgumentException("obj must be a NumberAtom");
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
		var x = Double.toString(value);
		if (extendedPrint) {
			return "Number: " + x;
		} else {
			return x;
		}
	}
}
