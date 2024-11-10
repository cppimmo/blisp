package com.bhoffpauir.blisp.lib;

import com.bhoffpauir.blisp.lib.exceptions.LispRuntimeException;
import com.bhoffpauir.blisp.lib.exceptions.RebindKeywordSymbolException;
import com.bhoffpauir.blisp.lib.exceptions.UnboundSymbolException;

import java.util.ArrayList;
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
     * 
     * @param symbol
     * @param proc
     */
    public void define(String symbol, Procedure proc) {
    	define(symbol, (Object)new Lambda(proc, this, new Evaluator(this)));
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
    public static void define(Map<String, Object> bindings, String symbol, Object value) {
    	if (keywords.contains(symbol)) {
    		throw new RebindKeywordSymbolException(symbol);
    	}
    	
    	bindings.put(symbol.toLowerCase(), value);
    }
    
    /**
     * Define a new symbol binding.
     */
    public static void define(Map<String, Object> bindings, SymbolAtom symbol, Object value) {
    	define(bindings, symbol.toString(), value);
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
    	
    	// TODO: Should traverse through the parent environment and child environments should overshadow parent bindings. 
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
    	Evaluator evaluator = new Evaluator(env);
    	// env.defineBindings();
    	Map<String, Object> builtins = env.defineBuiltIns();
    	builtins.forEach((key, value) -> {
    		env.bindings.put(key, new Lambda((Procedure) value, env, evaluator));
    	});
    	/*builtins.forEach((key, value) -> {
    		// Merge with the new binding
    		env.bindings.merge(key, value, (v1, v2) -> {
    			if (!(v2 instanceof Procedure))
    				throw new IllegalArgumentException("Builtin definitions must be procedures");
    			
    			return new Lambda((Procedure) v2, env, new Evaluator());
    		});
    	});*/
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
     * 
     * @return The builtin procedure definitions.
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
    			throw new LispRuntimeException("First argument to sprintf is a format string");
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
    			
    			subArgsArray[i] = switch (atom) {
    				case StringAtom str -> str.getValue();
    				case CharacterAtom ch -> ch.getValue();
    				case NumberAtom num -> num.getValue();
					default -> atom;
    			};
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
    	// Define "inc" procedure
		define(builtins, "inc", (Procedure) (args) -> {
			if (args.size() != 1)
				throw new LispRuntimeException("Invalid number of arguments for inc: " + args.size());
			
			if (!(args.get(0) instanceof NumberAtom))
				throw new LispRuntimeException("Invalid argument(s) for inc: " + args);
				
			var newNum = ((NumberAtom) args.get(0)).getValue().intValue() + 1;
			return new NumberAtom(newNum);
		});
		// Define "dec" procedure
		define(builtins, "dec", (Procedure) (args) -> {
			if (args.size() != 1)
				throw new LispRuntimeException("Invalid number of arguments for dec: " + args.size());
			
			if (!(args.get(0) instanceof NumberAtom))
				throw new LispRuntimeException("Invalid argument(s) for dec: " + args);
				
			var newNum = ((NumberAtom) args.get(0)).getValue().intValue() - 1;
			return new NumberAtom(newNum);
		});
    	// Define "+" procedure
		define(builtins, "+", (Procedure) (args) -> {
			double sum = 0.0;
			for (var arg : args) {
				if (!(arg instanceof NumberAtom)) {
					throw new LispRuntimeException("Invalid argument(s) for +: " + arg);
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
			return args.isEmpty() ? new ListAtom() : new ListAtom(args);
		});
		// Define "first" procedure
		define(builtins, "first", (Procedure) (args) -> {
			if (args.size() != 1)
				throw new LispRuntimeException("Invalid number of argument(s) to first: " + args.size());
			if (!(args.get(0) instanceof ListAtom))
				throw new LispRuntimeException("Invalid arguments for first: " + args);
			
			return ((ListAtom) args.get(0)).getValue().get(0);
		});
		// Define "rest" procedure
		define(builtins, "rest", (Procedure) (args) -> {
			if (args.size() != 1)
				throw new LispRuntimeException("Invalid number of argument(s) to rest: " + args.size());
			if (!(args.get(0) instanceof ListAtom))
				throw new LispRuntimeException("Invalid arguments for rest: " + args);
			
			List<Object> lst = ((ListAtom) args.get(0)).getValue(); 
			return lst.subList(1, lst.size());
		});
		// Define "last" procedure
		define(builtins, "last", (Procedure) (args) -> {
			if (args.size() != 1)
				throw new LispRuntimeException("Invalid number of argument(s) to last: " + args.size());
			if (!(args.get(0) instanceof ListAtom))
				throw new LispRuntimeException("Invalid arguments for last: " + args);
				
			List<Object> lst = ((ListAtom) args.get(0)).getValue(); 
			return lst.get(lst.size() - 1);
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
		// Define "map" procedure
		define(builtins, "map", (Procedure) (args) -> {
			if (args.size() != 2)
				throw new LispRuntimeException("Invalid number of argument(s) to map:" + args.size());
			
			if (!(args.get(0) instanceof Lambda) || !(args.get(1) instanceof ListAtom))
				throw new LispRuntimeException("Invalid arguments for map: " + args);

			Lambda lambda = (Lambda) args.get(0); // Transformation function
			List<Object> inputList = ((ListAtom) args.get(1)).getValue(); // Input list
			List<Object> outputList = new ArrayList<>(inputList.size()); // Output list
			
			// Transform the elements of the input list into the output list by applying the lambda
			for (int i = 0; i < inputList.size(); i++) {
				// TODO: Should a new evaluator be created?
				outputList.add(lambda.apply(List.of(inputList.get(i))));
			}
			// Create the new ListAtom from the newly transformed list
			return new ListAtom(outputList);
		});
		// Define "filter" procedure
		define(builtins, "filter", (Procedure) (args) -> {
			if (args.size() != 2)
				throw new LispRuntimeException("Invalid number of argument(s) to filter:" + args.size());

			if (!(args.get(0) instanceof Lambda) || !(args.get(1) instanceof ListAtom))
				throw new LispRuntimeException("Invalid arguments for filter: " + args);
			
			Lambda lambda = (Lambda) args.get(0); // Transformation function
			List<Object> inputList = ((ListAtom) args.get(1)).getValue(); // Input list
			List<Object> outputList = new ArrayList<>(inputList.size()); // Output list
				
			for (int i = 0; i < inputList.size(); i++) {
				// TODO: Should a new evaluator be created?
				Object result = lambda.apply(List.of(inputList.get(i)));
				// Check return type of predicate
				if (!(result instanceof BooleanAtom))
					throw new LispRuntimeException("Invalid return type for filter predicate: " + result);
				
				// Include the element in the list if the predicate is satisfied
				BooleanAtom booleanResult = (BooleanAtom) result;
				if (booleanResult.getValue())
					outputList.add(inputList.get(i));
			}
			// Create the new ListAtom from the newly transformed list
			return new ListAtom(outputList);
		});
		// Define "reduce" procedure
		define(builtins, "reduce", (Procedure) (args) -> {
			if (args.size() != 3)
				throw new LispRuntimeException("Invalid number of argument(s) to map:" + args.size());

			if (!(args.get(0) instanceof Lambda) || !(args.get(2) instanceof ListAtom))
				throw new LispRuntimeException("Invalid arguments for map: " + args);
			
			Lambda lambda = (Lambda) args.get(0); // Transformation function
			Object initial = args.get(1);
			List<Object> inputList = ((ListAtom) args.get(2)).getValue(); // Input list
			List<Object> outputList = new ArrayList<>(inputList.size()); // Output list
			
			// Transform the elements of the input list into the output list by applying the lambda
			for (int i = 0; i < inputList.size(); i++) {
				// TODO: Should a new evaluator be created?
				initial = lambda.apply(List.of(initial, inputList.get(i)));
				outputList.add(initial);
			}
			// Create the new ListAtom from the newly transformed list
			return initial;
		});
		define(builtins, "range", (Procedure) (args) -> {
			if (args.size() < 1 || args.size() > 3)
				throw new LispRuntimeException("Invalid number of argument(s) to range: " + args.size());
			
			int start = 0;
			int end = 0;
			int step = 1;
			if (args.get(0) instanceof NumberAtom)
				end = ((NumberAtom) args.get(0)).getValue().intValue();
			else
				throw new LispRuntimeException("Invalid arguments to range: " + args);
			
			if (args.size() > 1) {
				if (args.get(1) instanceof NumberAtom) {
					start = ((NumberAtom) args.get(0)).getValue().intValue();
					end = ((NumberAtom) args.get(1)).getValue().intValue();
				}
				else
					throw new LispRuntimeException("Invalid arguments to range: " + args); 
			}
			
			if (args.size() > 2) {
				if (args.get(2) instanceof NumberAtom) {
					start = ((NumberAtom) args.get(0)).getValue().intValue();
					end = ((NumberAtom) args.get(1)).getValue().intValue();
					step = ((NumberAtom) args.get(2)).getValue().intValue();
				}
				else
					throw new LispRuntimeException("Invalid arguments to range: " + args); 
			}
			
			List<Object> results = new ArrayList<>();
			for (int num = start; num < end; num += step) {
				results.add(new NumberAtom(num));
			}
			return new ListAtom(results);
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
			if (args.size() < 2)
				throw new LispRuntimeException("Invalid number of argument(s) to <: " + args.size());
			boolean result = true;
			
			var lastArg = args.get(0);
			for (var arg : args.subList(1, args.size())) {
				if (!(arg instanceof NumberAtom))
					throw new LispRuntimeException("Invalid argument to <: " + args);
				
				if (((NumberAtom) lastArg).compareTo((NumberAtom) arg) >= 0) {
					result = false;
					break;
				}
				lastArg = arg;
			}
			return new BooleanAtom(result);
		});
		// Define ">" predicate
		define(builtins, ">", (Procedure) (args) -> {
			if (args.size() < 2)
				throw new LispRuntimeException("Invalid number of argument(s) to >: " + args.size());
			boolean result = true;
			
			var lastArg = args.get(0);
			for (var arg : args.subList(1, args.size())) {
				if (!(arg instanceof NumberAtom))
					throw new LispRuntimeException("Invalid argument to >: " + args);
				
				if (((NumberAtom) lastArg).compareTo((NumberAtom) arg) <= 0) {
					result = false;
					break;
				}
				lastArg = arg;
			}
			return new BooleanAtom(result);
		});
		// Define "<=" predicate
		define(builtins, "<=", (Procedure) (args) -> {
			if (args.size() < 2)
				throw new LispRuntimeException("Invalid number of argument(s) to <=: " + args.size());
			boolean result = true;
			
			var lastArg = args.get(0);
			for (var arg : args.subList(1, args.size())) {
				if (!(arg instanceof NumberAtom))
					throw new LispRuntimeException("Invalid argument to <=: " + args);
				
				if (((NumberAtom) lastArg).compareTo((NumberAtom) arg) > 0) {
					result = false;
					break;
				}
				lastArg = arg;
			}
			return new BooleanAtom(result);
		});
		// Define ">=" predicate
		define(builtins, ">=", (Procedure) (args) -> {
			if (args.size() < 2)
				throw new LispRuntimeException("Invalid number of argument(s) to >=: " + args.size());
			boolean result = true;
			
			var lastArg = args.get(0);
			for (var arg : args.subList(1, args.size())) {
				if (!(arg instanceof NumberAtom))
					throw new LispRuntimeException("Invalid argument to >=: " + args);
				
				if (((NumberAtom) lastArg).compareTo((NumberAtom) arg) < 0) {
					result = false;
					break;
				}
				lastArg = arg;
			}
			return new BooleanAtom(result);
		});
		// Define "symbol?" predicate
		define(builtins, "symbol?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid argument(s) for symbol?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof SymbolAtom);
		});
		// Define "number?" predicate
		define(builtins, "number?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid argument(s) for number?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof NumberAtom);
		});
		// Define "boolean?" predicate
		define(builtins, "boolean?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid argument(s) for boolean?");
			}
			Object arg1 = args.get(0);
			return new BooleanAtom(arg1 instanceof BooleanAtom);
		});
		// Define "string?" predicate
		define(builtins, "string?", (Procedure) (args) -> {
			if (args.isEmpty()) {
				throw new LispRuntimeException("Invalid argument(s) for string?");
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
				throw new LispRuntimeException("Invalid argument(s) for list?");
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
