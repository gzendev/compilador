package lyc.compiler.model;

import java.io.Serial;

public class UnknownVariableException extends CompilerException {

    @Serial
    private static final long serialVersionUID = -8839023592847976068L;

    public UnknownVariableException(String unknownInput) {
        super("The variable [" + unknownInput + "]" + " does not exist");
    }
}
