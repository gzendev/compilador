package lyc.compiler.files;

import lyc.compiler.terceto.Terceto;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class IntermediateCodeGenerator implements FileGenerator {

    private List<Terceto> tercetosList;

    public IntermediateCodeGenerator() {
        this.tercetosList = Collections.emptyList();
    }

    public IntermediateCodeGenerator(List<Terceto> tercetos) {
        this.tercetosList = tercetos;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        try {
            if (this.tercetosList.isEmpty()) {
                fileWriter.write("SIN CODIGO INTERMEDIO");
                return;
            }

            fileWriter.write("LISTA DE TERCETOS:" + "\n");
            Integer numTerceto = 0;
            for (Terceto terceto : tercetosList) {
                try {
                    fileWriter.write(numTerceto.toString() + ") " + terceto.toString() + "\n");
                    numTerceto++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
