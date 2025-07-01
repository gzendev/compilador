package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lyc.compiler.terceto.Terceto;

public class IntermediateCodeGenerator implements FileGenerator {
    private final List<Terceto> listaTercetos;

    public IntermediateCodeGenerator(List<Terceto> listaTercetos) {
        this.listaTercetos = listaTercetos;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        int idx = 0;
        for (Terceto t : listaTercetos) {
            fileWriter.write(idx + "\t" + t.toString() + "\n");
            idx++;
        }
    }

    public List<Terceto> getListaTercetos() {
        return listaTercetos;
    }
}
