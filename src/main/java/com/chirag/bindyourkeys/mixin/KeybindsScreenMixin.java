package com.chirag.bindyourkeys.mixin;

import net.minecraft.class_6599;
import net.minecraft.class_459;
import net.minecraft.class_4667;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = class_6599.class, remap = false)
public abstract class KeybindsScreenMixin extends class_4667 {

    private class_459 searchBox;
    private String searchQuery = "";
    private boolean initialized = false;

    protected KeybindsScreenMixin(net.minecraft.class_2561 title) {
        super(title);
    }

    @Inject(method = "method_60329", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        searchBox = new class_459(
                this.field_22763,
                this.field_1839 / 2 - 100, 6, 200, 18,
                net.minecraft.class_2561.method_43471("Search keybinds..."));
        this.method_25330(searchBox);
        initialized = true;
    }

    @Inject(method = "method_25394", at = @At("TAIL"))
    private void onRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (searchBox != null) {
            searchBox.method_25394(context, mouseX, mouseY, delta);
        }
    }
}
