package lang;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiPredicate;

import lang.opcode.ConditionalJumpOpCode;
import lang.opcode.InvokeFunctionOpCode;
import lang.opcode.OpCode;
import lang.token.DoToken;
import lang.token.EndToken;
import lang.token.IdentifierToken;
import lang.token.IfToken;
import lang.token.NumberToken;
import lang.token.Token;
import lang.token.TokenInfo;
import lang.token.WhileToken;
import lang.util.Reporter;
import lang.util.Stack;

public class Parser {
    private Parser() {}

    private static String removeComments(String code) {
        StringBuilder cleaned = new StringBuilder();
        int parenDepth = 0;
        for (int i = 0; i < code.length(); ++i) {
            char c = code.charAt(i);
            if (c == '(') {
                ++parenDepth;
                cleaned.append(" ");
            } else if (c == ')') {
                --parenDepth;
                cleaned.append(" ");
            } else if (c == '\r' || c == '\n' || parenDepth == 0) {
                cleaned.append(c);
            } else {
                cleaned.append(" ");
            }
        }
        return cleaned.toString();
    }

    public static Queue<Token> parse(String code, String filename) {
        code = removeComments(code);
        String[] lines = removeComments(code).lines().toArray(String[]::new);
        Queue<Token> tokens = new ArrayDeque<>();

        for (int row = 0; row < lines.length; ++row) {
            String line = new String(lines[row]).replaceAll("\\s+$", "");
            int col = 1;
            while (!line.isBlank()) {
                int initialLen = line.length();
                line = line.trim();
                col += initialLen - line.length();

                StringBuilder word = new StringBuilder();
                for (int i = 0; i < line.length() && !Character.isWhitespace(line.charAt(i)); ++i) {
                    word.append(line.charAt(i));
                }

                tokens.add(Token.from(
                    word.toString(),
                    new TokenInfo(word.toString(), filename, row + 1, col, lines[row])
                ));

                col += word.length();
                line = line.substring(word.length());
            }
        }

        return tokens;
    }

    private static BiPredicate<Stack<Double>, Environment> compileCondition(Token start, Queue<Token> tokens, String filename) {
        List<OpCode> opcodes = new ArrayList<>();
        while (!tokens.isEmpty()) {
            Token token = tokens.element();
            if (token instanceof NumberToken) {
                NumberToken t = (NumberToken) token;
                opcodes.add(new InvokeFunctionOpCode((stack, _env) -> stack.push(t.number())));
                tokens.remove();
            } else if (token instanceof IdentifierToken) {
                IdentifierToken t = (IdentifierToken) token;
                opcodes.add(new InvokeFunctionOpCode((stack, env) -> env.find(t.identifier()).getValue().accept(t.info(), stack, env)));
                tokens.remove();
            } else if (token instanceof DoToken) {
                return (stack, env) -> {
                    createExecutor(opcodes, false, false, filename).fn().accept(stack, env);
                    return stack.pop() == 0;
                };
            } else if (token instanceof EndToken) {
                Reporter.freeEnd(token.info());
                throw new RuntimeException();
            } else if (token instanceof IfToken) {
                tokens.remove();
                opcodes.add(new ConditionalJumpOpCode(compileCondition(token, tokens, filename), 1));
                if (tokens.isEmpty()) {
                    Reporter.noBlock(token.info());
                    throw new RuntimeException();
                }
                opcodes.add(compileBlock(tokens.remove(), tokens, false, filename));
            } else if (token instanceof WhileToken) {
                opcodes.add(new ConditionalJumpOpCode(compileCondition(token, tokens, filename), 2));
                if (tokens.isEmpty()) {
                    Reporter.noBlock(token.info());
                    throw new RuntimeException();
                }
                opcodes.add(compileBlock(tokens.remove(), tokens, false, filename));
                opcodes.add(new ConditionalJumpOpCode((_stack, _env) -> true, -3));
            } else {
                System.out.println("Unknown token type: " + token.getClass().getSimpleName());
                throw new RuntimeException();
            }
        }
        System.out.println("Should be unreachable");
        throw new RuntimeException();
    }

    private static InvokeFunctionOpCode compileBlock(Token start, Queue<Token> tokens, boolean printStack, String filename) {
        List<OpCode> opcodes = new ArrayList<>();
        while (!tokens.isEmpty()) {
            Token token = tokens.remove();
            if (token instanceof NumberToken) {
                NumberToken t = (NumberToken) token;
                opcodes.add(new InvokeFunctionOpCode((stack, _env) -> stack.push(t.number())));
            } else if (token instanceof IdentifierToken) {
                IdentifierToken t = (IdentifierToken) token;
                opcodes.add(new InvokeFunctionOpCode((stack, env) -> env.find(t.identifier()).getValue().accept(t.info(), stack, env)));
            } else if (token instanceof DoToken) {
                opcodes.add(compileBlock(token, tokens, false, filename));
            } else if (token instanceof EndToken) {
                return createExecutor(new ArrayList<>(opcodes), printStack, false, filename);
            } else if (token instanceof IfToken) {
                opcodes.add(new ConditionalJumpOpCode(compileCondition(token, tokens, filename), 1));
                if (tokens.isEmpty()) {
                    Reporter.noBlock(token.info());
                    throw new RuntimeException();
                }
                opcodes.add(compileBlock(tokens.remove(), tokens, false, filename));
            } else if (token instanceof WhileToken) {
                opcodes.add(new ConditionalJumpOpCode(compileCondition(token, tokens, filename), 2));
                if (tokens.isEmpty()) {
                    Reporter.noBlock(token.info());
                    throw new RuntimeException();
                }
                opcodes.add(compileBlock(tokens.remove(), tokens, false, filename));
                opcodes.add(new ConditionalJumpOpCode((_stack, _env) -> true, -3));
            } else {
                System.out.println("Unknown token type: " + token.getClass().getSimpleName());
                throw new RuntimeException();
            }
        }

        if (start != null) {
            Reporter.unclosedBlock(start.info());
            throw new RuntimeException();
        }
        return createExecutor(opcodes, printStack, !printStack, filename);
    }

    private static InvokeFunctionOpCode createExecutor(List<OpCode> opcodes, boolean printStack, boolean reportUnhandled, String filename) {
        return new InvokeFunctionOpCode((stack, env) -> {
            int opPtr = 0;
            while (opPtr < opcodes.size()) {
                OpCode opcode = opcodes.get(opPtr);
                if (opcode instanceof InvokeFunctionOpCode) {
                    InvokeFunctionOpCode o = (InvokeFunctionOpCode) opcode;
                    o.fn().accept(stack, env);
                    opPtr = opcode.nextInstruction(opPtr);
                } else if (opcode instanceof ConditionalJumpOpCode) {
                    ConditionalJumpOpCode o = (ConditionalJumpOpCode) opcode;
                    if (o.toJumpTest().test(stack, env)) {
                        opPtr = opcode.nextInstruction(opPtr);
                    } else {
                        ++opPtr;
                    }
                } else {
                    System.out.println("Unknown opcode type: " + opcode.getClass().getSimpleName());
                    throw new RuntimeException();
                }
            }

            if (printStack) {
                Reporter.printStack(stack);
                System.out.println();
            } else if (reportUnhandled && !stack.isEmpty()) {
                Reporter.unhandledValues(filename, stack);
            }
        });
    }

    public static InvokeFunctionOpCode compile(Queue<Token> tokens, boolean printStack, String filename) {
        return compileBlock(null, tokens, printStack, filename);
    }
}
