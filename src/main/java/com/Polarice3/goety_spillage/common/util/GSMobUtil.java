package com.Polarice3.goety_spillage.common.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.goety_spillage.common.entities.ally.factory.IEngineerMachine;
import com.Polarice3.goety_spillage.common.network.GSNetwork;
import com.Polarice3.goety_spillage.common.network.server.SMobFollowSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class GSMobUtil {
    public static void mobFollowingSound(Level level, Entity entity, SoundEvent sound, float volume, float pitch, boolean loop) {
        if (!level.isClientSide) {
            GSNetwork.sentToTrackingEntity(entity, new SMobFollowSoundPacket(entity.getId(), sound, volume, pitch, loop));
        }
    }

    public static List<LivingEntity> getMachines(LivingEntity owner){
        return getMachines(owner.level, owner);
    }

    public static List<LivingEntity> getMachines(Level level, LivingEntity owner) {
        return level.getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(100.0D),
                (predicate) -> predicate instanceof IEngineerMachine
                        && predicate instanceof IOwned owned
                        && isOwner(owned, owner)
                        && predicate.isAlive());
    }

    public static boolean isOwner(IOwned owned, LivingEntity owner){
        if (owner instanceof OwnableEntity ownable){
            if (ownable.getOwner() != null){
                return owned.getTrueOwner() == ownable.getOwner();
            }
        }
        return owned.getTrueOwner() == owner;
    }
}
