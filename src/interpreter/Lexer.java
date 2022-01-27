package interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static interpreter.Constants.DOUBLE_QUOTES;
import static interpreter.Constants.NEW_LINE;
import static interpreter.Constants.SINGLE_ALLOWED_OPERATORS;
import static interpreter.Constants.SINGLE_QUOTES;
import static interpreter.Constants.STAR;
import static interpreter.Constants.UNDERSCORE;
import static interpreter.TokenType.CHAR_LITERAL;
import static interpreter.TokenType.COMMENT;
import static interpreter.TokenType.DOC;
import static interpreter.TokenType.EOF;
import static interpreter.TokenType.NEWLINE;
import static interpreter.TokenType.NUMBER;
import static interpreter.TokenType.OPERATOR;
import static interpreter.TokenType.SEPARATOR;
import static interpreter.TokenType.STRING_LITERAL;
import static interpreter.TokenType.WHITESPACE;
import static interpreter.TokenType.isTokenType;
import static interpreter.Utils.isEmpty;
import static interpreter.Utils.isValidSourceFile;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.util.Objects.isNull;

public class Lexer {

    private String sourcecode;
    private List<String> keywords;

    public Lexer() {
        keywords = loadKeywords();
    }

    public Lexer(File sourceFile) throws IOException {
        this();
        this.sourcecode = extractSourceCode(sourceFile);

    }

    public Lexer(String sourcecode) {
        this();
        this.sourcecode = sourcecode;
    }

    public List<Token> lex(File sourceFile) throws IOException {
        return lex(extractSourceCode(sourceFile));
    }

    public List<Token> lex(String sourceCode) {
        this.sourcecode = sourceCode;
        return lex();
    }

    public List<Token> lex() {
        return lex(new LinkedList<>());
    }

    public void lex(String sourceCode, List<Token> tokens) {
        this.sourcecode = sourceCode;
        lex(tokens);
    }

    public List<Token> lex(List<Token> tokens) {
        if (isNull(tokens)) {
            tokens = new LinkedList<>();
        }
        if(isEmpty(sourcecode)) {
            return tokens;
        }
        int index = 0;
        while(index < sourceSize()) {
            char symbol = getChar(index);
            if (WHITESPACE.matches(symbol) && !NEWLINE.matches(symbol)) {
                index++; // ignore whitespaces
            } else if (NEWLINE.matches(symbol)) {
                tokens.add(new Token(NEWLINE, symbol, index));
                index++;
            } else if(isLetter(symbol) || symbol == UNDERSCORE.charAt(0)) {
                int startOfToken = index;
                StringBuilder word = new StringBuilder();
                index = appendWord(word, index);
                TokenType type = deriveWordType(word.toString());
                tokens.add(new Token(type, word.toString(), startOfToken));
            } else if(isDigit(symbol)) {
                StringBuilder number = new StringBuilder();
                index = appendNumber(number, index);
                tokens.add(new Token(NUMBER, number.toString(), index));
            } else if(OPERATOR.matches(symbol) && !isStartOfComment(symbol, index)) {
                if(isSingleOnlyOperator(symbol) || (!isSingleOnlyOperator(symbol) && !isTokenType(OPERATOR, nextChar(index)))) {
                    tokens.add(new Token(OPERATOR, symbol, index));
                } else if(isSingleOnlyOperator(symbol) && isTokenType(OPERATOR, nextChar(index))) {
                    String doubleOperator = new StringBuilder(symbol).append(getChar(++index)).toString();
                    error("Nonexisting double operator: " + doubleOperator);
                } else if (!isSingleOnlyOperator(symbol) && isTokenType(OPERATOR, getChar(index))) {
                    String doubleOperator = new StringBuilder(symbol).append(getChar(++index)).toString();
                    tokens.add(new Token(OPERATOR, doubleOperator, index - 1));
                }
                index++;
            }  else if(CHAR_LITERAL.matches(symbol)) {
                StringBuilder sb = new StringBuilder();
                int literalStart = index;
                index = appendCharLiteral(sb, ++index);
                String literal = sb.toString();
                tokens.add(new Token(CHAR_LITERAL, literal, literalStart));
            } else if (STRING_LITERAL.matches(symbol)) {
                StringBuilder sb = new StringBuilder();
                int literalStart = index;
                index = appendStringLiteral(sb, ++index);
                tokens.add(new Token(STRING_LITERAL, sb.toString(), literalStart));
            } else if(SEPARATOR.matches(symbol)) {
                tokens.add(new Token(SEPARATOR, symbol, index));
                index++;
            } else if(COMMENT.matches(symbol) && isStartOfComment(symbol, index)) {
                int commentStart = index;
                StringBuilder sb = new StringBuilder();
                if(symbol == '/' && nextChar(index) == '/') {
                    index = appendLineComment(sb, index);
                } else if(symbol == '/' && nextChar(index) == '*') {
                    index = appendMultilineComment(sb, index);
                }

                if(sb.toString().startsWith(DOC.getValue())) {
                    tokens.add(new Token(DOC, sb.toString(), commentStart));
                }
                index++;
            } else {
                error(String.format("Unknown character %c at index %d", symbol, index));
            }
        }
        tokens.add(new Token(EOF, EOF.getValue(), sourceSize() - 1));
        return tokens;
    }

