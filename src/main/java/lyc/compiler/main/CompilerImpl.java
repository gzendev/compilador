package lyc.compiler.main;

import java.io.IOException;
import java.io.Reader;
import lyc.compiler.factories.FileFactory;
import lyc.compiler.factories.ParserFactory;
import lyc.compiler.files.FileOutputWriter;
import lyc.compiler.files.IntermediateCodeGenerator;
import lyc.compiler.files.SymbolTableGenerator;

public class CompilerImpl {
    private static CompilerImpl compilerImpl;
    private CompilerImpl() {}

    public static CompilerImpl getInstance() {
        if (compilerImpl == null)
            compilerImpl = new CompilerImpl();
        return compilerImpl;
    }

    public void main(String[] args) throws IOException, Exception {
        try (Reader reader = FileFactory.create(args[0])) {
            var parser = ParserFactory.create(reader);
            parser.getClass().getMethod("parse").invoke(parser);
            var symbolsList = parser.getClass().getMethod("getSymbolsList").invoke(parser);
            var tercetosList = parser.getClass().getMethod("getTercetosList").invoke(parser);
            FileOutputWriter.writeOutput("symbol-table.txt", new SymbolTableGenerator((java.util.List<lyc.compiler.symboltable.Symbol>) symbolsList));
            FileOutputWriter.writeOutput("intermediate-code.txt", new IntermediateCodeGenerator((java.util.List<lyc.compiler.terceto.Terceto>) tercetosList));
            FileOutputWriter.writeOutput("final.asm", new lyc.compiler.files.AsmCodeGenerator((java.util.List<lyc.compiler.terceto.Terceto>) tercetosList, (java.util.List<lyc.compiler.symboltable.Symbol>) symbolsList));
            // El assembler se puede agregar aquí si tienes la lógica
        }
    }
} 