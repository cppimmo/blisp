package com.bhoffpauir.blisp.interp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.EndOfFileException;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Reference;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.history.DefaultHistory;

import com.bhoffpauir.blisp.lib.Environment;
import com.bhoffpauir.blisp.lib.Evaluator;
import com.bhoffpauir.blisp.lib.Parser;
import com.bhoffpauir.blisp.lib.Procedure;
import com.bhoffpauir.blisp.lib.TokenTreeViewer;
import com.bhoffpauir.blisp.lib.Tokenizer;
import com.bhoffpauir.blisp.lib.TreeViewer;
import com.bhoffpauir.blisp.lib.Utils;
import com.bhoffpauir.blisp.lib.Version;
import com.bhoffpauir.blisp.lib.atom.Atom;
import com.bhoffpauir.blisp.lib.atom.SymbolAtom;
import com.bhoffpauir.blisp.lib.exception.LispRuntimeException;

/**
 * Interpreter implementation for the blisp language supporting both interactive REPL and
 * script file execution modes.
 */
public class Interpreter {
	private static final String PRGNAM = "blisp";
	private static final Version VERSION = new Version(1, 0, 0);
	private static final int EXIT_SUCCESS = 0, EXIT_FAILURE = 1;
	// Prompt strings:
	private static final String PS1 = ">>> "; // Primary prompt
	private static final String PS2 = "... "; // Secondary prompt
	// Website URLs:
	private static final URL WEBSITE_URL; // Homepage
	private static final URL REPORT_URL;  // Language paper
	private static final URL JAVADOC_URL; // Javadocs
	
	static {
		// Try to initialize the website URLs:
		try {
			WEBSITE_URL = new URI("https://cppimmo.github.io/blisp/").toURL();
			REPORT_URL  = new URI("https://cppimmo.github.io/blisp/docs/report.pdf").toURL();
			JAVADOC_URL = new URI("https://cppimmo.github.io/blisp/apidocs/index.html").toURL();
	    } catch (MalformedURLException | URISyntaxException ex) {
	    	// Handle the error (e.g., log it or wrap in a runtime exception)
	    	throw new RuntimeException("Error initializing URLs", ex);
	    }
	}
	// CLI options/arguments:
	private Options options;
	private boolean showTokens = false;
	private boolean showTokensTree = false;
	private boolean showParser = false;
	private boolean showStackTrace = false;
	private boolean extendedPrint = false;
	// Interpreter runtime:
	private File scriptFile = null;
	private InterpreterMode mode = InterpreterMode.SCRIPT; // Default is SCRIPT
	private boolean running = true;
	// Interpreter print stream aliases:
	private final PrintStream ps = System.out; // Print stream
	private final PrintStream es = System.err; // Error stream
	
    public static void main(String[] args) throws IOException {
        // Initialize the interpreter
        Interpreter interpreter = new Interpreter();
        interpreter.initialize(args);
        // Enter the main loop
        System.exit(interpreter.mainLoop());
    }
    
    /**
     * Initialize the interpreter.
     * 
     * @param args The command line arguments from {@code main}.
     */
    private void initialize(final String[] args) {
    	options = new Options();
    	// Add CLI options:
    	options.addOption("h", "help", false, "Show this usage message and exit.");
    	options.addOption("v", "version", false, "Show version information and exit.");
    	options.addOption("i", "interactive", false, "Run in REPL mode.");
    	options.addOption("t", "show-tokens", false, "Show each token from the input.");
    	options.addOption("tt", "show-tokens-tree", false, "Open a dialog with a tree view of the tokenization stage.");
    	options.addOption("p", "show-parser", false, "Show each expression parsed from the input.");
    	options.addOption("st", "stack-trace", false, "Show Java exception stack trace output.");
    	options.addOption("ep", "extended-print", false, "Turn on extented print in REPL Print stage.");
    	
    	parseArguments(args);
    }
    
