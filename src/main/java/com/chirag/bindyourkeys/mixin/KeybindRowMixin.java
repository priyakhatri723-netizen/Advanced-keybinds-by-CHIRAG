package com.chirag.bindyourkeys.mixin;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.class_4185$class_12231", remap = false)
public abstract class KeybindRowMixin {

    @Inject(method = "method_75752", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics graphics,
            int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
    }
}
