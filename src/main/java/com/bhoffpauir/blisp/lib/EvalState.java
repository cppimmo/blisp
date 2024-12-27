package com.bhoffpauir.blisp.lib;

/**
 * Enumeration to represent whether a value is to be treated as quoted or unquoted by the
 * parser.
 */
public enum EvalState {
	/** Quoted state prevents the evaluator from evaluating an expression. */
	QUOTED,
	/** The evaluator treats unquoted expressions normally. */
	UNQUOTED
}
