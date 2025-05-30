package lyc.compiler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private Map<String, SymbolTableEntry> symbols;
    private static final int MAX_ID_LENGTH = 32;
    private static final double MAX_FLOAT = 3.40282347e+38;
    private static final double MIN_FLOAT = -3.40282347e+38;
    private static final int MAX_INT = 32767;
    private static final int MIN_INT = -32768;

    public SymbolTable() {
        this.symbols = new HashMap<>();
    }

    public void addVariable(String name, String type) throws SemanticException {
        validateIdentifierLength(name);
        if (symbols.containsKey(name)) {
            throw new SemanticException("Variable '" + name + "' already declared");
        }
        symbols.put(name, new SymbolTableEntry(name, type, null, 0));
    }

    public void assignValue(String name, Object value) throws SemanticException {
        SymbolTableEntry entry = symbols.get(name);
        if (entry == null) {
            throw new SemanticException("Variable '" + name + "' not declared");
        }

        // If value is a variable name, get its value from the symbol table
        if (value instanceof String && symbols.containsKey(value.toString())) {
            SymbolTableEntry valueEntry = symbols.get(value.toString());
            if (!entry.getTipoDato().equalsIgnoreCase(valueEntry.getTipoDato())) {
                throw new SemanticException("Type mismatch: cannot assign " + valueEntry.getTipoDato() + " to " + entry.getTipoDato());
            }
            entry.setValor(valueEntry.getValor());
            entry.setLongitud(valueEntry.getLongitud());
            return;
        }

        validateTypeAndValue(entry.getTipoDato(), value);
        
        // Convert value to appropriate format and update length
        String strValue = value.toString();
        int length = 0;
        
        if (value instanceof String && ((String) value).startsWith("\"")) {
            // For strings, length is number of characters without quotes
            length = ((String) value).length() - 2;
            entry.setValor(strValue);
        } else {
            // For numbers, convert to appropriate format
            try {
                if (entry.getTipoDato().equalsIgnoreCase("int")) {
                    int intValue = Integer.parseInt(strValue);
                    entry.setValor(String.valueOf(intValue));
                } else if (entry.getTipoDato().equalsIgnoreCase("float")) {
                    double floatValue = Double.parseDouble(strValue);
                    entry.setValor(String.valueOf(floatValue));
                }
            } catch (NumberFormatException e) {
                throw new SemanticException("Invalid number format: " + strValue);
            }
        }
        
        entry.setLongitud(length);
    }

    private void validateIdentifierLength(String name) throws SemanticException {
        if (name.length() > MAX_ID_LENGTH) {
            throw new SemanticException("Identifier '" + name + "' exceeds maximum length of " + MAX_ID_LENGTH);
        }
    }

    private void validateTypeAndValue(String type, Object value) throws SemanticException {
        if (value == null) return;

        switch (type.toLowerCase()) {
            case "int":
                validateInteger(value);
                break;
            case "float":
                validateFloat(value);
                break;
            case "string":
                validateString(value);
                break;
            default:
                throw new SemanticException("Unknown type: " + type);
        }
    }

    private void validateInteger(Object value) throws SemanticException {
        try {
            int intValue;
            if (value instanceof String) {
                intValue = Integer.parseInt(value.toString());
            } else if (value instanceof Integer) {
                intValue = (Integer) value;
            } else {
                throw new SemanticException("Cannot convert " + value.getClass().getSimpleName() + " to Integer");
            }

            if (intValue < MIN_INT || intValue > MAX_INT) {
                throw new SemanticException("Integer value out of range [" + MIN_INT + ", " + MAX_INT + "]");
            }
        } catch (NumberFormatException e) {
            throw new SemanticException("Invalid integer value: " + value);
        }
    }

    private void validateFloat(Object value) throws SemanticException {
        try {
            double floatValue;
            if (value instanceof String) {
                floatValue = Double.parseDouble(value.toString());
            } else if (value instanceof Double) {
                floatValue = (Double) value;
            } else {
                throw new SemanticException("Cannot convert " + value.getClass().getSimpleName() + " to Float");
            }

            if (floatValue < MIN_FLOAT || floatValue > MAX_FLOAT) {
                throw new SemanticException("Float value out of range [" + MIN_FLOAT + ", " + MAX_FLOAT + "]");
            }
        } catch (NumberFormatException e) {
            throw new SemanticException("Invalid float value: " + value);
        }
    }

    private void validateString(Object value) throws SemanticException {
        if (!(value instanceof String)) {
            throw new SemanticException("Value must be a string");
        }
    }

    public List<SymbolTableEntry> getEntries() {
        return new ArrayList<>(symbols.values());
    }

    public String getType(String name) throws SemanticException {
        SymbolTableEntry entry = symbols.get(name);
        if (entry == null) {
            throw new SemanticException("Variable '" + name + "' not declared");
        }
        return entry.getTipoDato();
    }

    public Object getValue(String name) throws SemanticException {
        SymbolTableEntry entry = symbols.get(name);
        if (entry == null) {
            throw new SemanticException("Variable '" + name + "' not declared");
        }
        return entry.getValor();
    }
} 