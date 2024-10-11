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
			SymbolAtom symAtom = (SymbolAtom) expr;
			// nil symbol should always evaluate to itself
			if (symAtom.equals(SymbolAtom.nil))
				return symAtom;
			
			// Lookup symbol in the given environment
			String symbolStr = symAtom.getValue();
			Object symbolValue = (env != null) ? env.nullableLookup(symbolStr) : null;
			
			// If the symbol didn't resolve try the global environment
			if (symbolValue == null)
				return globalEnv.lookup(symbolStr);
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
			throw new LispRuntimeException("Empty list");
		}
		
		//Assume the first element is the operator
		Object operator = elements.get(0); // Unevaluated operator
		if ((operator instanceof SymbolAtom)) {
			String operatorStr = ((SymbolAtom) operator).getValue();
			// define
			if (operatorStr.equalsIgnoreCase("define")) {
				// TODO: Implement error checking.
				if (elements.size() < 3) {
					throw new LispRuntimeException("Incorrect define syntax");
				}
				
				Object nameOrFuncDecl = elements.get(1), value = SymbolAtom.nil;
				SymbolAtom name;
				if (nameOrFuncDecl instanceof SymbolAtom) {
					name = (SymbolAtom) nameOrFuncDecl;
					value = evaluate(elements.get(2), env); 
				} else if (nameOrFuncDecl instanceof ListAtom) {
					ListAtom funcDecl = (ListAtom) elements.get(1);
					List<Object> declList = funcDecl.getValue();
					name = (SymbolAtom) declList.get(0);
					// Collect the parameter symbols
					List<SymbolAtom> parameters = new ArrayList<>();
					List<Object> paramList = ((ListAtom) elements.get(1)).getValue();
					for (var param : declList.subList(1, declList.size())) {
						if (!(param instanceof SymbolAtom))
							throw new LispRuntimeException("Parameter names must be symbols.");

						parameters.add((SymbolAtom) param);
					}
					// Get the body atom
					ListAtom body = (ListAtom) elements.get(2);
					// Create and return the lambda expression
					value = new Lambda(parameters, body, env);
				} else {
					throw new LispRuntimeException("Incorrect args to define");
				}
				//System.out.println("Defining: " + name + " " + value);
				//System.out.println("Defining environment:\n" + env);
				env.define(name.getValue(), value);
				return value;
			}
			// Lambdas
			if (operatorStr.equalsIgnoreCase("λ") || operatorStr.equalsIgnoreCase("lambda")) {
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
			// If special form
			if (operatorStr.equalsIgnoreCase("if")) {
				if (elements.size() != 4) {
					throw new LispRuntimeException("Incorrect args to if");
				}
				Object expr = evaluate(elements.get(1), env);
				Object trueBody = elements.get(2);
				Object falseBody = elements.get(3);
				
				if (!(expr instanceof BooleanAtom)) {
					throw new LispRuntimeException("Incorrect args to if");
				}
				boolean evaluatedExpr = ((BooleanAtom) expr).getValue();
				Object bodyToRun = (evaluatedExpr) ? trueBody : falseBody;
				Object resultExpr = evaluate(bodyToRun, env);
				return resultExpr;
			}
			if (operatorStr.equalsIgnoreCase("begin")) {
				if (elements.isEmpty()) {
					return SymbolAtom.nil;
				}
				
				List<Object> exprs = elements.subList(1, elements.size());
				Object lastEvaluatedExpr = SymbolAtom.nil;
				for (var expr : exprs) {
					lastEvaluatedExpr = evaluate(expr, env);
				}
				return lastEvaluatedExpr;
			}
		}

		// Now evaluate the operator if it is not a special form 
		Object evaluatedOperator = evaluate(elements.get(0), env);
		//System.out.println("Elements: " + elements);
		//System.out.println("Evaluated operator: " + evaluatedOperator);
		//System.out.println(evaluatedOperator.getClass());
		
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
