package com.Polarice3.goety_spillage.common.capabilities.spillage;

import com.Polarice3.goety_spillage.common.network.GSNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.util.ArrayList;

public class SpillageCapHelper {
    public static ISpillage getCapability(LivingEntity livingEntity) {
        return livingEntity.getCapability(SpillageProvider.CAPABILITY).orElse(new SpillageImp());
    }

    public static boolean isCasting(LivingEntity livingEntity){
        return getCapability(livingEntity).isCasting();
    }

    public static void setCasting(LivingEntity livingEntity, boolean isCasting){
        getCapability(livingEntity).setCasting(isCasting);
        sendSpillageUpdatePacket(livingEntity);
    }

    public static boolean isSpinning(LivingEntity livingEntity){
        return getCapability(livingEntity).isSpinning();
    }

    public static void setSpinning(LivingEntity livingEntity, boolean isSpinning){
        getCapability(livingEntity).setSpinning(isSpinning);
        sendSpillageUpdatePacket(livingEntity);
    }

    public static CompoundTag save(CompoundTag tag, ISpillage spillage) {
        tag.putBoolean("isCasting", spillage.isCasting());
        tag.putBoolean("isSpinning", spillage.isSpinning());
        return tag;
    }

    public static ISpillage load(CompoundTag tag, ISpillage spillage) {
        if (tag.contains("isCasting")) {
            spillage.setCasting(tag.getBoolean("isCasting"));
        }
        if (tag.contains("isSpinning")) {
            spillage.setSpinning(tag.getBoolean("isSpinning"));
        }
        return spillage;
    }

    public static void sendSpillageUpdatePacket(LivingEntity livingEntity) {
        if (!livingEntity.level.isClientSide) {
            GSNetwork.sentToTrackingEntityAndPlayer(livingEntity, new SpillageCapUpdatePacket(livingEntity));
        }
    }
}
