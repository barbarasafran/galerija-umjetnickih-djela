package org.example.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.example.xml.AppConfig;

import java.io.IOException;
import java.io.InputStream;

public final class ConfigUtil {

    private static AppConfig config;

    private ConfigUtil() {
    }

    public static AppConfig ucitajKonfiguraciju() {
        if (config == null) {
            try {
                JAXBContext context = JAXBContext.newInstance(AppConfig.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();

                try (InputStream is = ConfigUtil.class.getResourceAsStream("/config/config.xml")) {
                    if (is == null) {
                        throw new RuntimeException("config.xml nije pronađen u resources/config/");
                    }
                    config = (AppConfig) unmarshaller.unmarshal(is);
                }
            } catch (JAXBException | IOException e) {
                throw new RuntimeException("Greška pri čitanju config.xml", e);
            }
        }
        return config;
    }
}