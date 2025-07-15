package lyc.compiler.terceto;

import lyc.compiler.asm.AsmCodeManager;

public class Terceto {
    private String primerElemento;
    private String segundoElemento;
    private String tercerElemento;
    private TercetoType tipo;
    private String resultAux;

    public Terceto(String operador, String operando1, String operando2) {
        this.primerElemento = operador;
        this.segundoElemento = operando1;
        this.tercerElemento = operando2;
        this.tipo = TercetoType.FULL;
    }

    public Terceto(String operador, String operando1, String operando2, String resultAux) {
        this.primerElemento = operador;
        this.segundoElemento = operando1;
        this.tercerElemento = operando2;
        this.resultAux = resultAux;
        this.tipo = TercetoType.FULL;
    }

    public Terceto(String operador, String operando1) {
        this.primerElemento = operador;
        this.segundoElemento = operando1;
        this.tercerElemento = null;
        this.tipo = TercetoType.SEMI;
    }

    public Terceto(String valor) {
        this.primerElemento = valor;
        this.segundoElemento = null;
        this.tercerElemento = null;
        this.tipo = TercetoType.SINGLE_VALUE;
    }


    public String getOperacion() {
        if (this.tipo != TercetoType.SINGLE_VALUE) {
            return primerElemento;
        }

        return null;
    }

    public void setOperacion(String operacion) {
        if (this.tipo != TercetoType.SINGLE_VALUE) {
            this.primerElemento = operacion;
        } else {
            this.segundoElemento = this.primerElemento;
            this.primerElemento = operacion;
            this.tipo = TercetoType.SEMI;
        }
    }

    public String getOperando1() {
        if (this.tipo != TercetoType.SINGLE_VALUE) {
            return this.segundoElemento;
        }

        return this.primerElemento;
    }

    private String getOperando1(IntermediateCodeManager intCodeManager) {
        String operando = this.getOperando1();

        if (operando.startsWith("#") && operando.length() > 1) {
            Integer numeroTerceto = Integer.valueOf(operando.substring(1));
            operando = intCodeManager.getOperandoFromNumeroTerceto(numeroTerceto);
        }

        return operando;
    }

    public void setOperando1(String operando1) {
        if (this.tipo != TercetoType.SINGLE_VALUE) {
            this.segundoElemento = operando1;
        } else {
            this.primerElemento = operando1;
        }
    }

    public String getOperando2() {
        if (this.tipo == TercetoType.FULL) {
            return this.tercerElemento;
        }

        return null;
    }

    public void setOperando2(String operando2) {
        if (this.tipo != TercetoType.SINGLE_VALUE) {
            this.tercerElemento = operando2;
        }
    }

    public TercetoType getType() {
        return this.tipo;
    }

    public String getResultAux() {
        return this.resultAux;
    }

    public Boolean esCte() {
        return getType() == TercetoType.SINGLE_VALUE &&
                getOperando1().matches("^-?\\d+(\\.\\d+)?$");
    }

    public Integer getCte() {
        return Integer.valueOf(getOperando1());
    }

    public Boolean esEtiqueta() {
        return getType() == TercetoType.SINGLE_VALUE &&
                getOperando1().matches("^ET_\\w+$");
    }

    private String operandoToAsm()
    {
        return AsmCodeManager.operadorToAsm(this.getOperacion());
    }

    public static String tercetoToAsm(Terceto terceto, IntermediateCodeManager intCodeManager)
    {
        String asm = "";

        if(terceto.getType() == TercetoType.SINGLE_VALUE)
        {
            String operando = terceto.getOperando1(intCodeManager);
            if(terceto.esCte())
            {
                operando = AsmCodeManager.cteToVarNameCte(operando);
            }
            if(terceto.esEtiqueta())
            {
                asm = "\n" + operando + ":" + "\n";
            }
            else
            {
                asm = "FLD " + operando + "\n";
            }
        }

        if(terceto.getType() == TercetoType.SEMI)
        {
            if(IntermediateCodeManager.esOperadorDeSalto(terceto.getOperacion()))
            {
                asm = terceto.operandoToAsm();
                asm += " " + terceto.getOperando1(intCodeManager)+ "\n";
            }

            if(terceto.getOperacion().equals("ESCRIBIR") || terceto.getOperacion().equals("WRITE"))
            {
                asm = "FSTP varSalida\n";
                asm += "PUSH dx\n" + "PUSH ax\n" +
                        "\n" + "LEA dx, varSalida\n" + "MOV ah, 9\n" + "INT 21h\n" +
                        "\n" + "POP ax\n" + "POP dx" + "\n";
            }

            if(terceto.getOperacion().equals("LEER") || terceto.getOperacion().equals("READ"))
            {
                asm = "FSTP varEntrada\n";
                asm += "LEA dx, varEntrada\n" + "MOV ah, 0Ah\n" + "INT 21h\n" +
                        "\n" + "MOV ah, 4Ch\n" + "INT 21h" + "\n";
            }

            if(terceto.getOperacion().equals("ESPRIMO"))
            {
                asm = "esPrimo\n";
                asm += "FLD " + "1" + "\n";
            }

        }

        if(terceto.getType() == TercetoType.FULL)
        {
            if(IntermediateCodeManager.esOperacionMatematica(terceto.getOperacion()))
            {
                asm = terceto.operandoToAsm() + "\n";
            }

            if(terceto.getOperacion().equals(":="))
            {
                asm += "FSTP " + terceto.getOperando2() + "\n";
            }

            if(terceto.getOperacion().equals("CMP"))
            {
                asm = "FXCH" + "\n";
                asm += "FCOM" + "\n";
                asm += "FSTSW ax" + "\n";
                asm += "SAHF" + "\n";
            }

        }

        return asm;
    }

    @Override
    public String toString() {
        if (this.tipo == TercetoType.SINGLE_VALUE) {
            return "(" + primerElemento + ", " + "_" + ", " + "_" + ")";
        }
        if (this.tipo == TercetoType.SEMI) {
            return "(" + primerElemento + ", " + segundoElemento + ", " + "_" + ")";
        }

        return "(" + primerElemento + ", " + segundoElemento + ", " + tercerElemento + ")";
    }
}