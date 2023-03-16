package main.java.rosenhristov;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static main.java.rosenhristov.Utils.isEmpty;
import static main.java.rosenhristov.Utils.isNotBlank;

public class RikConfig {

    private String configurationPath;
    private File configFile;

    public Map<String, String> keywords;

    private RikConfig(String configurationPath) {
        this.configurationPath = configurationPath;
        this.configFile = new File(configurationPath);
    }

    public static RikConfig of(String configurationPath) {
        return new RikConfig(configurationPath);
    }

    public boolean exists() {
        return !isNull(configFile) && this.configFile.exists();
    }

    public void configure() {

        FileInputStream inputStream = null;
        String configInstructions;
        try {
            inputStream = new FileInputStream(configFile);
            configInstructions = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Problems reading %s configuration file.", configFile.getName()), e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Problems closing configuration file input stream.", e);
            }
        }

        Map<String, Properties> properties = extractPropertiesPerCathegory(configInstructions);

        //TODO finish configuration implementation

    }

    private Map<String, Properties> extractPropertiesPerCathegory(String configInstructions) {

        List<String> yamlFiles = readYamlFiles(configInstructions);

        Map<String, Properties> result = new LinkedHashMap<>();

        yamlFiles.stream()
                .forEach(yaml -> {
                    List<String> yamlPropsList = Arrays.asList(yaml.split("\r\n"));
                    String category = yamlPropsList.get(0);

                    if (category.endsWith(":")) {
                        yamlPropsList = yamlPropsList.stream()
                                .filter(prop -> !prop.endsWith(":"))
                                .collect(toList());
                        category = category.substring(0, yaml.indexOf(":"));
                        result.put(category, extractProperties(yamlPropsList));
                    }
                });

        return result;
    }

    private List<String> readYamlFiles(String config) {
        if (isEmpty(config) || !(config.contains("---") && config.contains("..."))) {
            throw new RuntimeException("File rik-config.yml is not properly set up.\n" +
                    "It has to contain --- as a start and does not end with ... at the end.\n" +
                    "Please, follow the yaml syntax when sdetting up the custom configuration, " +
                    "otherwise the configuration will not work properly.");
        }
        config = config.trim();

        return Arrays.stream(config.split("---"))
                .map(yaml -> {
                    yaml = yaml.trim();
                    if (yaml.endsWith("...")) {
                        yaml = yaml.substring(0, yaml.indexOf("..."));
                    }
                    return yaml;
                }).filter(yaml -> isNotBlank(yaml))
                .collect(toList());
    }

    private Properties extractProperties(List<String> yamlPropsList) {
        Properties properties = new Properties(yamlPropsList.size());

        yamlPropsList.stream()
                .forEach(string -> {
                    string = string.trim();
                    string = string.startsWith("- ")
                            ? string.substring(string.indexOf("- ") + 2)
                            : string;
                    string = string.endsWith(":")
                            ? string.substring(0, string.indexOf(":"))
                            : string;
                    String[] propertyPair = string.split(":");

                    properties.put(propertyPair[0].trim(), propertyPair[1].trim());
                });

        return properties;
    }
}
