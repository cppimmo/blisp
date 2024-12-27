package com.bhoffpauir.blisp.interp;

/**
 * Enumeration representing the runtime modes of the interpreter.
 */
public enum InterpreterMode {
	/** Read-eval-print-loop. */
	REPL,
	
	/** Script file execution. */
	SCRIPT,
	
	/** Script file execution, followed by opening of a REPL. */
	SCRIPT_AND_REPL
}
