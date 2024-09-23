package com.bhoffpauir.blisp.lib;

import java.util.List;

public class Lambda {
    private List<SymbolAtom> parameters;
    private ListAtom body;
    private Environment parentEnv;

    public Lambda(List<SymbolAtom> parameters, ListAtom body, Environment parentEnv) {
        this.parameters = parameters;
        this.body = body;
        this.parentEnv = parentEnv; // Save the environment where the lambda was defined
    }

    public Object call(Evaluator evaluator, List<Object> args) {
        if (args.size() != parameters.size()) {
        	throw new RuntimeException("Argument count mistmatch. Expected " + parameters.size() + " but got " + args.size());
        }
    	
    	// Create a new environment for the lambda execution
        Environment lambdaEnv = new Environment(parentEnv); // Use the closure's environment as parent
        
        for (int i = 0; i < parameters.size(); i++) {
            lambdaEnv.define(parameters.get(i).getValue(), args.get(i));
        }
        // Evaluate the body of the lambda in the new environment
        return evaluator.evaluate(body, lambdaEnv);
    }
}
