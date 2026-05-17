package com.chirag.bindyourkeys.mixin;

import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.*;

@Mixin(value = KeyBindsScreen.class, remap = false)
public abstract class KeybindEntryMixin extends Screen {

    private EditBox searchBox;
    private final Map<String, String> toggleBindings = new HashMap<>();
    private final Map<String, String> normalBindings = new HashMap<>();
    private String selectedAction = null;
    private boolean settingToggle = false;
    private boolean settingNormal = false;

    protected KeybindEntryMixin(Component title) {
        super(title);
    }

    @Inject(method = "method_60329", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // Search box
        searchBox = new EditBox(
                this.font,
                this.width / 2 - 100, 22, 200, 18,
                Component.literal("Search keybinds..."));
        searchBox.setHint(Component.literal("Search keybinds..."));
        this.addRenderableWidget(searchBox);
    }

    @Inject(method = "method_25394", at = @At("TAIL"))
    private void onRender(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (searchBox != null) {
            searchBox.render(context, mouseX, mouseY, delta);
        }

        // Draw overlay buttons for each keybind row
        if (this.minecraft == null) return;
        KeyMapping[] keys = this.minecraft.options.keyMappings;
        String query = searchBox != null ? searchBox.getValue().toLowerCase() : "";

        int y = 48;
        for (KeyMapping key : keys) {
            if (!query.isEmpty() && !key.getName().toLowerCase().contains(query)) continue;
            if (y > this.height - 60) break;

            // Normal bind button (left half of original button area)
            String normalKey = normalBindings.getOrDefault(key.getName(), "Normal");
            context.fill(this.width - 220, y, this.width - 120, y + 18, 0xFF555555);
            context.drawString(this.font, normalKey, this.width - 215, y + 5, 0xFFFFFFFF, false);

            // Toggle bind button (right of normal)
            String toggleKey = toggleBindings.getOrDefault(key.getName(), "Toggle");
            context.fill(this.width - 118, y, this.width - 38, y + 18, 0xFF335533);
            context.drawString(this.font, toggleKey, this.width - 113, y + 5, 0xFF88FF88, false);

            y += 24;
        }
    }

    @Inject(method = "method_25402", at = @At("HEAD"))
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfo ci) {
        if (this.minecraft == null) return;
        KeyMapping[] keys = this.minecraft.options.keyMappings;
        String query = searchBox != null ? searchBox.getValue().toLowerCase() : "";

        int y = 48;
        for (KeyMapping key : keys) {
            if (!query.isEmpty() && !key.getName().toLowerCase().contains(query)) continue;
            if (y > this.height - 60) break;

            // Check normal button click
            if (mouseX >= this.width - 220 && mouseX <= this.width - 120
                    && mouseY >= y && mouseY <= y + 18) {
                selectedAction = key.getName();
                settingNormal = true;
                settingToggle = false;
            }

            // Check toggle button click
            if (mouseX >= this.width - 118 && mouseX <= this.width - 38
                    && mouseY >= y && mouseY <= y + 18) {
                selectedAction = key.getName();
                settingToggle = true;
                settingNormal = false;
            }

            y += 24;
        }
    }
}
