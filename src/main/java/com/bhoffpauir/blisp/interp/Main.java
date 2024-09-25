package com.bhoffpauir.blisp.interp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
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
	private static int EXIT_SUCCESS = 0, EXIT_FAILURE = 1;
	private boolean running = true;
	private File scriptFile = null;
	private InterpreterMode mode = InterpreterMode.REPL; // Default is REPL
	private List<String> previousCommands = new ArrayList<>();
	
    public static void main(String[] args) throws IOException {
        // Start the interpreter
        Main interpreter = new Main();
        interpreter.initialize(args);
        System.exit(interpreter.mainLoop());
    }
    
    private void initialize(String[] args) {
    	parseArguments(args);
    }
    
    private void parseArguments(String[] args) {
    	Options options = new Options();
    	options.addOption("h", "help", false, "Show this usage message.");
    	
    	CommandLineParser parser = new DefaultParser();
    	try {
    		CommandLine cmd = parser.parse(options, args);
    		
    		if (cmd.hasOption("h")) {
    			usage();
    		} else {
    			// Process other arguments
    			
    			if (args.length > 0) {
    				scriptFile = new File(args[args.length - 1]);
    				// Change the mode of the interpreter
    				mode = InterpreterMode.SCRIPT;
    			}
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
    		System.out.println("Press Ctrl+D or type \"(quit)\" to exit this REPL.");
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
    				running = false;
    				break;
    			}
    			// Tokenize the input
    			List<String> tokens = new Tokenizer(line).tokenize();
    			//System.out.println(tokens);
    			
    			if (tokens.isEmpty()) continue;
    			previousCommands.add(line.trim());
    			// Parse the tokenized input
    			Parser parser = new Parser(tokens);
    			Object parsedExpr = parser.parse();
    			//System.out.println(parsedExpr);
    			
    			// Evaluate using the global environment	
    			Evaluator evaluator = new Evaluator();
    			Object result = evaluator.evaluate(parsedExpr, env);
    			if (mode == InterpreterMode.REPL) {
        			// Output the result of evaluating the given expression
    				System.out.println(result);
    			}
    		} catch (LispRuntimeException ex) {
        		// Process blisp runtime exceptions
        		ex.printStackTrace();
        	}
    	} while (running);
    	return EXIT_SUCCESS;
    }
    /**
     * 
     */
    private void usage() {
    	System.out.printf("%s v%s:\n", PRGNAM, getVersion());
    	System.out.printf("\tUsage: [OPTION...] [SCRIPT-FILE]\n");
    	System.out.println();
    	System.out.println("Options:");
    	System.out.println("\t-h : Show this usage message.");
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
