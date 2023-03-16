package main.java.rosenhristov.interpreter;

public interface Constants {
    String SOURCE_FILE_EXTENSION = ".rik";
    String CONFIG_FILENAME = "rik-config.yml";

    String KEYWORDS = "abstract|base|bool|case|catch|char|class|const|default|do|else|enum|extends|f32|f64|i8|i16|i32|i64|i128|finally|for|if|impl|is|jump|loop|match|native|null|private|protected|package|return|skip|static|stritraitctfp|sync|this|throw|throws|trait|trans|try|use|volatile|while";

    String PRIMITIVE_TYPES = "bool|f32|f64|i8|i16|i32|i64";
    String MATH_OPERATORS = "+-*/<>%=";
    String UNARY_OPERATORS = "!|&";
    String LOGICAL_OPERATORS = "&|^";
    String LOGICAL_VALUE = "yes|no";
    String YES = "yes";
    String NO = "no";
    String SPACES = "\\s\\t\\n\\r";
    String HEX_PATTERN = "(0x[0-9a-fA-F]+)";
    String IDENTIFIER_PATTERN = "([[a-z]{1}[a-zA-Z0-9_]*]+)";
    String RELATIONAL_OPERATORS = "<= >= < > == != <>";

    String EMPTY_STRING = "";

    String SLASH = "/";
    char SLASH_CHAR = '/';
    char BACKSLASH_CHAR = '\\';
    String STAR = "*";
    char STAR_CHAR = '*';
    String NEW_LINE = "\n";
    char NEW_LINE_CHAR = '\n';
    String UNDERSCORE = "_";
    char UNDERSCORE_CHAR = '_';
    String PERCENT = "%";
    String DECIMAL_SIGN = ".";
    String SINGLE_QUOTES = "\'";
    char SINGLE_QUOTES_CHAR ='\'';
    String DOUBLE_QUOTES = "\"";
    char DOUBLE_QUOTES_CHAR = '\"';
    char U_CHAR_VALUE = 'u';
    String SINGLE_ALLOWED_OPERATORS = "*/%";
//    String Mulop = "(/|%)";
//    String Assignop = "(=|\\+=|-=)";
//    String Postfixop = "(\\+\\+|--)";
//    String Plusop = "(\\+)";
//    String Minusop = "(-)";
//    String Punctuation = "(\\(|\\)|\\[|\\]|\\{|\\}|;|,)";
//    String REAL_EXP = "([0-9]+\\.[0-9]+[eE][+-]?[0-9]+)";
//    String Real = "([0-9]+\\.[0-9]+)";
//    String Real = "(\\.[0-9]+[eE][+-]?[0-9]+)";
//    String Real = "(\\.[0-9]+)";
//    String Real = "([0-9]+[eE][+-]?[0-9]+)";
//    String Int = "([0-9]+)";
//    String Starop = "(\\*)";
//    String Comment = "(\\/\\*.*\\*\\/)";
//    String Commenterror = "(\\/\\*.*)";
//    String Newline  = "(\n|\r|\f)";
//    String Error = "(^[a-zA-z].+)";

}
