package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;
import java.math.BigInteger;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}


%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}


LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]
Comment = #+([^\r?\n])*?+#

Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = ":="
OpenBracket = "("
CloseBracket = ")"
OpenCurly = "{"
CloseCurly = "}"
CorcheteAbre = "["
CorcheteCierra = "]"
Semicolon = ";"
Op_men = "<"
Op_may = ">"
Distinct  = "!="
Coma = ","
DosPuntos = ":"
Point = "."

Letter = [a-zA-Z]
Digit = [0-9]
DoubleQuote = "\""
Arroba = "@"
Percent = "%"
WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Digit}+
StringConstant = {DoubleQuote}({Letter}|{Digit}|{WhiteSpace}|{Arroba}|{Percent})+{DoubleQuote}
FloatConstant = [-]?(({Digit}+{Point}{Digit}+)|({Point}{Digit}+)|({Digit}+\.))

%%


/* keywords */
<YYINITIAL> "while"     { System.out.println("Token: " + yytext() + " | Tipo: WHILE"); return symbol(ParserSym.WHILE); }
<YYINITIAL> "if"        { System.out.println("Token: " + yytext() + " | Tipo: IF"); return symbol(ParserSym.IF); }
<YYINITIAL> "else"      { System.out.println("Token: " + yytext() + " | Tipo: ELSE"); return symbol(ParserSym.ELSE); }
<YYINITIAL> "init"      { System.out.println("Token: " + yytext() + " | Tipo: INIT"); return symbol(ParserSym.INIT); }
<YYINITIAL> "AND"       { System.out.println("Token: " + yytext() + " | Tipo: AND"); return symbol(ParserSym.AND); }
<YYINITIAL> "OR"        { System.out.println("Token: " + yytext() + " | Tipo: OR"); return symbol(ParserSym.OR); }
<YYINITIAL> "NOT"       { System.out.println("Token: " + yytext() + " | Tipo: NOT"); return symbol(ParserSym.NOT); }
<YYINITIAL> "DISTINCT"  { System.out.println("Token: " + yytext() + " | Tipo: DISTINCT"); return symbol(ParserSym.DISTINCT); }
<YYINITIAL> "Float"     { System.out.println("Token: " + yytext() + " | Tipo: FLOAT"); return symbol(ParserSym.FLOAT); }
<YYINITIAL> "Int"       { System.out.println("Token: " + yytext() + " | Tipo: INT"); return symbol(ParserSym.INT); }
<YYINITIAL> "String"    { System.out.println("Token: " + yytext() + " | Tipo: STRING"); return symbol(ParserSym.STRING); }
<YYINITIAL> "read"      { System.out.println("Token: " + yytext() + " | Tipo: READ"); return symbol(ParserSym.READ); }
<YYINITIAL> "write"     { System.out.println("Token: " + yytext() + " | Tipo: WRITE"); return symbol(ParserSym.WRITE); }
<YYINITIAL> "negativeCalculation" { System.out.println("Token: " + yytext() + " | Tipo: NEGATIVE_CALC"); return symbol(ParserSym.NEGATIVE_CALC); }
<YYINITIAL> "sumFirstPrimes"      { System.out.println("Token: " + yytext() + " | Tipo: SUM_FIRST_PRIMES"); return symbol(ParserSym.SUM_FIRST_PRIMES); }

