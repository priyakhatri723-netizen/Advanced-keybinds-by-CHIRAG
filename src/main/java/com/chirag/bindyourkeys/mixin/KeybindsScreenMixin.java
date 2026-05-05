package com.chirag.bindyourkeys.mixin;

import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsScreen.class)
public abstract class KeybindsScreenMixin extends Screen {

    private EditBox searchBox;
    private String searchQuery = "";

    protected KeybindsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        searchBox = new EditBox(
                this.font,
                this.width / 2 - 100, 10, 200, 20,
                Component.literal("Search keybinds..."));
        searchBox.setHint(Component.literal("Search keybinds..."));
        searchBox.setResponder(text -> searchQuery = text.toLowerCase());
        this.addRenderableWidget(searchBox);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics context,
            int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (searchBox != null) {
            searchBox.render(context, mouseX, mouseY, delta);
        }
    }
}
