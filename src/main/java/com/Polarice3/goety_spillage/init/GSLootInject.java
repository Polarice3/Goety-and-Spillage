package com.Polarice3.goety_spillage.init;

import com.Polarice3.goety_spillage.GoetySpillage;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetySpillage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GSLootInject {
    @SubscribeEvent
    public static void InjectLootTables(LootTableLoadEvent evt) {
        String name = evt.getName().toString();
        if (name.equals("illageandspillage:entities/ragno")) {
            evt.getTable().addPool(getInjectPool("entities/ragno"));
        }
        if (name.equals("illageandspillage:entities/old_ragno")) {
            evt.getTable().addPool(getInjectPool("entities/ragno"));
        }
    }

    private static LootPool getInjectPool(String entryName) {
        return LootPool.lootPool().add(getInjectEntry(entryName)).name("goety_spillage_inject_pool").build();
    }

    private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name) {
        return LootTableReference.lootTableReference(GoetySpillage.location("inject/" + name));
    }
}
