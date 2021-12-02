package lang.opcode;

import java.util.function.BiConsumer;

import lang.Environment;
import lang.util.Stack;

public class InvokeFunctionOpCode implements OpCode {
    private BiConsumer<Stack<Double>, Environment> fn;

    public InvokeFunctionOpCode(BiConsumer<Stack<Double>, Environment> fn) {
        this.fn = fn;
    }

    public BiConsumer<Stack<Double>, Environment> fn() {
        return fn;
    }

    @Override
    public int nextInstruction(int currInstruction) {
        return currInstruction + 1;
    }
}
