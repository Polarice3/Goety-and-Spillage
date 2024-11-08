package com.Polarice3.goety_spillage.common.entities.ally.undead;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.entities.goal.StareAtDeadFreakGoal;
import com.yellowbrossproductions.illageandspillage.entities.projectile.BoneEntity;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;

public class GSFunnybone extends Summoned implements ICanBeAnimated {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(GSFunnybone.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(GSFunnybone.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GOOPY = SynchedEntityData.defineId(GSFunnybone.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOW_BONE = SynchedEntityData.defineId(GSFunnybone.class, EntityDataSerializers.BOOLEAN);
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState runAnimationState = new AnimationState();
    public AnimationState flyAnimationState = new AnimationState();
    public AnimationState spawnAnimationState = new AnimationState();
    public AnimationState throwAnimationState = new AnimationState();
    public boolean isThrowing;
    private int throwTicks;
    private int introTicks;
    private boolean circleDirection = true;
    protected int circleTick = 0;

    public GSFunnybone(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new StareAtDeadFreakGoal(this));
        this.goalSelector.addGoal(1, new FunnyboneAttackGoal(this, 8.0F));
        this.goalSelector.addGoal(8, new FunnyboneRandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(9, new FunnyboneLookAtEntityGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new FunnyboneLookAtEntityGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.9)
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(GOOPY, false);
        this.entityData.define(SHOW_BONE, false);
        this.entityData.define(FLYING, false);
    }

    public void addAdditionalSaveData(CompoundTag p_36848_) {
        super.addAdditionalSaveData(p_36848_);
        p_36848_.putBoolean("goopy", this.isGoopy());
    }

    public void readAdditionalSaveData(CompoundTag p_36844_) {
        super.readAdditionalSaveData(p_36844_);
        this.setGoopy(p_36844_.getBoolean("goopy"));
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return p_21197_.getEffect() != EffectRegisterer.MUTATION.get() && super.canBeAffected(p_21197_);
    }

    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
    }

    @Override
    public int xpReward() {
        return 0;
    }

    protected @Nullable SoundEvent getAmbientSound() {
        return this.isFlying() ? null : IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_FUNNYBONE_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_FUNNYBONE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_FUNNYBONE_DEATH.get();
    }

    protected void playStepSound(BlockPos p_32159_, BlockState p_32160_) {
        this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.0F);
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION_STATE, state);
    }

    public void setGoopy(boolean goopy) {
        this.entityData.set(GOOPY, goopy);
    }

    public boolean isGoopy() {
        return this.entityData.get(GOOPY);
    }

    public void setShowBone(boolean showBone) {
        this.entityData.set(SHOW_BONE, showBone);
    }

