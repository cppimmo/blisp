package com.bhoffpauir.blisp.lib;

import java.util.List;
import java.util.function.Function;

/**
 * A functional interface representing a procedure or function in blisp.
 * The {@code Procedure} interface extends {@link java.util.function.Function} to take a list of arguments 
 * and return a result, both as {@code Object} types.
 * 
 * <p>Procedures in this context are higher-order functions that can be passed around and invoked dynamically 
 * with varying arguments.</p>
 * 
 * <p>The parameters of the procedure are represented by a {@link java.util.List} of {@code Object}, 
 * and the result is also an {@code Object} to allow flexibility in handling different data types.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 *     Procedure add = args -> (NumberAtom) args.get(0) + (NumberAtom) args.get(1);
 *     Object result = add.apply(List.of(NumberAtom(1), NumberAtom(2))); // Result is 3
 * </pre>
 * 
 * @see java.util.function.Function
 */
public interface Procedure extends Function<List<Object>, Object> {
}
