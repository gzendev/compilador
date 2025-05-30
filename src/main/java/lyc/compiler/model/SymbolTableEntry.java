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

    public String getValor() {
        return valor;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return String.format("%-32s | %-10s | %-20s | %d", nombre, tipoDato, valor != null ? valor : "", longitud);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SymbolTableEntry other = (SymbolTableEntry) obj;
        return nombre.equals(other.nombre);
    }
} 