    private List<String> loadKeywords() {
        return List.of(Constants.KEYWORDS.split("\\|"));
    }

    private String extractSourceCode(File sourceFile) throws IOException {
        return isValidSourceFile(sourceFile)
                ? new String(new FileInputStream(sourceFile).readAllBytes())
                : "";
    }

    private int appendLineComment(StringBuilder sb, int index) {
        return appendCharsTillSymbol(sb, index, NEW_LINE.charAt(0));
    }

    private int appendMultilineComment(StringBuilder comment, int index) {
        int i = appendCharsTillSymbol(comment, index, STAR.charAt(0));
        comment.append(getChar(i));
        if (nextChar(i) == '/') {
            comment.append(getChar(++i));
        }
        i++;
        if(comment.toString().endsWith("*/")) {
            return i;
        }
        return appendMultilineComment(comment, i);
    }

    private int appendWord(StringBuilder word, int index) {
        while (index < sourceSize() && isWordAllowedChar(getChar(index))) {
            word.append(getChar(index));
            index++;
        }
        return index;
    }

    private int appendNumber(StringBuilder number, int index) {
        while (index < sourceSize() && isNumberAllowedChar(getChar(index), index)) {
            number.append(getChar(index));
            index++;
        }
        return index;
    }

    private int appendStringLiteral(StringBuilder string, int index) {
        while (index < sourceSize() && getChar(index) != DOUBLE_QUOTES.charAt(0)) {
            if(getChar(index) != '\\') {
                string.append(getChar(index));
            } else if (getChar(index) == '\\' && (nextChar(index) == '\\' || nextChar(index) == SINGLE_QUOTES.charAt(0) || nextChar(index) == DOUBLE_QUOTES.charAt(0))) {
                string.append(getChar(++index));
            }
            index++;
        }
        return ++index;
    }

    private int appendCharLiteral(StringBuilder sb, int index) {
        while (index < sourceSize() && getChar(index) != SINGLE_QUOTES.charAt(0)) {
            if(getChar(index) != '\\') {
                sb.append(getChar(index));
            } else if (getChar(index) == '\\'
                    && (nextChar(index) == '\\' || nextChar(index) == 'u' || nextChar(index) == SINGLE_QUOTES.charAt(0))) {
                sb.append(getChar(++index));
            }
            index++;
        }
        return ++index;
    }

    private int appendCharsTillSymbol(StringBuilder sb, int index, char symbol) {
        while (index < sourceSize() && getChar(index) != symbol) {
            sb.append(getChar(index));
            index++;
        }
        return index;
    }

    private int appendCharsTillMatch(StringBuilder sb, int index, TokenType tokenType) {
        while(index < sourceSize() && !tokenType.matches(getChar(index))) {
            sb.append(getChar(index));
            index++;
        }
        return index;
    }

    private TokenType deriveWordType(String word) {
        return TokenType.KEYWORD.matches(word) && keywords.contains(word)
                ? TokenType.KEYWORD
                : TokenType.IDENTIFIER;
    }

    private boolean isWordAllowedChar(char c) {
        return isLetter(c) || isDigit(c) || c == '_';
    }

    private boolean isNumberAllowedChar(char c, int index) {
        return isDigit(c)
                || isExponentialNumber(c, index)
                || isPowerSign(c, index)
                || isHexadecimalSign(c, index)
                || isDecimalOrReadabilitySeparator(c, index)
                || isHexadecimalDigit(c);
    }

    private boolean isExponentialNumber(char c, int index) {
        return isExponentialSign(c)
                && (isDigit(prevChar(index)) && (isDigit(nextChar(index)) || nextChar(index) == '-' || nextChar(index) == '+'));
    }

    private boolean isExponentialSign(char c) {
        return c == 'e' || c == 'E';
    }

    private boolean isPowerSign(char c, int index) {
        return (c == '+' || c == '-')
                && (isExponentialSign(prevChar(index)) && isDigit(getChar(index - 2)) && isDigit(nextChar(index)));
    }

    private boolean isHexadecimalSign(char c, int index) {
        return (c == 'x' || c == 'X')
                && (prevChar(index) == '0' && (isDigit(nextChar(index)) || isHexadecimalDigit(nextChar(index))));
    }

    private boolean isDecimalOrReadabilitySeparator(char c, int index) {
        return (c == '.' || c == ',' || c == '_' || c == ' ')
                && (isDigit(prevChar(index)) && isDigit(nextChar(index)));
    }

    private boolean isHexadecimalDigit(char c) {
        return (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    private boolean isSingleOnlyOperator(char symbol) {
        return SINGLE_ALLOWED_OPERATORS.contains(String.valueOf(symbol));
    }

    private boolean isStartOfComment(char symbol, int index) {
        return symbol == '/' && (nextChar(index) == '/' || nextChar(index) == '*');
    }

    private char nextChar(int index) {
        return getChar(++index);

    }

    private char prevChar(int index) {
        return getChar(--index);
    }

    private char getChar(int index) {
        return sourcecode.charAt(index);

    }

    private void error(String msg) {

    }

    public String getSourcecode() {
        return sourcecode;
    }

    public void setSourcecode(String sourcecode) {
        this.sourcecode = sourcecode;
    }

    public int sourceSize() {
        return sourcecode.length();
    }
}
