package com.bhoffpauir.blisp.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.bhoffpauir.blisp.lib.exceptions.LispRuntimeException;

public class Evaluator {
	private Environment globalEnv;
	
	public Evaluator() {
		// Create global environment for this evaluator
		globalEnv = Environment.createGlobalEnv();
	}
	
	public Object evaluate(Object expr, final Environment env) {
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

	private Object evaluateList(ListAtom list, final Environment env) {
		List<Object> elements = list.getValue();
		if (elements.isEmpty()) {
			throw new RuntimeException("Empty list.");
		}
		
		//Assume the first element is the operator
		Object operator = elements.get(0); // Unevaluated operator
		if ((operator instanceof SymbolAtom)) {
			String operatorStr = ((SymbolAtom) operator).getValue();
			// define
			if (operatorStr.compareTo("define") == 0) {
				// TODO: Implement error checking.
				SymbolAtom name = (SymbolAtom) elements.get(1);
				Object value = elements.get(2);
				//System.out.println("Defining: " + name + " " + value);
				//System.out.println("Defining environment:\n" + env);
				env.define(name.getValue(), value);
				return value;
			}
			// Lambdas
			if (operatorStr.compareTo("lambda") == 0) {
				// Collect the parameter symbols
				//List<Object> parameters = elements.get(1);
				List<SymbolAtom> parameters = new ArrayList<>();
				List<Object> paramList = ((ListAtom) elements.get(1)).getValue();
				for (var param : paramList) {
					if (!(param instanceof SymbolAtom)) {
						throw new LispRuntimeException("Parameter names must be symbols.");
					}
					parameters.add((SymbolAtom) param);
				}
				// Get the body atom
				ListAtom body = (ListAtom) elements.get(2);
				// Create and return the lambda expression
				return new Lambda(parameters, body, env);
			}
		}
		
		// Now evaluate the operator if it is not a special form 
		Object evaluatedOperator = evaluate(elements.get(0), env);
		
		var args = elements.subList(1, elements.size());
		if (evaluatedOperator instanceof Lambda) {
			Lambda lambda = (Lambda) evaluatedOperator;
			return lambda.call(this, args);
		} else if (evaluatedOperator instanceof Procedure) {
			return evaluateFunction((Procedure) evaluatedOperator, args, env);
		}
		
		throw new RuntimeException("Unknown operator: " + evaluatedOperator);
	}
	
	private Object evaluateFunction(Procedure func, List<Object> args, Environment env) {
		// Evaluate all arguments before sending them to the function
		for (int i = 0; i < args.size(); i++) {
			args.set(i, evaluate(args.get(i), env));
		}
		return func.apply(args);
	}
	
	public void define(String symbolName, Object value) {
		globalEnv.define(symbolName, value);
	}
}
