package com.bhoffpauir.blisp.lib;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;

/**
 * Utility methods.
 */
public final class Utils {
	/**
	 * @see com.bhoffpauir.blisp.lib.Utils.quote
	 * @see com.bhoffpauir.blisp.lib.Utils.unquote
	 */
	public enum Quote {
		SINGLE,
		DOUBLE
	}
	
	private static char quoteToChar(Quote quote) {
		return switch (quote) {
			case Quote.SINGLE -> '\'';
			case Quote.DOUBLE -> '"';
		};
	}
	
	/**
	 * @param unquoted
	 * @return {@code unquoted} surrounded by double quotes.
	 */
	public static String quote(String unquoted, Quote quote) {
		final char ch = quoteToChar(quote); 
		return String.format("%c%s%c", ch, unquoted, ch);
	}
	
	/**
	 * @param quoted
	 * @return {@code quoted} with double quotes removed.
	 */
	public static String unquote(String quoted, Quote quote) {
		final var str = Character.toString(quoteToChar(quote));
		return (quoted.startsWith(str) && quoted.endsWith(str))
				? quoted.substring(1, quoted.length() - 1)
				: quoted;
	}
	
	/**
     * Detect if the {@code input} string has unbalanced parentheses.
     * 
     * @param input The input string.
     * @return True if the string has unbalanced parentheses, false otherwise.
     */
    public static boolean hasUnmatchedParentheses(String input) {
    	if (!(input.contains("(") || input.contains(")"))) {
    		return false;
    	}
    	
        int openParenCount = 0;
        for (char ch : input.toCharArray()) {
            if (ch == '(') openParenCount++;
            if (ch == ')') openParenCount--;
        }
        return openParenCount != 0;
    }
    
    /**
     * Utility method to open a URL in the default browser.
     * 
     * @param url The URL to browse.
     */
    public static void openUrlInBrowser(URL url) {
    	if (Desktop.isDesktopSupported()) {
    		try {
    			// Get the system's Desktop instance & open the URL
    			Desktop desktop = Desktop.getDesktop();
    			desktop.browse(url.toURI());
    		} catch (Exception ex) {
    			System.err.println("Error opening URL: " + ex.getMessage());
    		}
    	} else {
    		System.err.println("Desktop is not supported on this system.");
    	}
    }
    
    /**
     * Overload for {@code openUrlInBrowser} with a {@code String} parameter.
     * 
     * @param urlString The URL to browse.
     */
    public static void openUrlInBrowser(String urlString) {
    	try {
    		openUrlInBrowser(new URI(urlString).toURL());
    	} catch (Exception ex) {
    		System.err.println("Invalid URL: " + ex.getMessage());
    	}
    }
}
