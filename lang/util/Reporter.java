package lang.util;

import lang.token.TokenInfo;
import lang.util.Stack;

public class Reporter {
    private Reporter() {}

    public static void identifierNotFound(TokenInfo tokenInfo) {
        report(
            tokenInfo,
            "The word `" + tokenInfo.raw() + "` could not be found.",
            "Unknown word"
        );
    }

    public static void notEnoughArgs(TokenInfo tokenInfo, int requiredArgs, Stack<Double> stack) {
        report(
            tokenInfo,
            "The word `" + tokenInfo.raw() + "` doesn't have enough values to operate on.",
            "Expected " + requiredArgs + " values, found " + stack.size()
        );
        printStack(stack);
        System.out.printf("%n");
    }

    public static void unclosedBlock(TokenInfo startInfo) {
        report(
            startInfo,
            "A block is not closed.",
            "Add `end` to close this block"
        );
    }

    public static void freeEnd(TokenInfo endInfo) {
        report(
            endInfo,
            "An `end` is not closing anything.",
            "Consider removing this `end`"
        );
    }

    public static void freeElse(TokenInfo elseInfo) {
        report(
            elseInfo,
            "An `else` without a corresponding `if` was found.",
            "Consider removing this `else`"
        );
    }

    public static void noBlock(TokenInfo startInfo) {
        report(
            startInfo,
            "A `" + startInfo.raw() + "` which requires a block doesn't have one.",
            "Consider adding a `do-end` construction after the condition"
        );
    }

    public static void noCondition(TokenInfo startInfo) {
        report(
            startInfo,
            "The word `" + startInfo.raw() + "` expected a condition but didn't find one.",
            "Add a condition to this `" + startInfo.raw() + "` keyword"
        );
    }

    public static void unhandledValues(String filename, Stack<Double> stack) {
        System.out.printf(
            "%s: ERROR: %d values are left unhandled on the stack. Consider dropping them.%n",
            filename, stack.size()
        );
        printStack(stack);
        System.out.printf("%n");
    }

    public static void printStack(Stack<Double> stack) {
        if (!stack.isEmpty()) {
            System.out.println("Stack:");
            System.out.println("^ " + stack.pop());
            while (!stack.isEmpty()) {
                System.out.println("| " + stack.pop());
            }
        }
    }

    public static void report(TokenInfo tokenInfo, String message, String description) {
        int colLen = Integer.toString(tokenInfo.col()).length();
        System.out.printf(
            "%s:%d:%d ERROR: %s%n" +
            "%s |%n" +
            "%d | %s%n" +
            "%s |%s%s %s%n%n",
            tokenInfo.filename(), tokenInfo.row(), tokenInfo.col(), message,
            " ".repeat(colLen),
            tokenInfo.row(), tokenInfo.line(),
            " ".repeat(colLen), " ".repeat(tokenInfo.col()), "^".repeat(tokenInfo.raw().length()), description
        );
    }
}
