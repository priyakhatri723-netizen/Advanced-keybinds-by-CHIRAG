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

@Mixin(value = KeyBindsScreen.class, remap = false)
public abstract class KeybindsScreenMixin extends Screen {

    private EditBox searchBox;
    private String searchQuery = "";

    protected KeybindsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "method_60329", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // Add search box at top
        searchBox = new EditBox(
                this.font,
                this.width / 2 - 100, 22, 200, 18,
                Component.literal("Search keybinds..."));
        searchBox.setHint(Component.literal("Search keybinds..."));
        searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchBox);

        // Move existing buttons up and split into two
        // "Bind Toggle" button (left half)
        this.addRenderableWidget(Button.builder(
                Component.literal("Bind Toggle"),
                btn -> {
                    // Toggle mode placeholder
                })
                .bounds(this.width / 2 - 155, this.height - 29, 150, 20)
                .build());

        // "Normal Bind" button (right half)  
        this.addRenderableWidget(Button.builder(
                Component.literal("Normal Bind"),
                btn -> {
                    // Normal bind mode placeholder
                })
                .bounds(this.width / 2 + 5, this.height - 29, 150, 20)
                .build());
    }

    private void onSearchChanged(String query) {
        searchQuery = query.toLowerCase();
        KeyBindsScreen screen = (KeyBindsScreen)(Object)this;
        // Filter keybindings list
        for (KeyMapping key : this.minecraft.options.keyMappings) {
            String name = key.getName().toLowerCase();
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
