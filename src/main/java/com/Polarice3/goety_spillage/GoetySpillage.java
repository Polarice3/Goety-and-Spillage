package com.Polarice3.goety_spillage;

import com.Polarice3.Goety.utils.ModPotionUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.GSSpiritHand;
import com.Polarice3.goety_spillage.common.entities.ally.illager.*;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundEngineer;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundFreakager;
import com.Polarice3.goety_spillage.common.entities.ally.undead.zombie.ZombieAbsorber;
import com.Polarice3.goety_spillage.common.entities.neutral.VillagerVictim;
import com.Polarice3.goety_spillage.common.entities.projectiles.*;
import com.Polarice3.goety_spillage.common.items.GSItems;
import com.Polarice3.goety_spillage.common.network.GSNetwork;
import com.Polarice3.goety_spillage.compat.GSOtherModCompat;
import com.Polarice3.goety_spillage.config.*;
import com.Polarice3.goety_spillage.init.GSCreativeTab;
import com.mojang.logging.LogUtils;
import com.yellowbrossproductions.illageandspillage.entities.*;
import com.yellowbrossproductions.illageandspillage.util.PotionRegisterer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
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
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

@Mod(GoetySpillage.MOD_ID)
public class GoetySpillage {
    public static final String MOD_ID = "goety_spillage";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public GoetySpillage() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GSEntityTypes.ENTITY_TYPE.register(modEventBus);
        GSCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::setupEntityAttributeCreation);
        modEventBus.addListener(this::enqueueIMC);

        getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve("goety_spillage"), "goety_spillage");
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

    public static Path getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of "+dirLabel);
        }
        if (!Files.isDirectory(dirPath))
        {
            LOGGER.debug(CORE, "Making {} directory : {}", dirLabel, dirPath);
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    LOGGER.error(CORE, "Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    LOGGER.error(CORE, "Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            LOGGER.debug(CORE, "Created {} directory : {}", dirLabel, dirPath);
        } else {
            LOGGER.debug(CORE, "Found existing {} directory : {}", dirLabel, dirPath);
        }
        return dirPath;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        GSNetwork.init();

        GSOtherModCompat.setup(event);

        event.enqueueWork(() -> {
            addBrewingRecipes();
        });
    }

    private static void addBrewingRecipes(){
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setPotion(Potions.AWKWARD), Ingredient.of(GSItems.MUTATED_FLESH.get()), ModPotionUtil.setPotion(PotionRegisterer.MUTATION.get())));
    }

    private void setupEntityAttributeCreation(final EntityAttributeCreationEvent event) {
        event.put(GSEntityTypes.IGNITER_SERVANT.get(), IgniterServant.setCustomAttributes().build());
        event.put(GSEntityTypes.ENGINEER_SERVANT.get(), EngineerServant.setCustomAttributes().build());
        event.put(GSEntityTypes.BOUND_ENGINEER.get(), BoundEngineer.setCustomAttributes().build());
        event.put(GSEntityTypes.CHAGRIN.get(), ChagrinSentryEntity.createAttributes().build());
        event.put(GSEntityTypes.HINDER.get(), HinderEntity.createAttributes().build());
        event.put(GSEntityTypes.FACTORY.get(), FactoryEntity.createAttributes().build());
        event.put(GSEntityTypes.BEEPER.get(), BeeperEntity.createAttributes().build());
        event.put(GSEntityTypes.SNIPER.get(), SniperEntity.createAttributes().build());
        event.put(GSEntityTypes.POKER.get(), PokerEntity.createAttributes().build());
        event.put(GSEntityTypes.ILLAGER_SOUL.get(), GSIllagerSoul.setCustomAttributes().build());
        event.put(GSEntityTypes.IMP.get(), GSImp.setCustomAttributes().build());
        event.put(GSEntityTypes.SPIRIT_HAND.get(), GSSpiritHand.setCustomAttributes().build());
        event.put(GSEntityTypes.CROCOFANG_SERVANT.get(), CrocofangServant.setCustomAttributes().build());
        event.put(GSEntityTypes.ZOMBIE_ABSORBER.get(), ZombieAbsorber.setCustomAttributes().build());
        event.put(GSEntityTypes.PRESERVER_SERVANT.get(), PreserverServant.setCustomAttributes().build());
        event.put(GSEntityTypes.VILLAGER_VICTIM.get(), VillagerVictim.setCustomAttributes().build());
        event.put(GSEntityTypes.BOUND_FREAKAGER.get(), BoundFreakager.setCustomAttributes().build());
        event.put(GSEntityTypes.RAGNO_SERVANT.get(), RagnoServant.setCustomAttributes().build());
        event.put(GSEntityTypes.EYESORE.get(), EyesoreEntity.createAttributes().build());
        event.put(GSEntityTypes.FUNNYBONE.get(), FunnyboneEntity.createAttributes().build());
        event.put(GSEntityTypes.SKULL_BOMB.get(), GSSkullBomb.setCustomAttributes().build());
        event.put(GSEntityTypes.PUMPKIN_BOMB.get(), GSPumpkinBomb.setCustomAttributes().build());
        event.put(GSEntityTypes.FREAKY_SCYTHE.get(), FreakyScythe.setCustomAttributes().build());
        event.put(GSEntityTypes.WEB.get(), WebProjectile.setCustomAttributes().build());
        event.put(GSEntityTypes.WEB_NET.get(), GSWebNet.setCustomAttributes().build());
        event.put(GSEntityTypes.TRICK_OR_TREAT.get(), TrickOrTreatEntity.createAttributes().build());
    }

    @SuppressWarnings("all")
    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.BODY.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.HEAD.getMessageBuilder().build());
    }
}
