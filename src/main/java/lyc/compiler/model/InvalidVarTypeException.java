package lyc.compiler.model;

import java.io.Serial;

public class InvalidVarTypeException extends CompilerException {

    @Serial
    private static final long serialVersionUID = -8839023592847976068L;

    public InvalidVarTypeException(String var) {
        super("Invalid type for variable [" + var + "]");
    }
}
