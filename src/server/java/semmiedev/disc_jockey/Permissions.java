package semmiedev.disc_jockey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static semmiedev.disc_jockey.CommonInitializer.LOGGER;

public class Permissions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("disc_jockey").toFile();
    private static final File ALLOWED_PLAYERS_FILE = new File(CONFIG_DIR, "allowed_players.json");
    private static final Type UUID_SET_TYPE = new TypeToken<Set<UUID>>() {}.getType();

    public static final Set<UUID> ALLOWED_PLAYERS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void save() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
        try (FileWriter writer = new FileWriter(ALLOWED_PLAYERS_FILE)) {
            GSON.toJson(ALLOWED_PLAYERS, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save allowed players list", e);
        }
    }

    public static void load() {
        if (!ALLOWED_PLAYERS_FILE.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(ALLOWED_PLAYERS_FILE)) {
            Set<UUID> loadedUuids = GSON.fromJson(reader, UUID_SET_TYPE);
            if (loadedUuids != null) {
                ALLOWED_PLAYERS.clear();
                ALLOWED_PLAYERS.addAll(loadedUuids);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load allowed players list", e);
        }
    }
} 