<YYINITIAL> {
  {StringConstant}                          {
                                                  String stringValue = yytext().substring(1, yytext().length() - 1); // Remueve las comillas
                                                  if (stringValue.length() <= STRING_RANGE) {
                                                      return symbol(ParserSym.STRING_CONSTANT, stringValue);
                                                  } else {
                                                      throw new InvalidLengthException("La constante de cadena [" + stringValue + "] excede los " + STRING_RANGE + " caracteres permitidos.");
                                                  }
                                              }
    /* identifiers */
    {Identifier}                              {
                                                  String stringValue = new String(yytext());
                                                  if (stringValue.length() <= MAX_LENGTH) {
                                                      return symbol(ParserSym.IDENTIFIER, yytext());
                                                  } else {
                                                      throw new InvalidLengthException("La constante de cadena [" + stringValue + "] excede los " + STRING_RANGE + " caracteres permitidos.");
                                                  }
                                              }
    /* Constants */
    {IntegerConstant}                         {
                                                  BigInteger intValue = new BigInteger(yytext());
                                                  //BigInteger minValue = BigInteger.valueOf(-(1L << (BITS_INT - 1))); // Calcula el valor mínimo permitido
                                                  BigInteger minValue = BigInteger.valueOf(0);
                                                  //BigInteger maxValue = BigInteger.valueOf((1L << (BITS_INT - 1)) - 1); // Calcula el valor máximo permitido
                                                  BigInteger maxValue = BigInteger.valueOf((1L << BITS_INT) - 1); // Calcula el valor máximo permitido

                                                  if (intValue.compareTo(minValue) >= 0 && intValue.compareTo(maxValue) <= 0) {
                                                      return symbol(ParserSym.INTEGER_CONSTANT, yytext());
                                                  } else {
                                                      String errorMessage;
                                                      if (intValue.compareTo(minValue) < 0) {
                                                          errorMessage = "La constante [" + yytext() + "] esta por debajo del limite de los enteros.";
                                                      } else {
                                                          errorMessage = "La constante [" + yytext() + "] esta por encima del limite de los enteros.";
                                                      }
                                                      throw new InvalidIntegerException(errorMessage);
                                                  }
                                              }
{FloatConstant}                                     
                                            {
                                                double floatValue = Double.parseDouble(yytext());
                                                if (floatValue >= -Double.MAX_VALUE && floatValue <= Double.MAX_VALUE) {
                                                    return symbol(ParserSym.FLOAT_CONSTANT, yytext());
                                                } else {
                                                    String errorMessage;
                                                    if (floatValue < -Double.MAX_VALUE) { // Si es más pequeño (más negativo) que el límite
                                                        errorMessage = "La constante [" + yytext() + "] está por debajo del límite de los números flotantes.";
                                                    } else { // Si es más grande (más positivo) que el límite
                                                        errorMessage = "La constante [" + yytext() + "] está por encima del límite de los números flotantes.";
                                                    }
                                                    throw new InvalidFloatException(errorMessage);
                                                }
                                            }

  /* operators */
  {Plus}                                    { System.out.println("Token: " + yytext() + " | Tipo: PLUS"); return symbol(ParserSym.PLUS); }
  {Sub}                                     { System.out.println("Token: " + yytext() + " | Tipo: SUB"); return symbol(ParserSym.SUB); }
  {Mult}                                    { System.out.println("Token: " + yytext() + " | Tipo: MULT"); return symbol(ParserSym.MULT); }
  {Div}                                     { System.out.println("Token: " + yytext() + " | Tipo: DIV"); return symbol(ParserSym.DIV); }
  {Assig}                                   { System.out.println("Token: " + yytext() + " | Tipo: ASSIG"); return symbol(ParserSym.ASSIG); }
  {OpenBracket}                             { System.out.println("Token: " + yytext() + " | Tipo: OPEN_BRACKET"); return symbol(ParserSym.OPEN_BRACKET); }
  {CloseBracket}                            { System.out.println("Token: " + yytext() + " | Tipo: CLOSE_BRACKET"); return symbol(ParserSym.CLOSE_BRACKET); }
  {OpenCurly}                               { System.out.println("Token: " + yytext() + " | Tipo: OPEN_CURLY"); return symbol(ParserSym.OPEN_CURLY); }
  {CloseCurly}                              { System.out.println("Token: " + yytext() + " | Tipo: CLOSE_CURLY"); return symbol(ParserSym.CLOSE_CURLY); }
  {CorcheteAbre}                            { System.out.println("Token: " + yytext() + " | Tipo: COR_A"); return symbol(ParserSym.COR_A); }
  {CorcheteCierra}                          { System.out.println("Token: " + yytext() + " | Tipo: COR_C"); return symbol(ParserSym.COR_C); }
  {Op_men}                                  { System.out.println("Token: " + yytext() + " | Tipo: OP_MEN"); return symbol(ParserSym.OP_MEN); }
  {Op_may}                                  { System.out.println("Token: " + yytext() + " | Tipo: OP_MAY"); return symbol(ParserSym.OP_MAY); }
  {Semicolon}                               { System.out.println("Token: " + yytext() + " | Tipo: PYC"); return symbol(ParserSym.PYC); }
  {Coma}                                    { System.out.println("Token: " + yytext() + " | Tipo: COMA"); return symbol(ParserSym.COMA); }
  {Point}                                   { return symbol(ParserSym.POINT); }
  {DosPuntos}                               { System.out.println("Token: " + yytext() + " | Tipo: DOS_PUNTOS"); return symbol(ParserSym.DOS_PUNTOS); }

  /* whitespace */
  {WhiteSpace}                              { /* ignore */ }
  {Comment} 				                { /* ignore */ }

}

/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }