package com.Polarice3.goety_spillage.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModCreativeTab;
import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.items.GSItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class GSCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GoetySpillage.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(Goety.MOD_ID, () -> CreativeModeTab.builder()
            .withTabsBefore(ModCreativeTab.TAB.getId())
            .icon(() -> GSItems.SKULL_BOMB.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.goety"))
            .displayItems((parameters, output) -> {
                GSItems.ITEMS.getEntries().forEach(i -> {
                    output.accept(i.get());
                });
            }).build());

}
