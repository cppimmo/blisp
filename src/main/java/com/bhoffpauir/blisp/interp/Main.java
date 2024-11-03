package com.bhoffpauir.blisp.interp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
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
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;

import com.bhoffpauir.blisp.lib.Atom;
import com.bhoffpauir.blisp.lib.Environment;
import com.bhoffpauir.blisp.lib.Evaluator;
import com.bhoffpauir.blisp.lib.Parser;
import com.bhoffpauir.blisp.lib.Procedure;
import com.bhoffpauir.blisp.lib.SymbolAtom;
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
	private boolean extendedPrint = false;
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
    
    /**
     * Initialize the interpreter.
     * 
     * @param args The command line arguments from {@code main}.
     */
    private void initialize(final String[] args) {
    	options = new Options();
    	parseArguments(args);
    }
    
    /**
     * Parse the command line arguments.
     * 
     * @param args The command line arguments to parse.
     */
    private void parseArguments(final String[] args) {
    	// Arg names should be UPPER_SNAKE_CASE
    	options.addOption("h", "help", false, "Show this usage message and exit.");
    	options.addOption("v", "version", false, "Show version information and exit.");
    	options.addOption("i", "interactive", false, "Run in REPL mode.");
    	options.addOption("t", "show-tokens", false, "Show each token from the input.");
    	options.addOption("p", "show-parser", false, "Show each expression parsed from the input.");
    	options.addOption("st", "stack-trace", false, "Show Java exception stack trace output.");
    	options.addOption("ep", "extended-print", false, "Turn on extented print in REPL Print stage.");
    	
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
    		if (cmd.hasOption("ep")) {
    			extendedPrint = true;
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
    
    /**
     * Starts the main loop for the interpreter.
     * 
     * @return The application exit code.
     * @throws IOException
     */
    private int mainLoop() throws IOException {
    	// Set the appropriate code source
    	
    	LineReader lineReader = createLineReader();
    	lineReader.setVariable(LineReader.HISTORY_FILE, Paths.get(System.getProperty("user.home"), ".blisp_history").toString());
        lineReader.setVariable(LineReader.HISTORY_FILE_SIZE, 100); // Sset history file size
        
    	//InputStream in = new LineReaderInputStream(lineReader);
    	InputStream in = System.in;
    	if (scriptFile != null) {
    		in = new FileInputStream(scriptFile);
    	}
    	InputStreamReader input = new InputStreamReader(in);
    	BufferedReader reader = new BufferedReader(input);
    	
    	History history = new DefaultHistory(lineReader);
    	
    	// Show initial messages at REPL
    	if (mode == InterpreterMode.REPL) {
    		String osName = System.getProperty("os.name");
    		String osArch = System.getProperty("os.arch");
    		String osVersion = System.getProperty("os.version");
    		System.out.printf("%s v%s on %s (%s) version %s\n", PRGNAM, getVersion(), osName,
    				osArch, osVersion);
    		System.out.println("Type \"(help)\" or \"(license)\" for more information.");
    		System.out.println("Press Ctrl+D or type \"(exit)\" to exit this REPL.");
    	}
    	// Turn on extended print
    	if (extendedPrint) {
    		Atom.setExtendedPrint(true);
    	}
    	
    	// Create the environment for the session
    	Environment env = Environment.createGlobalEnv();
    	env.define("help", (Procedure) (args) -> {
    		replUsage();
    		return SymbolAtom.nil;
    	});
    	env.define("license", (Procedure) (args) -> {
    		System.out.println("Eclipse Public License - v 2.0");
    		return SymbolAtom.nil;
    	});
    	// Build up input expressions line by line
    	StringBuilder expression = new StringBuilder();
    	int retcode = EXIT_SUCCESS;
    	
    	do {
    		try {
    			// Prompt (optionally) & read input
    			if (mode == InterpreterMode.REPL) {
    				if (expression.length() == 0)
    					System.out.print(">>> ");
    				else 
    					System.out.print("... ");
    			}
    		
    			String line = reader.readLine();
    			//System.out.println("LINE: " + line + "|EOL");
    			// Exit on EOF, Ctrl+D, or (quit) (implemented as a function)
    			if (line == null) {
    				// The script file is done executing now switch to REPL mode
    				if (mode == InterpreterMode.SCRIPT_AND_REPL) {
    					mode = InterpreterMode.REPL;
    					// Reconstruct the input stream and buffered reader
    					lineReader = createLineReader();
    			    	input = new InputStreamReader(new LineReaderInputStream(lineReader));
    			    	reader = new BufferedReader(input);
    					continue;
    				}
    				running = false;
    				break;
    			}
    			
    			// Append the line as well as a newline to the expression string builder
    			expression.append(line).append('\n');
    			// Add the line to the history
    			history.add(line);
    			
    			// If the expression string has unbalanced parentheses, then continue reading
    			final String exprStr = expression.toString();
    			if (hasUnmatchedParentheses(exprStr)) {
    				continue;
    			}
    			// Tokenize the input
    			List<String> tokens = new Tokenizer(exprStr).tokenize();
    			if (tokens.isEmpty()) continue;
    			// Display the tokenization stage output
    			if (showTokens) {
    				System.out.printf("  Tokens: %s\n", tokens);
    			}
    			
    			// Parse the tokenized input
    			Parser parser = new Parser(tokens);
    			Object parsedExpr = parser.parse();
    			// Display the parsing stage output
    			if (showParser) {
    				System.out.printf("  Parsed Expr(s): %s\n", parsedExpr);
    			}
    			
    			//System.out.println(env);
    			// Evaluate using the global environment	
    			Evaluator evaluator = new Evaluator(env);
    			Object result = evaluator.evaluate(parsedExpr, env);
    			if (mode == InterpreterMode.REPL) {
        			// Output the result of evaluating the given expression
    				System.out.println(result);
    			}
    			// Reset the expression string builder
    			expression.setLength(0);
    		} catch (LispRuntimeException ex) {
        		// Process blisp runtime exceptions
        		System.err.printf("Error:\n  %s\n", ex.getMessage());
    			if (showStackTrace)
        			ex.printStackTrace();
        	} catch (Exception ex) {
        		// Any exception not derived from LispRuntimeExeception should result in an exit
        		System.err.printf("Fatal Error:\n  %s\n", ex.getMessage());
        		if (showStackTrace)
        			ex.printStackTrace();
        		// Prepare for exit
        		running = false;
        		retcode = EXIT_FAILURE;
        		break;
        	}
    	} while (running);
    	return retcode;
    }

    private LineReader createLineReader() {
    	return LineReaderBuilder.builder().parser(new ImmediateLineParser()).build();
    }
    
    /**
     * Detect if the {@code input} string has unbalanced parentheses.
     * 
     * @param input The input string.
     * @return True if the string has unbalanced parentheses, false otherwise.
     */
    private boolean hasUnmatchedParentheses(String input) {
    	if (!(input.contains("(") || input.contains(")"))) {
    		return false;
    	}
    	
        int openParenCount = 0;
        for (char ch : input.toCharArray()) {
            if (ch == '(') openParenCount++;
            if (ch == ')') openParenCount--;
        }
        return openParenCount != 0;
    }
    
    /**
     * Show command line help information.
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
    
    /**
     * Show REPL help information.
     */
    private void replUsage() {
    	System.out.println("(exit)");
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
