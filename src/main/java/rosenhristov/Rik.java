package main.java.rosenhristov;

import main.java.rosenhristov.interpreter.Lexer;
import main.java.rosenhristov.interpreter.Token;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Rik {

    public static void main(String[] args) throws IOException {
        ProjectLoader projectLoader = new ProjectLoader();
        Map<File, List<File>> projectMap = projectLoader.createProject("C:\\Users\\rhristov\\personalProjects", "proj");
        List<File> sourceFiles = projectMap.values().stream()
                                    .flatMap(list -> list.stream())
                                    .collect(Collectors.toList());
        Lexer lexer = new Lexer();
        for (File sourceFile : sourceFiles) {
            List<Token> tokens = lexer.lex(sourceFile);
            for (Token token : tokens) {
                System.out.println(String.format("%s: %s", token.getTypeName(),
                        token.getToken().equals("\n") ? token.getTypeName().toLowerCase() : token.getToken()));
            }
        }
    }
}
