package com.Polarice3.goety_spillage.client.audio;

import com.Polarice3.goety_spillage.common.entities.ally.CrocofangServant;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSHinder;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

public class GSMobFollowingSound extends AbstractTickableSoundInstance {
    private final Entity entity;
    private final float volumeMultiplier;
    private final float pitchMultiplier;
    private int timePlayed;

    public GSMobFollowingSound(Entity entity, SoundEvent sound, float volume, float pitch, boolean loop) {
        super(sound, entity.getSoundSource(), SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.volumeMultiplier = volume;
        this.pitchMultiplier = pitch;
        this.looping = loop;
    }

    public float getVolume() {
        return super.getVolume() * this.volumeMultiplier;
    }

    public float getPitch() {
        return super.getPitch() * this.pitchMultiplier;
    }

    public void tick() {
        if (this.entity.isAlive() && !this.entity.isRemoved() && !this.entity.isSilent() && this.additionalPlayConditions()) {
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();
        } else {
            this.stop();
        }

        ++this.timePlayed;
    }

    private boolean additionalPlayConditions() {
        if (this.entity instanceof CrocofangServant) {
            return ((CrocofangServant)this.entity).isCharging() || this.timePlayed < 1;
        } else if (!(this.entity instanceof GSHinder)) {
            return true;
        } else {
            return ((GSHinder)this.entity).isHealing() || this.timePlayed < 1;
        }
    }
}
