package com.chirag.bindyourkeys;

import net.fabricmc.api.ClientModInitializer;

public class BindYourKeysMod implements ClientModInitializer {

    public static final String MOD_ID = "bindyourkeys";

    @Override
    public void onInitializeClient() {
        KeybindManager.init();
    }
}
