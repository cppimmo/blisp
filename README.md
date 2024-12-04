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

Visit the project website for more information:
[https://cppimmo.github.io/blisp/](https://cppimmo.github.io/blisp/).

## Building

blisp uses the Maven build system.  Dependency information is available on the
[website](https://cppimmo.github.io/blisp/dependencies.html).

To compile and run blisp at the root of the
repository, use the following command:

```
mvn clean compile exec:java
```
<!--
To supply cmdline args:
mvn clean compile exec:java -Dexec.args="-i"
-->

To create an executable JAR for blisp (depencies included), use:

```
mvn package
```

The JAR file will be located in the *target/* directory.  Use the
*make-release.sh* script to package blisp in a form suitable for releases.

To generate Javadoc documentation, use:

```
mvn javadoc:javadoc
```

The generated documentation will be located in the *target/reports/apidocs*
directory.

<!--
Site building:
mvn clean site

Site deployment:
mvn clean site site:stage scm-publish:publish-scm
-->

## Using the Interpreter

This section present the shell commands for interacting with blisp interpreter
and running scripts.

To run blisp in REPL mode:
```
java -jar blisp.jar
```

To execute a blisp script:
```
java -jar blisp.jar scripts/test.blisp
```

blisp will run in REPL/interactive mode if no script file is specified.  If the
`-i | --interactive` flag is supplied along with a script file, then blisp will
open a REPL after the file is evaluated:

```
java -jar blisp.jar -i scripts/test.blisp
```

To see command line usage information for the blisp interpreter, use the `-h |
--help` flag.

## Using the Library

This section is a work in progress.

## License

The license for this project is the Eclipse Public License - v 2.0.  Check the
[LICENSE.txt](LICENSE.txt) file for more information.

