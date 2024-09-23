package com.bhoffpauir.blisp.lib;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class Evaluator {
	private Environment globalEnv;
	
	public Evaluator() {
		// Create global environment for this evaluator
		globalEnv = Environment.createGlobalEnv();
	}
	
	public Object evaluate(Object expr, Environment env) {
		if (expr instanceof SymbolAtom) {
			// Lookup symbol in the given environment
			String symbolStr = ((SymbolAtom) expr).getValue();
			Object symbolValue = (env != null) ? env.nullableLookup(symbolStr) : null;
			
			// If the symbol didn't resolve try the global environment
			if (symbolValue == null)
				return globalEnv.lookup(((SymbolAtom) expr).getValue());
			else
				return symbolValue;
		} else if (expr instanceof BooleanAtom) {
			// Booleans evaluate to themselves
			return ((BooleanAtom) expr);
		} else if (expr instanceof NumberAtom) {
			// Numbers evaluate to themselves
			return ((NumberAtom) expr);
		} else if (expr instanceof StringAtom) {
			// Strings evaluate to themselves
			return ((StringAtom) expr);
		} else if (expr instanceof ListAtom) {
			// Lists need to be evaluated as expressions
			return evaluateList((ListAtom) expr, env);
		}
		throw new RuntimeException("Unknown expression type: " + expr);
	}

	private Object evaluateList(ListAtom list, Environment env) {
		List<Object> elements = list.getValue();
		if (elements.isEmpty()) {
			throw new RuntimeException("Empty list.");
		}
		
		// Assume the first element is the operator
		Object operator = evaluate(elements.get(0), env);
		
		if (operator instanceof Function && !(operator instanceof VariadicFunction)) {
			// Handle unary functions (one argument)
			if (elements.size() - 1 != 1) {
	            throw new RuntimeException("Invalid number of arguments for unary function: " + operator);
	        }
	        return evaluateFunction((Function<Object, Object>) operator, elements.get(1), env);
		} else if (operator instanceof BiFunction) {
			// Handle binary functions (two arguments)
			if (elements.size() - 1 != 2) {
				throw new RuntimeException("Invalid number of arguments for function: " + operator);
			}
			return evaluateFunction((BiFunction<Object, Object, Object>) operator, elements.get(1), elements.get(2), env);
		} else if (operator instanceof VariadicFunction) {
		    // Handle variadic functions (any number of arguments)
	    	return evaluateFunction((VariadicFunction) operator, elements.subList(1, elements.size()), env);
	    }
	    
		throw new RuntimeException("Unknown operator: " + operator);
	}
	
	private Object evaluateLambda(Lambda lambda, List<Object> args) {
	    return lambda.call(this, args);
	}
	
	// TODO: Need evaluator for functions with zero arguments.
	
	private Object evaluateFunction(Function<Object, Object> func, Object arg1, Environment env) {
		Object evaluatedArg1 = evaluate(arg1, env);
		return func.apply(evaluatedArg1);
	}
	
	private Object evaluateFunction(BiFunction<Object, Object, Object> func, Object arg1, Object arg2, Environment env) {
		Object evaluatedArg1 = evaluate(arg1, env);
		Object evaluatedArg2 = evaluate(arg2, env);
		return func.apply(evaluatedArg1, evaluatedArg2);
	}
	
	private Object evaluateFunction(VariadicFunction func, List<Object> args, Environment env) {
		// Evaluate each argument in the list
		List<Object> evaluatedArgs = args.stream()
										 .map(arg -> evaluate(arg, env))
										 .toList();
		return func.apply(evaluatedArgs);
	}
	
	public void define(String symbolName, Object value) {
		globalEnv.define(symbolName, value);
	}
}
