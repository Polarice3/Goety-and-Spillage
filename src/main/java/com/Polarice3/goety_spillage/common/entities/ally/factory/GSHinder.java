package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.common.items.GSItems;
import com.Polarice3.goety_spillage.config.GSMobsConfig;
import com.Polarice3.goety_spillage.util.GSMobUtil;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class GSHinder extends EngineerMachine implements IllagerAttack {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(GSHinder.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HEALING = SynchedEntityData.defineId(GSHinder.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> COOLING = SynchedEntityData.defineId(GSHinder.class, EntityDataSerializers.BOOLEAN);
    public AnimationState introAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    private int introTicks;
    private int healTicks;
    private int coolTicks;

    public GSHinder(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.MAX_HEALTH, 40.0);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(HEALING, false);
        this.entityData.define(COOLING, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HealTick")) {
            this.healTicks = compound.getInt("HealTick");
        }
        if (compound.contains("CoolTick")) {
            this.coolTicks = compound.getInt("CoolTick");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("HealTick", this.healTicks);
        compound.putInt("CoolTick", this.coolTicks);
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION_STATE, state);
    }

    public void setHealing(boolean healing) {
        this.entityData.set(HEALING, healing);
    }

    public boolean isHealing() {
        return this.entityData.get(HEALING);
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        if (this.isInMotion()) {
            this.introTicks = 1;
            this.setAnimationState(1);
            this.setInMotion(false);
        }

        return false;
    }

    public boolean isCooling() {
        return this.entityData.get(COOLING);
    }

    public void setCooling(boolean cooling) {
        this.entityData.set(COOLING, cooling);
    }

    public AnimationState getAnimationState(String var1) {
        if (Objects.equals(var1, "intro")) {
            return this.introAnimationState;
        } else {
            return Objects.equals(var1, "idle") ? this.idleAnimationState : new AnimationState();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ANIMATION_STATE.equals(p_21104_) && this.level().isClientSide) {
            switch (this.entityData.get(ANIMATION_STATE)) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.introAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.idleAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    private void stopAllAnimationStates() {
        this.introAnimationState.stop();
        this.idleAnimationState.stop();
    }

    public void makeParticleTrail(double srcX, double srcY, double srcZ, double destX, double destY, double destZ) {
        if (this.level instanceof ServerLevel serverLevel) {
            int particles = (int)(6.0 * (this.distanceToSqr(destX, destY, destZ) / this.distanceToSqr(destX, destY, destZ)));

            for(int i = 0; i < particles; ++i) {
                double trailFactor = (double)i / ((double)particles - 1.0);
                double tx = srcX + (destX - srcX) * trailFactor;
                double ty = srcY + (destY - srcY) * trailFactor;
                double tz = srcZ + (destZ - srcZ) * trailFactor;
                serverLevel.sendParticles(ParticleTypes.ENTITY_EFFECT, tx, ty, tz, 0, 0.8, 0.36, 0.67, 1.0F);
            }
        }
    }

    public void tick() {
        if (this.introTicks == 1) {
            this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);
        }

        if (this.introTicks > 0) {
            ++this.introTicks;
        }

        if (this.introTicks == 5) {
            this.playSound(SoundEvents.PISTON_EXTEND);
        }

        if (this.introTicks == 10) {
            this.playSound(SoundEvents.PISTON_EXTEND);
        }

        if (this.introTicks == 15) {
            this.playSound(SoundEvents.PISTON_EXTEND);
        }

        if (this.introTicks == 20) {
            this.setAnimationState(0);
            this.setAnimationState(2);
        }

        if (this.coolTicks > 0){
            --this.coolTicks;
        }

        if (!this.isInMotion()) {
            if (this.entityData.get(ANIMATION_STATE) != 1 && this.entityData.get(ANIMATION_STATE) != 2) {
                this.setAnimationState(0);
                this.setAnimationState(2);
            }

            if (this.entityData.get(ANIMATION_STATE) == 2) {
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0D), (predicate) -> !(predicate instanceof IllagerAttack) && MobUtil.areAllies(this, predicate) && this.hasLineOfSight(predicate) && predicate.isAlive() && predicate.getHealth() < predicate.getMaxHealth() && predicate.getMobType() != MobType.UNDEAD);
                if (list.isEmpty() || this.isCooling()) {
                    if (this.isHealing()) {
                        this.setHealing(false);
                    }
                } else {
                    if (!this.isHealing()) {
                        this.setHealing(true);
                        GSMobUtil.mobFollowingSound(this.level(), this, IllageAndSpillageSoundEvents.ENTITY_ENGINEER_HINDER_HEAL.get(), 0.5F, 2.0F, true);
                    } else {
                        if (GSMobsConfig.HinderCool.get()) {
                            ++this.healTicks;
                        }
                    }
                    for (LivingEntity entity : list) {
                        this.makeParticleTrail(this.getX(), this.getY() + 0.6, this.getZ(), entity.getBoundingBox().getCenter().x, entity.getBoundingBox().getCenter().y, entity.getBoundingBox().getCenter().z);
                        if (this.tickCount % 2 == 0) {
                            entity.heal(1.0F);
                        }
                    }
                }
            }

            if (GSMobsConfig.HinderCool.get()) {
                if (this.healTicks >= GSMobsConfig.HinderHealTime.get()) {
                    this.coolTicks = GSMobsConfig.HinderCoolTime.get();
                    if (!this.level.isClientSide) {
                        this.level.broadcastEntityEvent(this, (byte) 4);
                    }
                    this.healTicks = 0;
                }

                this.setCooling(this.coolTicks > 0);
            }

            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        }

        if (this.isCooling()){
            if (this.level instanceof ServerLevel serverLevel){
                for(int i = 0; i < 1; ++i) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, this.getRandomX(0.15) + (-0.5 + this.random.nextDouble()) * 2.5, this.getRandomY() + (-0.5 + this.random.nextDouble()) * 1.5, this.getRandomZ(0.15) + (-0.5 + this.random.nextDouble()) * 2.5, 1, 0.0F, 0.0F, 0.0F, 0.0F);
                }
            }
            if (!GSMobsConfig.HinderCool.get()) {
                this.setCooling(false);
            }
        }

        if (this.onGround() && this.isInMotion()) {
            if (this.introTicks < 1) {
                this.introTicks = 1;
            }
            this.setAnimationState(1);
            this.setInMotion(false);
        }

        super.tick();
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.coolTicks = GSMobsConfig.HinderCoolTime.get();
            this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public ItemStack getFactoryItem() {
        return GSItems.HINDER_PACKAGE.get().getDefaultInstance();
    }
}
