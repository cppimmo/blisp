package com.bhoffpauir.blisp.lib;

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
    
    private void defineBindings() {
    	define("foo", new NumberAtom(2.0));
    }
    
    private void defineBuiltIns() {
    	// Define "define" procedure/macro
    	define("define", (VariadicFunction) (args) -> {
    		if (args.size() != 2) {
    			throw new RuntimeException("");
    		}
    		
    		SymbolAtom arg1 = (SymbolAtom) args.get(0);
    		Atom arg2 = (Atom) args.get(1);
    		define(arg1.getValue(), arg2);
    		
    		return new SymbolAtom("nil");
    	});
    	// Define "print" procedure
    	define("print", (VariadicFunction) (args) -> {
    		for (int i = 0; i < args.size(); i++) {
    			System.out.print(args.get(i));
    			if (i < args.size() - 1) {
    				System.out.print(' ');
    			}
    		}
    		return new SymbolAtom("nil");
    	});
    	// Define "println" procedure
    	define("println", (VariadicFunction) (args) -> {
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
		define("+", (VariadicFunction) (args) -> {
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
		
		// Define "*" procedure
		
		// Define "/" procedure
		
		// Define "symbol?" predicate
		define("symbol?", (Function<Object, Object>) (a) -> {
			return new BooleanAtom(a instanceof SymbolAtom);
		});
		// Define "number?" predicate
		define("number?", (Function<Object, Object>) (a) -> {
			return new BooleanAtom(a instanceof NumberAtom);
		});
		// Define "boolean?" predicate
		define("boolean?", (Function<Object, Object>) (a) -> {
			return new BooleanAtom(a instanceof BooleanAtom);
		});
		// Define "string?" predicate
		define("string?", (Function<Object, Object>) (a) -> {
			return new BooleanAtom(a instanceof StringAtom);
		});
		// Define "list?" predicate
		define("list?", (Function<Object, Object>) (a) -> {
			return new BooleanAtom(a instanceof ListAtom);
		});
	}
}
