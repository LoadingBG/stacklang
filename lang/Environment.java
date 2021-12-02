package lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lang.token.TokenInfo;
import lang.util.Reporter;
import lang.util.Stack;
import lang.util.TriConsumer;

// TODO: runtime checks for arguments
public class Environment extends HashMap<String, TriConsumer<TokenInfo, Stack<Double>, Environment>> {
    private static Pattern DUP_PATTERN = Pattern.compile("^([1-9]\\d*)dup$");
    private static Pattern DROP_PATTERN = Pattern.compile("^([1-9]\\d*)drop$");
    private static Pattern REVERSE_PATTERN = Pattern.compile("^([1-9]\\d*)reverse$");
    private static Pattern SWAP_PATTERN = Pattern.compile("^([1-9]\\d*)swap([1-9]\\d*)$");
    private static Pattern OVER_PATTERN = Pattern.compile("^([1-9]\\d*)over([1-9]\\d*)$");

    public Environment() {
        super();
    }

    public Environment(HashMap<String, TriConsumer<TokenInfo, Stack<Double>, Environment>> env) {
        super(env);
    }

    public Map.Entry<String, TriConsumer<TokenInfo, Stack<Double>, Environment>> find(String name) {
        List<Map.Entry<String, TriConsumer<TokenInfo, Stack<Double>, Environment>>> entries = new ArrayList<>(entrySet());
        for (int i = 0; i < entries.size(); ++i) {
            Map.Entry<String, TriConsumer<TokenInfo, Stack<Double>, Environment>> fn = entries.get(i);
            if (fn.getKey().equals(name)) {
                return fn;
            }
        }

        Matcher dupMatcher = DUP_PATTERN.matcher(name);
        if (dupMatcher.find()) {
            // TODO: handle too high numbers
            int nums = Integer.parseInt(dupMatcher.group(1));
            return Map.entry(
                name,
                (info, stack, _env) -> {
                    if (stack.size() < nums) {
                        Reporter.notEnoughArgs(info, nums, stack);
                        throw new RuntimeException();
                    }
                    double[] values = new double[nums];
                    for (int i = nums - 1; i >= 0; --i) {
                        values[i] = stack.pop();
                    }
                    for (int i = 0; i < nums; ++i) {
                        stack.push(values[i]);
                    }
                    for (int i = 0; i < nums; ++i) {
                        stack.push(values[i]);
                    }
                }
            );
        }

        Matcher dropMatcher = DROP_PATTERN.matcher(name);
        if (dropMatcher.find()) {
            // TODO: handle too high numbers
            int times = Integer.parseInt(dropMatcher.group(1));
            return Map.entry(
                name,
                (info, stack, _env) -> {
                    if (stack.size() < times) {
                        Reporter.notEnoughArgs(info, times, stack);
                        throw new RuntimeException();
                    }
                    for (int i = 0; i < times; ++i) {
                        stack.pop();
                    }
                }
            );
        }

        Matcher reverseMatcher = REVERSE_PATTERN.matcher(name);
        if (reverseMatcher.find()) {
            // TODO: handle too high numbers
            // TODO: handle 1
            int nums = Integer.parseInt(reverseMatcher.group(1));
            return Map.entry(
                name,
                (info, stack, _env) -> {
                    if (stack.size() < nums) {
                        Reporter.notEnoughArgs(info, nums, stack);
                        throw new RuntimeException();
                    }
                    double[] tops = new double[nums];
                    for (int i = 0; i < nums; ++i) {
                        tops[i] = stack.pop();
                    }
                    for (int i = 0; i < nums; ++i) {
                        stack.push(tops[i]);
                    }
                }
            );
        }

        Matcher swapMatcher = SWAP_PATTERN.matcher(name);
        if (swapMatcher.find()) {
            // TODO: handle too high numbers
            int m = Integer.parseInt(swapMatcher.group(1));
            int n = Integer.parseInt(swapMatcher.group(2));
            return Map.entry(
                name,
                (info, stack, _env) -> {
                    if (stack.size() < n + m) {
                        Reporter.notEnoughArgs(info, n + m, stack);
                        throw new RuntimeException();
                    }
                    double[] tops = new double[n];
                    for (int i = n - 1; i >= 0; --i) {
                        tops[i] = stack.pop();
                    }
                    double[] bots = new double[m];
                    for (int i = m - 1; i >= 0; --i) {
                        bots[i] = stack.pop();
                    }

                    for (int i = 0; i < n; ++i) {
                        stack.push(tops[i]);
                    }
                    for (int i = 0; i < m; ++i) {
                        stack.push(bots[i]);
                    }
                }
            );
        }

        Matcher overMatcher = OVER_PATTERN.matcher(name);
        if (overMatcher.find()) {
            // TODO: handle too high numbers
            int m = Integer.parseInt(overMatcher.group(1));
            int n = Integer.parseInt(overMatcher.group(2));
            return Map.entry(
                name,
                (info, stack, _env) -> {
                    if (stack.size() < n + m) {
                        Reporter.notEnoughArgs(info, n + m, stack);
                        throw new RuntimeException();
                    }
                    double[] tops = new double[n];
                    for (int i = n - 1; i >= 0; --i) {
                        tops[i] = stack.pop();
                    }
                    double[] bots = new double[m];
                    for (int i = m - 1; i >= 0; --i) {
                        bots[i] = stack.pop();
                    }

                    for (int i = 0; i < m; ++i) {
                        stack.push(bots[i]);
                    }
                    for (int i = 0; i < n; ++i) {
                        stack.push(tops[i]);
                    }
                    for (int i = 0; i < m; ++i) {
                        stack.push(bots[i]);
                    }
                }
            );
        }

        return null;
    }

