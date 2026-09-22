package de.gtmextra.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.gtmextra.GTMExtra;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persistent, client-only settings for GTM Extra. */
public final class GTMExtraConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("gtm-extra.json");
    private static boolean sneakAnimationEnabled = true;
    private static boolean outlineHighlighterEnabled;
    private static int outlineColor = 0xFF0095;

    private GTMExtraConfig() {
    }

    public static void load() {
        if (!Files.exists(FILE)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            sneakAnimationEnabled = getBoolean(root, "sneakAnimationEnabled", true);
            outlineHighlighterEnabled = getBoolean(root, "outlineHighlighterEnabled", false);
            outlineColor = getColor(root, "outlineColor", outlineColor);
        } catch (Exception exception) {
            GTMExtra.LOGGER.warn("Could not read GTM Extra's client configuration", exception);
        }
    }

    public static boolean isSneakAnimationEnabled() {
        return sneakAnimationEnabled;
    }

    public static void setSneakAnimationEnabled(boolean enabled) {
        sneakAnimationEnabled = enabled;
        save();
    }

    public static boolean isOutlineHighlighterEnabled() {
        return outlineHighlighterEnabled;
    }

    public static void setOutlineHighlighterEnabled(boolean enabled) {
        outlineHighlighterEnabled = enabled;
        save();
    }

    public static int getOutlineColor() {
        return outlineColor;
    }

    public static void setOutlineColor(int rgb) {
        outlineColor = rgb & 0xFFFFFF;
        save();
    }

    public static void save() {
        JsonObject root = new JsonObject();
        root.addProperty("sneakAnimationEnabled", sneakAnimationEnabled);
        root.addProperty("outlineHighlighterEnabled", outlineHighlighterEnabled);
        root.addProperty("outlineColor", String.format("#%06X", outlineColor));

        Path temporaryFile = FILE.resolveSibling(FILE.getFileName() + ".tmp");
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
            try {
                Files.move(temporaryFile, FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            GTMExtra.LOGGER.warn("Could not save GTM Extra's client configuration", exception);
        }
    }

    private static boolean getBoolean(JsonObject root, String key, boolean fallback) {
        return root.has(key) ? root.get(key).getAsBoolean() : fallback;
    }

    private static int getColor(JsonObject root, String key, int fallback) {
        if (!root.has(key)) {
            return fallback;
        }

        String value = root.get(key).getAsString().replace("#", "");
        try {
            return Integer.parseInt(value, 16) & 0xFFFFFF;
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }
}
