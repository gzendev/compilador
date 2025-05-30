package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;
import java.util.List;
import java.util.ArrayList;

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

  public SymbolTableStruct currentSymbol;
  public List<SymbolTableStruct> symbolList = new ArrayList();


  private void addToSymbolListIfNotExists(SymbolTableStruct symbol){
  		if(!symbolList.contains(symbol))
  			symbolList.add(symbol);
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

Letter = [a-zA-Z]
Digit = [0-9]
DoubleQuote = "\""
Arroba = "@"
Percent = "%"
WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = [-]?{Digit}+
StringConstant = {DoubleQuote}({Letter}|{Digit}|{WhiteSpace}|{Arroba}|{Percent})+{DoubleQuote}
FloatConstants = [-]?{Digit}+[.]{Digit}+([eE][-+]?{Digit}+)? | [.]{Digit}+ | {Digit}+[.] | [-]?{Digit}+[eE][-+]?{Digit}+

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
<YYINITIAL> "negativeCalculation" { System.out.println("Token: " + yytext() + " | Tipo: NEGATIVECALC"); return symbol(ParserSym.NEGATIVECALC); }
<YYINITIAL> "sumFirstPrimes"      { System.out.println("Token: " + yytext() + " | Tipo: SUMFIRSTPRIMES"); return symbol(ParserSym.SUMFIRSTPRIMES); }

<YYINITIAL> {
  /* identifiers */
  {Identifier} {
      if (yytext().length() > 50) {
        throw new InvalidLengthException("Identifier too long: " + yytext());
      }
      System.out.println("Token: " + yytext() + " | Tipo: IDENTIFIER");
      addToSymbolListIfNotExists(new SymbolTableStruct("_".concat(yytext()), null, null, 0));
      return symbol(ParserSym.IDENTIFIER, yytext());
  }
  /* Constants */
  {IntegerConstant} {
      try {
          int value = Integer.parseInt(yytext());
          if (value < -32768 || value > 32767) {
              throw new InvalidIntegerException("Integer constant out of 16-bit range: " + yytext());
          }
      } catch (NumberFormatException e) {
          throw new InvalidIntegerException("Invalid integer constant: " + yytext());
      }
      System.out.println("Token: " + yytext() + " | Tipo: INTEGER_CONSTANT");
      addToSymbolListIfNotExists(new SymbolTableStruct("_".concat(yytext()), "Int", yytext(), 0));
      return symbol(ParserSym.INTEGER_CONSTANT, yytext());
  }
  {FloatConstants} {
      try {
          double value = Double.parseDouble(yytext());
          if (value < -3.40282347e+38 || value > 3.40282347e+38) {
              throw new InvalidFloatException("Float constant out of range: " + yytext());
          }
      } catch (NumberFormatException e) {
          throw new InvalidFloatException("Invalid float constant: " + yytext());
      }
      System.out.println("Token: " + yytext() + " | Tipo: FLOAT_CONSTANT");
      addToSymbolListIfNotExists(new SymbolTableStruct("_".concat(yytext()), "Float", yytext(), 0));
      return symbol(ParserSym.FLOAT_CONSTANT, yytext());
  }
  {StringConstant} {
      if (yytext().length() > STR_MAX) {
        throw new InvalidLengthException("String constant too long: " + yytext());
      }
      System.out.println("Token: " + yytext() + " | Tipo: STRING_CONSTANT");
      addToSymbolListIfNotExists(new SymbolTableStruct("_".concat(yytext()), "String", yytext(), yytext().length()));
      return symbol(ParserSym.STRING_CONSTANT, yytext());
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
  {DosPuntos}                               { System.out.println("Token: " + yytext() + " | Tipo: DOS_PUNTOS"); return symbol(ParserSym.DOS_PUNTOS); }

  /* whitespace */
  {WhiteSpace}                              { /* ignore */ }
  {Comment} 				                { /* ignore */ }

}


/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }