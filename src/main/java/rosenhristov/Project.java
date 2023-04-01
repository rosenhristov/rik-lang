package main.java.rosenhristov;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
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

    public Map<String, List<String>> buildSourceCodeMap(Map<File, List<File>> projectMap) {
        Map<String, List<String>> sourcecodeMap = new LinkedHashMap<>();
        projectMap.entrySet()
                .stream()
                .forEach(entry -> {
                    if (entry.getValue() == null || entry.getValue().size() == 0) {
                        return;
                    }
                    sourcecodeMap.put(
                            entry.getKey().getPath(),
                            entry.getValue()
                                    .stream()
                                    .map(file -> {
                                        FileInputStream fileInputStream = null;
                                        String sourceCode;
                                        try {
                                            fileInputStream = new FileInputStream(file);
                                            sourceCode = new String(fileInputStream.readAllBytes());
                                        } catch (FileNotFoundException e) {
                                            throw new RuntimeException(String.format("File %s does not exist.", file.getName()), e);
                                        } catch (IOException e) {
                                            throw new RuntimeException(String.format("Problems reading file %s.", file.getName()), e);
                                        } finally {
                                            if (fileInputStream != null) {
                                                try {
                                                    fileInputStream.close();
                                                } catch (IOException e) {
                                                    throw new RuntimeException(
                                                            "Problems closing input stream for reading file "
                                                                    + file.getName(), e);
                                                }
                                            }
                                        }

                                        return sourceCode;
                                    })
                                    .collect(toList()));
                });

        return sourcecodeMap;
    }


    private boolean isSourceFile(File file) {
        return nonNull(file) && file.exists() && file.isFile() && file.getName().endsWith(SOURCE_FILE_EXTENSION);
    }

    private static String normalizePath(String locationPath) {
        return locationPath.endsWith("/") ? locationPath : locationPath + "/";
    }

}
