package com.crafthelper;

import net.fabricmc.api.ClientModInitializer;

public class CraftHelperClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CraftHelper.LOGGER.info("Start Client");
    }
}
