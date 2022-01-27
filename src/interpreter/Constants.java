package interpreter;

public interface Constants {
    String SOURCE_FILE_EXTENSION = ".rik";

    String KEYWORDS = "abstract|base|bool|case|catch|char|class|default|do|else|enum|extends|f32|f64|i8|i16|i32|i64|final|finally|for|if|implements|interface|is|jump|loop|match|native|null|private|protected|return|skip|space|static|strictfp|sync|this|throw|throws|transient|try|use|volatile|while";

    String PRIMITIVE_TYPES = "f32|f64|i8|i16|i32|i64|bool|";
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

    String SLASH = "/";
    String STAR = "*";
    String NEW_LINE = "\n";
    String UNDERSCORE = "_";
    String PERCENT = "%";
    String DECIMAL_SIGN = ".";
    String SINGLE_QUOTES = "\'";
    String DOUBLE_QUOTES = "\"";
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
