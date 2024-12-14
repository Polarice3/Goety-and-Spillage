package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class GSHinder extends Owned implements ICanBeAnimated, IEngineerMachine, IllagerAttack {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(GSHinder.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IN_MOTION = SynchedEntityData.defineId(GSHinder.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HEALING = SynchedEntityData.defineId(GSHinder.class, EntityDataSerializers.BOOLEAN);
    public AnimationState introAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    private int introTicks;

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
        this.entityData.define(IN_MOTION, false);
        this.entityData.define(HEALING, false);
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

    public boolean isInMotion() {
        return this.entityData.get(IN_MOTION);
    }

    public void setInMotion(boolean motion) {
        this.entityData.set(IN_MOTION, motion);
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_DISPENSER_DESTROY.get();
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

    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
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

        if (!this.isInMotion()) {
            if (this.entityData.get(ANIMATION_STATE) != 1 && (Integer)this.entityData.get(ANIMATION_STATE) != 2) {
                this.setAnimationState(0);
                this.setAnimationState(2);
            }

            if (this.entityData.get(ANIMATION_STATE) == 2) {
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0), (predicate) -> {
                    return !(predicate instanceof IllagerAttack) && MobUtil.areAllies(this, predicate) && this.hasLineOfSight(predicate) && predicate.isAlive() && predicate.getHealth() < predicate.getMaxHealth() && predicate.getMobType() != MobType.UNDEAD;
                });
                if (list.isEmpty()) {
                    this.setHealing(false);
                } else {
                    for (LivingEntity entity : list) {
                        this.makeParticleTrail(this.getX(), this.getY() + 0.6, this.getZ(), entity.getBoundingBox().getCenter().x, entity.getBoundingBox().getCenter().y, entity.getBoundingBox().getCenter().z);
                        this.setHealing(true);
                        if (this.tickCount % 5 == 0) {
                            this.playSound(IllageAndSpillageSoundEvents.ENTITY_ENGINEER_HINDER_HEAL.get(), 0.5F, 2.0F);
                            entity.heal(1.0F);
                        }
                    }
                }
            }

            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
        }

        if (this.onGround() && this.isInMotion()) {
            if (this.introTicks < 1) {
                this.introTicks = 1;
            }

            this.setInMotion(false);
        }

        super.tick();
    }

    public boolean isPersistenceRequired() {
        return true;
    }

    public void die(DamageSource p_70645_1_) {
        super.die(p_70645_1_);
        if (this.level.isClientSide) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), d0, d1, d2);
        }

        this.deathTime = 19;
    }
}
