package org.currency_bot.services;

import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.currency_bot.dto.settings.UsersSettings;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import static org.currency_bot.constants.Constants.GSON;

public class ReadAndWrite {

    public static void writeSettingsToFile(Map<Long, UsersSettings> settings) throws IOException {
        try (FileWriter writer = new FileWriter("src/main/resources/entities.json")) {
            writer.write(GSON.toJson(settings));
            writer.flush();
        }
    }

    @SneakyThrows
    public static void readSavedSettings(Map<Long, UsersSettings> settings) {
        try (FileReader reader = new FileReader("src/main/resources/entities.json")) {
            Type type = new TypeToken<Map<Long, UsersSettings>>(){}.getType();
            Map<Long, UsersSettings> savedSettings = GSON.fromJson(reader, type);
            settings.putAll(savedSettings);
        }
    }
}
