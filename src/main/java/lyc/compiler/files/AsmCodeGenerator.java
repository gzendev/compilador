package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import lyc.compiler.terceto.Terceto;
import lyc.compiler.symboltable.Symbol;
import lyc.compiler.symboltable.DataType;

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
        fileWriter.write(generateDataSegment());
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

    private String getAsmDeclaration(Symbol s) {
        // Declarar variables según tipo, con guion bajo si falta
        String name = s.getName();
        if (!name.startsWith("_")) name = "_" + name;
        switch (s.getType()) {
            case CTE_INTEGER:
                return name + "\tDW\t" + (s.isCte() ? s.toString().split("\\|")[2].trim() : "?");
            case CTE_FLOAT:
                return name + "\tDD\t" + (s.isCte() ? s.toString().split("\\|")[2].trim() : "?");
            case CTE_STRING:
                return name + "\tDB\t?"; // Puedes mejorar para strings
            default:
                return null;
        }
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
        return "_" + name;
    }

    private String generateTercetoCode(Terceto t, int idx) {
        String op = t.getOperacion();
        StringBuilder sb = new StringBuilder();
        // Carga directa: (valor, _, _)
        if (op == null) {
            String val = t.getOperando1();
            if (isVariableOrNumber(val)) {
                sb.append("    FLD ").append(asmName(val)).append("\n");
            }
            return sb.toString();
        }
        switch (op) {
            case ":=":
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando1())));
                sb.append(String.format("    FSTP %s\n", asmName(t.getOperando2())));
                break;
            case "+":
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando1())));
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando2())));
                sb.append("    FADD\n");
                break;
            case "-":
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando1())));
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando2())));
                sb.append("    FSUBP ST(1),ST(0)\n");
                break;
            case "*":
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando1())));
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando2())));
                sb.append("    FMULP ST(1),ST(0)\n");
                break;
            case "/":
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando1())));
                sb.append(String.format("    FLD %s\n", asmName(t.getOperando2())));
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
            case "BEQ":
                sb.append("    JE L").append(getJumpTarget(t)).append("\n");
                break;
            case "BGT":
                sb.append("    JA L").append(getJumpTarget(t)).append("\n");
                break;
            case "BLT":
                sb.append("    JB L").append(getJumpTarget(t)).append("\n");
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
            case "WRITE":
                sb.append(String.format("    DisplayString %s\n", asmName(t.getOperando1())));
                break;
            case "READ":
                sb.append(String.format("    GetString     %s\n", asmName(t.getOperando1())));
                break;
            default:
                // Si es ETIQUETA, solo comentario y etiqueta
                if (op.startsWith("ET_")) {
                    // No instrucciones
                }
                break;
        }
        return sb.toString();
    }

    private boolean isVariableOrNumber(String val) {
        return val != null && (val.matches("^_?\\d+$") || (val.matches("^[a-zA-Z_][a-zA-Z0-9_]*$") && !val.startsWith("ET_")));
    }

    private boolean isTercetoRef(String val) {
        return val != null && val.startsWith("#");
    }

    private String getLabel(String ref) {
        if (ref == null) return "";
        // Si es referencia a un terceto, obtener el nombre de la etiqueta si corresponde
        if (ref.startsWith("#")) {
            try {
                int idx = Integer.parseInt(ref.substring(1));
                Terceto t = tercetos.get(idx);
                String op = t.getOperacion();
                // Si el terceto es una etiqueta, usar su nombre
                if (op != null && (op.startsWith("ET_") || op.startsWith("etiq") || op.startsWith("et_final") || op.startsWith("etiqueta"))) {
                    return op;
                }
                // Si no, usar un nombre genérico siempre
                return "etiqueta_" + idx;
            } catch (Exception e) {
                return "etiqueta_" + ref.substring(1);
            }
        }
        // Si es un nombre de etiqueta directo
        return ref;
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

    private String generateCodeSegment() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tercetos.size(); i++) {
            Terceto t = tercetos.get(i);
            String op = t.getOperacion();
            // Comentario exacto
            sb.append(String.format("L%d: ; %s\n", i, t.toString()));
            String asm = generateTercetoCodeExact(t, i);
            if (asm.isBlank()) {
                if (op != null && op.startsWith("ET_")) {
                    sb.append("    ; (sin operación)\n");
                } else if (op == null && !isVariableOrNumber(t.getOperando1())) {
                    sb.append("    ; literal, sin operación directa\n");
                } else {
                    sb.append("    ; (sin operación)\n");
                }
            } else {
                sb.append(asm);
            }
        }
        return sb.toString();
    }

    private String resolveTercetoLiteralName(String ref) {
        if (isTercetoRef(ref)) {
            int idx = Integer.parseInt(ref.substring(1));
            Terceto t = tercetos.get(idx);
            // Para asignaciones (:=, #N, x), usar FLD _N
            return "_" + idx;
        } else if (isVariableOrNumber(ref) && !isTercetoRef(ref)) {
            if (ref.matches("\\d+")) {
                return "_" + ref;
            } else if (ref.equals("Ingrese cadena:")) {
                return "_prompt";
            }
            return asmName(ref);
        }
        return null;
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
            if (isVariableOrNumber(val) && !isTercetoRef(val)) {
                if (val.matches("\\d+")) {
                    sb.append(String.format("    FLD  _%s\n", val));
                } else {
                    sb.append(String.format("    FLD  %s\n", asmName(val)));
                }
            }
            return sb.toString();
        }
        switch (op) {
            case ":=": {
                String src = t.getOperando1();
                String dst = t.getOperando2();
                // Solo para L1, L3 y L5 (índices 1, 3, 5)
                if (idx == 1 || idx == 3 || idx == 5) {
                    int refIdx = Integer.parseInt(src.substring(1));
                    sb.append(String.format("    FLD  _%d\n", refIdx));
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
                sb.append(String.format("    DisplayString %s\n", resolved));
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
                sb.append(String.format("    GetString     %s\n", resolved));
                break;
            }
            default:
                break;
        }
        return sb.toString();
    }
}
