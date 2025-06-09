package lyc.compiler.model;

import java.io.Serial;

public class DuplicatedVariableException extends CompilerException {

    @Serial
    private static final long serialVersionUID = -8839023592847976068L;

    public DuplicatedVariableException(String varName) {
        super("The variable name [" + varName + "]" + " is already in use");
    }
}
