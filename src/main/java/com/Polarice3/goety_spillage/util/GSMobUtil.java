package com.Polarice3.goety_spillage.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.EventTask;
import com.Polarice3.goety_spillage.common.entities.ally.factory.IEngineerMachine;
import com.Polarice3.goety_spillage.common.network.GSNetwork;
import com.Polarice3.goety_spillage.common.network.server.SMobFollowSoundPacket;
import com.yellowbrossproductions.illageandspillage.entities.FreakagerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

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

    public static class FreakagerExplodeTask implements EventTask {
        UUID freakager;
        UUID simp;
        ServerLevel level;
        int ticks = 0;

        public FreakagerExplodeTask(UUID freakager, UUID simp, ServerLevel level) {
            this.freakager = freakager;
            this.simp = simp;
            this.level = level;
        }

        @Override
        public boolean getAsBoolean() {
            boolean flag = false;
            if (this.level.getEntity(this.freakager) instanceof FreakagerEntity freakager1){
                ++this.ticks;
                freakager1.setFreakagerFace(4);
                if (this.ticks == 5) {
                    if (this.multiplayer()) {
                        if (this.level.getEntity(this.simp) instanceof LivingEntity livingEntity) {
                            this.level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("info.goety_spillage.freaky_simp", freakager1.getDisplayName(), livingEntity.getDisplayName()), false);
                        }
                    }
                }
                if (this.ticks >= 10) {
                    flag = true;
                }
            } else {
                flag = true;
            }

            return flag;
        }

        @Override
        public void endTask() {
            if (this.level.getEntity(this.freakager) instanceof FreakagerEntity freakager1) {
                Vec3 vec3 = freakager1.position();
                if (this.multiplayer()) {
                    this.level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("multiplayer.player.left", freakager1.getDisplayName()).withStyle(ChatFormatting.YELLOW), false);
                } else {
                    this.level.explode(freakager1, GSDamageSource.frickYou(freakager1), null, vec3.x, vec3.y, vec3.z, 20, true, Level.ExplosionInteraction.MOB);
                }
                freakager1.discard();
            }
        }

        public boolean multiplayer() {
            return this.level.getServer().getPlayerList().getPlayers().size() > 1;
        }
    }
}
