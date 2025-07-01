package lyc.compiler.factories;

import java.io.Reader;
public final class ParserFactory {

    private ParserFactory(){}

    public static Object create(Object lexer) {
        try {
            Class<?> parserClass = Class.forName("lyc.compiler.Parser");
            Class<?> scannerInterface = Class.forName("java_cup.runtime.Scanner");
            return parserClass.getConstructor(scannerInterface).newInstance(lexer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object create(String input) {
        Object lexer = LexerFactory.create(input);
        return create(lexer);
    }

    public static Object create(Reader reader) {
        Object lexer = LexerFactory.create(reader);
        return create(lexer);
    }


}
