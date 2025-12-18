package com.crafthelper.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModZHCNLanProvider extends FabricLanguageProvider {
    public ModZHCNLanProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput,"zh_cn", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("screen.custom_item.title","物品选择器 - ");
        translationBuilder.add("screen.custom_item.select_item.text_field","搜索物品...");
        translationBuilder.add("screen.custom_item.select_item.item_type.all","全部");
        translationBuilder.add("screen.custom_item.select_item.item_type.others","其他");

        translationBuilder.add("screen.custom_item.text.item_count","物品数量");
        translationBuilder.add("screen.custom_item.select_item.button.enchant","附魔属性");
        translationBuilder.add("screen.custom_item.select_item.button.attribute","属性");
        translationBuilder.add("screen.custom_item.select_item.button.info","信息属性");
        translationBuilder.add("screen.custom_item.select_item.button.tool","工具属性");
        translationBuilder.add("screen.custom_item.select_item.button.food","食物属性");
        translationBuilder.add("screen.custom_item.select_item.button.other","其他属性");

        translationBuilder.add("screen.custom_item.select_item.button.done","完成");
        translationBuilder.add("screen.custom_item.select_item.button.back","× 返回");
        translationBuilder.add("screen.custom_item.selectedItemInfo.selected","选中: ");
        translationBuilder.add("screen.custom_item.selectedItemInfo.itemId","物品id: ");
        translationBuilder.add("key.crafthelper.custom_item","自定义物品界面");
    }
}
