package main.java.rosenhristov.interpreter;

public class LexedSourceFile {

    private LineTokensMap lineTokensMap;

    public LexedSourceFile(LineTokensMap lexedLineMaps) {
        this.lineTokensMap = lexedLineMaps;
    }

    public LineTokensMap getLineTokensMap() {
        return lineTokensMap;
    }

    public void setLexedLines(LineTokensMap lineTokensMap) {
        this.lineTokensMap = lineTokensMap;
    }
}
