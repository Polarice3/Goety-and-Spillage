package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class EngineerMachine extends Summoned implements ICanBeAnimated, IEngineerMachine {
    public static final EntityDataAccessor<Boolean> IN_MOTION = SynchedEntityData.defineId(EngineerMachine.class, EntityDataSerializers.BOOLEAN);
    public boolean malletBorn;

    public EngineerMachine(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void followGoal() {
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IN_MOTION, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("MalletBorn")) {
            this.malletBorn = compound.getBoolean("MalletBorn");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("MalletBorn", this.malletBorn);
    }

    public void setMalletBorn(boolean born) {
        this.malletBorn = born;
    }

    public boolean isMalletBorn() {
        return this.malletBorn;
    }

    public boolean canSpawnArmor() {
        return false;
    }

    @Override
    public boolean canUpdateMove() {
        return false;
    }

    @Override
    public boolean canBeCommanded() {
        return false;
    }

    @Override
    public boolean isCommanded() {
        return false;
    }

    public boolean isInMotion() {
        return this.entityData.get(IN_MOTION);
    }

    public void setInMotion(boolean motion) {
        this.entityData.set(IN_MOTION, motion);
    }

    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_DISPENSER_DESTROY.get();
    }

    public boolean isPersistenceRequired() {
        return true;
    }

    public void die(DamageSource p_70645_1_) {
        super.die(p_70645_1_);
        this.deathParticles();
    }

    public void deathParticles(){
        if (this.level.isClientSide) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), d0, d1, d2);
        }

        this.deathTime = 19;
    }
}
