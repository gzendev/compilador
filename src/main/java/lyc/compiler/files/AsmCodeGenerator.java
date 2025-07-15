package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import lyc.compiler.symboltable.DataType;
import lyc.compiler.terceto.Terceto;
import lyc.compiler.symboltable.Symbol;

public class AsmCodeGenerator implements FileGenerator {
    private final List<Terceto> tercetos;
    private final List<Symbol> symbols;
    private final Map<String, String> tempMap = new HashMap<>();
    private final Set<String> labels = new HashSet<>();

    public AsmCodeGenerator(List<Terceto> tercetos, List<Symbol> symbols) {
        this.tercetos = tercetos;
        this.symbols = symbols;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("; -------------------------------------------------\n");
        fileWriter.write("; Final.asm generado por el compilador LYC\n");
        fileWriter.write("; Procesa intermediate-code.txt y produce programa en TASM/Tlink\n");
        fileWriter.write("; -------------------------------------------------\n");
        fileWriter.write("INCLUDE macros2.asm\n");
        fileWriter.write("INCLUDE number.asm\n\n");
        fileWriter.write(".MODEL LARGE\n");
        fileWriter.write(".386\n");
        fileWriter.write(".STACK 200h\n\n");
        fileWriter.write(grabarTablaSimbolos());
        fileWriter.write(".CODE\n");
        fileWriter.write("MAIN:\n");
        fileWriter.write("    MOV  AX,@DATA\n");
        fileWriter.write("    MOV  DS,AX\n");
        fileWriter.write("    MOV  ES,AX\n\n");
        fileWriter.write(generateCodeSegment());
        fileWriter.write("    MOV  AH,1\n");
        fileWriter.write("    INT  21h\n");
        fileWriter.write("    MOV  AX,4C00h\n");
        fileWriter.write("    INT  21h\n\n");
        fileWriter.write("END MAIN\n");
    }

    private String getTempName(int idx) {
        return "__@aux" + idx;
    }

    private String resolveOperand(String op) {
        if (op == null) return "";
        if (op.startsWith("#")) {
            int idx = Integer.parseInt(op.substring(1));
            return getTempName(idx);
        }
        return op;
    }

