package interpreter;

import java.io.File;

import static interpreter.Constants.SOURCE_FILE_EXTENSION;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Utils {

    public static boolean isValidSourceFile(File sourceFile) {
        return nonNull(sourceFile) || sourceFile.exists() || sourceFile.getName().endsWith(SOURCE_FILE_EXTENSION);
    }

    public static boolean isEmpty(String string) {
        return isNull(string) || string.length() == 0;
    }
}
