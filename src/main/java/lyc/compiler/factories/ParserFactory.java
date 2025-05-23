package lyc.compiler.factories;

import lyc.compiler.Lexer;
import lyc.compiler.Parser;
import java.io.Reader;
public final class ParserFactory {

    private ParserFactory(){}

    public static Parser create(Lexer lexer) {
        return new Parser(lexer);
    }

    public static Parser create(String input) {
        return new Parser(LexerFactory.create(input));
    }

    public static Parser create(Reader reader) {
        return new Parser(LexerFactory.create(reader));
    }


}
