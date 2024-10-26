package com.bhoffpauir.blisp.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import com.bhoffpauir.blisp.lib.exceptions.LispRuntimeException;
import com.bhoffpauir.blisp.lib.exceptions.UnbalancedParenthesisException;

public class Parser {
    private List<String> tokens;
    private int currentIndex;

    public Parser(List<String> tokens) {
        this.tokens = tokens;
        this.currentIndex = 0;
    }

    public Object parse() {
    	if (tokens.isEmpty()) {
    		throw new IllegalArgumentException("No tokens to parse.");
    	}
    	
    	return parseExpression();
    }
    
    public Object parseExpression() {
    	if (currentIndex >= tokens.size()) {
    		throw new NoSuchElementException("Unexpected end of input.");
    	}
    	
    	String token = tokens.get(currentIndex);
    	currentIndex++;
    	
    	if (token.equals('\'')) {
    		currentIndex++;
    		return parseExpression();
    	} else if (token.equals("(")) {
    		// Parse a list
    		List<Object> list = new ArrayList<>();
    		// Note: Right now trying to parse a single ( as input will result in out of bounds
    		while (!tokens.get(currentIndex).equals(")")) {
    			list.add(parseExpression());
    			if (currentIndex >= tokens.size()) {
    				throw new UnbalancedParenthesisException("No closing parenthesis");
    			}
    		}
    		currentIndex++; // Skip the closing parenthesis
    		return new ListAtom(list);
    	} else if (token.equals(")")) {
    		throw new UnbalancedParenthesisException("Unexpected closing parenthesis.");
    	} else {
    		// Parse an atom (symbol, number, or string)
    		return parseAtom(token);
    	}
    }
    
    private Object parseAtom(String token) {
    	if (token.isEmpty()) {
    		throw new IllegalArgumentException("Cannot process empty atom.");
    	}

    	Pattern charPattern = new CharacterAtom('A').getRegexPattern();
    	Pattern symbolPattern = new SymbolAtom("").getRegexPattern();
    	Pattern stringPattern = new StringAtom().getRegexPattern();
    	
    	if (token.startsWith("\\")) {
    		String subToken = token.substring(1);
    		if (charPattern.matcher(token).matches()) {
    			if (subToken.startsWith("0x")) {
    				// Extract the hexadecimal Unicode number
    				int decimalValue = Integer.parseInt(subToken.substring(2), 16);
    				return new CharacterAtom((char) decimalValue);
    			} else
    				return new CharacterAtom(subToken.charAt(0));
    		}
    		throw new LispRuntimeException(token + " is not a valid character");
    	}
    	
    	if (token.startsWith("\"")) {
    		if (stringPattern.matcher(token).matches()) {
    			return new StringAtom(token.substring(1, token.length() - 1));
    		}
    		// TODO: Throw exception for unbalanced quotes.
    	}
    	// Check if it's a valid symbol using regex
        if (symbolPattern.matcher(token).matches()) {
        	// Check for special symbols
        	Pattern booleanPattern = new BooleanAtom(true).getRegexPattern();
        	if (booleanPattern.matcher(token).matches())
        		return new BooleanAtom(Boolean.parseBoolean(token));
        	else
        		return new SymbolAtom(token); // Return a SymbolAtom
        } else {
        	NumberAtom numAtom = null;
        	try {
        		Integer value = Integer.parseInt(token);
        		numAtom = new NumberAtom(value);
        	} catch (NumberFormatException ex) {
        		Double value = Double.parseDouble(token);
        		numAtom = new NumberAtom(value);
        	}

        	if (numAtom == null)
        		throw new LispRuntimeException("Invalid number: " + token);
        	else
        		return numAtom;
    	}
    }
}
