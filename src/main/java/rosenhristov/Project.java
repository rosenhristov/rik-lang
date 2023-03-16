package main.java.rosenhristov;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static main.java.rosenhristov.interpreter.Constants.SOURCE_FILE_EXTENSION;
import static main.java.rosenhristov.Utils.isFileOrEmptyDir;

public class Project {

    private String locationPath;
    private String projectName;
    private String projectLocation;
    private boolean isExistingProject;

    private Project(String locationPath, String projectName, boolean isExistingProject) {
        this.locationPath = locationPath;
        this.projectName = projectName;
        this.projectLocation = normalizePath(locationPath) + projectName;
        this.isExistingProject = isExistingProject;
    }

    public static Project of(String locationPath, String projectName, boolean isExistingProject) {
        return new Project(locationPath, projectName, isExistingProject);
    }
    public Map<File, List<File>> buildProjectMap() {
        return isExistingProject ?
                loadExistingProject(this.projectLocation)
                : createNewProject(locationPath, projectName);
    }

    public Map<File, List<File>> createNewProject(String locationPath, String projectName) {
        locationPath = normalizePath(locationPath);
        File main = new File(locationPath + projectName + "/src/main/");
        main.mkdirs();
        File test = new File(locationPath + projectName + "/src/test/");
        test.mkdirs();
        Map projectDirs = new LinkedHashMap();
        projectDirs.put(main, new LinkedList<>());
        projectDirs.put(test, new LinkedList<>());

        return projectDirs;
    }

    public Map<File, List<File>> loadExistingProject(String projectPath) {
        File projectRoot = new File(projectPath);
        Map<File, List<File>> projectFiles = new LinkedHashMap<>();
        if(isFileOrEmptyDir(projectRoot)) {
            return projectFiles;
        }
        Set<File> allDirs = new LinkedHashSet<>();
        listDirectories(projectRoot, allDirs);
        allDirs.stream().forEach(dir -> projectFiles.put(dir, listSourceFiles(dir)));
        return projectFiles;
    }

    public List<File> listSourceFiles(File directory) {
        return Arrays.stream(directory.listFiles())
                .filter(file -> isSourceFile(file))
                .collect(Collectors.toList());
    }

    public void listDirectories(File directory, Set<File> allDirs) {
        if (isNull(allDirs)) {
            allDirs = new LinkedHashSet<>();
        }
        if (isFileOrEmptyDir(directory)) {
            return;
        }
        allDirs.add(directory);
        List<File> childDirs = Arrays
                .stream(directory.listFiles())
                .filter(record -> record.isDirectory())
                .collect(Collectors.toList());
        allDirs.addAll(childDirs);
        for (File dir : childDirs) {
            listDirectories(dir, allDirs);
        }
    }

    private boolean isSourceFile(File file) {
        return nonNull(file) && file.exists() && file.isFile() && file.getName().endsWith(SOURCE_FILE_EXTENSION);
    }

    private static String normalizePath(String locationPath) {
        return locationPath.endsWith("/") ? locationPath : locationPath + "/";
    }

}
