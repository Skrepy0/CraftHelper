package com.crafthelper.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModENUSLanProvider extends FabricLanguageProvider {
    public ModENUSLanProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput,"en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("screen.custom_item.title","Item Selector - ");
        translationBuilder.add("screen.custom_item.select_item.text_field","Search Item...");
        translationBuilder.add("screen.custom_item.select_item.item_type.all","All Items");
        translationBuilder.add("screen.custom_item.select_item.item_type.others","Others");
        translationBuilder.add("screen.custom_item.select_item.button.done","Done");
        translationBuilder.add("screen.custom_item.select_item.button.back","× Back");
        translationBuilder.add("screen.custom_item.selectedItemInfo.selected","Selected: ");
        translationBuilder.add("screen.custom_item.selectedItemInfo.itemId","id: ");
    }
}
