package com.bhoffpauir.blisp.lib;

import java.util.regex.Pattern;

/**
 * 
 */
public class CharacterAtom extends Atom<Character> implements Comparable<CharacterAtom> {
	/**
	 * 
	 * 
	 * @param value
	 */
	public CharacterAtom(Character value) {
		super(value);
	}
	
	/**
	 * 
	 * 
	 * @param value
	 * @param state
	 */
	public CharacterAtom(Character value, EvalState state) {
		super(value, state);
	}
	
	/**
     * Tests equality of characters.
     * 
     * @param obj the object to compare to, which should be a {@code CharacterAtom}
     * @return {@code true} if the characters are equal, {@code false} otherwise
     * @throws IllegalArgumentException if the provided object is not a {@code CharacterAtom}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharacterAtom) {
            CharacterAtom charAtom = (CharacterAtom) obj;
            return value.equals(charAtom.value);
        } else {
            throw new IllegalArgumentException("obj must be a CharacterAtom");
        }
    }
    
    /**
     * Compares this {@code CharacterAtom} to another character, using lexicographic ordering.
     * 
     * @param obj the {@code CharacterAtom} to compare to.
     * @return a negative integer, zero, or a positive integer as this character is lexicographically less than, 
     * equal to, or greater than the specified character.
     */
    @Override
    public int compareTo(CharacterAtom obj) {
        return value.compareTo(obj.value);
    }

	@Override
	public Pattern getRegexPattern() {
		return Pattern.compile("\\\\(.|0x[0-9A-Fa-f]+)");
	}

	@Override
	public String toString() {
		final var quoted = "'" + value + "'";
		if (extendedPrint) {
			return "Character: " + quoted;
		} else {
			return quoted;
		}
	}
}
