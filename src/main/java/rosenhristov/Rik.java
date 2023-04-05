package main.java.rosenhristov;

import main.java.rosenhristov.interpreter.Lexer;
import main.java.rosenhristov.interpreter.LexedMap;

import java.io.*;
import java.util.List;
import java.util.Map;

import static main.java.rosenhristov.interpreter.Constants.CONFIG_FILENAME;
import static main.java.rosenhristov.Utils.*;

public class Rik {

    private static String locationPath = "src/main/resources";

    private static String projectName = "proj";

    private static boolean isExistingProject = true;

    public static void main(String[] args) throws IOException {
        checkInitialProjectData();

        RikConfig rikConfig = getConfiguration();

        if (!rikConfig.exists()) {
            throw new RuntimeException("Could not load Rik configuration successfully.");
        }

        rikConfig.configure();

        Project project = Project.of(locationPath, projectName, isExistingProject);
        Map<File, List<File>> projectMap = project.buildProjectMap();
        Map<ProjectDir, List<SourceCode>> sourceCodeMap = project.buildSourceCodeMap(projectMap);
        LexedMap lexedMap = Lexer.of(sourceCodeMap).lexSourceCodeMap();

        lexedMap.print();
    }

    private static void checkInitialProjectData() {
        if (isBlank(locationPath) || isBlank(projectName)) {
            getProjectDataInput();
        }
    }

    private static RikConfig getConfiguration() {
        return RikConfig.of(buildConfigurationPath());
    }

    private static void getProjectDataInput() {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader scanner = new BufferedReader(reader);
        askForProjectLocation(scanner);
        askForProjectName(scanner);
        askForProjectType(scanner);
        try {
            scanner.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Problems closing the stream readers for user input", e);
        }
    }

    private static void askForProjectLocation(BufferedReader scanner) {
        do  {
            System.out.println("Please, enter project location:");
            try {
                locationPath = scanner.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Problems reading project location path", e);
            }
        } while (!isValidLocation(locationPath));
    }


    private static void askForProjectName(BufferedReader scanner) {
        do {
            System.out.println("Please, enter project name:");
            try {
                Rik.projectName = scanner.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Problems reading project name", e);
            }
        } while (!isValidProjectName(projectName));
    }

    private static void askForProjectType(BufferedReader scanner) {
        String isExistingProject;
        do  {
            System.out.println("Is it an existing project?:");
            try {
                isExistingProject = scanner.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Problems reading project location path", e);
            }
        } while (!isValidProjectType(isExistingProject));
        Rik.isExistingProject = Boolean.parseBoolean(isExistingProject);
    }

    private static boolean isValidProjectType(String isExistingProject) {
        return isExistingProject.equals("true") || isExistingProject.equals("false");
    }

    private static String buildConfigurationPath() {
        return String.format("%s/%s/src/%s", locationPath, projectName, CONFIG_FILENAME);
    }
}
