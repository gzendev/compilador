package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import lyc.compiler.tercetos.GeneradorTercetos;
import lyc.compiler.model.Terceto;

public class IntermediateCodeGenerator implements FileGenerator {
    private GeneradorTercetos generador;

    public IntermediateCodeGenerator(GeneradorTercetos generador) {
        this.generador = generador;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Terceto terceto : generador.getTercetos()) {
            fileWriter.write(terceto.toString() + "\n");
        }
    }
}
