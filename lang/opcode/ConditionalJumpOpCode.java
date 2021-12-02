package lang.opcode;

import java.util.function.BiPredicate;

import lang.Environment;
import lang.util.Stack;

public class ConditionalJumpOpCode implements OpCode {
    private BiPredicate<Stack<Double>, Environment> toJumpTest;
    private int instructionsToSkip;

    public ConditionalJumpOpCode(BiPredicate<Stack<Double>, Environment> toJumpTest, int instructionsToSkip) {
        this.toJumpTest = toJumpTest;
        this.instructionsToSkip = instructionsToSkip;
    }

    public BiPredicate<Stack<Double>, Environment> toJumpTest() {
        return toJumpTest;
    }


    @Override
    public int nextInstruction(int currInstruction) {
        return currInstruction + instructionsToSkip + 1;
    }
}
