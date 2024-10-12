package com.bhoffpauir.blisp.interp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.bhoffpauir.blisp.lib.Atom;
import com.bhoffpauir.blisp.lib.Environment;
import com.bhoffpauir.blisp.lib.Evaluator;
import com.bhoffpauir.blisp.lib.NumberAtom;
import com.bhoffpauir.blisp.lib.Parser;
import com.bhoffpauir.blisp.lib.Tokenizer;
import com.bhoffpauir.blisp.lib.exceptions.LispRuntimeException;
/**
 * Interpreter implementation for the blisp language supporting both interactive REPL and
 * script file execution modes.
 */
public class Main {
	private static final String PRGNAM = "blisp";
	private Options options;
	private boolean showTokens = false;
	private boolean showParser = false;
	private boolean showStackTrace = false;
	private static int EXIT_SUCCESS = 0, EXIT_FAILURE = 1;
	private File scriptFile = null;
	private InterpreterMode mode = InterpreterMode.SCRIPT; // Default is SCRIPT
	private List<String> previousCommands = new ArrayList<>();
	private boolean running = true;
	
    public static void main(String[] args) throws IOException {
        // Start the interpreter
        Main interpreter = new Main();
        interpreter.initialize(args);
        System.exit(interpreter.mainLoop());
    }
    
    private void initialize(final String[] args) {
    	options = new Options();
    	parseArguments(args);
    }
    
    private void parseArguments(final String[] args) {
    	// Arg names should be UPPER_SNAKE_CASE
    	options.addOption("h", "help", false, "Show this usage message and exit.");
    	options.addOption("v", "version", false, "Show version information and exit.");
    	options.addOption("i", "interactive", false, "Run in REPL mode.");
    	options.addOption("t", "show-tokens", false, "Show each token from the input.");
    	options.addOption("p", "show-parser", false, "Show each expression parsed from the input.");
    	options.addOption("st", "stack-trace", false, "Show Java exception stack trace output.");
    	
    	CommandLineParser parser = new DefaultParser();
    	try {
    		CommandLine cmd = parser.parse(options, args);
    		//List<String> argList = cmd.getArgList();
    		
    		if (cmd.hasOption('h')) {
    			usage();
    			System.exit(EXIT_SUCCESS);
    		}
    		if (cmd.hasOption('v')) {
    			// TODO: Display version information.
    			System.exit(EXIT_SUCCESS);
    		}
    		// Process other arguments
    		if (cmd.hasOption('i')) {
    			mode = InterpreterMode.REPL;
    		}
    		if (cmd.hasOption('t')) {
    			showTokens = true;
    		}
    		if (cmd.hasOption('p')) {
    			showParser = true;
    		}
    		if (cmd.hasOption("st")) {
    			showStackTrace = true;
    		}

    		for (int i = 0; i < args.length; i++) {
    			var arg = args[i];
    			// Args starting with dash
    			if (arg.startsWith("--") || arg.startsWith("-")) {
    				if (cmd.hasOption(arg) && cmd.getOptionValue(arg) != null) {
    					i++;
    				}
    				continue;
    			}
    			
    			scriptFile = new File(arg);
    			if (!scriptFile.exists()) {
    				throw new RuntimeException("Script file does not exist.");
    				//break;
    			}
    			// Change the mode of the interpreter
    			if (mode == InterpreterMode.REPL)
    				mode = InterpreterMode.SCRIPT_AND_REPL;
    			else
    				mode = InterpreterMode.SCRIPT;
    		}
    		// No script file argument provided, fallback to REPL mode
    		if (scriptFile == null) {
    			mode = InterpreterMode.REPL;
    		}
    	} catch (ParseException ex) {
    		System.err.println("Error parsing arguments: " + ex.getMessage());
    		usage();
    	}
    }
    
