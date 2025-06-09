package lyc.compiler.files;

import lyc.compiler.symboltable.Symbol;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SymbolTableGenerator implements FileGenerator {

    private List<Symbol> symbolsList;

    public SymbolTableGenerator() {
        this.symbolsList = Collections.emptyList();
    }

    public SymbolTableGenerator(List<Symbol> symbols) {
        this.symbolsList = symbols;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        try {
            if (this.symbolsList.isEmpty()) {
                fileWriter.write("SIN SIMBOLOS");
                return;
            }

            fileWriter.write(String.format("%-20s|%-20s|%-20s|%-20s", "NOMBRE", "TIPODATO", "VALOR", "LONGITUD") + "\n");
            symbolsList.forEach(symbol -> {
                try {
                    fileWriter.write(symbol.toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}