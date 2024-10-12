package com.bhoffpauir.blisp.lib;

import java.util.List;
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
public class Lambda extends Atom<Procedure> {
    private List<SymbolAtom> parameters;  // The parameters for this lambda
    private ListAtom body;                // The body of the lambda, which is a list of expressions
    private Environment parentEnv;        // The closure environment where the lambda was defined

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
    public Lambda(List<SymbolAtom> parameters, ListAtom body, Environment parentEnv) {
        // TODO: Use the procedure value of the atom correctly.
    	super(null);
        // Setup a lambda based on this 
    	//super((Procedure));
    	this.parameters = parameters;
        this.body = body;
        this.parentEnv = parentEnv; // Save the environment where the lambda was defined
    }

    /**
     * Calls this lambda with the provided arguments, using the given evaluator.
     * 
     * <p>This method creates a new environment for the lambda invocation, binds the argument values 
     * to the parameter names, and evaluates the lambda body in this environment. The lambda inherits 
     * from the closure's environment where it was originally defined.</p>
     * 
     * @param evaluator The evaluator used to evaluate the body of the lambda.
     * @param args A list of argument values to pass to the lambda. The size of this list must match 
     *        the number of parameters.
     * @return The result of evaluating the lambda body.
     * @throws RuntimeException if the number of arguments does not match the number of parameters.
     */
    public Object call(Evaluator evaluator, List<Object> args) {
        if (args.size() != parameters.size()) {
        	throw new RuntimeException("Argument count mistmatch. Expected " + parameters.size() + " but got " + args.size());
        }
    	
    	// Create a new environment for the lambda execution
        Environment lambdaEnv = new Environment(parentEnv); // Use the closure's environment as parent
        lambdaEnv.define("recur", this);
        for (int i = 0; i < parameters.size(); i++) {
            lambdaEnv.define(parameters.get(i).getValue(), args.get(i));
        }
        //System.out.println(lambdaEnv);

        // Evaluate the body of the lambda in the new environment
        return evaluator.evaluate(body, lambdaEnv);
    }
    
    /**
     * Throws an {@code UnsupportedOperationException} as regular expressions do not apply to lambdas.
     * 
     * @return This method does not return anything as it throws an exception.
     * @throws UnsupportedOperationException when called.
     */
	@Override
	public Pattern getRegexPattern() {
		throw new UnsupportedOperationException();
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
    	sb.append("Body: " + body + '\n');
    	//sb.append("Parent Env: " + )
    	return sb.toString();
    }
}