    public boolean shouldShowBone() {
        return this.entityData.get(SHOW_BONE);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATION_STATE);
    }

    public AnimationState getAnimationState(String var1) {
        if (Objects.equals(var1, "idle")) {
            return this.idleAnimationState;
        } else if (Objects.equals(var1, "run")) {
            return this.runAnimationState;
        } else if (Objects.equals(var1, "fly")) {
            return this.flyAnimationState;
        } else if (Objects.equals(var1, "spawn")) {
            return this.spawnAnimationState;
        } else {
            return Objects.equals(var1, "throw") ? this.throwAnimationState : new AnimationState();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ANIMATION_STATE.equals(p_21104_) && this.level().isClientSide) {
            switch (this.getAnimationState()) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.flyAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.spawnAnimationState.start(this.tickCount);
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.throwAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    private void stopAllAnimationStates() {
        this.flyAnimationState.stop();
        this.spawnAnimationState.stop();
        this.throwAnimationState.stop();
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        if (this.isFlying()) {
            this.introTicks = 1;
            this.setFlying(false);
            return false;
        } else {
            return super.causeFallDamage(p_225503_1_, p_225503_2_, p_147189_);
        }
    }

    public void tick() {
        if (this.introTicks == 1) {
            this.playSound(SoundEvents.SKELETON_STEP);
            this.playAmbientSound();
            this.setAnimationState(2);
        }

        if (this.introTicks > 0) {
            ++this.introTicks;
        }

        if (this.introTicks == 22) {
            this.setAnimationState(0);
        }

        if (this.isFlying() && this.getAnimationState() != 1) {
            this.setAnimationState(1);
        }

        if (this.onGround() && this.isFlying()) {
            if (this.introTicks < 1) {
                this.introTicks = 1;
            }

            this.setFlying(false);
        }

        if (this.isAlive()) {
            if (this.isThrowing) {
                ++this.throwTicks;
            }

            if (this.throwTicks == 1) {
                this.setAnimationState(3);
                this.setShowBone(true);
            }

            if (this.throwTicks == 7) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_FUNNYBONE_THROW.get(), this.getSoundVolume(), this.getVoicePitch());
                this.playSound(SoundEvents.WITCH_THROW, 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                this.setShowBone(false);
                if (!this.level.isClientSide && this.getTarget() != null) {
                    double x = this.getX() - this.getTarget().getX();
                    double y = this.getY() + 1.0 - (this.getTarget().getY() + (double)(this.getTarget().getEyeHeight() / 2.0F) + 0.5);
                    double z = this.getZ() - this.getTarget().getZ();
                    BoneEntity projectile = new BoneEntity(this.level(), this, -x, -y, -z);
                    projectile.moveTo(this.getX(), this.getY() + 1.0, this.getZ());
                    CompoundTag tag = this.getPersistentData().getCompound("Rotation");
                    projectile.readAdditionalSaveData(tag);
                    projectile.isGoopy = this.isGoopy();
                    projectile.setShooter(this);
                    this.level.addFreshEntity(projectile);
                }
            }

            if (this.throwTicks == 15) {
                this.setAnimationState(0);
                this.isThrowing = false;
                this.throwTicks = 0;
            }
        }

        super.tick();
    }

    private boolean canMove() {
        return !this.isFlying() && (this.introTicks == 0 || this.introTicks > 22);
    }

    protected Vec3 updateCirclingPosition(float radius, float speed) {
        LivingEntity target = this.getTarget();
        if (target != null) {
            if (this.random.nextInt(200) == 0) {
                this.circleDirection = !this.circleDirection;
            }

            if (this.circleDirection) {
                ++this.circleTick;
            } else {
                --this.circleTick;
            }

            return this.circleEntityPosition(target, radius, speed, true, this.circleTick, 0.0F);
        } else {
            return null;
        }
    }

    public Vec3 circleEntityPosition(Entity target, float radius, float speed, boolean direction, int circleFrame, float offset) {
        int directionInt = direction ? 1 : -1;
        double t = (double)(directionInt * circleFrame) * 0.5 * (double)speed / (double)radius + (double)offset;
        return target.position().add((double)radius * Math.cos(t), 0.0, (double)radius * Math.sin(t));
    }

    protected static class FunnyboneAttackGoal extends Goal {
        private final GSFunnybone mob;
        private final float attackRadius;
        private int strafingLeftRightMul;
        private int strafingFrontBackMul;
        private boolean chasing = false;
        protected boolean attacking = false;
        private int timeSinceAttack = 0;

        public FunnyboneAttackGoal(GSFunnybone mob, float attackRadius) {
            this.mob = mob;
            this.attackRadius = attackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return this.mob.getTarget() != null && this.mob.canMove() && !this.mob.isThrowing;
        }

        public boolean canContinueToUse() {
            return (this.canUse() || !this.mob.getNavigation().isDone()) && this.mob.canMove() && !this.mob.isThrowing;
        }

        public void start() {
            super.start();
            this.mob.setAggressive(true);
            this.timeSinceAttack = this.mob.random.nextInt(80);
        }

        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
            this.mob.getMoveControl().strafe(0.0F, 0.0F);
            this.attacking = false;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                if (this.timeSinceAttack < 80) {
                    ++this.timeSinceAttack;
                }

                double distToTarget = (double)this.mob.distanceTo(target);
                float frontBackDistBuffer = 2.0F;
                float leftRightDistBuffer = 1.5F;
                if (this.chasing && distToTarget <= (double)this.attackRadius) {
                    this.chasing = false;
                }

                if (!this.chasing && distToTarget >= (double)(this.attackRadius + frontBackDistBuffer)) {
                    this.chasing = true;
                }

                if (this.chasing) {
                    this.mob.getNavigation().moveTo(target, 0.35);
                    this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    this.mob.getMoveControl().strafe(0.0F, 0.0F);
                } else {
                    if (!this.attacking) {
                        this.mob.getNavigation().stop();
                        float strafeSpeed = 0.55F;
                        Vec3 circlePos = this.mob.updateCirclingPosition(this.attackRadius, strafeSpeed - 0.2F);
                        double distToCirclePos = this.mob.position().distanceTo(circlePos);
                        if (distToCirclePos <= (double)leftRightDistBuffer) {
                            if (distToTarget > (double)this.attackRadius + 0.5) {
                                this.strafingFrontBackMul = 1;
                            } else if (distToTarget < (double)this.attackRadius - 0.5) {
                                this.strafingFrontBackMul = -1;
                            } else {
                                this.strafingFrontBackMul = 0;
                            }

                            Vec3 toTarget = target.position().subtract(this.mob.position()).multiply(1.0, 0.0, 1.0).normalize();
                            Vec3 toCirclePos = circlePos.subtract(this.mob.position()).multiply(1.0, 0.0, 1.0).normalize();
                            Vec3 cross = toTarget.cross(toCirclePos);
                            if (cross.y > 0.0) {
                                this.strafingLeftRightMul = 1;
                            } else if (cross.y < 0.0) {
                                this.strafingLeftRightMul = -1;
                            } else {
                                this.strafingLeftRightMul = 0;
                            }

                            float distScale = (float)Math.min(Math.pow(distToCirclePos * 1.0 / (double)leftRightDistBuffer, 0.7), 1.0);
                            this.mob.getMoveControl().strafe((float)this.strafingFrontBackMul * strafeSpeed, (float)this.strafingLeftRightMul * strafeSpeed * distScale);
                            this.mob.lookAt(target, 30.0F, 30.0F);
                        } else {
                            this.mob.getMoveControl().strafe(0.0F, 0.0F);
                            this.mob.getNavigation().moveTo(circlePos.x, circlePos.y, circlePos.z, 0.35);
                            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                        }
                    } else {
                        this.mob.getMoveControl().strafe(0.0F, 0.0F);
                    }

                    if (this.mob.random.nextInt(80) == 0 && this.timeSinceAttack >= 80 && this.mob.getSensing().hasLineOfSight(target)) {
                        this.attacking = true;
                    }

                    if (this.attacking) {
                        this.mob.isThrowing = true;
                    }
                }
            }

        }
    }

    class FunnyboneRandomStrollGoal extends RandomStrollGoal {
        public FunnyboneRandomStrollGoal(PathfinderMob p_25734_, double p_25735_) {
            super(p_25734_, p_25735_);
        }

        public boolean canUse() {
            return super.canUse() && GSFunnybone.this.canMove();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && GSFunnybone.this.canMove();
        }
    }

    class FunnyboneLookAtEntityGoal extends LookAtPlayerGoal {
        public FunnyboneLookAtEntityGoal(Mob p_25524_, Class<? extends LivingEntity> p_25525_, float p_25526_, float p_25527_) {
            super(p_25524_, p_25525_, p_25526_, p_25527_);
        }

        public FunnyboneLookAtEntityGoal(Mob p_25524_, Class<? extends LivingEntity> p_25525_, float p_25526_) {
            super(p_25524_, p_25525_, p_25526_);
        }

        public boolean canUse() {
            return super.canUse() && GSFunnybone.this.canMove();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && GSFunnybone.this.canMove();
        }
    }
}
