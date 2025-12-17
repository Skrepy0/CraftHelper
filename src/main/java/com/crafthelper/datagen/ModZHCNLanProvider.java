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
        translationBuilder.add("screen.custom_item.select_item.button.done","确定");
        translationBuilder.add("screen.custom_item.select_item.button.back","× 返回");
        translationBuilder.add("screen.custom_item.selectedItemInfo.selected","选中: ");
        translationBuilder.add("screen.custom_item.selectedItemInfo.itemId","物品id: ");
    }
}
