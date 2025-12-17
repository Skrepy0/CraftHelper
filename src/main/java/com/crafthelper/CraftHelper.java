package com.crafthelper;

import com.crafthelper.command.ModCommands;
import com.crafthelper.event.ModEvents;
import com.crafthelper.item.ModItemTags;
import com.crafthelper.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftHelper implements ModInitializer {
    public static final String MOD_ID = "crafthelper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initialize Mod Register");

        // 注册物品相关
        ModItems.registerModItems();
        ModItemTags.registerModItemTags();

        // 注册命令
        ModCommands.registerModCommands();

        // 注册事件
        ModEvents.registerEvents();
    }
}