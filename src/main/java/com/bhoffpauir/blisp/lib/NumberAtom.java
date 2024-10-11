package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

/**
 * Atom representation for numbers.
 * 
 * Number representations:
 * - 
 */
// TODO: Switch NumberAtom to use Number base type, then add more constructors.
public class NumberAtom extends Atom<Double> implements Comparable<NumberAtom> {
	public NumberAtom() {
		super(0.0);
	}
	// TODO: Implement the comparable/comparator interface.
	public NumberAtom(Double value) {
		super(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NumberAtom) {
			NumberAtom numAtom = (NumberAtom) obj;
			return value.equals(numAtom.value);
		} else
			throw new IllegalArgumentException("obj must be a NumberAtom");
	}
	
	@Override
	public int compareTo(NumberAtom obj) {
		return value.compareTo(obj.getValue());
	}
	
	@Override
	public Pattern getRegexPattern() {
		return Pattern.compile("-?\\\\d+(\\\\.\\\\d+)?([eE][+-]?\\\\d+)?");
	}

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
