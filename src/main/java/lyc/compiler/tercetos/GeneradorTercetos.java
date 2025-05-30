package lyc.compiler.tercetos;

import lyc.compiler.model.Terceto;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GeneradorTercetos {
    private List<Terceto> tercetos;
    private Stack<String> pila;
    private int indice;

    public GeneradorTercetos() {
        this.tercetos = new ArrayList<>();
        this.pila = new Stack<>();
        this.indice = 0;
    }

    public void apilar(String valor) {
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            pila.push(valor);
        } else {
            pila.push(valor);
        }
    }

    public String desapilar() {
        return !pila.isEmpty() ? pila.pop() : null;
    }

    public Terceto crearTerceto(String operador, String op1, String op2) {
        Terceto terceto = new Terceto(operador, op1, op2, indice++);
        tercetos.add(terceto);
        return terceto;
    }

    public Terceto crearTercetoOperacion(String operador) {
        String op2 = desapilar();
        String op1 = desapilar();
        Terceto terceto = crearTerceto(operador, op1, op2);
        apilar(terceto.toReference());
        return terceto;
    }

    public Terceto crearTercetoAsignacion() {
        String valor = desapilar();
        String id = desapilar();
        return crearTerceto(":=", id, valor);
    }

    public Terceto crearTercetoComparacion(String operador) {
        String op2 = desapilar();
        String op1 = desapilar();
        return crearTerceto(operador, op1, op2);
    }

    public void crearTercetoBranch(String tipoSalto, String etiquetaDestino) {
        crearTerceto(tipoSalto, etiquetaDestino, "_");
    }

    public List<Terceto> getTercetos() {
        return tercetos;
    }

    public int getIndiceActual() {
        return indice;
    }

    public void actualizarTerceto(int indice, String operador, String op1, String op2) {
        if (indice >= 0 && indice < tercetos.size()) {
            tercetos.set(indice, new Terceto(operador, op1, op2, indice));
        }
    }
} 