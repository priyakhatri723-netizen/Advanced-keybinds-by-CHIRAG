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
import java.util.Arrays;

@Mixin(value = KeyBindsScreen.class, remap = false)
public abstract class KeybindsScreenMixin extends Screen {

    @Shadow @Final private net.minecraft.client.Options options = null;

    private EditBox searchBox;
    private String searchQuery = "";

    protected KeybindsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "method_60329", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // Search box at top
        searchBox = new EditBox(
                this.font,
                this.width / 2 - 100, 22, 200, 18,
                Component.literal("Search keybinds..."));
        searchBox.setHint(Component.literal("Search keybinds..."));
        searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchBox);

        // Bind Toggle button - positioned ABOVE the existing buttons
        this.addRenderableWidget(Button.builder(
                Component.literal("Bind Toggle"),
                btn -> { })
                .bounds(this.width / 2 - 155, this.height - 52, 150, 20)
                .build());

        // Normal Bind button
        this.addRenderableWidget(Button.builder(
                Component.literal("Normal Bind"),
                btn -> { })
                .bounds(this.width / 2 + 5, this.height - 52, 150, 20)
                .build());
    }

    private void onSearchChanged(String query) {
        searchQuery = query.toLowerCase().trim();
        // Filter keybindings
        if (this.minecraft != null && this.minecraft.options != null) {
            KeyMapping[] keys = this.minecraft.options.keyMappings;
            for (KeyMapping key : keys) {
                boolean visible = searchQuery.isEmpty() ||
                    key.getName().toLowerCase().contains(searchQuery);
            }
        }
    }

    @Inject(method = "method_25394", at = @At("TAIL"))
    private void onRender(GuiGraphics context,
            int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (searchBox != null) {
            searchBox.render(context, mouseX, mouseY, delta);
        }
    }
}
