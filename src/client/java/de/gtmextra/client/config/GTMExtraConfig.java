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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Persistent, client-only settings for GTM Extra. */
public final class GTMExtraConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("gtm-extra.json");
    private static final Map<String, ItemOptions> ITEM_WHITELIST = createDefaultWhitelist();
    private static boolean itemGlowEnabled;
    private static int itemGlowColor = 0xFF8C00;

    private GTMExtraConfig() {
    }

    public static void load() {
        if (!Files.exists(FILE)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            itemGlowEnabled = getBoolean(root, "itemGlowEnabled", false);
            itemGlowColor = getColor(root, "itemGlowColor", itemGlowColor);
            loadItemWhitelist(root);
        } catch (Exception exception) {
            GTMExtra.LOGGER.warn("Could not read GTM Extra's client configuration", exception);
        }
    }

    public static boolean isItemGlowEnabled() {
        return itemGlowEnabled;
    }

    public static void setItemGlowEnabled(boolean enabled) {
        itemGlowEnabled = enabled;
        save();
    }

    public static List<String> getWhitelistedItemIds() {
        return List.copyOf(ITEM_WHITELIST.keySet());
    }

    public static boolean isItemGlowAllowed(String itemId) {
        ItemOptions options = ITEM_WHITELIST.get(itemId);
        return options != null && options.glow;
    }

    public static void setItemGlowAllowed(String itemId, boolean allowed) {
        ItemOptions options = ITEM_WHITELIST.get(itemId);
        if (options != null) {
            options.glow = allowed;
            save();
        }
    }

    public static boolean isItemNameAllowed(String itemId) {
        ItemOptions options = ITEM_WHITELIST.get(itemId);
        return options != null && options.name;
    }

    public static void setItemNameAllowed(String itemId, boolean allowed) {
        ItemOptions options = ITEM_WHITELIST.get(itemId);
        if (options != null) {
            options.name = allowed;
            save();
        }
    }

    public static int getItemGlowColor() {
        return itemGlowColor;
    }

    public static void setItemGlowColor(int rgb) {
        itemGlowColor = rgb & 0xFFFFFF;
        save();
    }

    public static void save() {
        JsonObject root = new JsonObject();
        root.addProperty("itemGlowEnabled", itemGlowEnabled);
        root.addProperty("itemGlowColor", String.format("#%06X", itemGlowColor));
        root.add("itemWhitelist", GSON.toJsonTree(ITEM_WHITELIST));

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

    private static Map<String, ItemOptions> createDefaultWhitelist() {
        Map<String, ItemOptions> whitelist = new LinkedHashMap<>();
        whitelist.put("jetpack", new ItemOptions());
        whitelist.put("katana", new ItemOptions());
        whitelist.put("golf_club", new ItemOptions());
        whitelist.put("wingsuit", new ItemOptions());
        whitelist.put("chain_boots", new ItemOptions());
        whitelist.put("kwons_booty_shorts", new ItemOptions());
        return whitelist;
    }

    private static void loadItemWhitelist(JsonObject root) {
        for (ItemOptions options : ITEM_WHITELIST.values()) {
            options.glow = true;
            options.name = true;
        }
        if (!root.has("itemWhitelist") || !root.get("itemWhitelist").isJsonObject()) {
            return;
        }
        JsonObject savedWhitelist = root.getAsJsonObject("itemWhitelist");
        savedWhitelist.entrySet().forEach(entry -> {
            ItemOptions options = ITEM_WHITELIST.get(entry.getKey());
            if (options == null || !entry.getValue().isJsonObject()) {
                return;
            }
            JsonObject savedOptions = entry.getValue().getAsJsonObject();
            options.glow = getBoolean(savedOptions, "glow", true);
            options.name = getBoolean(savedOptions, "name", true);
        });
    }

    private static final class ItemOptions {
        private boolean glow = true;
        private boolean name = true;
    }
}
