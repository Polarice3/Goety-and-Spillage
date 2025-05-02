package com.Polarice3.goety_spillage.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class GSMobFollowingSoundPlayer {
    public static void playSound(Level level, Entity entity, SoundEvent sound, float volume, float pitch, boolean loop) {
        if (level.isClientSide) {
            Minecraft.getInstance().getSoundManager().play(new GSMobFollowingSound(entity, sound, volume, pitch, loop));
        }

    }
}
