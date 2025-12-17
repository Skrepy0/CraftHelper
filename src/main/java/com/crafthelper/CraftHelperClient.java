package com.crafthelper;

import com.crafthelper.client.gui.key.ModKeyEvents;
import net.fabricmc.api.ClientModInitializer;

public class CraftHelperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CraftHelper.LOGGER.info("Start Client");
        // 注册ui与按键
        ModKeyEvents.registerModKeyEvents();
    }
}
