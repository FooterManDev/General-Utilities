package dev.footer.gutils.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static dev.footer.gutils.GeneralUtilities.LOGGER;

public class ExportJSON {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void export(Map<String, Object> props, String fileName, JsonExportDirs exportDir) {
        if(Config.clientConfig.json.get()) {

            JsonObject json = new JsonObject();
            props.forEach((key, value) -> {
                switch (value) {
                    case Number number -> json.addProperty(key, number);
                    case Boolean b -> json.addProperty(key, b);
                    case Character c -> json.addProperty(key, c);
                    case null, default -> json.addProperty(key, Objects.requireNonNull(value).toString());
                }
            });

            String jsonOut = gson.toJson(json);

            File subOutputDir = new File(Constants.json_out, exportDir.getDirName());
            if (!subOutputDir.exists() && !subOutputDir.mkdirs()) {
                LOGGER.error("Failed to create directory: {}", subOutputDir.getAbsolutePath());
                return;
            }

            File outFile = new File(subOutputDir, fileName + ".json");

            try (FileWriter file = new FileWriter(outFile)) {
                file.write(jsonOut);
                file.flush();
            } catch (IOException e) {
                LOGGER.error("Failed to write to file: {} {}", outFile.getAbsolutePath(), e);
            }
        }
    }
}
