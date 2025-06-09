package com.Polarice3.goety_spillage.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSChagrin;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSFactory;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSHinder;
import com.Polarice3.goety_spillage.util.GSMobUtil;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EngineerServant extends AbstractIllagerServant implements ICanBeAnimated {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(EngineerServant.class, EntityDataSerializers.INT);
    public AnimationState throwAnimationState = new AnimationState();
    public AnimationState repairAnimationState = new AnimationState();
    private int throwCooldown = 100;
    private int repairCooldown;
    private int attackTicks;
    private int repairTicks;
    private int attackType;
    private LivingEntity toRepair;
    private final int THROW_ATTACK = 1;
    private final int REPAIR_ATTACK = 2;

    public EngineerServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EngineerAvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 0.8, 1.0));
        this.goalSelector.addGoal(2, new ThrowMachineGoal());
        this.goalSelector.addGoal(2, new RepairGoal());
        this.goalSelector.addGoal(8, new EngineerRandomStrollGoal<>(this, 0.6));
        this.goalSelector.addGoal(9, new EngineerLookAtEntityGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new EngineerLookAtEntityGoal(this, Mob.class, 15.0F));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ThrowCoolDown", this.throwCooldown);
        pCompound.putInt("RepairCoolDown", this.repairCooldown);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ThrowCoolDown")) {
            this.throwCooldown = pCompound.getInt("ThrowCoolDown");
        }
        if (pCompound.contains("RepairCoolDown")) {
            this.repairCooldown = pCompound.getInt("RepairCoolDown");
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        super.onSyncedDataUpdated(p_21104_);
        if (ANIMATION_STATE.equals(p_21104_) && this.level.isClientSide) {
            switch (this.entityData.get(ANIMATION_STATE)) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.throwAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.repairAnimationState.start(this.tickCount);
            }
        }

    }

    private void stopAllAnimationStates() {
        this.throwAnimationState.stop();
        this.repairAnimationState.stop();
    }

    public SoundEvent getCelebrateSound() {
        return IllageAndSpillageSoundEvents.ENTITY_ENGINEER_AMBIENT.get();
    }

    protected @Nullable SoundEvent getAmbientSound() {
        return IllageAndSpillageSoundEvents.ENTITY_ENGINEER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return IllageAndSpillageSoundEvents.ENTITY_ENGINEER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_ENGINEER_DEATH.get();
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION_STATE, state);
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATION_STATE);
    }

    public AnimationState getAnimationState(String input) {
        if (Objects.equals(input, "throw")) {
            return this.throwAnimationState;
        } else {
            return Objects.equals(input, "repair") ? this.repairAnimationState : new AnimationState();
        }
    }

    public void tick() {
        super.tick();
        if (this.attackType < THROW_ATTACK) {
            --this.throwCooldown;
            --this.repairCooldown;
        } else {
            ++this.attackTicks;
        }

        if (this.repairTicks > 0) {
            ++this.repairTicks;
        }

        if (this.isAlive()) {
            if (this.attackType == THROW_ATTACK && this.attackTicks == 5) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_ENGINEER_THROW.get(), 2.0F, this.getVoicePitch());
                this.playSound(SoundEvents.WITCH_THROW, 1.0F, this.getVoicePitch());
                if (this.level instanceof ServerLevel serverLevel) {
                    int randomSelection = this.random.nextInt(0, 3);
                    LivingEntity owner = this.getTrueOwner() != null ? this.getTrueOwner() : this;
                    if (randomSelection == 0) {
                        GSHinder hinder = GSEntityTypes.HINDER.get().create(serverLevel);
                        if (hinder != null) {
                            hinder.setPos(this.getX(), this.getY() + 1.0, this.getZ());
                            hinder.setDeltaMovement((double) (-2 + this.random.nextInt(5)) * 0.4, 0.6, (double) (-2 + this.random.nextInt(5)) * 0.4);
                            hinder.setInMotion(true);
                            hinder.setTrueOwner(owner);
                            hinder.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            this.level.addFreshEntity(hinder);
                        }
                    } else if (randomSelection == 1) {
                        GSChagrin sentry = GSEntityTypes.CHAGRIN.get().create(serverLevel);
                        if (sentry != null) {
                            sentry.setPos(this.getX(), this.getY() + 1.0, this.getZ());
                            sentry.setDeltaMovement((double) (-2 + this.random.nextInt(5)) * 0.4, 0.6, (double) (-2 + this.random.nextInt(5)) * 0.4);
                            sentry.setInMotion(true);
                            sentry.setTrueOwner(owner);
                            sentry.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            this.level.addFreshEntity(sentry);
                        }
                    } else {
                        GSFactory factory = GSEntityTypes.FACTORY.get().create(serverLevel);
                        if (factory != null) {
                            factory.setPos(this.getX(), this.getY() + 1.0, this.getZ());
                            factory.setDeltaMovement((double) (-2 + this.random.nextInt(5)) * 0.4, 0.6, (double) (-2 + this.random.nextInt(5)) * 0.4);
                            factory.setInMotion(true);
                            factory.setTrueOwner(owner);
                            factory.setAnimationState(1);
                            factory.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            serverLevel.addFreshEntity(factory);
                        }
                    }
                }
            }

            if (this.attackType == REPAIR_ATTACK) {
                if (this.repairTicks < 1 && this.distanceToSqr(this.toRepair) <= 4.0) {
                    this.repairTicks = 1;
                    this.setAnimationState(2);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_ENGINEER_THROW.get(), 2.0F, this.getVoicePitch());
                }

                if (this.repairTicks == 8 || this.repairTicks == 21 || this.repairTicks == 31) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_ENGINEER_REPAIR.get(), 2.0F, this.getVoicePitch());
                    if (!this.level.isClientSide) {
                        this.toRepair.heal(5.0F);
                    }
                }
            }
        }

    }

    class EngineerAvoidEntityGoal<T extends LivingEntity> extends AvoidTargetGoal<T> {
        public EngineerAvoidEntityGoal(PathfinderMob p_25027_, Class<T> p_25028_, float p_25029_, double p_25030_, double p_25031_) {
            super(p_25027_, p_25028_, p_25029_, p_25030_, p_25031_);
        }

        public boolean canUse() {
            return super.canUse() && EngineerServant.this.attackType == 0;
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && EngineerServant.this.attackType == 0;
        }
    }

    class ThrowMachineGoal extends Goal {
        ThrowMachineGoal() {
        }

        public boolean canUse() {
            return EngineerServant.this.attackType == 0
                    && EngineerServant.this.getTarget() != null
                    && EngineerServant.this.throwCooldown < 1
                    && GSMobUtil.getMachines(EngineerServant.this).size() < 3;
        }

        public void start() {
            super.start();
            EngineerServant.this.setAnimationState(1);
            EngineerServant.this.attackType = THROW_ATTACK;
        }

        public boolean canContinueToUse() {
            return EngineerServant.this.attackTicks <= 20;
        }

        public void stop() {
            EngineerServant.this.attackType = 0;
            EngineerServant.this.attackTicks = 0;
            EngineerServant.this.setAnimationState(0);
            EngineerServant.this.throwCooldown = EngineerServant.this.random.nextInt(300, 501);
        }
    }

    class RepairGoal extends Goal {
        private Path path;

        RepairGoal() {
        }

        public boolean canUse() {
            boolean initialConditions = EngineerServant.this.attackType == 0 && EngineerServant.this.repairCooldown < 1;
            if (EngineerServant.this.isStaying() || EngineerServant.this.isCommanded()){
                return false;
            } else if (!initialConditions) {
                return false;
            } else {
                double closestDistance = Double.MAX_VALUE;
                LivingEntity closestToRepair = null;

                for (LivingEntity livingEntity : GSMobUtil.getMachines(EngineerServant.this)) {
                    if (livingEntity.getHealth() <= livingEntity.getMaxHealth() / 2.0F) {
                        double distance = EngineerServant.this.distanceToSqr(livingEntity);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestToRepair = livingEntity;
                        }
                    }
                }

                EngineerServant.this.toRepair = closestToRepair;
                this.path = EngineerServant.this.toRepair == null ? null : EngineerServant.this.getNavigation().createPath(EngineerServant.this.toRepair, 0);
                return this.path != null;
            }
        }

        public void start() {
            EngineerServant.this.attackType = REPAIR_ATTACK;
            EngineerServant.this.getNavigation().stop();
            EngineerServant.this.getNavigation().moveTo(this.path, 1.0);
        }

        public boolean canContinueToUse() {
            if (EngineerServant.this.isStaying() || EngineerServant.this.isCommanded()){
                return false;
            } else if ((EngineerServant.this.repairTicks <= 0 || !(EngineerServant.this.distanceToSqr(EngineerServant.this.toRepair) > 4.0)) && EngineerServant.this.toRepair.isAlive()) {
                return EngineerServant.this.toRepair != null && EngineerServant.this.isAlive() && EngineerServant.this.toRepair.getHealth() < EngineerServant.this.toRepair.getMaxHealth() && EngineerServant.this.repairTicks > 0 ? EngineerServant.this.repairTicks <= 40 : EngineerServant.this.attackTicks <= 300;
            } else {
                EngineerServant.this.getNavigation().stop();
                return false;
            }
        }

        public void tick() {
            if (EngineerServant.this.repairTicks > 0) {
                EngineerServant.this.getNavigation().stop();
            }

            EngineerServant.this.setTarget(null);
            if (EngineerServant.this.toRepair != null) {
                EngineerServant.this.getLookControl().setLookAt(EngineerServant.this.toRepair, 100.0F, 100.0F);
            }

        }

        public void stop() {
            EngineerServant.this.attackType = 0;
            if (EngineerServant.this.attackTicks > 300 && EngineerServant.this.repairTicks < 1) {
                EngineerServant.this.getNavigation().stop();
            }

            EngineerServant.this.attackTicks = 0;
            EngineerServant.this.repairTicks = 0;
            EngineerServant.this.setAnimationState(0);
            EngineerServant.this.repairCooldown = 300;
        }
    }

    class EngineerRandomStrollGoal<T extends RaiderServant> extends RaiderWanderGoal<T> {
        public EngineerRandomStrollGoal(T p_25734_, double p_25735_) {
            super(p_25734_, p_25735_);
        }

        public boolean canUse() {
            return super.canUse() && EngineerServant.this.attackType == 0;
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && EngineerServant.this.attackType == 0;
        }

        public void stop() {
            if (EngineerServant.this.attackType != REPAIR_ATTACK) {
                super.stop();
            }

        }
    }

    class EngineerLookAtEntityGoal extends LookAtPlayerGoal {
        public EngineerLookAtEntityGoal(Mob p_25524_, Class<? extends LivingEntity> p_25525_, float p_25526_, float p_25527_) {
            super(p_25524_, p_25525_, p_25526_, p_25527_);
        }

        public EngineerLookAtEntityGoal(Mob p_25524_, Class<? extends LivingEntity> p_25525_, float p_25526_) {
            super(p_25524_, p_25525_, p_25526_);
        }

        public boolean canUse() {
            return super.canUse() && EngineerServant.this.attackType == 0;
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && EngineerServant.this.attackType == 0;
        }
    }
}
