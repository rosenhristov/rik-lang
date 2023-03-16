package main.java.rosenhristov;

import main.java.rosenhristov.interpreter.Lexer;
import main.java.rosenhristov.interpreter.LexingMap;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static main.java.rosenhristov.interpreter.Constants.CONFIG_FILENAME;
import static main.java.rosenhristov.Utils.*;

public class Rik {

    private static String locationPath = "src/main/resources";

    private static String projectName = "proj";

    private static boolean isExistingProject = true;

    public static void main(String[] args) throws IOException {

        checkInitialProjectData();

        RikConfig rikConfig = getConfiguration();

        if (rikConfig.exists()) {
            rikConfig.configure();
        }

        Project project = Project.of(locationPath, projectName, isExistingProject);
        Map<File, List<File>> projectMap = project.buildProjectMap();
        Map<String, List<String>> sourceCodeMap = buildSourceCodeMap(projectMap);

        LexingMap lexingMap = Lexer.of(projectMap).lexProjectMap();

        lexingMap.print();
    }



    private static void checkInitialProjectData() {
        if (isBlank(locationPath) || isBlank(projectName)) {
            getProjectDataInput();
        }
    }

    private static RikConfig getConfiguration() {
        return RikConfig.of(buildConfigurationPath());
    }

    private static Map<String, List<String>> buildSourceCodeMap(Map<File, List<File>> projectMap) {
        Map<String, List<String>> sourcecodeMap = new LinkedHashMap<>();
        projectMap.entrySet()
                .stream()
                .forEach(entry -> sourcecodeMap.put(entry.getKey().getPath(),
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
                                .collect(toList())));

        return sourcecodeMap;
    }

    private static void getProjectDataInput() {
        String projLocation = null;
        String projectName = null;
        String isExistingProj = null;
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader scanner = new BufferedReader(reader);
        askForProjectLocation(scanner, projLocation);
        askForProjectName(scanner, projectName);
        askForProjectType(scanner, isExistingProj);
        try {
            scanner.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Problems closing the stream readers for user input", e);
        }
    }

    private static void askForProjectLocation(BufferedReader scanner, String projLocation) {
        do  {
            System.out.println("Please, enter project location:");
            try {
                projLocation = scanner.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Problems reading project location path", e);
            }
        } while (!isValidLocation(projLocation));

        locationPath = projLocation;
    }


    private static void askForProjectName(BufferedReader scanner, String projectName) {
        do {
            System.out.println("Please, enter project name:");
            try {
                projectName = scanner.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Problems reading project name", e);
            }
        } while (!isValidProjectName(projectName));

        Rik.projectName = projectName;
    }

    private static void askForProjectType( BufferedReader scanner, String isExistingProj) {
        do  {
            System.out.println("Is it an existing project?:");
            try {
                isExistingProj = scanner.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Problems reading project location path", e);
            }
        } while (!isValidProjectType(isExistingProj));

        isExistingProject = Boolean.parseBoolean(isExistingProj);
    }

    private static boolean isValidProjectType(String isExistingProject) {
        return isExistingProject.equals("true") || isExistingProject.equals("false");
    }

    private static String buildConfigurationPath() {
        return String.format("%s/%s/src/%s", locationPath, projectName, CONFIG_FILENAME);
    }
}
