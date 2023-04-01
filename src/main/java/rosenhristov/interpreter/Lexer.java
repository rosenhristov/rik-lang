package main.java.rosenhristov.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static main.java.rosenhristov.interpreter.Constants.*;
import static main.java.rosenhristov.interpreter.Constants.SINGLE_QUOTES_CHAR;
import static main.java.rosenhristov.interpreter.TokenType.CHAR_LITERAL;
import static main.java.rosenhristov.interpreter.TokenType.COMMENT;
import static main.java.rosenhristov.interpreter.TokenType.DOC;
import static main.java.rosenhristov.interpreter.TokenType.EOF;
import static main.java.rosenhristov.interpreter.TokenType.NEWLINE;
import static main.java.rosenhristov.interpreter.TokenType.NUMBER;
import static main.java.rosenhristov.interpreter.TokenType.OPERATOR;
import static main.java.rosenhristov.interpreter.TokenType.SEPARATOR;
import static main.java.rosenhristov.interpreter.TokenType.STRING_LITERAL;
import static main.java.rosenhristov.interpreter.TokenType.WHITESPACE;
import static main.java.rosenhristov.interpreter.TokenType.isTokenType;
import static main.java.rosenhristov.Utils.isEmpty;
import static main.java.rosenhristov.Utils.isValidSourceFile;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.util.Objects.isNull;

public class Lexer {

    Map<String, List<String>> sourceCodeMap;

    private String sourceCode;

    private List<String> keywords;

    private LexingResult lexingResult;

    private Lexer() {
        keywords = loadKeywords();
    }

    private Lexer(Map<String, List<String>> sourceCodeMap) {
        this();
        this.sourceCodeMap = sourceCodeMap;
    }

    public static Lexer of(Map<String, List<String>> sourceCodeMap) {
        return new Lexer (sourceCodeMap);
    }

    public LexingMap lexSourcecodeMap() {
        LexingMap lexedMap = new LexingMap();
        sourceCodeMap.entrySet().stream().forEach(
                entry -> lexedMap.put(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .map(code -> lex(code))
                                        .collect(Collectors.toList())
                )
        );

        return lexedMap;
    }



    public LexingResult lex(String sourceCode) {
        this.sourceCode = sourceCode;
        if (isNull(lexingResult)) {
            lexingResult = new LexingResult(this.sourceCode);
        }

        if(isEmpty(sourceCode)) {
            lexingResult.addError("There is no source code in this file");
            return lexingResult;
        }
        int index = 0;
        while(index < sourceSize()) {
            char symbol = getChar(index);
            if (WHITESPACE.matches(symbol) && !NEWLINE.matches(symbol)) {
                index++; // ignore whitespaces
            } else if (NEWLINE.matches(symbol)) {
                tokens().add(new Token(NEWLINE, symbol, index));
                index++;
            } else if(isLetter(symbol) || symbol == UNDERSCORE_CHAR) {
                int startOfToken = index;
                StringBuilder word = new StringBuilder();
                index = appendWord(word, index);
                TokenType type = deriveWordType(word.toString());
                tokens().add(new Token(type, word.toString(), startOfToken));
            } else if(isDigit(symbol)) {
                StringBuilder number = new StringBuilder();
                index = appendNumber(number, index);
                tokens().add(new Token(NUMBER, number.toString(), index));
            } else if(OPERATOR.matches(symbol) && !isStartOfComment(symbol, index)) {
                if(isSingleOnlyOperator(symbol) || (!isSingleOnlyOperator(symbol) && !isTokenType(OPERATOR, nextChar(index)))) {
                    tokens().add(new Token(OPERATOR, symbol, index));
                } else if(isSingleOnlyOperator(symbol) && isTokenType(OPERATOR, nextChar(index))) {
                    String doubleOperator = new StringBuilder(symbol).append(getChar(++index)).toString();
                    error("Nonexistent double operator: " + doubleOperator);
                } else if (!isSingleOnlyOperator(symbol) && isTokenType(OPERATOR, getChar(index))) {
                    String doubleOperator = new StringBuilder(symbol).append(getChar(++index)).toString();
                    tokens().add(new Token(OPERATOR, doubleOperator, index - 1));
                }
                index++;
            }  else if(CHAR_LITERAL.matches(symbol)) {
                StringBuilder sb = new StringBuilder();
                int literalStart = index;
                index = appendCharLiteral(sb, ++index);
                String literal = sb.toString();
                tokens().add(new Token(CHAR_LITERAL, literal, literalStart));
            } else if (STRING_LITERAL.matches(symbol)) {
                StringBuilder sb = new StringBuilder();
                int literalStart = index;
                index = appendStringLiteral(sb, ++index);
                tokens().add(new Token(STRING_LITERAL, sb.toString(), literalStart));
            } else if(SEPARATOR.matches(symbol)) {
                tokens().add(new Token(SEPARATOR, symbol, index));
                index++;
            } else if(COMMENT.matches(symbol) && isStartOfComment(symbol, index)) {
                int commentStart = index;
                StringBuilder sb = new StringBuilder();
                if(symbol == SLASH_CHAR && nextChar(index) == SLASH_CHAR) {
                    index = appendLineComment(sb, index);
                } else if(symbol == SLASH_CHAR && nextChar(index) == STAR_CHAR) {
                    index = appendMultilineComment(sb, index);
                }

                if(sb.toString().startsWith(DOC.getValue())) {
                    tokens().add(new Token(DOC, sb.toString(), commentStart));
                }
                index++;
            } else {
                error(String.format("Unknown character '%c' at index %d", symbol, index));
            }
        }
        tokens().add(new Token(EOF, EOF.getValue(), sourceSize() - 1));

        return lexingResult;
    }

    private List<String> loadKeywords() {
        return List.of(Constants.KEYWORDS.split("\\|"));
    }

    private String extractSourceCode(File sourceFile) {
        boolean isValidSourceFile = isValidSourceFile(sourceFile);
        if (!isValidSourceFile) {
            error(String.format("File %s is not a valid source file", sourceFile.getName()));
            return EMPTY_STRING;
        }
        FileInputStream inputStream = null;
        String sourceCode;
        try {
            inputStream = new FileInputStream(sourceFile);
            sourceCode = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Problems reading source file %s input stream", sourceFile.getName()), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    throw new RuntimeException(String.format(
                            "Problems closing source file %s input stream", sourceFile.getName()), e);
                }
            }
        }

        return sourceCode;

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

    private boolean error(String error) {
        return this.lexingResult.addError(error);
    }

    public String getSourcecode() {
        return sourceCode;
    }

    public void setSourcecode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public int sourceSize() {
        return sourceCode.length();
    }


    private List<Token> tokens() {
        return lexingResult.getTokens();
    }

    private boolean addToken(Token token) {
        return lexingResult.addToken(token);
    }

}
