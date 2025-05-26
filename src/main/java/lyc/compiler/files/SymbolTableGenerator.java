package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lyc.compiler.model.SymbolTableEntry;

public class SymbolTableGenerator implements FileGenerator {
    private List<SymbolTableEntry> symbolTable;

    public SymbolTableGenerator(List<SymbolTableEntry> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        // Escribir encabezado
        fileWriter.write(String.format("%-30s|%-15s|%-30s|%-10s\n", "NOMBRE", "TIPO", "VALOR", "LONGITUD"));
        fileWriter.write("-".repeat(85) + "\n");
        
        // Escribir entradas
        for (SymbolTableEntry entry : symbolTable) {
            fileWriter.write(entry.toString() + "\n");
        }
    }
}
