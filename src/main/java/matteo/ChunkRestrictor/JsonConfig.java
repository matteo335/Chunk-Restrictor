package matteo.ChunkRestrictor;

import matteo.ChunkRestrictor.json.ConfigValuesRecord;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;

public class JsonConfig {
    public static final Map<String, ConfigValuesRecord> VALUES = new HashMap<>();
    public static File FILE = FMLPaths.CONFIGDIR.get().resolve("Chunk-Restrictor-Compats.json").toFile();
    public static Gson GSON = new Gson();

    public static Map<String, ConfigValuesRecord> getValues() {
        return VALUES;
    }

    public static void readJsonConfig() {
        try (FileReader reader = new FileReader(FILE)) {
            ConfigValuesRecord object = GSON.fromJson(reader, ConfigValuesRecord.class);

            VALUES.put("FTBChunksCompat", object);

        } catch (IOException exception) {
            throw new RuntimeException("Chunk Restrictor was not able to properly read Chunk-Restrictor-Compats.json", exception);
        }
    }

    public static void createJsonConfig() {
        Main.LOGGER.info("Chunk-Restrictor-Compats.json was not found in the config folder, creating this file...");
        try (FileWriter writer = new FileWriter(FILE)) {
            writer.write("{\n\"FTBChunksCompat\": true\n}");
        } catch (IOException exception) {
            throw new RuntimeException("Chunk Restrictor was not able to create Chunk-Restrictor-Compats.json", exception);
        }
    }
}
