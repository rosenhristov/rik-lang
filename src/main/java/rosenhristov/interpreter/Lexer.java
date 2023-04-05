package main.java.rosenhristov.interpreter;

import main.java.rosenhristov.ProjectDir;
import main.java.rosenhristov.SourceCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static main.java.rosenhristov.interpreter.Constants.*;
import static main.java.rosenhristov.interpreter.Constants.SINGLE_QUOTES_CHAR;
import static main.java.rosenhristov.Utils.isEmpty;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static main.java.rosenhristov.interpreter.TokenType.*;

public class Lexer {

    Map<ProjectDir, List<SourceCode>> sourceCodeMap;

    private String sourceCode;

    private final List<String> keywords;

    private Errors errors;

    private Lexer() {
        keywords = loadKeywords();
    }

    private Lexer(Map<ProjectDir, List<SourceCode>>sourceCodeMap) {
        this();
        this.sourceCodeMap = sourceCodeMap;
    }

    public static Lexer of(Map<ProjectDir, List<SourceCode>> sourceCodeMap) {
        return new Lexer (sourceCodeMap);
    }

    public LexedMap lexSourceCodeMap() {
        LexedMap lexedMap = new LexedMap();
        sourceCodeMap.entrySet().forEach(entry -> lexedMap.put(
                entry.getKey(),
                entry.getValue().stream()
                        .map(sourceFileContent -> new LexedSourceFile(
                                lexToLineTokensMap(sourceFileContent.getSourceString())))
                        .collect(Collectors.toList())
                )
        );
        return lexedMap;
    }

    public LineTokensMap lexToLineTokensMap(String sourceCode) {
        this.sourceCode = sourceCode;
        if (isEmpty(sourceCode)) {
            addLexerError("There is no source code in this file");
            return new LineTokensMap();
        }
        return lex(sourceCode).buildLineTokensMap();
    }

    public LexingResult lex(String sourceCode) {
        LexingResult lexingResult = new LexingResult(sourceCode);
        int index = 0;
        while (index < sourceCode.length()) {
            char symbol = getChar(index);
            if (WHITESPACE.matches(symbol) && !NEWLINE.matches(symbol)) {
                index++; // ignore whitespaces
            } else if (NEWLINE.matches(symbol)) {
                lexingResult.addToken(new Token(NEWLINE, symbol, index));
                index++;
            } else if(isLetter(symbol) || symbol == UNDERSCORE_CHAR) {
                int startOfToken = index;
                StringBuilder word = new StringBuilder();
                index = appendWord(word, index);
                TokenType type = deriveWordType(word.toString());
                lexingResult.addToken(new Token(type, word.toString(), startOfToken));
            } else if (isDigit(symbol)) {
                StringBuilder number = new StringBuilder();
                index = appendNumber(number, index);
                lexingResult.addToken(new Token(NUMBER, number.toString(), index));
            } else if (OPERATOR.matches(symbol) && !isStartOfComment(symbol, index)) {
                if (isSingleOnlyOperator(symbol) || (!isSingleOnlyOperator(symbol) && !isTokenType(OPERATOR, nextChar(index)))) {
                    lexingResult.addToken(new Token(OPERATOR, symbol, index));
                } else if (isSingleOnlyOperator(symbol) && isTokenType(OPERATOR, nextChar(index))) {
                    String doubleOperator = new StringBuilder(symbol).append(getChar(++index)).toString();
                    lexingResult.addError(String.format("Nonexistent double operator %s at %s:%s: ", doubleOperator, index));
                } else if (!isSingleOnlyOperator(symbol) && isTokenType(OPERATOR, getChar(index))) {
                    String doubleOperator = new StringBuilder(symbol).append(getChar(++index)).toString();
                    lexingResult.addToken(new Token(OPERATOR, doubleOperator, index - 1));
                }
                index++;
            } else if (CHAR_LITERAL.matches(symbol)) {
                StringBuilder sb = new StringBuilder();
                int literalStart = index;
                index = appendCharLiteral(sb, ++index);
                String literal = sb.toString();
                lexingResult.addToken(new Token(CHAR_LITERAL, literal, literalStart));
            } else if (STRING_LITERAL.matches(symbol)) {
                StringBuilder sb = new StringBuilder();
                int literalStart = index;
                index = appendStringLiteral(sb, ++index);
                lexingResult.addToken(new Token(STRING_LITERAL, sb.toString(), literalStart));
            } else if (SEPARATOR.matches(symbol)) {
                lexingResult.addToken(new Token(SEPARATOR, symbol, index));
                index++;
            } else if (COMMENT.matches(symbol) && isStartOfComment(symbol, index)) {
                int commentStart = index;
                Integer commentEnd;
                StringBuilder sb = new StringBuilder();
                if (symbol == SLASH_CHAR && nextChar(index) == SLASH_CHAR) {
                    index = appendLineComment(sb, index);
                    lexingResult.addToken(new Token(COMMENT, sb.toString(), commentStart));
                } else if (symbol == SLASH_CHAR && nextChar(index) == STAR_CHAR && nextChar(index + 1) != STAR_CHAR) {
                    index = appendMultilineComment(sb, index);
                    commentEnd = index;
                    lexingResult.addToken(new Token(MULTILINE_COMMENT, sb.toString(), commentStart, commentEnd));
                } else if (symbol == SLASH_CHAR && nextChar(index) == STAR_CHAR && nextChar(index + 1) == STAR_CHAR) {
                    index = appendMultilineComment(sb, index);
                    commentEnd = index;
                    lexingResult.addToken(new Token(DOC, sb.toString(), commentStart, commentEnd));
                }
                index++;
            } else {
                lexingResult.addError(String.format("Unknown character '%c' at index %d", symbol, index));
            }
        }
        lexingResult.addToken(new Token(EOF, EOF.getValue(), sourceSize() - 1));

        return lexingResult;
    }

