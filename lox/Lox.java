package lox;

import java.util.List;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class Lox {
	static boolean hadError = false;

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: java -jar jlox.jar [script]");
			System.exit(64); // Incorrect command usage.
			
			/* NOTE: Exit codes are used as the conventions defined
			 | in <sysexits.h> header file. It consists of preferable
			 | exit codes for programs. It is a good measure.
			*/
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}

		// TODO: Add additional flags like -h or --version.
	}

	// Methods for running Lox scripts.
	private static void runFile(String path) throws IOException {
		byte[] fileBytes = Files.readAllBytes(Paths.get(path));
		run(new String(fileBytes, Charset.defaultCharset()));

		// Indicate an error in the exit code.
		if (hadError)
			System.exit(65);
	}

	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
    	BufferedReader reader = new BufferedReader(input);

    	while (true) {
    		System.out.print(">> ");
    		String line = reader.readLine();
    		if (line == null || line.isEmpty()) {
    			System.out.println("Exiting the REPL...");
    			break;
    		}
    		run(line);

    		// Reset error flags for REPL.
    		hadError = false;
    	}
	}

	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		// Printing the tokens.
		for (Token token : tokens) {
			System.out.println(token);
			// NOTE: This calls the token.toString() method.
		}
	}


	// Methods for error handling.
	public static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		System.out.println(
			"[line " + line + "] Error" + where + ": " + message
		); // NOTE: System.err is more appropriate.
		hadError = true;
	}
}