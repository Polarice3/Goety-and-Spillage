package com.Polarice3.goety_spillage;

import com.Polarice3.Goety.utils.ModPotionUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.GSSpiritHand;
import com.Polarice3.goety_spillage.common.entities.ally.RagnoServant;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundFreakager;
import com.Polarice3.goety_spillage.common.entities.neutral.VillagerVictim;
import com.Polarice3.goety_spillage.common.entities.projectiles.*;
import com.Polarice3.goety_spillage.common.items.GSItems;
import com.Polarice3.goety_spillage.common.network.GSNetwork;
import com.Polarice3.goety_spillage.compat.GSOtherModCompat;
import com.Polarice3.goety_spillage.config.*;
import com.yellowbrossproductions.illageandspillage.util.PotionRegisterer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(GoetySpillage.MOD_ID)
public class GoetySpillage {
    public static final String MOD_ID = "goety_spillage";

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public GoetySpillage() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GSEntityTypes.ENTITY_TYPE.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::setupEntityAttributeCreation);
        modEventBus.addListener(this::enqueueIMC);

        FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve("goety_spillage"), "goety_spillage");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GSMainConfig.SPEC, "goety_spillage/goety_spillage.toml");
        GSMainConfig.loadConfig(GSMainConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety_spillage/goety_spillage.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GSAttributesConfig.SPEC, "goety_spillage/goety_spillage-attributes.toml");
        GSAttributesConfig.loadConfig(GSAttributesConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety_spillage/goety_spillage-attributes.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GSSpellConfig.SPEC, "goety_spillage/goety_spillage-spells.toml");
        GSSpellConfig.loadConfig(GSSpellConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety_spillage/goety_spillage-spells.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GSMobsConfig.SPEC, "goety_spillage/goety_spillage-mobs.toml");
        GSMobsConfig.loadConfig(GSMobsConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety_spillage/goety_spillage-mobs.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GSItemConfig.SPEC, "goety_spillage/goety_spillage-items.toml");
        GSItemConfig.loadConfig(GSItemConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety_spillage/goety_spillage-items.toml").toString());

        MinecraftForge.EVENT_BUS.register(this);
        GSItems.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        GSNetwork.init();

        GSOtherModCompat.setup(event);

        event.enqueueWork(() -> {
            addBrewingRecipes();
        });
    }

    private static void addBrewingRecipes(){
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setPotion(Potions.AWKWARD)), Ingredient.of(GSItems.MUTATED_FLESH.get()), ModPotionUtil.setPotion(PotionRegisterer.MUTATION.get())));
    }

    private void setupEntityAttributeCreation(final EntityAttributeCreationEvent event) {
        event.put(GSEntityTypes.ILLAGER_SOUL.get(), GSIllagerSoul.setCustomAttributes().build());
        event.put(GSEntityTypes.SPIRIT_HAND.get(), GSSpiritHand.setCustomAttributes().build());
        event.put(GSEntityTypes.VILLAGER_VICTIM.get(), VillagerVictim.setCustomAttributes().build());
        event.put(GSEntityTypes.BOUND_FREAKAGER.get(), BoundFreakager.setCustomAttributes().build());
        event.put(GSEntityTypes.RAGNO_SERVANT.get(), RagnoServant.setCustomAttributes().build());
        event.put(GSEntityTypes.SKULL_BOMB.get(), GSSkullBomb.setCustomAttributes().build());
        event.put(GSEntityTypes.PUMPKIN_BOMB.get(), GSPumpkinBomb.setCustomAttributes().build());
        event.put(GSEntityTypes.THROWN_AXE.get(), ThrownAxe.setCustomAttributes().build());
        event.put(GSEntityTypes.FREAKY_SCYTHE.get(), FreakyScythe.setCustomAttributes().build());
        event.put(GSEntityTypes.WEB.get(), WebProjectile.setCustomAttributes().build());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
    }
}
