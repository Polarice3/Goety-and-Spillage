package com.Polarice3.goety_spillage.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.common.entities.ally.factory.IEngineerMachine;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.Polarice3.goety_spillage.config.GSMobsConfig;
import com.yellowbrossproductions.illageandspillage.Config;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class PreserverServant extends AbstractIllagerServant {
    private static final EntityDataAccessor<Boolean> TRYING_TO_PROTECT = SynchedEntityData.defineId(PreserverServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> JUMP_ANIM_TICKS = SynchedEntityData.defineId(PreserverServant.class, EntityDataSerializers.INT);
    private LivingEntity thingToProtect = null;
    private int cooldownTime;
    private LivingEntity entityToParticle;
    private int tickCountWhenProtected;

    public PreserverServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TryToProtectGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.MAX_HEALTH, GSAttributesConfig.PreserverServantHealth.get())
                .add(Attributes.ARMOR, GSAttributesConfig.PreserverServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), GSAttributesConfig.PreserverServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), GSAttributesConfig.PreserverServantArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRYING_TO_PROTECT, false);
        this.entityData.define(JUMP_ANIM_TICKS, 0);
    }

    public boolean canBeLeader() {
        return false;
    }

    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        if (this.isTryingToProtect()) {
            if (this.getThingToProtect() != null && this.getThingToProtect().isAlive() && this.getThingToProtect().distanceToSqr(this) < 6.0D) {
                this.getThingToProtect().addEffect(new MobEffectInstance(EffectRegisterer.PRESERVED.get(), MathHelper.secondsToTicks(GSMobsConfig.PreserverDuration.get()), 0, false, false));
                this.entityToParticle = this.getThingToProtect();
                this.tickCountWhenProtected = this.getThingToProtect().tickCount;
            }

            this.setTryingToProtect(false);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_PRESERVER_LAND.get(), 1.0F, 1.0F);
            if (!this.level.isClientSide) {
                this.setJumpAnimationTick(0);
            }

            this.cooldownTime = 200;
        }

        return false;
    }

    public void tick() {
        super.tick();

        if (this.entityToParticle != null && this.entityToParticle.level instanceof ServerLevel serverLevel && this.entityToParticle.tickCount - 10 < this.tickCountWhenProtected) {
            for (int i = 0; i < 10; i++) {
                double d0 = (-0.5 + this.entityToParticle.getRandom().nextGaussian()) / 4.0;
                double d1 = (-0.5 + this.entityToParticle.getRandom().nextGaussian()) / 4.0;
                double d2 = (-0.5 + this.entityToParticle.getRandom().nextGaussian()) / 4.0;
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.entityToParticle.getRandomX(1.0), this.entityToParticle.getRandomY() + 1.0, this.entityToParticle.getRandomZ(1.0), 0, d0, d1, d2, 1.0F);
            }
        }

        if (this.cooldownTime > 0) {
            --this.cooldownTime;
        }

        if (this.isTryingToProtect()) {
            if (!this.level.isClientSide) {
                this.setJumpAnimationTick(this.getJumpAnimationTick() + 1);
            }

            this.setYRot(this.getYHeadRot());
            this.yBodyRot = this.getYRot();
            LivingEntity thing = this.getThingToProtect();
            if (this.getThingToProtect() != null) {
                this.getLookControl().setLookAt(this.getThingToProtect(), 100.0F, 100.0F);
            }

            if (this.getThingToProtect() != null) {
                thing.setDeltaMovement(0.0, thing.getDeltaMovement().y, 0.0);
            }

            if (this.getJumpAnimationTick() == 20 && this.getThingToProtect() != null) {
                double multiplier = 0.4;
                this.setDeltaMovement((thing.getX() - this.getX()) * multiplier, (thing.getY() - this.getY()) * multiplier, (thing.getZ() - this.getZ()) * multiplier);
            }
        }

    }

    public int getJumpAnimationTick() {
        return this.entityData.get(JUMP_ANIM_TICKS);
    }

    public void setJumpAnimationTick(int tick) {
        this.entityData.set(JUMP_ANIM_TICKS, tick);
    }

    public boolean isTryingToProtect() {
        return this.entityData.get(TRYING_TO_PROTECT);
    }

    public void setTryingToProtect(boolean trying) {
        this.entityData.set(TRYING_TO_PROTECT, trying);
    }

    public LivingEntity getThingToProtect() {
        return this.thingToProtect;
    }

    public void setThingToProtect(LivingEntity thingToProtect) {
        this.thingToProtect = thingToProtect;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_213386_1_, DifficultyInstance p_213386_2_, MobSpawnType p_213386_3_, @Nullable SpawnGroupData p_213386_4_, @Nullable CompoundTag p_213386_5_) {
        this.cooldownTime = 100;
        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.GENERIC_KILL) && !source.is(DamageTypeTags.IS_FIRE)) {
            amount *= 0.5F;
        }

        return super.hurt(source, amount);
    }

    public SoundEvent getCelebrateSound() {
        return IllageAndSpillageSoundEvents.ENTITY_PRESERVER_AMBIENT.get();
    }

    protected SoundEvent getAmbientSound() {
        return IllageAndSpillageSoundEvents.ENTITY_PRESERVER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return IllageAndSpillageSoundEvents.ENTITY_PRESERVER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_PRESERVER_DEATH.get();
    }

    public Predicate<LivingEntity> getProtectablePredicate(){
        return predicate -> predicate instanceof RaiderServant servant
                && (servant.isRaiding() || servant.getTarget() != null)
                && !(predicate instanceof IllagerAttack)
                && !(predicate instanceof IEngineerMachine)
                && (double) predicate.getBbWidth() < 1.0D
                && (double) predicate.getBbHeight() < 2.5D
                && this.hasLineOfSight(predicate)
                && predicate.isAlive()
                && !(predicate instanceof PreserverServant)
                && !predicate.hasEffect(EffectRegisterer.PRESERVED.get())
                && MobUtil.areAllies(predicate, this)
                && !(Config.CommonConfig.preserver_cannotProtect.get()).contains(predicate.getEncodeId());
    }

    public boolean isSomethingProtectableNearby() {
        return !this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(12.0), this.getProtectablePredicate()).isEmpty();
    }

    protected float getStandingEyeHeight(Pose p_21131_, EntityDimensions p_21132_) {
        return this.isTryingToProtect() ? 1.125F : 1.75F;
    }

    public boolean canWearArmor() {
        return false;
    }

    public boolean canHaveWeapon() {
        return false;
    }

    class TryToProtectGoal extends Goal {

        public boolean canUse() {
            return PreserverServant.this.random.nextInt(8) == 0
                    && PreserverServant.this.isSomethingProtectableNearby()
                    && PreserverServant.this.onGround()
                    && PreserverServant.this.cooldownTime < 1
                    && PreserverServant.this.hurtTime < 1
                    && !PreserverServant.this.isTryingToProtect();
        }

        public boolean canContinueToUse() {
            return PreserverServant.this.isTryingToProtect();
        }

        public TryToProtectGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public void start() {
            List<LivingEntity> list = PreserverServant.this.level().getEntitiesOfClass(LivingEntity.class, PreserverServant.this.getBoundingBox().inflate(6.0), PreserverServant.this.getProtectablePredicate());
            if (!list.isEmpty()) {
                LivingEntity thing = list.get(PreserverServant.this.random.nextInt(list.size()));
                PreserverServant.this.setThingToProtect(thing);
                double multiplier = 0.2D;
                PreserverServant.this.setDeltaMovement((thing.getX() - PreserverServant.this.getX()) * multiplier, 1.2, (thing.getZ() - PreserverServant.this.getZ()) * multiplier);
                PreserverServant.this.setTryingToProtect(true);
                PreserverServant.this.playSound(IllageAndSpillageSoundEvents.ENTITY_PRESERVER_JUMP.get(), 1.0F, 1.0F);
            }

        }
    }

}
