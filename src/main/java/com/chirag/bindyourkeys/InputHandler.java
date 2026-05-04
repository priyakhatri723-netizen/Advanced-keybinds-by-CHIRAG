package com.chirag.bindyourkeys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import java.util.*;

public class InputHandler {

    private static final Set<String> heldKeys = new HashSet<>();

    public static void onKeyPressed(String key) {
        heldKeys.add(key);
        checkBindings();
    }

    public static void onKeyReleased(String key) {
        heldKeys.remove(key);
    }

    private static void checkBindings() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        for (KeyBinding binding : client.options.allKeys) {
            String actionId = binding.getTranslationKey();

            // Check solo bindings
            List<String> solos = KeybindManager.soloBindings
                    .getOrDefault(actionId, Collections.emptyList());
            for (String key : solos) {
                if (heldKeys.contains(key) && heldKeys.size() == 1) {
                    KeyBinding.onKeyPressed(
                            net.minecraft.client.util.InputUtil
                                    .fromTranslationKey(binding.boundKeyTranslationKey));
                }
            }

            // Check combo bindings
            List<List<String>> combos = KeybindManager.comboBindings
                    .getOrDefault(actionId, Collections.emptyList());
            for (List<String> combo : combos) {
                if (heldKeys.containsAll(combo) 
                        && heldKeys.size() == combo.size()) {
                    KeyBinding.onKeyPressed(
                            net.minecraft.client.util.InputUtil
                                    .fromTranslationKey(binding.boundKeyTranslationKey));
                }
            }
        }
    }
}
