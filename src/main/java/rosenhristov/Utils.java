package main.java.rosenhristov;

import java.io.File;

import static main.java.rosenhristov.interpreter.Constants.SOURCE_FILE_EXTENSION;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Utils {

    public static boolean isValidSourceFile(File sourceFile) {
        return nonNull(sourceFile) || sourceFile.exists() || sourceFile.getName().endsWith(SOURCE_FILE_EXTENSION);
    }

    public static boolean isValidProjectName(String projectName) {
        return isNotBlank(projectName);
    }

    public static boolean isValidLocation(String projectLocation) {
        return isNotBlank(projectLocation) && (projectLocation.contains("/") || projectLocation.contains("\\"));
    }

    public static boolean isBlank(String string) {
        return isEmpty(string) || string.equals(" ");
    }

    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }

    public static boolean isEmpty(String string) {
        return isNull(string) || string.isEmpty();
    }

    public static boolean isFileOrEmptyDir(File dir) {
        return isNull(dir) || !dir.exists() || !dir.isDirectory() || dir.listFiles().length == 0;
    }

    public static boolean isNotEmptyDir(File dir) {
        return !isFileOrEmptyDir(dir);
    }

}
