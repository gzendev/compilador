package lyc.compiler.model;

public class SymbolTableEntry {
    private String nombre;
    private String tipoDato;
    private String valor;
    private int longitud;

    public SymbolTableEntry(String nombre, String tipoDato, String valor, int longitud) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.valor = valor;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getValor() {
        return valor;
    }

    public int getLongitud() {
        return longitud;
    }

    @Override
    public String toString() {
        return String.format("%-30s|%-15s|%-30s|%-10d", nombre, tipoDato, valor, longitud);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SymbolTableEntry other = (SymbolTableEntry) obj;
        return nombre.equals(other.nombre);
    }
} 