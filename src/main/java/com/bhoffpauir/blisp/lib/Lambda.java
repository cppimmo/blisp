package com.bhoffpauir.blisp.lib;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Represents a Lambda function in blisp.
 * 
 * <p>This class encapsulates a lambda function, which includes the function's parameters, body, 
 * and the environment in which it was defined (known as the closure). Lambdas can be invoked 
 * with the {@link #call(Evaluator, List)} method, which creates a new environment for the function 
 * execution and evaluates the lambda body in this environment.</p>
 * 
 * <p>Lambdas are a fundamental part of blisp, enabling functional programming features like closures.</p>
 * 
 * @see SymbolAtom
 * @see ListAtom
 * @see Environment
 * @see Evaluator
 */
public class Lambda implements Procedure { // Atom<Procedure>
    private List<SymbolAtom> parameters;  // The parameters for this lambda
    private ListAtom body;                // The body of the lambda, which is a list of expressions
    private Procedure procBody;           // The body of the lambda, if defined internally
    private Environment parentEnv;        // The closure environment where the lambda was defined
    private Evaluator evaluator;          // The evaluator to use to evaluate this lambda
    
    private Lambda(Environment parentEnv, Evaluator evaluator) {
    	this.parameters = null;
    	this.body = null;
    	this.procBody = null;
    	this.parentEnv = parentEnv;
    	this.evaluator = evaluator; // Save the environment where the lambda was defined
    }
    
    /**
     * Constructs a lambda with the given parameters, body, and parent environment.
     * 
     * <p>The {@code parentEnv} is the environment in which this lambda was created, 
     * and it is used as the parent scope during lambda execution.</p>
     * 
     * @param parameters A list of {@code SymbolAtom} representing the parameter names for this lambda.
     * @param body The body of the lambda, represented by a {@link ListAtom} containing the expressions to execute.
     * @param parentEnv The environment where the lambda was defined, providing scope for closures.
     */
    public Lambda(List<SymbolAtom> parameters, ListAtom body, Environment parentEnv, Evaluator evaluator) {
        this(parentEnv, evaluator);
    	this.parameters = parameters;
        this.body = body;
    }
    
    public Lambda(Procedure proc, Environment parentEnv, Evaluator evaluator) {
    	this(parentEnv, evaluator);
    	this.procBody = proc;
    }
    
    /**
     * Calls this lambda with the provided arguments, using the evaluator.
     * 
     * <p>This method creates a new environment for the lambda invocation, binds the argument values 
     * to the parameter names, and evaluates the lambda body in this environment. The lambda inherits 
     * from the closure's environment where it was originally defined.</p>
     * 
     * @param args A list of argument values to pass to the lambda. The size of this list must match 
     *        the number of parameters.
     * @return The result of evaluating the lambda body.
     * @throws RuntimeException if the number of arguments does not match the number of parameters.
     */
    @Override
    public Object apply(List<Object> args) {
    	if (procBody == null) {
    		if (args.size() != parameters.size()) {
    			throw new RuntimeException("Argument count mistmatch. Expected " + parameters.size() + " but got " + args.size());
    		}
    	
    		// Create a new environment for the lambda execution
    		Environment lambdaEnv = new Environment(parentEnv); // Use the closure's environment as parent
    		lambdaEnv.define("recur", this);
    		for (int i = 0; i < parameters.size(); i++) {
    			lambdaEnv.define(parameters.get(i).getValue(), args.get(i));
    		}
    		// System.out.println(lambdaEnv);

    		// Evaluate the body of the lambda in the new environment
    		return evaluator.evaluate(body, lambdaEnv);
    	} else {
    		return procBody.apply(args);
    	}
    }
    
    /**
     * Retrieve the list of parameter symbols.
     * 
     * @return The parameter symbol list.
     */
    public List<SymbolAtom> getParameters() {
    	return parameters;
    }
    
    /**
     * Retrieve the body of the lambda.
     * 
     * @return The body of the lambda.
     */
    public ListAtom getBody() {
    	return (procBody == null) ? body : new ListAtom();
    }
    
	/**
     * Returns a string representation of this lambda, displaying its parameters and body.
     * 
     * <p>If extended printing is enabled, more detailed information can be included (e.g., environment),
     * but in its basic form, it lists the parameters and body of the lambda.</p>
     * 
     * @return A string describing the lambda function.
     */
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Params: " + parameters + '\n');
    	sb.append("Body: " + body);
    	//sb.append("Parent Env: " + parentEnv);
    	return sb.toString();
    }
}
