package org.lab3.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationManager {
    private final Properties props;
    private final String driverName;
    private ConfigurationManager() {
        driverName = "com.mysql.cj.jdbc.Driver";
        props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("C:\\Users\\alesy\\Desktop\\OnlyFamcs\\3_year\\WEB\\lab3_Vyshnikova\\src\\main\\resources\\database.properties"))){
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ConfigurationManager getInstance() {
        return new ConfigurationManager();
    }
    public String getURL() {
        return props.getProperty("url");
    }
    public String getUsername() {
        return props.getProperty("username");
    }
    public String getPassword() {
        return props.getProperty("password");
    }
    public String getDriverName() {
        return driverName;
    }
}

