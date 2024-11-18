package ru.kate.ebook.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Configurator {

    private final static ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private final static String MAIN_FILE_NAME = "main.config";
    private final static String GRAPHICS_FILE_NAME = "graphics.config";
    private final static String NETWORK_FILE_NAME = "network.config";

    @Getter
    private final MainConfig mainConfig;
    @Getter
    private final GraphicsConfig graphicsConfig;
    @Getter
    private final NetworkConfig networkConfig;

    public Configurator() {
        mainConfig = loadMainConfig();
        graphicsConfig = loadGraphicsConfig();
        networkConfig = loadNetworkConfig();

    }

    private MainConfig loadMainConfig() {
        File file = new File(MAIN_FILE_NAME);
        if (file.exists()) {
            return mapper.convertValue(file, MainConfig.class);
        }
        return new MainConfig();
    }

    private GraphicsConfig loadGraphicsConfig() {
        return GraphicsConfig.builder().build();
    }

    private NetworkConfig loadNetworkConfig() {
        return NetworkConfig.builder().build();
    }

    private void saveMainConfig() throws IOException {
        FileWriter configFile = new FileWriter(MAIN_FILE_NAME);
        configFile.write(mapper.writeValueAsString(mainConfig));
    }

    /*
    String jarPath = System.getProperty("java.class.path");
        System.out.println("Path 1: " + jarPath);
        String jarPath2 = Config.class.getProtectionDomain().toString();
        System.out.println("Path 2: " + jarPath2);
     */
}
