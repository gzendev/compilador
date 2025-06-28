package lyc.compiler.symboltable;

import lyc.compiler.model.DuplicatedVariableException;
import lyc.compiler.model.InvalidVarTypeException;
import lyc.compiler.model.UnknownVariableException;
import java.util.*;

public class SymbolTableManager {
    private List<Symbol> symbolsList;
    private Map<String, Integer> symbolsPosMap;

    private Integer getSymbolPos(String name) {
        return symbolsPosMap.get(name);
    }

    public Boolean isSymbolInTable(String name) {
        return !(getSymbolPos(name) == null);
    }

    public SymbolTableManager() {
        this.symbolsList = new ArrayList<Symbol>();
        this.symbolsPosMap = new HashMap<String, Integer>();
    }

    public void addSymbol(String name, DataType type) {
        if (!isSymbolInTable(name)) {
            Integer symbolPos = symbolsList.size();
            Symbol symbol = new Symbol(name, type);
            symbolsList.add(symbol);
            symbolsPosMap.put(symbol.getName(), symbolPos);
        }
    }

    public void addSymbol(String name, String value) {
        if (!isSymbolInTable(name)) {
            Integer symbolPos = symbolsList.size();
            Symbol symbol = new Symbol(name, value);
            symbolsList.add(symbol);
            symbolsPosMap.put(symbol.getName(), symbolPos);
        }
    }

    public void addSymbol(String name, Integer value) {
        if (!isSymbolInTable(name)) {
            Integer symbolPos = symbolsList.size();
            Symbol symbol = new Symbol(name, value);
            symbolsList.add(symbol);
            symbolsPosMap.put(symbol.getName(), symbolPos);
        }
    }

    public void addSymbol(String name, Double value) {
        if (!isSymbolInTable(name)) {
            Integer symbolPos = symbolsList.size();
            Symbol symbol = new Symbol(name, value);
            symbolsList.add(symbol);
            symbolsPosMap.put(symbol.getName(), symbolPos);
        }
    }

    public void addVars(ArrayList<String> varsList, DataType type) {
        varsList.forEach(var ->
        {
            this.addSymbol(var, type);
        });
        varsList.clear();
    }

    public void setVarValue(String name, String value) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            symbolsList.get(getSymbolPos(name)).setValue(value);
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public void setVarValue(String name, Integer value) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            symbolsList.get(getSymbolPos(name)).setValue(value);
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public void setVarValue(String name, Double value) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            symbolsList.get(getSymbolPos(name)).setValue(value);
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public Object getValue(String name) throws UnknownVariableException {
        // CORREGIDO: Cambiado 'contains(name)' por 'isSymbolInTable(name)'
        if (isSymbolInTable(name)) {
            Symbol symbol = symbolsList.get(getSymbolPos(name));

            if (symbol.getType() == DataType.CTE_INTEGER) {
                // CORREGIDO: Usar getValue() y castear a Integer
                return (Integer) symbol.getValue();
            } else if (symbol.getType() == DataType.CTE_FLOAT) {
                // CORREGIDO: Usar getValue() y castear a Double
                return (Double) symbol.getValue();
            } else if (symbol.getType() == DataType.CTE_STRING) {
                return symbol.getStringValue();
            }
            return null;
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public Integer getVarLength(String name) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            return symbolsList.get(getSymbolPos(name)).getLength();
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public String getVarValue(String name) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            return symbolsList.get(getSymbolPos(name)).getStringValue();
        } else {
            throw new UnknownVariableException(name);
        }
    }

    private Boolean isVarNumeric(String name) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            return symbolsList.get(getSymbolPos(name)).getType() != DataType.CTE_STRING;
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public void checkVarIsNumeric(String name) throws InvalidVarTypeException, UnknownVariableException {
        if (!isVarNumeric(name)) {
            throw new InvalidVarTypeException(name);
        }
    }

    private Boolean isVarInteger(String name) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            return symbolsList.get(getSymbolPos(name)).getType() == DataType.CTE_INTEGER;
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public void checkVarIsInteger(String name) throws InvalidVarTypeException, UnknownVariableException {
        if (!isVarInteger(name)) {
            throw new InvalidVarTypeException(name);
        }
    }

    private Boolean isVarString(String name) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            return symbolsList.get(getSymbolPos(name)).getType() == DataType.CTE_STRING;
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public void checkVarIsString(String name) throws InvalidVarTypeException, UnknownVariableException {
        if (!isVarString(name)) {
            throw new InvalidVarTypeException(name);
        }
    }

    private Boolean isVarFloat(String name) throws UnknownVariableException {
        if (isSymbolInTable(name)) {
            return symbolsList.get(getSymbolPos(name)).getType() == DataType.CTE_FLOAT;
        } else {
            throw new UnknownVariableException(name);
        }
    }

    public void checkVarIsFloat(String name) throws InvalidVarTypeException, UnknownVariableException {
        if (!isVarFloat(name)) {
            throw new InvalidVarTypeException(name);
        }
    }

    public void checkVarExists(String name) throws UnknownVariableException {
        if (!isSymbolInTable(name)) {
            throw new UnknownVariableException(name);
        }
    }

    public void checkVarDuplicated(String name) throws DuplicatedVariableException {
        if (isSymbolInTable(name)) {
            throw new DuplicatedVariableException(name);
        }
    }

    public void cargarAuxiliares(Queue<String> auxiliares, DataType type) {
        for (String aux : auxiliares) {
            this.addSymbol(aux, type);
        }
    }

    public List<Symbol> getSymbolsList() {
        return symbolsList;
    }

    public String addTemporal(Object value) {
        String tempName = "@TEMP_" + (symbolsList.size());

        if (value instanceof Integer) {
            addSymbol(tempName, (Integer) value);
        }
        else
            if (value instanceof Double) {
                addSymbol(tempName, (Double) value);
            }
            else
                if (value instanceof String) {
                    addSymbol(tempName, (String) value);
                }
                else
                {
                    throw new IllegalArgumentException("Tipo de dato no soportado para variable temporal: " + value.getClass().getName());
                }
        return tempName;
    }
}