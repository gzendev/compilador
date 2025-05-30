package lyc.compiler.model;

public class Terceto {
    private String operador;
    private String operando1;
    private String operando2;
    private int indice;

    public Terceto(String operador, String operando1, String operando2, int indice) {
        this.operador = operador;
        this.operando1 = operando1;
        this.operando2 = operando2;
        this.indice = indice;
    }

    public String getOperador() {
        return operador;
    }

    public String getOperando1() {
        return operando1;
    }

    public String getOperando2() {
        return operando2;
    }

    public int getIndice() {
        return indice;
    }

    @Override
    public String toString() {
        return "[" + indice + "] (" + operador + ", " + operando1 + ", " + operando2 + ")";
    }

    public String toReference() {
        return "[" + indice + "]";
    }
} 