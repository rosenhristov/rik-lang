package main.java.rosenhristov;

import main.java.rosenhristov.interpreter.Constants;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ProjectLoader {

    public Map<File, List<File>> createProject(String locationPath, String projectName) {
        locationPath = normalizePath(locationPath);
        File src = new File(locationPath + projectName + "/src/main/rik");
        src.mkdirs();
        File test = new File(locationPath + "/proj/src/test/rik");
        test.mkdirs();
        return loadProjectFiles(locationPath + projectName);
    }

    public Map<File, List<File>> loadProjectFiles(String locationPath) {
        Map<File, List<File>> projectFiles = new HashMap<>();
        File location = new File(locationPath);
        if(isNull(location)) {
           return projectFiles;
        }
        List<File> sourceFiles = listSourceFiles(location);
        projectFiles.put(location, sourceFiles);
        List<File> dirs = listDirectories(location);
        for(File dir : dirs) {
            projectFiles.putAll(loadProjectFiles(dir.getPath()));
        }
        return projectFiles;
    }

    public List<File> listSourceFiles(File directory) {
        return Arrays.stream(directory.listFiles())
                .filter(file -> isSourceFile(file))
                .collect(Collectors.toList());
    }

    public List<File> listDirectories(File directory) {
        return Arrays.stream(directory.listFiles())
                .filter(file -> file.isDirectory())
                .collect(Collectors.toList());
    }

    private boolean isSourceFile(File file) {
        return nonNull(file) && file.exists() && file.isFile() && file.getName().endsWith(Constants.SOURCE_FILE_EXTENSION);
    }

    private String normalizePath(String locationPath) {
        return locationPath.endsWith("/") ? locationPath : locationPath + "/";
    }
}
