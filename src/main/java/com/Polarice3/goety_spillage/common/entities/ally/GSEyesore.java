package com.Polarice3.goety_spillage.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.yellowbrossproductions.illageandspillage.entities.goal.StareAtDeadFreakGoal;
import com.yellowbrossproductions.illageandspillage.particle.ParticleRegisterer;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;

public class GSEyesore extends Summoned {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(GSEyesore.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(GSEyesore.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SCARED = SynchedEntityData.defineId(GSEyesore.class, EntityDataSerializers.BOOLEAN);
    public AnimationState slitherAnimationState = new AnimationState();
    public AnimationState flyAnimationState = new AnimationState();
    private BlockPos targetPos;

    public GSEyesore(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new StareAtDeadFreakGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AvoidRagnoGoal<>(this, RagnoServant.class, 16.0F, 0.6499999761581421, 0.6000000238418579));
        this.goalSelector.addGoal(1, new SlitherGoal(this, 0.5));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.75)
                .add(Attributes.MAX_HEALTH, 5.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(FLYING, false);
        this.entityData.define(SCARED, false);
    }

    @Override
    public int xpReward() {
        return 0;
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return p_21197_.getEffect() != EffectRegisterer.MUTATION.get() && super.canBeAffected(p_21197_);
    }

    protected @Nullable SoundEvent getAmbientSound() {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_EYESORE_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_EYESORE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_EYESORE_DEATH.get();
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION_STATE, state);
    }

    public AnimationState getAnimationState(String var1) {
        if (Objects.equals(var1, "fly")) {
            return this.flyAnimationState;
        } else {
            return Objects.equals(var1, "slither") ? this.slitherAnimationState : new AnimationState();
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
                    this.slitherAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.flyAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    private void stopAllAnimationStates() {
        this.slitherAnimationState.stop();
        this.flyAnimationState.stop();
    }

    public float getAnimationSpeed() {
        return this.entityData.get(ANIMATION_STATE) == 1 ? 2.5F : 1.0F;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        if (this.isFlying()) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_EYESORE_LAND.get(), 2.0F, this.getVoicePitch());
            this.setFlying(false);
            return false;
        } else {
            return super.causeFallDamage(p_225503_1_, p_225503_2_, p_147189_);
        }
    }

    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        boolean shouldHurt = super.hurt(p_37849_, p_37850_);
        if (shouldHurt) {
            this.makeBloodParticles();
        }

        return shouldHurt;
    }

    public void makeCryParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            double d0 = -0.5 + this.random.nextGaussian();
            double d1 = -0.5 + this.random.nextGaussian();
            double d2 = -0.5 + this.random.nextGaussian();
            serverLevel.sendParticles(ParticleTypes.SPLASH, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0, d0, d1, d2, 0.5F);
        }
    }

    public void makeBloodParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 5; ++i) {
                double d0 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d1 = (1.0 + this.random.nextGaussian()) / 4.0;
                double d2 = (-0.5 + this.random.nextGaussian()) / 4.0;
                serverLevel.sendParticles(ParticleRegisterer.BLOOD_PARTICLES.get(), this.getX(), this.getY(), this.getZ(), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public void tick() {
        super.tick();
        if (!this.isFlying() && this.entityData.get(ANIMATION_STATE) != 1) {
            this.setAnimationState(1);
        }

        this.targetPos = this.getTarget() != null && this.getTarget().isAlive() ? this.getTarget().getOnPos() : null;

        for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.7))){
            if (this.isEffectiveAi() && !MobUtil.areAllies(livingEntity, this)) {
                this.dealDamage(livingEntity);
            }
        }

        if (this.onGround()) {
            this.setFlying(false);
        }

        if (this.isScared()) {
            this.makeCryParticles();
        }

    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
        this.setAnimationState(flying ? 2 : 1);
    }

    public boolean isScared() {
        return this.entityData.get(SCARED);
    }

    public void setScared(boolean scared) {
        this.entityData.set(SCARED, scared);
    }

    protected void dealDamage(LivingEntity entity) {
        DamageSource damageSource = this.damageSources().mobAttack(this);
        if (this.getTrueOwner() != null){
            damageSource = ModDamageSource.summonAttack(this, this.getTrueOwner());
        }
        if (this.isAlive() && this.hasLineOfSight(entity) && entity.hurt(damageSource, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
            double d2 = entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            double d1 = Math.max(0.0, 1.0 - d2);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.4D * d1, 0.0));
            this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.doEnchantDamageEffects(this, entity);
        }

    }

    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        super.knockback(p_147241_ * 1.5, p_147242_ * 2.0, p_147243_ * 1.5);
    }

    class AvoidRagnoGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        public AvoidRagnoGoal(PathfinderMob p_25033_, Class<T> tClass, float p_25035_, double p_25036_, double p_25037_) {
            super(p_25033_, tClass, p_25035_, p_25036_, p_25037_, AvoidRagnoGoal::isEntityCrazyRagno);
        }

        public static boolean isEntityCrazyRagno(Entity entity) {
            return entity instanceof RagnoServant && ((RagnoServant)entity).isCrazy();
        }

        public void start() {
            super.start();
            GSEyesore.this.setScared(true);
        }

        public void stop() {
            super.stop();
            GSEyesore.this.setScared(false);
        }
    }

    class SlitherGoal extends Goal {
        protected final GSEyesore eyesore;
        protected final double speedModifier;
        protected double posX;
        protected double posY;
        protected double posZ;

        public SlitherGoal(GSEyesore p_25691_, double p_25692_) {
            this.eyesore = p_25691_;
            this.speedModifier = p_25692_;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            BlockPos targetPos = GSEyesore.this.targetPos;
            Vec3 vec3;
            if (targetPos != null && !(this.eyesore.position().distanceToSqr(targetPos.getCenter()) <= 256.0)) {
                LivingEntity mob = this.eyesore.getTarget() != null ? this.eyesore.getTarget() : this.eyesore;
                vec3 = mob.getOnPos().getCenter();
            } else {
                vec3 = DefaultRandomPos.getPos(this.eyesore, 5, 4);
            }

            if (targetPos != null && vec3 != null) {
                this.posX = vec3.x;
                this.posY = vec3.y;
                this.posZ = vec3.z;
                return true;
            } else if (targetPos == null && vec3 != null) {
                this.posX = vec3.x;
                this.posY = vec3.y;
                this.posZ = vec3.z;
                return true;
            } else {
                return false;
            }
        }

        public void start() {
            this.eyesore.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        }

        public boolean canContinueToUse() {
            return !this.eyesore.getNavigation().isDone();
        }
    }
}