    /**
     * Parse the command line arguments.
     * 
     * @param args The command line arguments to parse.
     */
    private void parseArguments(final String[] args) {
    	CommandLineParser parser = new DefaultParser();
    	try {
    		CommandLine cmd = parser.parse(options, args);
    		// Arguments that alter program execution
    		if (cmd.hasOption('h')) {
    			usage(); // Display program help
    			System.exit(EXIT_SUCCESS);
    		}
    		if (cmd.hasOption('v')) {
    			version(); // Display version info
    			System.exit(EXIT_SUCCESS);
    		}
    		// Process other arguments
    		if (cmd.hasOption('i')) {
    			mode = InterpreterMode.REPL;
    		}
    		if (cmd.hasOption('t')) {
    			showTokens = true;
    		}
    		if (cmd.hasOption("tt")) {
    			showTokensTree = true;
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
    		// Handle script file argument
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
    		es.println("Error parsing arguments: " + ex.getMessage());
    		usage();
    	}
    }
    
    /**
     * Instance for the most recently opened tree viewer.
     */
    private TreeViewer lastTokenTreeViewer = null;
    
    /**
     * Starts the main loop for the interpreter.
     * 
     * @return The application exit code.
     * @throws IOException
     */
    private int mainLoop() throws IOException {
    	// Set the appropriate code source
    	
    	final LineReader lineReader = createLineReader();
    	lineReader.setVariable(LineReader.HISTORY_FILE, Paths.get(System.getProperty("user.home"), ".blisp_history").toString());
        lineReader.setVariable(LineReader.HISTORY_FILE_SIZE, 100); // Sset history file size
        lineReader.setVariable(LineReader.SECONDARY_PROMPT_PATTERN, PS2);
        lineReader.setKeyMap(LineReader.EMACS); // Set keymap to GNU/Emacs
        
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
    		replHello();
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
    		ps.println("Eclipse Public License - v 2.0");
    		return SymbolAtom.nil;
    	});
    	env.define("website", (Procedure) (args) -> {
    		Utils.openUrlInBrowser(WEBSITE_URL);
    		return SymbolAtom.nil;
    	});
    	env.define("javadoc", (Procedure) (args) -> {
    		Utils.openUrlInBrowser(JAVADOC_URL);
    		return SymbolAtom.nil;
    	});
    	// Build up input expressions line by line
    	StringBuilder expression = new StringBuilder();
    	int retcode = EXIT_SUCCESS; // REPL return value
    	
    	lineReader.getWidgets().put("input-cancel", () -> {
    		//lineReader.callWidget(LineReader.CLEAR_SCREEN);
    		// Clear the buffer and expression
    	    lineReader.getBuffer().clear();
    	    expression.setLength(0);

    	    // Reset prompt depending on the context
    	    //lineReader.setPrompt(expression.length() == 0 ? PS1 : PS2);

    	    // Redraw the prompt
    	    lineReader.callWidget(LineReader.REDISPLAY);
    	    return true; // Continue the loop
    	});
    	
    	// Bind the input cancel widget to Ctrl+G
    	KeyMap<Binding> keyMap = lineReader.getKeyMaps().get(LineReader.MAIN);
    	keyMap.bind(new Reference("input-cancel"), "\u0007"); // \u0007 = Ctrl+G
    	
