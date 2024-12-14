package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
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
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class GSChagrin extends Summoned implements RangedAttackMob, IEngineerMachine, ICanBeAnimated, IllagerAttack {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(GSChagrin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> PARTIAL_TICKS = SynchedEntityData.defineId(GSChagrin.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IN_MOTION = SynchedEntityData.defineId(GSChagrin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOW_LOCKER = SynchedEntityData.defineId(GSChagrin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STUN_TICKS = SynchedEntityData.defineId(GSChagrin.class, EntityDataSerializers.INT);
    public AnimationState introAnimationState = new AnimationState();
    public AnimationState shootingAnimationState = new AnimationState();
    public AnimationState stunAnimationState = new AnimationState();
    private int introTicks;
    private int attackTicks;
    private boolean isPlayingIntro;
    private boolean isShooting;
    private boolean isStunned;
    private int arrowsFired;

    public GSChagrin(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new StunGoal());
        this.goalSelector.addGoal(1, new RapidFireGoal());
        this.goalSelector.addGoal(9, new ChagrinSentryLookAtEntityGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new ChagrinSentryLookAtEntityGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(IN_MOTION, false);
        this.entityData.define(SHOW_LOCKER, false);
        this.entityData.define(STUN_TICKS, 0);
        this.entityData.define(PARTIAL_TICKS, 0.0F);
    }

    public boolean canSpawnArmor() {
        return false;
    }

    @Override
    public boolean canUpdateMove() {
        return false;
    }

    @Override
    public boolean isCommanded() {
        return false;
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION_STATE, state);
    }

    public boolean isInMotion() {
        return this.entityData.get(IN_MOTION);
    }

    public void setInMotion(boolean motion) {
        this.entityData.set(IN_MOTION, motion);
    }

    public boolean shouldShowLocker() {
        return this.entityData.get(SHOW_LOCKER);
    }

    public void setShowLocker(boolean showLocker) {
        this.entityData.set(SHOW_LOCKER, showLocker);
    }

    public int getStunTicks() {
        return this.entityData.get(STUN_TICKS);
    }

    public void setStunTicks(int stunTicks) {
        this.entityData.set(STUN_TICKS, stunTicks);
    }

    public float getPartialTicks() {
        return this.entityData.get(PARTIAL_TICKS);
    }

    public void setPartialTicks(float partialTicks) {
        this.entityData.set(PARTIAL_TICKS, partialTicks);
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        if (this.isInMotion()) {
            this.introTicks = 1;
            this.isPlayingIntro = true;
            this.setAnimationState(1);
            this.setInMotion(false);
        }

        return false;
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
        } else if (Objects.equals(var1, "shooting")) {
            return this.shootingAnimationState;
        } else {
            return Objects.equals(var1, "stun") ? this.stunAnimationState : new AnimationState();
        }
    }

    public float getAnimationSpeed() {
        return this.entityData.get(ANIMATION_STATE) == 2 ? 1.25F : 1.0F;
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
                    this.shootingAnimationState.start(this.tickCount);
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.stunAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    private void stopAllAnimationStates() {
        this.introAnimationState.stop();
        this.shootingAnimationState.stop();
        this.stunAnimationState.stop();
    }

    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
    }

    public void tick() {
        if (this.introTicks == 1) {
            this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);
            this.setShowLocker(true);
        }

        if (this.introTicks > 0) {
            ++this.introTicks;
        }

        if (this.introTicks == 25) {
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 0.8F);
        }

        if (this.introTicks == 30) {
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 0.8F);
        }

        if (this.introTicks == 57) {
            this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);
        }

        if (!this.level().isClientSide && (this.introTicks < 1 || this.introTicks > 60)) {
            this.setShowLocker(this.isInMotion());
        }

        if (this.introTicks == 60) {
            this.setShowLocker(false);
            this.setAnimationState(0);
            this.isPlayingIntro = false;
        }

        if (this.isShooting) {
            ++this.attackTicks;
        }

        if (this.isAlive()) {
            if (this.attackTicks == 6) {
                this.setAnimationState(2);
            }

            if (this.getTarget() != null && this.attackTicks >= 6 && this.attackTicks % 2 == 0) {
                this.performRangedAttack(this.getTarget(), 1.0F);
                ++this.arrowsFired;
            }
        }

        if (!this.isShooting && this.arrowsFired > 0 && this.tickCount % 5 == 0) {
            --this.arrowsFired;
        }

        if (this.arrowsFired >= 75 && !this.isStunned && this.random.nextInt(5) == 0) {
            this.makeOverheatParticles();
        }

        if (this.isStunned) {
            if (this.getStunTicks() < 190) {
                this.makeOverheatParticles();
            }

            this.setStunTicks(this.getStunTicks() + 1);
        }

        if (!this.isInMotion()) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
        }

        if (this.onGround() && this.isInMotion()) {
            if (this.introTicks < 1) {
                this.introTicks = 1;
                this.isPlayingIntro = true;
                this.setAnimationState(1);
            }

            this.setInMotion(false);
        }

        super.tick();
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
    }

    public void makeOverheatParticles() {
        if (this.level instanceof ServerLevel serverLevel){
            for(int i = 0; i < 1; ++i) {
                serverLevel.sendParticles(ParticleTypes.SMOKE, this.getRandomX(0.15) + (-0.5 + this.random.nextDouble()) * 2.5, this.getRandomY() + (-0.5 + this.random.nextDouble()) * 1.5, this.getRandomZ(0.15) + (-0.5 + this.random.nextDouble()) * 2.5, 1, 0.0F, 0.0F, 0.0F, 0.0F);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        AbstractArrow abstractarrowentity = this.getArrow(Items.BOW.getDefaultInstance(), velocity);
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrowentity = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
        }

        float f = this.yBodyRot * 0.017453292F * 0.25F;
        float f1 = Mth.cos(f);
        float f2 = Mth.sin(f);
        if (this.attackTicks % 4 == 0) {
            abstractarrowentity.setPos(this.getX() + (double)f1 * 0.6, this.getY() + 0.8, this.getZ() + (double)f2 * 0.6);
        } else {
            abstractarrowentity.setPos(this.getX() - (double)f1 * 0.6, this.getY() + 0.8, this.getZ() - (double)f2 * 0.6);
        }

        double d0 = target.getX() - abstractarrowentity.getX();
        double d1 = target.getY(0.3333333333333333) - abstractarrowentity.getY() - (double)target.getBbHeight() / 2.0;
        double d2 = target.getZ() - abstractarrowentity.getZ();
        double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
        float speed;
        if (this.distanceToSqr(target) > 22.5) {
            speed = 2.5F;
        } else {
            speed = (float)(this.distanceToSqr(target) / 9.0);
        }

        abstractarrowentity.shoot(d0, d1 + d3 * 0.20000000298023224, d2, speed, 5.0F);
        this.level().addFreshEntity(abstractarrowentity);
        this.playSound(IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_SHOOT.get(), 0.5F, this.getVoicePitch());
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, this.getVoicePitch());
    }

    protected AbstractArrow getArrow(ItemStack p_213624_1_, float p_213624_2_) {
        return ProjectileUtil.getMobArrow(this, p_213624_1_, p_213624_2_);
    }

    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return p_21016_.getEntity() != this && super.hurt(p_21016_, p_21017_);
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

    class StunGoal extends Goal {
        StunGoal() {
        }

        public boolean canUse() {
            return !GSChagrin.this.isInMotion() && GSChagrin.this.getStunTicks() < 1 && !GSChagrin.this.isPlayingIntro && GSChagrin.this.arrowsFired > 100;
        }

        public void start() {
            GSChagrin.this.setAnimationState(3);
            GSChagrin.this.isStunned = true;
            GSChagrin.this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 1.0F, 0.8F);
        }

        public boolean canContinueToUse() {
            return GSChagrin.this.getStunTicks() <= 200;
        }

        public void stop() {
            GSChagrin.this.setAnimationState(0);
            GSChagrin.this.isStunned = false;
            GSChagrin.this.setStunTicks(0);
            GSChagrin.this.arrowsFired = 0;
        }
    }

    class RapidFireGoal extends Goal {
        RapidFireGoal() {
        }

        public boolean canUse() {
            return !GSChagrin.this.isInMotion() && GSChagrin.this.getStunTicks() < 1 && !GSChagrin.this.isPlayingIntro && GSChagrin.this.getTarget() != null && GSChagrin.this.getTarget().isAlive() && GSChagrin.this.hasLineOfSight(GSChagrin.this.getTarget()) && GSChagrin.this.arrowsFired <= 100;
        }

        public void start() {
            GSChagrin.this.isShooting = true;
            GSChagrin.this.playSound(IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_LOAD.get(), 2.0F, 1.0F);
        }

        public void tick() {
            if (GSChagrin.this.getTarget() != null) {
                GSChagrin.this.getLookControl().setLookAt(GSChagrin.this.getTarget(), 100.0F, 100.0F);
            }

        }

        public boolean canContinueToUse() {
            return this.canUse();
        }

        public void stop() {
            GSChagrin.this.setAnimationState(0);
            GSChagrin.this.isShooting = false;
            GSChagrin.this.attackTicks = 0;
        }
    }

    class ChagrinSentryLookAtEntityGoal extends LookAtPlayerGoal {
        public ChagrinSentryLookAtEntityGoal(Mob p_25524_, Class<? extends LivingEntity> p_25525_, float p_25526_, float p_25527_) {
            super(p_25524_, p_25525_, p_25526_, p_25527_);
        }

        public ChagrinSentryLookAtEntityGoal(Mob p_25524_, Class<? extends LivingEntity> p_25525_, float p_25526_) {
            super(p_25524_, p_25525_, p_25526_);
        }

        public boolean canUse() {
            return super.canUse() && !GSChagrin.this.isShooting && !GSChagrin.this.isStunned;
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && !GSChagrin.this.isShooting && !GSChagrin.this.isStunned;
        }
    }
}
