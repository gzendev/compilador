package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import lyc.compiler.symboltable.Symbol;

public class SymbolTableGenerator implements FileGenerator {
    private final List<Symbol> tablaSimbolos;

    public SymbolTableGenerator(List<Symbol> tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        LinkedHashSet<Symbol> set = new LinkedHashSet<>(tablaSimbolos);
        fileWriter.write("NOMBRE|TIPODATO|VALOR|LONGITUD\n");
        for (Symbol s : set) {
            fileWriter.write(s.toString() + "\n");
        }
    }

    public List<Symbol> getTablaSimbolos() {
        return tablaSimbolos;
    }
}