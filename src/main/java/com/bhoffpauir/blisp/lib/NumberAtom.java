package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

/**
 * Atom representation for numbers.
 * 
 * Number representations:
 * - 
 */
public class NumberAtom extends Atom<Double> {
	public NumberAtom() {
		super(0.0);
	}
	
	public NumberAtom(Double value) {
		super(value);
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
