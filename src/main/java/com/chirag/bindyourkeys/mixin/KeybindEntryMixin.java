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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.*;

@Mixin(value = KeyBindsScreen.class, remap = false)
public abstract class KeybindEntryMixin extends Screen {

    private EditBox searchBox;
    private final Map<String, String> toggleBindings = new HashMap<>();
    private final Map<String, String> normalBindings = new HashMap<>();
    private String pendingAction = null;
    private boolean settingToggle = false;
    private boolean settingNormal = false;
    private final List<Button> normalButtons = new ArrayList<>();
    private final List<Button> toggleButtons = new ArrayList<>();
    private final List<Button> resetButtons = new ArrayList<>();
    private final List<KeyMapping> visibleKeys = new ArrayList<>();

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
        searchBox.setResponder(query -> rebuildButtons());
        this.addRenderableWidget(searchBox);

        rebuildButtons();
    }

    private void rebuildButtons() {
        // Remove old buttons
        for (Button b : normalButtons) this.removeWidget(b);
        for (Button b : toggleButtons) this.removeWidget(b);
        for (Button b : resetButtons) this.removeWidget(b);
        normalButtons.clear();
        toggleButtons.clear();
        resetButtons.clear();
        visibleKeys.clear();

        if (this.minecraft == null) return;

        String query = searchBox != null ? searchBox.getValue().toLowerCase() : "";
        KeyMapping[] keys = this.minecraft.options.keyMappings;

        int btnWidth = 70;
        int btnHeight = 16;
        int resetWidth = 40;
        int resetX = this.width - 10 - resetWidth;
        int toggleX = resetX - 5 - btnWidth;
        int normalX = toggleX - 3 - btnWidth;

        int y = 48;
        for (KeyMapping key : keys) {
            if (!query.isEmpty() && !key.getName().toLowerCase().contains(query)) continue;
            if (y > this.height - 60) break;

            visibleKeys.add(key);
            final String actionName = key.getName();
            final int btnY = y + 2;

            // Normal button
            String normalLabel = normalBindings.getOrDefault(actionName, key.getTranslatedKeyMessage().getString());
            Button normalBtn = Button.builder(
                    Component.literal(normalLabel),
                    btn -> {
                        pendingAction = actionName;
                        settingNormal = true;
                        settingToggle = false;
                        btn.setMessage(Component.literal("> Press Key <"));
                    })
                    .bounds(normalX, btnY, btnWidth, btnHeight)
                    .build();
            this.addRenderableWidget(normalBtn);
            normalButtons.add(normalBtn);

            // Toggle button
            String toggleLabel = toggleBindings.getOrDefault(actionName, "---");
            Button toggleBtn = Button.builder(
                    Component.literal(toggleLabel),
                    btn -> {
                        pendingAction = actionName;
                        settingToggle = true;
                        settingNormal = false;
                        btn.setMessage(Component.literal("> Press Key <"));
                    })
                    .bounds(toggleX, btnY, btnWidth, btnHeight)
                    .build();
            this.addRenderableWidget(toggleBtn);
            toggleButtons.add(toggleBtn);

            // Reset button
            Button resetBtn = Button.builder(
                    Component.literal("Reset"),
                    btn -> {
                        normalBindings.remove(actionName);
                        toggleBindings.remove(actionName);
                        key.setKey(key.getDefaultKey());
                        KeyMapping.resetMapping();
                        rebuildButtons();
                    })
                    .bounds(resetX, btnY, resetWidth, btnHeight)
                    .build();
            this.addRenderableWidget(resetBtn);
            resetButtons.add(resetBtn);

            y += 24;
        }
    }

    @Inject(method = "method_25394", at = @At("TAIL"))
    private void onRender(GuiGraphics context,
            int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (searchBox != null) {
            searchBox.render(context, mouseX, mouseY, delta);
        }
        if (pendingAction != null) {
            context.drawCenteredString(this.font,
                    settingNormal ? "Press key for Normal..." : "Press key for Toggle...",
                    this.width / 2, this.height - 52, 0xFFFFFF00);
        }
    }

    @Inject(method = "method_1447", at = @At("HEAD"))
    private void onKeyPressed(int keyCode, int scanCode, int modifiers,
            CallbackInfoReturnable<Boolean> cir) {
        if (pendingAction == null) return;
        String keyName = org.lwjgl.glfw.GLFW.glfwGetKeyName(keyCode, scanCode);
        if (keyName == null) keyName = "Key" + keyCode;
        keyName = keyName.toUpperCase();
        if (settingNormal) {
            normalBindings.put(pendingAction, keyName);
        } else if (settingToggle) {
            toggleBindings.put(pendingAction, keyName);
        }
        pendingAction = null;
        settingNormal = false;
        settingToggle = false;
        rebuildButtons();
    }
}
