package com.chirag.bindyourkeys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
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
        Minecraft client = Minecraft.getInstance();
        if (client == null) return;

        for (KeyMapping binding : client.options.keyMappings) {
            String actionId = binding.getName();

            List<String> solos = KeybindManager.soloBindings
                    .getOrDefault(actionId, Collections.emptyList());
            for (String key : solos) {
                if (heldKeys.contains(key) && heldKeys.size() == 1) {
                    KeyMapping.click(
                        InputConstants.getKey(
                            binding.getDefaultKey().getName()));
                }
            }

            List<List<String>> combos = KeybindManager.comboBindings
                    .getOrDefault(actionId, Collections.emptyList());
            for (List<String> combo : combos) {
                if (heldKeys.containsAll(combo)
                        && heldKeys.size() == combo.size()) {
                    KeyMapping.click(
                        InputConstants.getKey(
                            binding.getDefaultKey().getName()));
                }
            }
        }
    }
}
