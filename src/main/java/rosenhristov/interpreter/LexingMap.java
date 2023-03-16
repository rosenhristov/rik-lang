package main.java.rosenhristov.interpreter;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class LexingMap extends LinkedHashMap<String, List<LexingResult>> {

    public void print() {
        this.values().stream()
                .flatMap(list -> list.stream())
                .collect(toList())
                .stream()
                .forEach(lexingResult -> {
                    System.out.printf(
                            "\n--------------------\n%s:\n--------------------\n\n",
                            lexingResult.getSourceFile().getName());

                    if (lexingResult.hasErrors()) {
                        lexingResult.printErrors();
                    }
                    lexingResult.printTokens();
                });
    }

}
