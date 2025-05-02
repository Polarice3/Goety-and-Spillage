package com.Polarice3.goety_spillage.common.items;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.ServantSpawnEggs;
import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.items.curios.FreakyHatItem;
import com.Polarice3.goety_spillage.common.items.curios.FreakyRobeItem;
import com.Polarice3.goety_spillage.common.magic.spells.ImpSpell;
import com.Polarice3.goety_spillage.common.magic.spells.RequiemSpell;
import com.Polarice3.goety_spillage.common.magic.spells.SoulBeamSpell;
import com.Polarice3.goety_spillage.common.magic.spells.SpiritHandSpell;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GSItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GoetySpillage.MOD_ID);

    public static void init() {
        GSItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final FoodProperties MUTATED_FLESH_FOOD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.1F).effect(() -> new MobEffectInstance(EffectRegisterer.MUTATION.get(), 600, 0), 1.0F).meat().build();

    public static RegistryObject<Item> MUTATED_FLESH = ITEMS.register("mutated_flesh", () -> new Item(new Item.Properties().food(MUTATED_FLESH_FOOD)));

    public static RegistryObject<Item> PUMPKIN_BOMB = ITEMS.register("pumpkin_bomb", PumpkinBombItem::new);
    public static RegistryObject<Item> SKULL_BOMB = ITEMS.register("skull_bomb", SkullBombItem::new);

    public static RegistryObject<Item> CHAGRIN_PACKAGE = ITEMS.register("chagrin_package", () -> new FactoryItem(0));
    public static RegistryObject<Item> HINDER_PACKAGE = ITEMS.register("hinder_package", () -> new FactoryItem(1));
    public static RegistryObject<Item> FACTORY_PACKAGE = ITEMS.register("factory_package", () -> new FactoryItem(2));

    public static RegistryObject<Item> MUTATION_POTION = ITEMS.register("mutation_potion", MutationPotion::new);

    public static RegistryObject<Item> ENGINEER_MALLET = ITEMS.register("engineer_mallet", EngineerMalletItem::new);

    //Curios
    public static final RegistryObject<SingleStackItem> FREAKY_HAT = ITEMS.register("freaky_hat", FreakyHatItem::new);
    public static final RegistryObject<SingleStackItem> FREAKY_ROBE = ITEMS.register("freaky_robe", FreakyRobeItem::new);

    //Focus
    ///Magic
    public static RegistryObject<Item> IMPISH_FOCUS = ITEMS.register("impish_focus", () -> new MagicFocus(new ImpSpell()));
    public static RegistryObject<Item> SPIRIT_HAND_FOCUS = ITEMS.register("spirit_hand_focus", () -> new MagicFocus(new SpiritHandSpell()));
    public static RegistryObject<Item> REQUIEM_FOCUS = ITEMS.register("requiem_focus", () -> new MagicFocus(new RequiemSpell()));
    public static RegistryObject<Item> SOUL_BEAM_FOCUS = ITEMS.register("soul_beam_focus", () -> new MagicFocus(new SoulBeamSpell()));

    //Spawn Eggs
    public static final RegistryObject<ServantSpawnEggItem> ZOMBIE_ABSORBER_SPAWN_EGG = ITEMS.register("zombie_absorber_spawn_egg",
            () -> new ServantSpawnEggItem(GSEntityTypes.ZOMBIE_ABSORBER, 0x523c37, 0x384d23, ServantSpawnEggs.egg()));
    public static final RegistryObject<ServantSpawnEggItem> RAGNO_SERVANT_SPAWN_EGG = ITEMS.register("ragno_servant_spawn_egg",
            () -> new ServantSpawnEggItem(GSEntityTypes.RAGNO_SERVANT, 0x1a1c20, 0x525a68, ServantSpawnEggs.egg()));
    public static final RegistryObject<ServantSpawnEggItem> BOUND_ENGINEER_SPAWN_EGG = ITEMS.register("bound_engineer_spawn_egg",
            () -> new ServantSpawnEggItem(GSEntityTypes.BOUND_ENGINEER, 0x292c30, 0xbfb177, ServantSpawnEggs.egg()));
    public static final RegistryObject<ServantSpawnEggItem> BOUND_FREAKAGER_SPAWN_EGG = ITEMS.register("bound_freakager_spawn_egg",
            () -> new ServantSpawnEggItem(GSEntityTypes.BOUND_FREAKAGER, 0x202226, 0x3b3e47, ServantSpawnEggs.egg()));

}
