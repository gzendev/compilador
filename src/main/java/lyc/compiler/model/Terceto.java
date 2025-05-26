package lyc.compiler.model;

public class Terceto {
    private String operador;
    private String operando1;
    private String operando2;
    private int numero;

    public Terceto(String operador, String operando1, String operando2, int numero) {
        this.operador = operador;
        this.operando1 = operando1;
        this.operando2 = operando2;
        this.numero = numero;
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

    public int getNumero() {
        return numero;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public void setOperando1(String operando1) {
        this.operando1 = operando1;
    }

    public void setOperando2(String operando2) {
        this.operando2 = operando2;
    }

    @Override
    public String toString() {
        return String.format("[%d] (%s, %s, %s)", numero, operador, operando1, operando2);
    }

    public String toReference() {
        return "[" + numero + "]";
    }
} 