    private List<String> loadKeywords() {
        return List.of(Constants.KEYWORDS.split("\\|"));
    }

    private boolean addLexerError(String error) {
        return this.getErrors().addError(error);
    }

    private Errors getErrors() {
        if (isNull(errors)) {
            errors = new Errors();
        }
        return errors;
    }

    private int appendLineComment(StringBuilder sb, int index) {
        return appendCharsTillSymbol(sb, index, NEW_LINE_CHAR);
    }

    private int appendMultilineComment(StringBuilder comment, int index) {
        int i = appendCharsTillSymbol(comment, index, STAR_CHAR);
        comment.append(getChar(i));
        if (nextChar(i) == SLASH_CHAR) {
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
        while (index < sourceSize() && getChar(index) != DOUBLE_QUOTES_CHAR) {
            if(getChar(index) != BACKSLASH_CHAR) {
                string.append(getChar(index));
            } else if (isEscapedChar(index)) {
                string.append(getChar(++index));
            }
            index++;
        }
        return ++index;
    }

    private boolean isEscapedChar(int index) {
        return getChar(index) == BACKSLASH_CHAR
                && (nextChar(index) == BACKSLASH_CHAR
                        || nextChar(index) == SINGLE_QUOTES_CHAR
                        || nextChar(index) == DOUBLE_QUOTES_CHAR);
    }

    private int appendCharLiteral(StringBuilder sb, int index) {
        while (index < sourceSize() && getChar(index) != SINGLE_QUOTES_CHAR) {
            if(getChar(index) != BACKSLASH_CHAR) {
                sb.append(getChar(index));
            } else if (isCharValue(index)) {
                sb.append(getChar(++index));
            }
            index++;
        }
        return ++index;
    }

    private boolean isCharValue(int index) {
        return getChar(index) == BACKSLASH_CHAR
                && (nextChar(index) == BACKSLASH_CHAR
                        || nextChar(index) == U_CHAR_VALUE
                        || nextChar(index) == SINGLE_QUOTES_CHAR);
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
        return sourceCode.charAt(index);

    }

    public int sourceSize() {
        return sourceCode.length();
    }

}
