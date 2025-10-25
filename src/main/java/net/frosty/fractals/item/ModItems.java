package net.frosty.fractals.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.frosty.fractals.Fractals;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Fractals.MOD_ID, name), item);
    }

    public static void registerModItems(){
        Fractals.LOGGER.info("Registering Mod Items for " + Fractals.MOD_ID);

//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.#).register(fabricItemGroupEntries -> {
//            fabricItemGroupEntries.add(#);
//        });
    }

}
