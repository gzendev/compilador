package lyc.compiler;

import lyc.compiler.factories.LexerFactory;
import lyc.compiler.model.CompilerException;
import lyc.compiler.model.InvalidIntegerException;
import lyc.compiler.model.InvalidLengthException;
import lyc.compiler.model.UnknownCharacterException;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static com.google.common.truth.Truth.assertThat;
import static lyc.compiler.constants.Constants.MAX_LENGTH;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerTest {

  private Object lexer;


  @Test
  public void comment() throws Exception{
    scan("#+This is a comment+#");
    assertThat(nextToken()).isEqualTo(getParserSymValue("EOF"));
  }

  @Test
  public void invalidStringConstantLength() {
    assertThrows(InvalidLengthException.class, () -> {
      try {
        scan("%s".formatted(getRandomString()));
        nextToken();
      } catch (Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidLengthException) throw (InvalidLengthException) cause;
        throw e;
      }
    });
  }

  @Test
  public void invalidIdLength() {
    assertThrows(InvalidLengthException.class, () -> {
      try {
        scan(getRandomString());
        nextToken();
      } catch (Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidLengthException) throw (InvalidLengthException) cause;
        throw e;
      }
    });
  }

  @Test
  public void invalidPositiveIntegerConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      try {
        scan("%d".formatted(9223372036854775807L));
        nextToken();
      } catch (Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidIntegerException) throw (InvalidIntegerException) cause;
        throw e;
      }
    });
  }

  @Test
  public void invalidNegativeIntegerConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      try {
        scan("%d".formatted(-9223372036854775807L));
        nextToken();
        nextToken();
      } catch (Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidIntegerException) throw (InvalidIntegerException) cause;
        throw e;
      }
    });
  }


  @Test
  public void assignmentWithExpressions() throws Exception {
    scan("c:=d*(e-21)/4");
    assertThat(nextToken()).isEqualTo(getParserSymValue("IDENTIFIER"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("ASSIG"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("IDENTIFIER"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("MULT"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("OPEN_BRACKET"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("IDENTIFIER"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("SUB"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("INTEGER_CONSTANT"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("CLOSE_BRACKET"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("DIV"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("INTEGER_CONSTANT"));
    assertThat(nextToken()).isEqualTo(getParserSymValue("EOF"));
  }

  @Test
  public void unknownCharacter() {
    assertThrows(UnknownCharacterException.class, () -> {
      try {
        scan("#");
        nextToken();
      } catch (Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof UnknownCharacterException) throw (UnknownCharacterException) cause;
        throw e;
      }
    });
  }

  @AfterEach
  public void resetLexer() {
    lexer = null;
  }

  private void scan(String input) {
    lexer = LexerFactory.create(input);
  }

  private int nextToken() throws Exception {
    Object symbol = lexer.getClass().getMethod("next_token").invoke(lexer);
    return (int) symbol.getClass().getField("sym").get(symbol);
  }

  private static String getRandomString() {
    return new RandomStringGenerator.Builder()
            .filteredBy(CharacterPredicates.LETTERS)
            .withinRange('a', 'z')
            .build().generate(MAX_LENGTH * 2);
  }

  private int getParserSymValue(String name) throws Exception {
    Class<?> parserSymClass = Class.forName("lyc.compiler.ParserSym");
    return parserSymClass.getField(name).getInt(null);
  }

}
