package com.chirag.bindyourkeys;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class KeybindManager {

    public static final Map<String, List<List<String>>> comboBindings = new HashMap<>();
    public static final Map<String, List<String>> soloBindings = new HashMap<>();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("bindyourkeys.json");

    public static void init() {
        load();
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            JsonObject combos = new JsonObject();
            JsonObject solos = new JsonObject();

            comboBindings.forEach((action, lists) -> {
                JsonArray outer = new JsonArray();
                for (List<String> combo : lists) {
                    JsonArray inner = new JsonArray();
                    combo.forEach(inner::add);
                    outer.add(inner);
                }
                combos.add(action, outer);
            });

            soloBindings.forEach((action, keys) -> {
                JsonArray arr = new JsonArray();
                keys.forEach(arr::add);
                solos.add(action, arr);
            });

            root.add("combos", combos);
            root.add("solos", solos);

            Files.writeString(CONFIG_PATH,
                    new GsonBuilder().setPrettyPrinting().create().toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) return;
        try {
            JsonObject root = JsonParser.parseString(
                    Files.readString(CONFIG_PATH)).getAsJsonObject();

            if (root.has("combos")) {
                root.getAsJsonObject("combos").entrySet().forEach(e -> {
                    List<List<String>> lists = new ArrayList<>();
                    e.getValue().getAsJsonArray().forEach(outer -> {
                        List<String> combo = new ArrayList<>();
                        outer.getAsJsonArray().forEach(k -> combo.add(k.getAsString()));
                        lists.add(combo);
                    });
                    comboBindings.put(e.getKey(), lists);
                });
            }

            if (root.has("solos")) {
                root.getAsJsonObject("solos").entrySet().forEach(e -> {
                    List<String> keys = new ArrayList<>();
                    e.getValue().getAsJsonArray().forEach(k -> keys.add(k.getAsString()));
                    soloBindings.put(e.getKey(), keys);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearAll(String actionId) {
        comboBindings.remove(actionId);
        soloBindings.remove(actionId);
        save();
    }
}
