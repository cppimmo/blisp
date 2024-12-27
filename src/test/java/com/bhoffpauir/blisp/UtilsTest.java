package com.bhoffpauir.blisp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bhoffpauir.blisp.lib.Utils;

/**
 * Unit test for the {@code Utils} class.
 */
public class UtilsTest {
    @Test
    public void testQuote() {
        assertEquals(Utils.quote("hi", Utils.Quote.SINGLE), "'hi'");
        assertEquals(Utils.quote("hi", Utils.Quote.DOUBLE), "\"hi\"");
    }
    
    @Test
    public void testUnquote() {
    	assertEquals(Utils.unquote("'hi'", Utils.Quote.SINGLE), "hi");
        assertEquals(Utils.unquote("\"hi\"", Utils.Quote.DOUBLE), "hi");
    }
    
    @Test
    public void testHasUnbalancedParenthesis() {
    	final String str1 = "(list 1 2 3)";
    	final String str2 = "(list 1 2 3))";
    	assertFalse(Utils.hasUnmatchedParentheses(str1));
    	assertTrue(Utils.hasUnmatchedParentheses(str2));
    }
}
