package com.bhoffpauir.blisp.lib;

import java.util.List;
import java.util.function.Function;
/**
 * Functional interface for procedures/functions. Parameters are a list of {@code Object}
 * and the return type is an {@code Object}.
 */
public interface Procedure extends Function<List<Object>, Object> {
}
