package com.crafthelper.item;

import com.crafthelper.CraftHelper;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTags {
    public static TagKey<Item> BUILDING_BLOCKS = of("building_blocks");
    public static TagKey<Item> COLOR_BLOCKS = of("color_blocks");
    public static TagKey<Item> NATURAL_BLOCKS = of("natural_blocks");
    public static TagKey<Item> FUNCTIONAL_BLOCKS = of("functional_blocks");
    public static TagKey<Item> REDSTONE_BLOCKS = of("redstone_blocks");
    public static TagKey<Item> TOOLS_UTILITIES = of("tools_utilities");
    public static TagKey<Item> COMBAT = of("combat");
    public static TagKey<Item> FOOD_DRINKS = of("food_drinks");
    public static TagKey<Item> INGREDIENTS = of("ingredients");
    public static TagKey<Item> SPAWN_EGGS = of("spawn_eggs");
    public static TagKey<Item> OPERATOR_UTILITIES = of("operator_utilities");
    public static TagKey<Item> of(String id){
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(CraftHelper.MOD_ID,id));
    }
    public static void registerModItemTags(){
        CraftHelper.LOGGER.info("Register Mod Item Tags");
    }

}
