package com.crafthelper;

import com.crafthelper.datagen.ModENUSLanProvider;
import com.crafthelper.datagen.ModItemTagProvider;
import com.crafthelper.datagen.ModModelsProvider;
import com.crafthelper.datagen.ModZHCNLanProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;

public class CraftHelperDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModZHCNLanProvider::new);
        pack.addProvider(ModENUSLanProvider::new);
        pack.addProvider(ModModelsProvider::new);
        pack.addProvider(ModItemTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {

    }
}
