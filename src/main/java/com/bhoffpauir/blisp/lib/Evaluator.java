package com.bhoffpauir.blisp.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
		// TODO: Handle syntax quote evaluation.
		
		return switch (expr) {
			case SymbolAtom sym -> {
				// nil symbol should always evaluate to itself
				if (sym.equals(SymbolAtom.nil))
					yield sym;
				
				// Lookup symbol in the given environment
				String symbolStr = sym.getValue();
				Object symbolValue = (env != null) ? env.nullableLookup(symbolStr) : null;
				
				// If the symbol didn't resolve try the global environment
				if (symbolValue == null)
					yield globalEnv.lookup(symbolStr);
				else
					yield symbolValue;
			}
			case BooleanAtom bool -> bool;
			case NumberAtom num -> num;
			case CharacterAtom ch -> ch;
			case StringAtom str -> str;
			case ListAtom lst -> {
				//System.out.println("\nEvaluating list rn\n");
				yield evaluateList(lst, env);
			}
			default -> throw new LispRuntimeException("Unexpected expression type: " + expr);
		};
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
			Optional<Object> result = evaluateSpecialForm(operatorStr, elements.subList(1, elements.size()), env);
			if (result.isPresent()) {
				return result.get();
			}
		}

		// Now evaluate the operator if it is not a special form 
		Object evaluatedOperator = evaluate(elements.get(0), env);
		//System.out.println("Elements: " + elements);
		//System.out.println("Evaluated operator: " + evaluatedOperator);
		//System.out.println(evaluatedOperator.getClass());
		
		// Evaluate all arguments before sending them to a procedure/lambda
		List<Object> args = elements.subList(1, elements.size());
		List<Object> evaluatedArgs = new ArrayList<>(args.size());
		args.forEach(arg -> evaluatedArgs.add(evaluate(arg, env)));
		
		// Check if the evaluated operator is a lambda or a procedure
		return switch (evaluatedOperator) {
			case Lambda lambda -> lambda.call(this, evaluatedArgs);
			case Procedure proc -> evaluateProcedure(proc, evaluatedArgs, env);
			default -> throw new LispRuntimeException("Unknown operator: " + evaluatedOperator);
		};
	}
	
	private Optional<Object> evaluateSpecialForm(final String operator, List<Object> args, final Environment env) {
		String op = operator.toLowerCase();
		// Process special forms
		switch (op) {
		case "define": {
			// TODO: Implement error checking.
			if (args.size() < 2) {
				throw new LispRuntimeException("Incorrect define syntax");
			}
			
			Object nameOrFuncDecl = args.get(0), value = SymbolAtom.nil;
			SymbolAtom name;
			if (nameOrFuncDecl instanceof SymbolAtom) {
				name = (SymbolAtom) nameOrFuncDecl;
				value = evaluate(args.get(1), env); 
			} else if (nameOrFuncDecl instanceof ListAtom) {
				ListAtom funcDecl = (ListAtom) args.get(0);
				List<Object> declList = funcDecl.getValue();
				name = (SymbolAtom) declList.get(0);
				// Collect the parameter symbols
				List<SymbolAtom> parameters = new ArrayList<>();
				List<Object> paramList = ((ListAtom) args.get(0)).getValue();
				for (var param : declList.subList(1, declList.size())) {
					if (!(param instanceof SymbolAtom))
						throw new LispRuntimeException("Parameter names must be symbols.");

					parameters.add((SymbolAtom) param);
				}
				// Get the body atom
				ListAtom body = (ListAtom) args.get(1);
				// Create and return the lambda expression
				value = new Lambda(parameters, body, env);
			} else {
				throw new LispRuntimeException("Incorrect args to define");
			}
			//System.out.println("Defining: " + name + " " + value);
			//System.out.println("Defining environment:\n" + env);
			env.define(name.getValue(), value);
			return Optional.of(value);
		}
		case "λ":
		case "lambda": {
			// Collect the parameter symbols
			//List<Object> parameters = args.get(0);
			List<SymbolAtom> parameters = new ArrayList<>();
			List<Object> paramList = ((ListAtom) args.get(0)).getValue();
			for (var param : paramList) {
				if (!(param instanceof SymbolAtom)) {
					throw new LispRuntimeException("Parameter names must be symbols.");
				}
				parameters.add((SymbolAtom) param);
			}
			// Get the body atom
			ListAtom body = (ListAtom) args.get(1);
			// Create and return the lambda expression
			return Optional.of(new Lambda(parameters, body, env));
		}
		case "if": {
			if (args.size() != 3) {
				throw new LispRuntimeException("Incorrect args to if");
			}
			Object expr = evaluate(args.get(0), env);
			Object trueBody = args.get(1);
			Object falseBody = args.get(2);
			
			if (!(expr instanceof BooleanAtom)) {
				throw new LispRuntimeException("Incorrect args to if");
			}
			boolean evaluatedExpr = ((BooleanAtom) expr).getValue();
			Object bodyToRun = (evaluatedExpr) ? trueBody : falseBody;
			Object resultExpr = evaluate(bodyToRun, env);
			return Optional.of(resultExpr);	
		}
		case "begin": {
			if (args.isEmpty()) {
				return Optional.of(SymbolAtom.nil);
			}
			
			List<Object> exprs = args;
			Object lastEvaluatedExpr = SymbolAtom.nil;
			for (var expr : exprs) {
				lastEvaluatedExpr = evaluate(expr, env);
			}
			return Optional.of(lastEvaluatedExpr);
		}
		case "quote": {
			if (args.size() != 1) {
				throw new LispRuntimeException("Incorrect args to quote");
			}
			// Return unevaluated lists or atoms
			return Optional.of(args.get(0));
		}
		default:
			return Optional.empty();
		}
	}
	
	private Object evaluateProcedure(Procedure func, List<Object> args, Environment env) {
		return func.apply(args);
	}
	
	public void define(String symbolName, Object value) {
		globalEnv.define(symbolName, value);
	}
}
