package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GSFactory extends Summoned implements ICanBeAnimated, IEngineerMachine {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(GSFactory.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IN_MOTION = SynchedEntityData.defineId(GSFactory.class, EntityDataSerializers.BOOLEAN);
    public AnimationState introAnimationState = new AnimationState();
    public AnimationState spinAnimationState = new AnimationState();
    private int introTicks;
    private int spawnTicks;

    public GSFactory(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.MAX_HEALTH, 30.0);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(IN_MOTION, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SpawnTicks")){
            this.spawnTicks = compound.getInt("SpawnTicks");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpawnTicks", this.spawnTicks);
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

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_MAGISPELLER_DISPENSER_DESTROY.get();
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION_STATE, state);
    }

    public AnimationState getAnimationState(String var1) {
        if (Objects.equals(var1, "spin")) {
            return this.spinAnimationState;
        } else {
            return Objects.equals(var1, "intro") ? this.introAnimationState : new AnimationState();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ANIMATION_STATE.equals(p_21104_) && this.level.isClientSide) {
            switch (this.entityData.get(ANIMATION_STATE)) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.spinAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.introAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    private void stopAllAnimationStates() {
        this.spinAnimationState.stop();
        this.introAnimationState.stop();
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        if (this.isInMotion()) {
            this.introTicks = 1;
            this.setAnimationState(2);
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

    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
    }

    public List<FactoryServant> getMinions() {
        List<FactoryServant> list = new ArrayList<>();
        if (this.level instanceof ServerLevel serverLevel){
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof FactoryServant servant){
                    if (servant.getTrueOwner() == this && servant.isAlive()){
                        list.add(servant);
                    }
                }
            }
        }
        return list;
    }

    public void tick() {
        if (this.introTicks == 1) {
            this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);
        }

        if (this.introTicks > 0) {
            ++this.introTicks;
        }

        if (this.introTicks == 11) {
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 0.75F);
        }

        if (this.introTicks == 16) {
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 1.0F);
        }

        if (this.introTicks == 21) {
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 1.25F);
        }

        if (!this.isInMotion()) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
            if (this.level instanceof ServerLevel serverLevel){
                ++this.spawnTicks;
                if (this.spawnTicks > 60 && this.isAlive() && this.getMinions().size() < 5) {
                    this.playSound(SoundEvents.DISPENSER_LAUNCH, 1.0F, 1.0F);
                    int randomSelection = this.random.nextInt(0, 3);
                    float f = this.yBodyRot * 0.017453292F * 0.25F;
                    float f1 = Mth.cos(f);
                    double jump = 0.5D;
                    FactoryServant summoned;
                    Vec3 vec3;
                    if (randomSelection == 0) {
                        summoned = GSEntityTypes.BEEPER.get().create(serverLevel);
                        vec3 = new Vec3(this.getX(), this.getY(), this.getZ() + (double)f1 * -0.1);
                    } else if (randomSelection == 1) {
                        summoned = GSEntityTypes.SNIPER.get().create(serverLevel);
                        vec3 = new Vec3(this.getX() + (double)f1 * 0.45, this.getY(), this.getZ() + (double)f1 * -0.2);
                        jump = 0.1D;
                    } else {
                        summoned = GSEntityTypes.POKER.get().create(serverLevel);
                        vec3 = new Vec3(this.getX() + (double)f1 * -0.55, this.getY(), this.getZ() + (double)f1 * 0.05);
                    }
                    if (summoned != null) {
                        summoned.setPos(vec3);
                        summoned.setDeltaMovement(0.0D, jump, 0.0D);
                        summoned.setTrueOwner(this);
                        summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        serverLevel.addFreshEntity(summoned);
                    }

                    this.spawnTicks = 0;
                }
            }
        }

        if (this.onGround() && this.isInMotion()) {
            if (this.introTicks < 1) {
                this.introTicks = 1;
            }

            this.setAnimationState(2);
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
