package lang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import lang.util.Stack;

public class Main {
    private Main() {}

    public static void main(String[] args) {
        if (args.length < 1) {
            repl();
        }
        try {
            String filepath = args[0];
            String code = new String(Files.readAllBytes(Paths.get(filepath)));
            step(code, filepath, Environment.generateStdLib(), false, true);
        } catch (IOException e) {
            System.out.println("Could not read file `" + args[0] + "`");
        }
    }

    public static void repl() {
        Environment env = Environment.generateReplEnv();
        System.out.println("Type `exit` to exit");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(">>> ");
            String line = sc.nextLine();
            step(line, "<REPL>", env, true, false);
        }
    }

    public static void step(String code, String filename, Environment env, boolean printStack, boolean haltOnError) {
        try {
            Parser.compile(Parser.parse(code, filename), printStack, filename).fn().accept(new Stack<>(), env);
        } catch (RuntimeException e) {
            if (haltOnError) {
                System.exit(-1);
            }
        }
    }
}