    public static Environment generateReplEnv() {
        Environment stdLib = generateStdLib();

        stdLib.put(
            "exit",
            (_info, _stack, _env) -> System.exit(0)
        );

        return stdLib;
    }

    // TODO: stack trace
    public static Environment generateStdLib() {
        Environment stdLib = new Environment();

        // Stack operations
        stdLib.put(
            "reverse",
            (info, stack, env) -> env.find(stack.size() + "reverse").getValue().accept(info, stack, env)
        );

        // Arithmetics
        stdLib.put(
            "+",
            (info, stack, _env) -> {
                if (stack.size() < 2) {
                    Reporter.notEnoughArgs(info, 2, stack);
                    throw new RuntimeException();
                }
                stack.push(stack.pop() + stack.pop());
            }
        );
        stdLib.put(
            "-",
            (info, stack, _env) -> {
                if (stack.size() < 2) {
                    Reporter.notEnoughArgs(info, 2, stack);
                    throw new RuntimeException();
                }
                double second = stack.pop();
                stack.push(stack.pop() - second);
            }
        );
        stdLib.put(
            "*",
            (info, stack, _env) -> {
                if (stack.size() < 2) {
                    Reporter.notEnoughArgs(info, 2, stack);
                    throw new RuntimeException();
                }
                stack.push(stack.pop() * stack.pop());
            }
        );
        stdLib.put(
            "/",
            (info, stack, env) -> {
                env.find("divmod").getValue().accept(info, stack, env);
                env.find("1drop").getValue().accept(info, stack, env);
            }
        );
        stdLib.put(
            "%",
            (info, stack, env) -> {
                env.find("divmod").getValue().accept(info, stack, env);
                env.find("swap").getValue().accept(info, stack, env);
                env.find("1drop").getValue().accept(info,stack, env);
            }
        );
        stdLib.put(
            "divmod",
            (info, stack, _env) -> {
                if (stack.size() < 2) {
                    Reporter.notEnoughArgs(info, 2, stack);
                    throw new RuntimeException();
                }
                double second = stack.pop();
                double first = stack.pop();
                stack.push(first / second);
                stack.push(first % second);
            }
        );
        stdLib.put(
            "pow",
            (info, stack, _env) -> {
                if (stack.size() < 2) {
                    Reporter.notEnoughArgs(info, 2, stack);
                    throw new RuntimeException();
                }
                double second = stack.pop();
                stack.push(Math.pow(stack.pop(), second));
            }
        );
        stdLib.put(
            "sqrt",
            (info, stack, _env) -> {
                if (stack.size() < 1) {
                    Reporter.notEnoughArgs(info, 1, stack);
                    throw new RuntimeException();
                }
                stack.push(Math.sqrt(stack.pop()));
            }
        );

        // Trigonometry
        stdLib.put(
            "sin",
            (info, stack, _env) -> {
                if (stack.size() < 1) {
                    Reporter.notEnoughArgs(info, 1, stack);
                    throw new RuntimeException();
                }
                stack.push(Math.sin(stack.pop()));
            }
        );
        stdLib.put(
            "cos",
            (info, stack, _env) -> {
                if (stack.size() < 1) {
                    Reporter.notEnoughArgs(info, 1, stack);
                    throw new RuntimeException();
                }
                stack.push(Math.cos(stack.pop()));
            }
        );

        // Comparisons
        stdLib.put(
            "<",
            (info, stack, _env) -> {
                if (stack.size() < 2) {
                    Reporter.notEnoughArgs(info, 2, stack);
                    throw new RuntimeException();
                }
                double second = stack.pop();
                stack.push(stack.pop() < second ? 1.0 : 0.0);
            }
        );
        stdLib.put(
            ">",
            (info, stack, _env) -> {
                if (stack.size() < 2) {
                    Reporter.notEnoughArgs(info, 2, stack);
                    throw new RuntimeException();
                }
                double second = stack.pop();
                stack.push(stack.pop() > second ? 1.0 : 0.0);
            }
        );

        // Debugging
        stdLib.put(
            "print",
            (info, stack, _env) -> {
                if (stack.size() < 1) {
                    Reporter.notEnoughArgs(info, 1, stack);
                    throw new RuntimeException();
                }
                System.out.println(stack.pop());
            }
        );
        return stdLib;
    }
}
