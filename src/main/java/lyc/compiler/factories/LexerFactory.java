package lyc.compiler.factories;

import java.io.Reader;
import java.io.StringReader;

public final class LexerFactory {

    private LexerFactory() {}

    public static Object create(String input) {
        Reader reader = new StringReader(input);
        return create(reader);
    }

    public static Object create(Reader reader) {
        try {
            Class<?> lexerClass = Class.forName("lyc.compiler.Lexer");
            return lexerClass.getConstructor(Reader.class).newInstance(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