    private int mainLoop() throws IOException {
    	// Set the appropriate code source
    	InputStream in = System.in;
    	if (scriptFile != null) {
    		in = new FileInputStream(scriptFile);
    	}
    	InputStreamReader input = new InputStreamReader(in);
    	BufferedReader reader = new BufferedReader(input);
    	
    	// Show initial messages at REPL
    	if (mode == InterpreterMode.REPL) {
    		String osName = System.getProperty("os.name");
    		String osArch = System.getProperty("os.arch");
    		String osVersion = System.getProperty("os.version");
    		System.out.printf("%s v%s on %s (%s) version %s\n", PRGNAM, getVersion(), osName,
    				osArch, osVersion);
    		System.out.println("Type \"help\" or \"license\" for more information.");
    		System.out.println("Press Ctrl+D or type \"(exit)\" to exit this REPL.");
    	}
    	
    	//Atom.toggleExtenedPrint(); // For testing
    	
    	// Create the environment for the session
    	Environment env = new Environment();
    	
    	do {
    		try {
    			// Prompt (optionally) & read input
    			if (mode == InterpreterMode.REPL) {
    				System.out.print(">>> ");
    			}
    		
    			String line = reader.readLine();
    			// Exit on EOF, Ctrl+D, or (quit) (implemented as a function)
    			if (line == null) {
    				// The script file is done executing now switch to REPL mode
    				if (mode == InterpreterMode.SCRIPT_AND_REPL) {
    					mode = InterpreterMode.REPL;
    					// Reconstruct the input stream and buffered reader
    					input = new InputStreamReader(System.in);
    			    	reader = new BufferedReader(input);
    					continue;
    				}
    				running = false;
    				break;
    			}
    			// Tokenize the input
    			List<String> tokens = new Tokenizer(line).tokenize();
    			if (tokens.isEmpty()) continue;
    			// Display the tokenization stage output
    			if (showTokens) {
    				System.out.printf("  Tokens: %s\n", tokens);
    			}
    			previousCommands.add(line.trim());
    			
    			// Parse the tokenized input
    			Parser parser = new Parser(tokens);
    			Object parsedExpr = parser.parse();
    			// Display the parsing stage output
    			if (showParser) {
    				System.out.printf("  Parsed Expr(s): %s\n", parsedExpr);
    			}
    			
    			// Evaluate using the global environment	
    			Evaluator evaluator = new Evaluator();
    			Object result = evaluator.evaluate(parsedExpr, env);
    			if (mode == InterpreterMode.REPL) {
        			// Output the result of evaluating the given expression
    				System.out.println(result);
    			}
    		} catch (LispRuntimeException ex) {
        		// Process blisp runtime exceptions
        		System.err.printf("Error:\n  %s\n", ex.getMessage());
    			if (showStackTrace)
        			ex.printStackTrace();
        	}
    	} while (running);
    	return EXIT_SUCCESS;
    }
    /**
     * 
     */
    private void usage() {
    	//HelpFormatter formatter = new HelpFormatter();
    	//formatter.printUsage(new PrintWriter(System.out), 80, PRGNAM, options);
    	System.out.printf("%s v%s:\n", PRGNAM, getVersion());
    	System.out.printf("Usage: [OPTION...] [SCRIPT_FILE]\n");
    	System.out.println();
    	System.out.println("Options:");
    	// Display each argument
    	options.getOptions().forEach(opt -> {
    		String shortOpt = opt.getOpt();
    		String longOpt = opt.getLongOpt();
    		String desc = opt.getDescription();
    		// Create argument short & long name string
    		StringBuilder argSb = new StringBuilder();
    		argSb.append(shortOpt);
    		argSb.append((longOpt == null) ? "" : ", --" + longOpt);
    		argSb.append(opt.hasArgName() ? opt.getArgName() : "");
    		
    		System.out.printf("  -%-20s : %s\n", argSb.toString(), desc);
    	});
    }
    
    private String getVersion() {
    	/*final Properties properties = new Properties();
    	try {
			properties.load(getClass().getResourceAsStream("project.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return properties.getProperty("blisp.version");*/
    	return "1.0";
    }
}
