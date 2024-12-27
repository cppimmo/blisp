package com.bhoffpauir.blisp.lib;

/**
 * Defines the various types of tokens in blisp.
 * 
 * <p>
 * Tokens are categorized into two broad groups:
 * <ul>
 * <li>Delimiters/Operators: Symbols and constructs that define the structure and operations of the language.</li>
 * <li>Atoms: Self-contained values like booleans, numbers, strings, and symbols.</li>
 * </ul>
 * </p>
 * 
 * <p>Token Types:</p>
 * <ul>
 *   <li><b>LEFT_PAREN</b>: Represents the opening parenthesis "(".</li>
 *   <li><b>RIGHT_PAREN</b>: Represents the closing parenthesis ")".</li>
 *   <li><b>DOUBLE_QUOTE</b>: Represents the double-quote symbol "\"" used for strings.</li>
 *   <li><b>SINGLE_QUOTE</b>: Represents the single-quote symbol "'" used as the quote operator.</li>
 *   <li><b>SPLICE_QUOTE</b>: Represents the splice-quote symbol "`" (backtick), typically used for quasi-quotation.</li>
 *   <li><b>COMMA</b>: Represents the comma "," symbol, often used in quasi-quotation for unquoting.</li>
 *   <li><b>BOOLEAN</b>: Represents a boolean literal such as `true` or `false`.</li>
 *   <li><b>CHARACTER</b>: Represents a single character literal.</li>
 *   <li><b>NUMBER</b>: Represents a numeric value, including integers, floats, or ratios.</li>
 *   <li><b>STRING</b>: Represents a string literal enclosed in double quotes.</li>
 *   <li><b>SYMBOL</b>: Represents a symbolic identifier used for variables, functions, or special forms.</li>
 *   <li><b>NIL</b>: Represents the `nil` value, used for empty lists or the absence of a value.</li>
 *   <li><b>EOF</b>: Represents the end of the input stream, indicating no more tokens are available.</li>
 * </ul>
 * 
 * <p>
 * This enum is used when tokenized input is passed to the parsing stage.
 * </p>
 * 
 * @see com.bhoffpauir.blisp.lib.Tokenizer
 * @since 1.0
 */
public enum TokenType {
    // Delimiters/operators:
    /** Represents the opening parenthesis "(". */
    LEFT_PAREN,
    
    /** Represents the closing parenthesis ")". */
    RIGHT_PAREN,
    
    /** Represents the double-quote symbol "\"" used for strings. */
    DOUBLE_QUOTE,
    
    /** Represents the single-quote symbol "'" used as the quote operator. */
    SINGLE_QUOTE,
    
    /** Represents the splice-quote symbol "`" (backtick) used for quasi-quotation. */
    SPLICE_QUOTE,
    
    /** Represents the comma "," symbol often used in quasi-quotation for unquoting. */
    COMMA,
    
    // Atoms:
    /** Represents a boolean literal such as `true` or `false`. */
    BOOLEAN,
    
    /** Represents a single character literal. */
    CHARACTER,
    
    /** Represents a numeric value, including integers, floats, or ratios. */
    NUMBER,
    
    /** Represents a string literal enclosed in double quotes. */
    STRING,
    
    /** Represents a symbolic identifier used for variables, functions, or special forms. */
    SYMBOL,
    
    /** Represents the `nil` value, used for empty lists or the absence of a value. */
    NIL,
    
    /** Represents the end of the input stream, indicating no more tokens are available. */
    EOF
}
