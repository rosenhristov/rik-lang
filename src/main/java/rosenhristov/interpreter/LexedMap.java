package main.java.rosenhristov.interpreter;

import main.java.rosenhristov.ProjectDir;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class LexedMap extends LinkedHashMap<ProjectDir, List<LexedSourceFile>> {

    public void print() {
        this.values().stream()
                .flatMap(list -> list.stream())
                .collect(toList())
                .forEach(lexedFile -> {
                    LineTokensMap lexedLines = lexedFile.getLineTokensMap();
                    if (lexedLines.hasErrors()) {
                        lexedLines.printErrors();
                    }
                    lexedLines.printTokens();
                }
        );
    }

}
