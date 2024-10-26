package com.bhoffpauir.blisp.lib;

import com.bhoffpauir.blisp.lib.exceptions.LispRuntimeException;
import com.bhoffpauir.blisp.lib.exceptions.RebindKeywordSymbolException;
import com.bhoffpauir.blisp.lib.exceptions.UnboundSymbolException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class Environment {
	static private Set<String> keywords;
    private Map<String, Object> bindings;
    private Environment parent;

    static {
    	keywords = new HashSet<>(Arrays.asList(
    		"define", "λ", "lambda", "if", "begin"
    	));
    }
    
    /**
     * 
     */
    public Environment() {
        this.bindings = new HashMap<>();
        this.parent = null;
    }
    
    /**
     * 
     * 
     * @param bindings
     */
    public Environment(Map<String, Object> bindings) {
    	this.bindings = new HashMap<>(bindings);
    	this.parent = null;
    }
    
    /**
     * 
     * 
     * @param parent
     */
    public Environment(Environment parent) {
        this.bindings = new HashMap<>();
        this.parent = parent;
    }
    
    /**
     * 
     * 
     * @param parent
     * @param bindings
     */
    public Environment(Environment parent, Map<String, Object> bindings) {
    	this.bindings = new HashMap<>(bindings);
    	this.parent = parent;
    }
    
    /**
     * Define a new symbol binding.
     * @param symbol
     * @param value
     */
    public void define(String symbol, Object value) {
    	if (keywords.contains(symbol)) {
    		throw new RebindKeywordSymbolException(symbol);
    	}
    	
    	bindings.put(symbol.toLowerCase(), value);
    }
    
    /**
     * Define a new symbol binding.
     */
    public void define(SymbolAtom symbol, Object value) {
    	define(symbol.toString(), value);
    }
    
    /**
     * Define a new symbol binding.
     * @param symbol
     * @param value
     */
    public void define(Map<String, Object> bindings, String symbol, Object value) {
    	if (keywords.contains(symbol)) {
    		throw new RebindKeywordSymbolException(symbol);
    	}
    	
    	bindings.put(symbol.toLowerCase(), value);
    }
    
    /**
     * Define a new symbol binding.
     */
    public void define(Map<String, Object> bindings, SymbolAtom symbol, Object value) {
    	define(symbol.toString(), value);
    }
    
    /**
     * Lookup {@code symbol} in this environment.
     * 
     * @throws UnboundSymbolException if the symbol doesn't exist.
     * @param symbol The symbol to lookup.
     * @return The symbol value.
     */
    public Object lookup(final String symbol) {
    	var loweredSym = symbol.toLowerCase();
        // Lookup a variable in the current environment
    	if (bindings.containsKey(loweredSym)) {
    		return bindings.get(loweredSym);
    	}
    	throw new UnboundSymbolException(symbol); // Symbol doesn't exist
    }
    
    /**
     * Lookup {@code symbol} in this environment.
     * 
     * @param symbol The symbol to lookup.
     * @return The symbol value or null if it doesn't exist.
     */
    public Object nullableLookup(final String symbol) {
    	var loweredSym = symbol.toLowerCase();
        if (bindings.containsKey(loweredSym)) {
        	return bindings.get(loweredSym);
        } else {
        	return null; // Symbol doesn't exist
        }
    }
    
    /**
     * Lookup {@code symbol} in this environment.
     * 
     * @throws UnboundSymbolException if the symbol doesn't exist.
     * @param symbol The symbol to lookup.
     * @return The symbol value.
     */
    public Object lookup(final SymbolAtom symbol) {
    	return lookup(symbol.getValue());
    }
    
    /**
     * Lookup {@code symbol} in this environment.
     * 
     * @param symbol The symbol to lookup.
     * @return The symbol value or null if it doesn't exist.
     */
    public Object nullableLookup(final SymbolAtom symbol) {
    	return nullableLookup(symbol.getValue());
    }
    
    /**
     * Factory method for creating global environments. Global environments have access
     * to built-in symbol bindings.
     * 
     * @return The newly created global environment.
     */
    public static Environment createGlobalEnv() {
    	Environment env = new Environment();
    	// env.defineBindings();
    	Map<String, Object> builtins = env.defineBuiltIns();
    	builtins.forEach((key, value) -> {
    		env.bindings.merge(key, value, (v1, v2) -> v2); // Merge with the new binding
    	});
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
    private Map<String, Object> defineBuiltIns() {
    	Map<String, Object> builtins = new HashMap<>();
    	
    	// TODO: Update the arithmetic procedures to promote to double.
    	
    	// Define "quit" procedure
    	define(builtins, "exit", (Procedure) (args) -> {
    		int exitCode = 0; 
    		if (!args.isEmpty()) {
    			Object arg1 = args.get(0);
    			if (arg1 instanceof NumberAtom)
    				exitCode = ((NumberAtom) arg1).getValue().intValue();
    		}
    		System.exit(exitCode);
    		return new SymbolAtom("nil");
    	});
    	// Define "print" procedure
    	define(builtins, "print", (Procedure) (args) -> {
    		for (int i = 0; i < args.size(); i++) {
    			Object arg = args.get(i);
    			if (arg instanceof StringAtom) {
    				System.out.print(((StringAtom) arg).getValue());
    			} else {
    				System.out.print(args.get(i));
    			}
    			
    			if (i < args.size() - 1) {
    				System.out.print(' ');
    			}
    		}
    		return new SymbolAtom("nil");
    	});
    	// Define "sprintf" procedure
    	define(builtins, "sprintf", (Procedure) (args) -> {
    		if (args.isEmpty() || !(args.get(0) instanceof StringAtom)) {
    			throw new IllegalArgumentException("First argument to sprintf is a format string");
    		}
    		String fmt = ((StringAtom) args.get(0)).getValue(); // Format string
    		
    		// Only print of the format string
    		if (args.size() == 1) {
    			System.out.printf(fmt);
    			return new StringAtom(String.format(fmt));
    		}
    		// Continue and print using the varargs [1, size)
    		List<Object> subArgsList = args.subList(1, args.size());
    		Object[] subArgsArray = new Object[subArgsList.size()];
    		for (int i = 0; i < subArgsArray.length; i++) {
    			var atom = subArgsList.get(i);
    			
    			if (atom instanceof StringAtom)
    				subArgsArray[i] = ((StringAtom) atom).getValue();
    			else if (atom instanceof CharacterAtom)
    				subArgsArray[i] = ((CharacterAtom) atom).getValue();
    			else if (atom instanceof NumberAtom)
    				subArgsArray[i] = ((NumberAtom) atom).getValue();
    			else
    				subArgsArray[i] = atom;
    		}
    		return new StringAtom(String.format(fmt, subArgsArray));
    	});
    	// Define "printf" procedure
    	define(builtins, "printf", (Procedure) (args) -> {
    		// Define in term of the "sprintf" procedure
    		Procedure sprintf = (Procedure)builtins.get("sprintf");
    		String formatStr = ((StringAtom) sprintf.apply(args)).getValue();
    		System.out.print(formatStr);
    		return new SymbolAtom("nil");
    	});
    	// Define "println" procedure
    	define(builtins, "println", (Procedure) (args) -> {
    		// Defined in terms of "print"
    		Procedure print = (Procedure)builtins.get("print");
    		print.apply(args);
    		System.out.println();
    		return new SymbolAtom("nil");
    	});
    	// Define "+" procedure
		define(builtins, "+", (Procedure) (args) -> {
			double sum = 0.0;
			for (var arg : args) {
				if (!(arg instanceof NumberAtom)) {
					throw new RuntimeException("Invalid argument(s) for +: " + arg);
				}
				sum += ((NumberAtom) arg).getValue().doubleValue();
			}
			return new NumberAtom(sum);
		});
		// Define "-" procedure
		define(builtins, "-", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid number of arguments for -");
			}
			if (!(args.get(0) instanceof NumberAtom)) {
				throw new LispRuntimeException("Invalid argument(s) for -: " + args.get(0));
			}
			
			// Start with the first argument as the base
			double difference = ((NumberAtom) args.get(0)).getValue().doubleValue();
			// Subtract subsequent arguments
			for (int i = 1; i < args.size(); i++) {
				var arg = args.get(i);
				if (!(arg instanceof NumberAtom)) {
					throw new LispRuntimeException("Invalid argument(s) for -: " + arg);
				}
				difference -= ((NumberAtom) arg).getValue().doubleValue();
			}
			return new NumberAtom(difference);
		});
		// Define "*" procedure
		define(builtins, "*", (Procedure) (args) -> {
			double product = 1.0;
			for (var arg : args) {
				if (!(arg instanceof NumberAtom)) {
					throw new LispRuntimeException("Invalid argument(s) for *: " + arg);
				}
				product *= ((NumberAtom) arg).getValue().doubleValue();
			}
			return new NumberAtom(product);
		});
		// Define "/" procedure
		define(builtins, "/", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid number of arguments for /");
			}
			if (!(args.get(0) instanceof NumberAtom)) {
				throw new LispRuntimeException("Invalid argument(s) for /: " + args.get(0));
			}
			
			// Start with the first argument as the base
			double quotient = ((NumberAtom) args.get(0)).getValue().doubleValue();
			// Divide by subsequent arguments
			for (int i = 1; i < args.size(); i++) {
				var arg = args.get(i);
				if (!(arg instanceof NumberAtom)) {
					throw new LispRuntimeException("Invalid argument(s) for /: " + arg);
				}
				double value = ((NumberAtom) arg).getValue().doubleValue();
				if (value == 0.0) {
					throw new LispRuntimeException("Division by zero");
				}
				quotient /= value;
			}
			return new NumberAtom(quotient);
		});
		// Define "mod" procedure
		define(builtins, "mod", (Procedure) (args) -> {
			if (args.size() != 2) {
				throw new LispRuntimeException("Invalid number of arguments for mod");
			}
			if (!(args.get(0) instanceof NumberAtom) || !(args.get(1) instanceof NumberAtom)) {
				throw new LispRuntimeException("Invalid argument(s) for mod: " + args.get(0)
					+ ", " + args.get(1));
			}
			
			NumberAtom arg1 = (NumberAtom)args.get(0), arg2 = (NumberAtom)args.get(1);
			return new NumberAtom(Math.floor(arg1.getValue().intValue()) % (int)Math.floor(arg2.getValue().intValue()));
		});
		// Define "list" procedure
		define(builtins, "list", (Procedure) (args) -> {
			return new ListAtom(args);
		});
		// Define "nth" procedure
		define(builtins, "nth", (Procedure) (args) -> {
			if (args.size() != 2) {
				throw new LispRuntimeException("Invalid number of argument(s) to nth: " + args.size());
			}
			if (!(args.get(0) instanceof ListAtom) || !(args.get(1) instanceof NumberAtom))
				throw new LispRuntimeException("Invalid arguments for nth: " + args);
			
			int index = ((NumberAtom) args.get(1)).getValue().intValue();
			return ((ListAtom) args.get(0)).getValue().get(index);
		});
		// Define "count" procedure
		define(builtins, "count", (Procedure) (args) -> {
			if (args.size() != 1)
				throw new LispRuntimeException("Invalid number of argument(s) to count: " + args.size());
			if (!(args.get(0) instanceof ListAtom))
				throw new LispRuntimeException("Invalid arguments for count: " + args);
			ListAtom listAtom = (ListAtom) args.get(0);
			return new NumberAtom((double) listAtom.getValue().size());
		});
		// Define "=" predicate
		define(builtins, "=", (Procedure) (args) -> {
			boolean result = false;
			Object arg1 = args.get(0);
			Object arg2 = args.get(1);
			result = arg1.equals(arg2);
			// TODO: Implement.
			return new BooleanAtom(result);
		});
		// Define "not=" predicate
		define(builtins, "not=", (Procedure) (args) -> {
			// Defined in terms of "="
			Procedure equals = (Procedure)builtins.get("=");
			boolean result = ((BooleanAtom)equals.apply(args)).getValue();
			return new BooleanAtom(!result);
		});
		// Define "<" predicate
		define(builtins, "<", (Procedure) (args) -> {
			boolean result = false;
					
			// TODO: Implement.
			
			return new BooleanAtom(result);
		});
		// Define ">" predicate
		define(builtins, ">", (Procedure) (args) -> {
			boolean result = false;
							
			// TODO: Implement.
					
			return new BooleanAtom(result);
		});
		// Define "<=" predicate
		define(builtins, "<=", (Procedure) (args) -> {
			boolean result = false;
			
			// TODO: Implement.
							
			return new BooleanAtom(result);
		});
		// Define ">=" predicate
		define(builtins, ">=", (Procedure) (args) -> {
			boolean result = false;
					
			// TODO: Implement.
									
			return new BooleanAtom(result);
		});
		// Define "symbol?" predicate
		define(builtins, "symbol?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for symbol?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof SymbolAtom);
		});
		// Define "number?" predicate
		define(builtins, "number?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for number?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof NumberAtom);
		});
		// Define "boolean?" predicate
		define(builtins, "boolean?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for boolean?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof BooleanAtom);
		});
		// Define "string?" predicate
		define(builtins, "string?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for string?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof StringAtom);
		});
		// Define "char?" predicate
		define(builtins, "char?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid argument(s) for char?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof CharacterAtom);
		});
		// Define "list?" predicate
		define(builtins, "list?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new RuntimeException("Invalid argument(s) for list?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof ListAtom);
		});
		
		return builtins;
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
