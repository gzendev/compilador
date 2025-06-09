package lyc.compiler.utils;

public class StringUtil {

    private static final int LONGITUD_PARA_TABLA = 60;

    public static String centrarString(String texto) {
        if (texto == null) {
            texto = "";
        }

        int longitudTexto = texto.length();

        int espaciosIzquierda = (LONGITUD_PARA_TABLA - longitudTexto) / 2;
        int espaciosDerecha = LONGITUD_PARA_TABLA - longitudTexto - espaciosIzquierda;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espaciosIzquierda; i++) {
            sb.append(' ');
        }
        sb.append(texto);
        for (int i = 0; i < espaciosDerecha; i++) {
            sb.append(' ');
        }
        while (sb.length() < 60) {
            sb.append(' ');
        }
        return sb.toString();
    }
}