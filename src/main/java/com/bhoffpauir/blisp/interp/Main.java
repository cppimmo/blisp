package com.bhoffpauir.blisp.interp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
/**
 * Entry point for interpreter.
 */
public class Main {
	private static final String PRGNAM = "blisp";
	private static int EXIT_SUCCESS = 0, EXIT_FAILURE = 1;
	private boolean running = true;
	
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        
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
    			
    			if (args.length > 0 && (new File(args[args.length - 1])).exists()) {
    				System.out.println("Script file exists.");
    			}
    		}
    	} catch (ParseException ex) {
    		System.err.println("Error parsing arguments: " + ex.getMessage());
    		usage();
    	}
    }
    
    private int mainLoop() throws IOException {
    	InputStreamReader input = new InputStreamReader(System.in);
    	BufferedReader reader = new BufferedReader(input);
    	
    	do {
    		// Prompt & read input
    		System.out.print(">>> ");
        	String line = reader.readLine();
        	// Exit on EOF, Ctrl+D, or (quit)
    		if (line == null || line.compareTo("q") == 0) {
    			running = false;
    			break;
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
    	final Properties properties = new Properties();
    	try {
			properties.load(getClass().getResourceAsStream("project.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return properties.getProperty("blisp.version");
    }
}