    	do {
    		try {
    			// Prompt (optionally) & read input
    			String line;
    	        if (mode == InterpreterMode.REPL) {
    	            // Primary prompt for the first line
    	            String prompt = expression.length() == 0 ? PS1 : PS2;
    	            // Read input, secondary prompt is handled automatically by LineReader
    	            line = lineReader.readLine(prompt);
    	        } else {
    	            line = reader.readLine();
    	        }
    			
    			// Exit on EOF, Ctrl+D, or (quit) (implemented as a function)
    			if (line == null) {
    				// The script file is done executing now switch to REPL mode
    				if (mode == InterpreterMode.SCRIPT_AND_REPL) {
    					mode = InterpreterMode.REPL;
    					// Reconstruct the input stream and buffered reader
    					//lineReader = createLineReader();
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
    			
    			// Continue reading if the expression string has unbalanced parentheses
    			final String exprStr = expression.toString();
    			if (Utils.hasUnmatchedParentheses(exprStr)) {
    				continue;
    			}
    			// Tokenize the input
    			List<String> tokens = new Tokenizer(exprStr).tokenize();
    			if (tokens.isEmpty()) continue;
    			// Display the tokenization stage output
    			if (showTokens) {
    				ps.printf("  Tokens: %s\n", tokens);
    			}
    			// Display token tree (Swing dialog)
    			if (showTokensTree) {
    				// Close the viewer if one is already open
    				if (lastTokenTreeViewer != null)
    					lastTokenTreeViewer.closeViewer();
    				
    				List<Object> temp = new ArrayList<>(tokens);
    				lastTokenTreeViewer = new TokenTreeViewer(temp);
    				lastTokenTreeViewer.showViewer();
    			}
    			
    			// Parse the tokenized input
    			Parser parser = new Parser(tokens);
    			Object parsedExpr = parser.parse();
    			// Display the parsing stage output
    			if (showParser) {
    				ps.printf("  Parsed Expr(s): %s\n", parsedExpr);
    			}
    			
    			//System.out.println(env);
    			// Evaluate using the global environment	
    			Evaluator evaluator = new Evaluator(env);
    			Object result = evaluator.evaluate(parsedExpr, env);
    			if (mode == InterpreterMode.REPL) {
        			// Output the result of evaluating the given expression
    				ps.println(result);
    			}
    			// Reset the expression string builder
    			expression.setLength(0);
    		} catch (EndOfFileException | UserInterruptException ex) {
    			// Handle Ctrl+C & Ctrl+D with JLine gracefully
    			running = false;
    			break;
    		} catch (LispRuntimeException ex) {
        		// Process blisp runtime exceptions
        		es.printf("Error:\n  %s\n", ex.getMessage());
    			if (showStackTrace)
        			ex.printStackTrace();
        	} catch (Exception ex) {
        		// Any exception not derived from LispRuntimeExeception should result in an exit
        		es.printf("Fatal Error:\n  %s\n", ex.getMessage());
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
     * Show command line help information.
     */
    private void usage() {
    	//HelpFormatter formatter = new HelpFormatter();
    	//formatter.printUsage(new PrintWriter(System.out), 80, PRGNAM, options);
    	ps.printf("%s %s:\n", PRGNAM, VERSION);
    	ps.printf("Website: %s\n", WEBSITE_URL.toString());
    	ps.printf("Usage: [OPTION...] [SCRIPT_FILE]\n");
    	ps.println();
    	ps.println("Options:");
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
    		
    		ps.printf("  -%-20s : %s\n", argSb.toString(), desc);
    	});
    }
    
    /**
     * Initial messages displayed by the REPL.
     */
    private void replHello() {
    	String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");
		String osVersion = System.getProperty("os.version");
		ps.printf("%s %s on %s (%s) version %s\n", PRGNAM, VERSION, osName, osArch, osVersion);
		ps.println("Type \"(help)\" or \"(license)\" for more information.");
		ps.println("Press Ctrl+D or type \"(exit)\" to exit this REPL.");
    }
    
    /**
     * Show REPL help information.
     */
    private void replUsage() {
    	ps.println("Welcome to the blisp REPL!");
        ps.println("Here are some useful procedures and tips to get started:");
        ps.println();
        ps.println("Useful Procedures:");
        ps.println("  (help)    - Display this usage message.");
        ps.println("  (license) - Show licensing information.");
        ps.println("  (website) - Open the blisp website in your default browser.");
        ps.println("  (javadoc) - Open the blisp Javadocs in your default browswer.");
        ps.println("  (exit)    - Exit the interpreter.");
        ps.println();
        ps.println("Tips:");
        ps.println("  - You can write multi-line expressions; use proper parentheses matching.");
        ps.println("  - To clear the current input line, use Ctrl+G.");
        ps.println("  - Use standard Lisp syntax for expressions.");
        ps.println("  - Use the arrow keys to navigate through previous commands.");
        ps.println();
        ps.println("Shortcuts (others GNU/Emacs based):");
        ps.println("  Ctrl+G        - Interrupt the current command.");
        ps.println("  Ctrl+D/Ctrl+C - Exit the interpreter.");
        ps.println();
        ps.println("For more information, consult the online documentation or source code.");
        ps.println("Website:        " + WEBSITE_URL.toString());
        ps.println("Language paper: " + REPORT_URL.toString());
        ps.println("Javadoc:        " + JAVADOC_URL.toString());
        ps.println("Happy LISPing!");
    }
    
    private void version() {
    	/*final Properties properties = new Properties();
    	try {
			properties.load(getClass().getResourceAsStream("project.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return properties.getProperty("blisp.version");*/
    	ps.println(VERSION);
    }
}
