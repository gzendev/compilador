package lyc.compiler.asm;

import lyc.compiler.symboltable.DataType;
import lyc.compiler.symboltable.Symbol;
import lyc.compiler.symboltable.SymbolTableManager;
import lyc.compiler.terceto.IntermediateCodeManager;
import lyc.compiler.terceto.Terceto;
import java.util.HashMap;
import java.util.List;

public class AsmCodeManager {

    private static final HashMap<String, String> operadorToAsm;
    static {
        operadorToAsm = new HashMap<String, String>();
        operadorToAsm.put("+", "FADD");
        operadorToAsm.put("-", "FSUB");
        operadorToAsm.put("*", "FMUL");
        operadorToAsm.put("/", "FDIV");
        operadorToAsm.put("BEQ", "JE");
        operadorToAsm.put("BNE", "JNE");
        operadorToAsm.put("BLT", "JB");
        operadorToAsm.put("BLE", "JBE");
        operadorToAsm.put("BGT", "JA");
        operadorToAsm.put("BGE", "JAE");
    };

    public static String operadorToAsm(String operador)
    {
        return operadorToAsm.get(operador);
    }

    public static String generaAssembler(SymbolTableManager symbolTableManager, IntermediateCodeManager intCodeManager)
    {
        String result = "";
        String header = strAsmHeader();
        result = header + "\n";
        symbolTableManager.cargarAuxiliares(intCodeManager.getAuxiliares(DataType.CTE_FLOAT), DataType.CTE_FLOAT);
        symbolTableManager.cargarAuxiliares(intCodeManager.getAuxiliares(DataType.CTE_STRING), DataType.CTE_STRING);
        result += symbolTableToAsm(symbolTableManager) + "\n";
        result += strAsmCodeHeader() + "\n";
        String tercetosAsm = "";
        List<Terceto> tercetosList = intCodeManager.getTercetosList();
        for (Terceto terceto : tercetosList)
        {
            tercetosAsm += Terceto.tercetoToAsm(terceto, intCodeManager);
        }
        result += tercetosAsm + "\n";
        result += strAsmEnd();
        return result;
    }

    private static String strAsmHeader()
    {
        return "include macros2.asm\n" +
                "include number.asm\n\n" +
                ".MODEL LARGE\n" +
                ".386\n" +
                ".STACK 200h\n";
    }

    private static String strAsmCodeHeader()
    {
        return ".CODE\n" +
                "MOV AX,@DATA\n" +
                "MOV DS,AX\n" +
                "MOV ES,AX\n" +
                "FINIT;\n";
    }

    private static String strAsmEnd()
    {
        return "FINAL:\n\t" +
                    "MOV AX,4c00h\n\t" +
                    "INT 21h\n" +
                "END;\n";
    }

    private static String symbolTableToAsm(SymbolTableManager symbolTableManager)
    {
        String result = ".DATA\n";
        List<Symbol> symbolList = symbolTableManager.getSymbolsList();
        for (Symbol symbol : symbolList)
        {
            result += symbol.getNameForAsm() + "\t" + symbol.getAsmType() + "\t" + symbol.getValueForAsm() + "\n";
        }
        return result;
    }

    public static String cteToVarNameCte(String cte)
    {
        String cteRes = cte.replace(".", "_");
        cteRes = cteRes.replace("-", "m");
        return "cte_" + cteRes;
    }
}
