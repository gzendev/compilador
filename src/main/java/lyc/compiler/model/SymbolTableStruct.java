package lyc.compiler.model;

import lyc.compiler.utils.StringUtil;

import java.util.Objects;

public class SymbolTableStruct {

    private Object nombre;
    private String tipoDato;
    private Object valor;
    private int longitud;

    public SymbolTableStruct(Object nombre, String tipoDato, Object valor, int longitud) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.valor = valor;
        this.longitud = longitud;
    }

    public Object getNombre() {
        return nombre;
    }

    public void setNombre(Object nombre) {
        this.nombre = nombre;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        String ret = "";
        if (this.nombre != null)
            ret = ret.concat(StringUtil.centrarString(nombre.toString()));
        if (this.tipoDato != null) {
            ret = ret.concat(StringUtil.centrarString(tipoDato));
        }
        if (this.valor != null) {
            ret = ret.concat(StringUtil.centrarString(valor.toString()));
        }
        if (this.longitud > 0) {
            ret = ret.concat(StringUtil.centrarString(String.valueOf(longitud)));
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SymbolTableStruct other = (SymbolTableStruct) obj;

        return (Objects.equals(nombre, other.nombre))
                && (Objects.equals(tipoDato, other.tipoDato))
                && (Objects.equals(valor, other.valor)) && longitud == other.longitud;
    }
}