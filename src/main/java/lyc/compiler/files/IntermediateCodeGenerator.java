package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;11import java.util.List;
import lyc.compiler.model.Terceto;

public class IntermediateCodeGenerator implements FileGenerator {
    private List<Terceto> tercetos;

    public IntermediateCodeGenerator(List<Terceto> tercetos) {
        this.tercetos = tercetos;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Terceto terceto : tercetos) {
            fileWriter.write(terceto.toString() + "\n");
        }
    }
}
