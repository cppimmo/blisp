package com.bhoffpauir.blisp.lib;

import com.bhoffpauir.blisp.lib.exceptions.LispRuntimeException;
import com.bhoffpauir.blisp.lib.exceptions.UnboundSymbolException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
// TODO: Create a default environment that contains the symbols: true, false, nil, etc.
/**
 * 
 */
public class Environment {
    private Map<String, Object> bindings;
    private Environment parent;

    public Environment() {
        this.bindings = new HashMap<>();
        this.parent = null;
    }
    
    public Environment(Environment parent) {
        this.bindings = new HashMap<>();
        this.parent = parent;
    }
    /**
     * Define a new variable.
     * @param symbol
     * @param value
     */
    public void define(String symbol, Object value) {
    	bindings.put(symbol, value);
    }
    /**
     * 
     * @param symbol
     * @return
     */
    public Object lookup(String symbol) {
        // Lookup a variable in the current environment
    	if (bindings.containsKey(symbol)) {
    		return bindings.get(symbol);
    	}
    	throw new UnboundSymbolException(symbol); // Symbol doesn't exist
    }
    /**
     * 
     */
    public Object nullableLookup(String symbol) {
        if (bindings.containsKey(symbol)) {
        	return bindings.get(symbol);
        } else {
        	return null; // Symbol doesn't exist
        }
    }
    /**
     * Factory method for creating global environments. Global environments have access
     * to built-in symbol bindings.
     * @return The newly created global environment.
     */
    public static Environment createGlobalEnv() {
    	Environment env = new Environment();
    	env.defineBindings();
    	env.defineBuiltIns();
    	return env;
    }
    /**
     * Define builtin global variable bindings.
     */
    private void defineBindings() {
    	//define("foo", new NumberAtom(2.0));
    }
    /**
     * Define builtin procedures.
     */
    private void defineBuiltIns() {
    	// Define "quit" procedure
    	define("quit", (Procedure) (args) -> {
    		int exitCode = 0; 
    		if (!args.isEmpty()) {
    			Object arg1 = args.get(0);
    			if (arg1 instanceof NumberAtom) {
    				Double value = ((NumberAtom) arg1).getValue();
    				exitCode = value.intValue();
    			}
    		}
    		System.exit(exitCode);
    		return new SymbolAtom("nil");
    	});
    	// Define "print" procedure
    	define("print", (Procedure) (args) -> {
    		for (int i = 0; i < args.size(); i++) {
    			System.out.print(args.get(i));
    			if (i < args.size() - 1) {
    				System.out.print(' ');
    			}
    		}
    		return new SymbolAtom("nil");
    	});
    	// Define "println" procedure
    	define("println", (Procedure) (args) -> {
    		for (int i = 0; i < args.size(); i++) {
    			System.out.print(args.get(i));
    			if (i < args.size() - 1) {
    				System.out.print(' ');
    			}
    		}
    		System.out.println();
    		return new SymbolAtom("nil");
    	});
    	// Define "+" procedure
		define("+", (Procedure) (args) -> {
			double sum = 0.0;
			for (var arg : args) {
				if (!(arg instanceof NumberAtom)) {
					throw new RuntimeException("Invalid argument(s) for +: " + arg);
				}
				sum += ((NumberAtom) arg).getValue();
			}
			return new NumberAtom(sum);
		});
		// Define "-" procedure
		define("-", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid number of arguments for -");
			}
			if (!(args.get(0) instanceof NumberAtom)) {
				throw new LispRuntimeException("Invalid argument(s) for -: " + args.get(0));
			}
			
			// Start with the first argument as the base
			double difference = ((NumberAtom) args.get(0)).getValue();
			// Subtract subsequent arguments
			for (int i = 1; i < args.size(); i++) {
				var arg = args.get(i);
				if (!(arg instanceof NumberAtom)) {
					throw new LispRuntimeException("Invalid argument(s) for -: " + arg);
				}
				difference -= ((NumberAtom) arg).getValue();
			}
			return new NumberAtom(difference);
		});
		// Define "*" procedure
		define("*", (Procedure) (args) -> {
			double product = 1.0;
			for (var arg : args) {
				if (!(arg instanceof NumberAtom)) {
					throw new LispRuntimeException("Invalid argument(s) for *: " + arg);
				}
				product *= ((NumberAtom) arg).getValue();
			}
			return new NumberAtom(product);
		});
		// Define "/" procedure
		define("/", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid number of arguments for /");
			}
			if (!(args.get(0) instanceof NumberAtom)) {
				throw new LispRuntimeException("Invalid argument(s) for /: " + args.get(0));
			}
			
			// Start with the first argument as the base
			double quotient = ((NumberAtom) args.get(0)).getValue();
			// Divide by subsequent arguments
			for (int i = 1; i < args.size(); i++) {
				var arg = args.get(i);
				if (!(arg instanceof NumberAtom)) {
					throw new LispRuntimeException("Invalid argument(s) for /: " + arg);
				}
				double value = ((NumberAtom) arg).getValue();
				if (value == 0.0) {
					throw new LispRuntimeException("Division by zero");
				}
				quotient /= value;
			}
			return new NumberAtom(quotient);
		});
		// Define "symbol?" predicate
		define("symbol?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for symbol?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof SymbolAtom);
		});
		// Define "number?" predicate
		define("number?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for number?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof NumberAtom);
		});
		// Define "boolean?" predicate
		define("boolean?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for boolean?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof BooleanAtom);
		});
		// Define "string?" predicate
		define("string?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for string?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof StringAtom);
		});
		// Define "list?" predicate
		define("list?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for list?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof ListAtom);
		});
	}
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for (var entry : bindings.entrySet() ) {
    		sb.append(String.format("%s : %s\n", entry.getKey(), entry.getValue()));
    	}
    	return sb.toString();
    }
}
