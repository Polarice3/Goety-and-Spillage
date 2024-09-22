package com.Polarice3.goety_spillage.client.events;

import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.capabilities.spillage.SpillageCapHelper;
import com.Polarice3.goety_spillage.common.network.GSNetwork;
import com.Polarice3.goety_spillage.common.network.client.CFreakyRobePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = GoetySpillage.MOD_ID, value = Dist.CLIENT)
public class GSClientEvents {

    @SubscribeEvent
    public static void onRenderPlayerEvent(RenderPlayerEvent event) {
        Player player = event.getEntity();
        PlayerRenderer renderer = event.getRenderer();
        if (player != null) {
            if (SpillageCapHelper.isCasting(player)){
                renderer.getModel().rightArmPose = SPELL_CASTING;
                renderer.getModel().leftArmPose = SPELL_CASTING;
            }
            if (SpillageCapHelper.isSpinning(player)){
                renderer.getModel().rightArmPose = SPIN_THROWING;
                renderer.getModel().leftArmPose = SPIN_THROWING;
            }
        }
    }

    public static HumanoidModel.ArmPose SPELL_CASTING =
            HumanoidModel.ArmPose.create("SPELL_CASTING", false, (model, entity, arm) -> {
                float ageInTicks = entity.tickCount + Minecraft.getInstance().getPartialTick();
                model.rightArm.z = 0.0F;
                model.rightArm.x = -5.0F;
                model.leftArm.z = 0.0F;
                model.leftArm.x = 5.0F;
                model.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                model.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                model.rightArm.zRot = 2.3561945F;
                model.leftArm.zRot = -2.3561945F;
                model.rightArm.yRot = 0.0F;
                model.leftArm.yRot = 0.0F;
    });

    public static HumanoidModel.ArmPose SPIN_THROWING =
            HumanoidModel.ArmPose.create("SPIN_THROWING", false, (model, entity, arm) -> {
                model.rightArm.xRot = entity.tickCount * 90;
                model.leftArm.xRot = -entity.tickCount * 90;
                model.rightArm.zRot = 0.0F;
                model.leftArm.zRot = 0.0F;
                model.rightArm.yRot = 0.0F;
                model.leftArm.yRot = 0.0F;
            });

    @SubscribeEvent
    public static void HandEvents(RenderHandEvent event){
        Minecraft MINECRAFT = Minecraft.getInstance();

        if (MINECRAFT.getCameraEntity() instanceof LivingEntity livingEntity){
            if (SpillageCapHelper.isCasting(livingEntity) || SpillageCapHelper.isSpinning(livingEntity)){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void KeyInputs(InputEvent.Key event) {
        Minecraft MINECRAFT = Minecraft.getInstance();

        if (ModKeybindings.keyBindings[3].isDown() && MINECRAFT.isWindowActive()){
            GSNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CFreakyRobePacket());
        }
    }
}
