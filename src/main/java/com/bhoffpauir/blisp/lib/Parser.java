package com.bhoffpauir.blisp.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

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
    	
    	if (token.equals("(")) {
    		// Parse a list
    		List<Object> list = new ArrayList<>();
    		// Note: Right now trying to parse a single ( as input will result in out of bounds
    		while (!tokens.get(currentIndex).equals(")")) {
    			list.add(parseExpression());
    			if (currentIndex >= tokens.size()) {
    				throw new NoSuchElementException("Missing closing parenthesis.");
    			}
    		}
    		currentIndex++; // Skip the closing parenthesis
    		return new ListAtom(list);
    	} else if (token.equals(")")) {
    		throw new IllegalArgumentException("Unexpected closing parenthesis.");
    	} else {
    		// Parse an atom (symbol, number, or string)
    		return parseAtom(token);
    	}
    }
    
    private Object parseAtom(String token) {
    	if (token.isEmpty()) {
    		throw new IllegalArgumentException("Cannot process empty atom.");
    	}

    	Pattern symbolPattern = new SymbolAtom("").getRegexPattern();
    	Pattern stringPattern = new StringAtom().getRegexPattern();
    	
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
    		return new NumberAtom(Double.parseDouble(token));
    		// TODO: Catch and re-throw number format exceptions with a custom exception.
    	}
    }
}
