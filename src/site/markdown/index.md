![blisp](images/blisp_logo_small.png "blisp logo.")

```
(define (hello x)
  (println "Hello, " x))
  
(hello "blisp")
```

blisp is a simple, Scheme-like LISP programming language and interpreter written
in Java. It implements the core features commonly found in LISP dialects, such
as atoms, lists, symbols, and lambdas. The language offers a flexible API that
supports both interactive REPL (Read-Eval-Print Loop) sessions and script
execution.

blisp draws inspiration from Scheme, and it also introduces its own unique
design features, including an embeddable scripting API. This makes it a useful
tool for building embedded LISP functionality into Java applications. The core
features of the language include:

- Atoms, which represent immutable scalar values.
- Symbols, which are used for binding values in both global and lexical environments.
- Lists, the fundamental data structure in LISP dialects.
- Lambdas, for anonymous functions and code reuse.
- Special forms and macros, for custom evaluation behavior.

This website provides an overview of blisp's design, core features, and examples
of its usage. The primary source of information about blisp is the [“blisp - A
Simple LISP Interpreter”](docs/report.pdf) paper, which can be found on this
site.

**Useful Links**

- [GitHub Repository](https://github.com/cppimmo/blisp)
- [GitHub Releases](https://github.com/cppimmo/blisp/releases)
- [“blisp - A Simple LISP Interpreter” Paper](docs/report.pdf)
- [Javadocs](apidocs/index.html)

