package com.chirag.bindyourkeys.mixin;

import com.chirag.bindyourkeys.KeybindManager;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.Screen;
import java.util.*;

@Mixin(KeybindsScreen.class)
public abstract class KeybindsScreenMixin extends Screen {

    private TextFieldWidget searchBox;
    private String searchQuery = "";

    protected KeybindsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        searchBox = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100, 10, 200, 20,
                Text.literal("Search keybinds..."));
        searchBox.setPlaceholder(Text.literal("Search keybinds..."));
        searchBox.setChangedListener(text -> searchQuery = text.toLowerCase());
        this.addDrawableChild(searchBox);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(net.minecraft.client.gui.DrawContext context,
            int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (searchBox != null) {
            searchBox.render(context, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void onMouseClicked(double mouseX, double mouseY,
            int button, CallbackInfo ci) {
        if (searchBox != null) {
            searchBox.mouseClicked(mouseX, mouseY, button);
        }
    }
}
