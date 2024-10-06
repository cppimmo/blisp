# blisp

Blisp is a simple Scheme-like LISP language library and interpreter written in Java.

## Building

Use the following command to compile and run blisp at the root of the repository:

```
mvn clean compile exec:java
```

To generate Javadoc documentation, use the following command:

```
mvn javadoc:javadoc
```

The generated documentation will be located in the *target/reports/apidocs* directory.

## Using the Language

```
mvn exec:java -Dexec.args="./src/main/resources/sample-script.blisp"
```

## License

The license for this project is Eclipse Public License - v 2.0.  Check the [LICENSE.txt](LICENSE.txt) file for more information.
