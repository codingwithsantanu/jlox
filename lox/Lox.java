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
            System.exit(64); // Incorrect command usage (EX_USAGE).

            /* NOTE: These custom exit codes used are the from
             | the conventions defined in the <sysexits.h> header
             | file. It consists of preferable exit codes for
             | programs. It is a good measure.
             */
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }

        // TODO: Handle additional flags like -h or --version.
    }

    // Main methods for running Lox scripts.
    private static void runFile(String path) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(path));
        run(new String(fileBytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (hadError)
            System.exit(65); // Invalid USER input (EX_DATAERR).
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.println(">> ");
            String line = reader.readLine();

            if (line == null || line.trim().isEmpty()) {
                System.out.println("Exiting the shell.");
                break;
            }
            /* NOTE: BufferedReader.readLine() returns null
             | only when a kill instruction is given to the
             | program, usually via CTRL+D in a shell or
             | terminal.

             * FACT: These types of interactive shells are
             | also called as REPLs (Read the input, Evaluate
             | it, Print the result, Loop).
             */

            run(line);

            // Reset error flags, we don't want to ruin one's
            // entire REPL session because of one mistake.
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // Print the generated tokens.
        for (Token token : tokens) {
            System.out.println(token);
            // NOTE: println() calls token.toString().
        }
    }


    // Methods for Error handling.
    public static void error(int line, String message) {
        report(line, "", message);
    }


    private static void report(int line, String where, String message) {
        System.out.println(
            "[line " + line + "] Error" + where + ": " + message
        ); // TODO: System.err suits this better.
        hadError = true;
    }
}
