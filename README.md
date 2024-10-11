# blisp

![blisp](src/site/resources/images/blisp_logo_small.png "blisp logo.")

blisp is a simple, Scheme-like LISP programming language library and interpreter
written in Java. blisp attempts to implement the features that are fundamental
to all LISP dialects such as atoms, lists, special forms, and lambdas. The
language not adhere to any standard put forth by other LISP dialects, but it
does attempt to closely resemble Scheme in both appearance and behavior. The
language library implements a flexible API that can evaluate expressions or even
support embedded scripting. The interpreter program provides a means to execute
blisp programs or present the user with an interactive REPL.

## Building

Use the following command to compile and run blisp at the root of the
repository:

```
mvn clean compile exec:java
```

To generate Javadoc documentation, use the following command:

```
mvn javadoc:javadoc
```

The generated documentation will be located in the *target/reports/apidocs*
directory.

## Using the Language

```
mvn exec:java -Dexec.args="./src/main/resources/sample-script.blisp"
```

## License

The license for this project is Eclipse Public License - v 2.0.  Check the
[LICENSE.txt](LICENSE.txt) file for more information.