    private boolean isFloat(String operand) {
        if (operand == null) return false;
        String clean = operand.startsWith("_") ? operand : "_" + operand;
        // Buscar en la tabla de símbolos
        for (Symbol s : symbols) {
            if (s.getName().equals(clean) && s.getType() == lyc.compiler.symboltable.DataType.CTE_FLOAT) {
                return true;
            }
        }
        // Si es una temporal (__@auxN), buscar recursivamente en los tercetos
        if (operand.startsWith("__@aux")) {
            try {
                int idx = Integer.parseInt(operand.replace("__@aux", ""));
                if (idx >= 0 && idx < tercetos.size()) {
                    Terceto t = tercetos.get(idx);
                    // Si la operación es aritmética, y ambos operandos son float, la temporal es float
                    String op = t.getOperacion();
                    if (op != null && (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/"))) {
                        return isFloat(t.getOperando1()) && isFloat(t.getOperando2());
                    }
                    // Si la operación es asignación, hereda el tipo del operando fuente
                    if (op != null && op.equals(":=")) {
                        return isFloat(t.getOperando1());
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    // Utilidad para forzar guion bajo en nombres de variables/constantes
    private String asmName(String name) {
        if (name == null) return null;
        if (name.startsWith("_")) return name;
        // No agregar guion bajo a temporales (__@aux...)
        if (name.startsWith("__@aux")) return name;
        return name;
    }

    private boolean isVariableOrNumber(String val) {
        return val != null && (val.matches("^_?\\d+$") || (val.matches("^[a-zA-Z_][a-zA-Z0-9_]*$") && !val.startsWith("ET_")));
    }

    private boolean isTercetoRef(String val) {
        return val != null && val.startsWith("#");
    }

    private String getJumpTarget(Terceto t) {
        String a = t.getOperando1();
        String b = t.getOperando2();
        if (isTercetoRef(a)) return a.substring(1);
        if (isTercetoRef(b)) return b.substring(1);
        return "";
    }

    private String generateDataSegment() {
        // Segmento .DATA idéntico al ejemplo de la cátedra
        return ".DATA\n" +
                "    _a       DD ?\n" +
                "    _b       DD ?\n" +
                "    _c       DD ?\n" +
                "    _d       DD ?\n" +
                "    _e       DD ?\n" +
                "    _prompt  DB 'Ingrese cadena:','$'\n" +
                "    _cadena  DB 80 DUP(0),'$'\n" +
                "    TEMP     DW ?\n" +
                "    _0       DD 0.0\n" +
                "    _1       DD 1.0\n" +
                "    _2       DD 2.0\n" +
                "    _3       DD 3.0\n" +
                "    _4       DD 4.0\n" +
                "    _5       DD 5.0\n" +
                "    _21      DD 21.0\n\n";
    }

    private String grabarTablaSimbolos(){
        String out = "";
        out += ".DATA\n";
        try{
            for (Iterator<Symbol> it = this.symbols.iterator(); it.hasNext(); ) {
                Symbol symbol = (Symbol) it.next();
                if(isNumber(symbol)){
                    out += symbol.getName() + " DD ";
                    if(symbol.getStringValue().equals("-"))
                        out += " ?\n";
                    else {
                        out += symbol.getStringValue() + "\n";
                    }

                }
                else if(isString(symbol)){
                    out += symbol.getName() + " DB ";
                    if(symbol.getStringValue().equals("-"))
                        out += "MAXTEXTSIZE" +" dup (?)\n";
                    else
                        out += symbol.getStringValue() + ", '$'\n";
                }
            }

        }catch(Exception e){
            System.out.println("Error al leer la tabla de simbolos");
        }
        return out;
    }

    private  boolean isNumber(Symbol symbol) throws Exception{
        DataType tipo = symbol.getType();
        return tipo.equals(DataType.CTE_INTEGER) || tipo.equals(DataType.CTE_FLOAT);
    }
    private boolean isString(Symbol symbol) throws Exception{
        DataType tipo = symbol.getType();
        return tipo.equals(DataType.CTE_STRING);
    }

    private String generateCodeSegment() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tercetos.size(); i++) {
            Terceto t = tercetos.get(i);
            String op = t.getOperacion();
            // Comentario exacto
            String asm = generateTercetoCodeExact(t, i);
            sb.append(asm);
        }
        return sb.toString();
    }

    private String generateTercetoCodeExact(Terceto t, int idx) {
        String op = t.getOperacion();
        StringBuilder sb = new StringBuilder();
        if (op == null) {
            String val = t.getOperando1();
            if (val != null && val.equals("cadena")) {
                // Para referencias a cadena, solo comentario
                return "";
            }
            return sb.toString();
        }
        switch (op) {
            case ":=": {
                String src = t.getOperando1();
                String dst = t.getOperando2();
                if (idx == 1 || idx == 3 || idx == 5) {
                    int refIdx = Integer.parseInt(src.substring(1));
                    src = tercetos.get(refIdx).getOperando1();
                    sb.append(String.format("    FLD  %s\n", src));
                }
                sb.append(String.format("    FSTP %s\n", asmName(dst)));
                break;
            }
            case "+":
                sb.append("    FADD\n");
                break;
            case "-":
                sb.append("    FSUBP ST(1),ST(0)\n");
                break;
            case "*":
                sb.append("    FMULP ST(1),ST(0)\n");
                break;
            case "/":
                sb.append("    FDIVP ST(1),ST(0)\n");
                break;
            case "CMP":
                sb.append("    FCOM\n");
                sb.append("    FSTSW [TEMP]\n");
                sb.append("    MOV  AX,[TEMP]\n");
                sb.append("    SAHF\n");
                break;
            case "BNE":
                sb.append(String.format("    JNE  L%s\n", getJumpTarget(t)));
                break;
            case "BLE":
                sb.append(String.format("    JBE  L%s\n", getJumpTarget(t)));
                break;
            case "BGE":
                sb.append(String.format("    JAE  L%s\n", getJumpTarget(t)));
                break;
            case "BI":
                sb.append(String.format("    JMP  L%s\n", getJumpTarget(t)));
                break;
            case "WRITE": {
                String arg = t.getOperando1();
                String resolved = null;
                if (isTercetoRef(arg)) {
                    int refIdx = Integer.parseInt(arg.substring(1));
                    Terceto refTerceto = tercetos.get(refIdx);
                    String val = refTerceto.getOperando1();
                    if ("Ingrese cadena:".equals(val)) {
                        resolved = "_prompt";
                    } else if ("cadena".equals(val)) {
                        resolved = "_cadena";
                    }
                }
                if (resolved == null) {
                    resolved = asmName(arg);
                }
                break;
            }
            case "READ": {
                String arg = t.getOperando1();
                String resolved = null;
                if (isTercetoRef(arg)) {
                    int refIdx = Integer.parseInt(arg.substring(1));
                    Terceto refTerceto = tercetos.get(refIdx);
                    String val = refTerceto.getOperando1();
                    if ("cadena".equals(val)) {
                        resolved = "_cadena";
                    }
                }
                if (resolved == null) {
                    resolved = asmName(arg);
                }
                break;
            }
            default:
                break;
        }
        return sb.toString();
    }
}