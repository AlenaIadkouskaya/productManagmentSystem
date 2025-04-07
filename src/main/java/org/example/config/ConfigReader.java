package org.example.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigReader {
    private String url;
    private String user;
    private String password;
    private String ordersDirectory;

    public ConfigReader(String configFilePath) {
        loadConfig(configFilePath);
    }

    private void loadConfig(String configFilePath) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Plik konfiguracyjny nie został znaleziony: " + configFilePath);
            }

            Map<String, Object> config = yaml.load(inputStream);
            Map<String, String> databaseConfig = (Map<String, String>) config.get("database");

            this.url = databaseConfig.get("url");
            this.user = databaseConfig.get("user");
            this.password = databaseConfig.get("password");

            Map<String, String> fileConfig = (Map<String, String>) config.get("file");
            this.ordersDirectory = fileConfig.get("ordersDirectory");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Błąd podczas ładowania konfiguracji", e);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getOrdersDirectory() {
        return ordersDirectory;
    }
}