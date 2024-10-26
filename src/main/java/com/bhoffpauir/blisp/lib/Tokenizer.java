package com.bhoffpauir.blisp.lib;

import com.bhoffpauir.blisp.lib.exceptions.LispRuntimeException;
import com.bhoffpauir.blisp.lib.exceptions.UnknownTokenException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
/**
 * The {@code Tokenizer} takes an syntax string as input. It 
 */
public class Tokenizer {
    private String input; // Store the input string
    private int currentIndex; // Keep track of the current character index

    public Tokenizer(String input) {
        this.input = input;
        this.currentIndex = 0;
    }
    /**
     * 
     * 
     * @return The language tokens as strings.
     */
    public List<String> tokenize() {
        // Tokenize input, e.g., break into atoms, numbers, parentheses
        List<String> tokens = new ArrayList<>();
        while (currentIndex < input.length()) {
        	char ch = input.charAt(currentIndex); // The current character
        	
        	// Skip whitespace
        	if (Character.isWhitespace(ch)) {
        		currentIndex++;
        		continue;
        	}
        	
        	// Handle comments (skip until the end of the line)
        	if (ch == ';') {
        		skipComment();
        		continue;
        	}
        	
        	// Handle syntax quote
        	if (ch == '\'') {
        		tokens.add(Character.toString('\''));
        		currentIndex++;
        		continue;
        	}
        	
        	// Handle parentheses
        	if (ch == '(' || ch == ')') {
        		tokens.add(Character.toString(ch));
        		currentIndex++;
        		continue;
        	}
        	
        	// Handle strings (quoted)
        	if (ch == '"') {
        		tokens.add(readString());
        		continue;
        	}
        	
        	// Handle characters
        	if (ch == '\\') {
        		tokens.add(readCharacter());
        		continue;
        	}
        	
        	// Handle numbers and symbols
        	if (Character.isLetter(ch) || isSymbolStart(ch)) {
        		tokens.add(readSymbolOrNumber());
        		continue;
        	}

        	currentIndex++; // Skip unknown characters (not really)
        	// If this point is reach the token is unknown (let the REPL handle it)
        	throw new UnknownTokenException(tokens.isEmpty() ? "N/A" : tokens.get(tokens.size() - 1));
        }
        return tokens;
    }
    
    /**
     * Skip single-line comments.
     */
    private void skipComment() {
    	// Skip until the end of the line or input
    	while (currentIndex < input.length() && input.charAt(currentIndex) != '\n') {
    		currentIndex++;
    	}
    	// Move past the newline character
    	currentIndex++;
    }
    
    /**
     * Handle the reading of quoted strings.
     * 
     * @return The read string (quotes included).
     */
    private String readString() {
    	StringBuilder sb = new StringBuilder();
    	currentIndex++; // Skip over the initial quote
    	
    	while (currentIndex < input.length()) {
    		char ch = input.charAt(currentIndex);
    		
    		// Check for the closing quote
    		if (ch == '"') {
    			currentIndex++; // Skip the closing quote
    			break;
    		}
    		
    		// Check for escape sequences
    		if (ch == '\\') {
    			currentIndex++; // Move the character past the backslash
    			
    			if (currentIndex >= input.length()) {
    				throw new LispRuntimeException("Unterminated escape sequence");
    			}
    			
    			char next = input.charAt(currentIndex);
    			switch (next) {
    			case 'n': sb.append('\n'); break;
                case 't': sb.append('\t'); break;
                case 'b': sb.append('\b'); break;
                case 'r': sb.append('\r'); break;
                case 'f': sb.append('\f'); break;
                case '"': sb.append('"'); break;
                case '\\': sb.append('\\'); break;
                default:
                    // If it's an unrecognized escape sequence, include the `\` and the character as is
                    sb.append('\\').append(next);
                    break;
    			}
    		} else {
    			sb.append(ch);
    		}
    		
    		currentIndex++;
    	}
    	return String.format("\"%s\"", sb.toString()); // Return the string value with quotes
    }
    
    /**
     * Handle the reading of characters.
     * 
     * @return The character in reader syntax.
     */
    private String readCharacter() {
    	StringBuilder sb = new StringBuilder();
    	
    	// Keep reading until encountering whitespace or some other delimeter
    	while (currentIndex < input.length()) {
    		char ch = input.charAt(currentIndex);
    		if (Character.isWhitespace(ch) || ch == '(' || ch == ')') {
    			break;
    		}
    		sb.append(ch);
    		currentIndex++;
    	}
    	return sb.toString();
    }
    
    /**
     * Handle the reading of symbols or numbers.
     * 
     * @return .
     */
    private String readSymbolOrNumber() {
    	StringBuilder sb = new StringBuilder();
    	
    	while (currentIndex < input.length()) {
    		char ch = input.charAt(currentIndex);
    		if (Character.isWhitespace(ch) || ch == '(' || ch == ')') {
    			break;
    		}
    		sb.append(ch);
    		currentIndex++;
    	}
    	return sb.toString();
    }
    
    /**
     * Check if a character can be the start of a symbol.
	 *
     * @param ch The character to check.
     * @return True if the character can be the start of a symbol, false otherwise.
     */
    public static boolean isSymbolStart(char ch) {
    	return "+-*/<=>!$%&?^_~".indexOf(ch) >= 0 || Character.isDigit(ch);
    }
}
