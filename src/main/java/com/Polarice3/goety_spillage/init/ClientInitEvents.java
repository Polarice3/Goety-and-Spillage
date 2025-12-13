package com.Polarice3.goety_spillage.init;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.gui.overlay.FreakyRobeGui;
import com.Polarice3.goety_spillage.client.render.*;
import com.Polarice3.goety_spillage.client.render.model.FreakyHatModel;
import com.Polarice3.goety_spillage.client.render.model.VillagerVictimModel;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GoetySpillage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInitEvents {

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event){
        GSCuriosRenderer.register();
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(FreakyHatModel.LAYER_LOCATION, FreakyHatModel::createBodyLayer);
        event.registerLayerDefinition(VillagerVictimModel.LAYER_LOCATION, VillagerVictimModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGUI(final RegisterGuiOverlaysEvent event){
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "freaky_robe", FreakyRobeGui.OVERLAY);
    }

    @SubscribeEvent
    public static void onRegisterRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GSEntityTypes.DARK_CLOUD_EFFECT.get(), NoopRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.IGNITER_SERVANT.get(), IgniterServantRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.ENGINEER_SERVANT.get(), EngineerServantRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.BOUND_ENGINEER.get(), BoundEngineerRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.CHAGRIN.get(), GSChagrinRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.HINDER.get(), GSHinderRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.FACTORY.get(), GSFactoryRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.BEEPER.get(), GSBeeperRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.SNIPER.get(), GSSniperRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.POKER.get(), GSPokerRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.ILLAGER_SOUL.get(), GSIllagerSoulRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.IMP.get(), GSImpRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.SPIRIT_HAND.get(), GSSpiritHandRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.CROCOFANG_SERVANT.get(), CrocofangServantRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.ZOMBIE_ABSORBER.get(), ZombieAbsorberRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.PRESERVER_SERVANT.get(), PreserverServantRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.VILLAGER_VICTIM.get(), VillagerVictimRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.BOUND_FREAKAGER.get(), BoundFreakagerRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.RAGNO_SERVANT.get(), RagnoServantRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.EYESORE.get(), GSEyesoreRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.FUNNYBONE.get(), GSFunnyboneRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.SOUL_BEAM.get(), GSSoulBeamRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.SKULL_BOMB.get(), GSSkullBombRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.PUMPKIN_BOMB.get(), GSPumpkinBombRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.THROWN_AXE.get(), ThrownAxeRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.FREAKY_SCYTHE.get(), FreakyScytheRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.DARK_POTION.get(), (p_174064_) -> new ThrownItemRenderer<>(p_174064_, 0.75F, true));
        event.registerEntityRenderer(GSEntityTypes.WEB.get(), (p_174064_) -> new ThrownItemRenderer<>(p_174064_, 1.5F, true));
        event.registerEntityRenderer(GSEntityTypes.WEB_NET.get(), GSWebNetRenderer::new);
        event.registerEntityRenderer(GSEntityTypes.TRICK_OR_TREAT.get(), GSTotRenderer::new);
    }

}
