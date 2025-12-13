package com.Polarice3.goety_spillage.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.Polarice3.goety_spillage.util.GSMobUtil;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.util.EntityUtil;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CrocofangServant extends RaiderServant implements PlayerRideable, IAutoRideable, ICanBeAnimated {
    private static final UUID SPEED_PENALTY_UUID = UUID.fromString("5BD14A52-AB9A-42D3-A649-90FDE044281E");
    private static final AttributeModifier SPEED_PENALTY = new AttributeModifier(SPEED_PENALTY_UUID, "STOP MOVING AROUND STUPID", -0.35, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(CrocofangServant .class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TRIED_SPAWN = SynchedEntityData.defineId(CrocofangServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(CrocofangServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(CrocofangServant.class, EntityDataSerializers.BOOLEAN);
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState prechargeAnimationState = new AnimationState();
    public AnimationState chargeAnimationState = new AnimationState();
    public AnimationState stunnedAnimationState = new AnimationState();
    private int biteTime;
    private int chargeTime;
    private int stunnedTime;
    private int happyCool;
    public boolean clientAttacking;
    double chargeX;
    double chargeZ;

    public CrocofangServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new StunGoal());
        this.goalSelector.addGoal(0, new ChargeGoal());
        this.goalSelector.addGoal(0, new AttackGoal());
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
    }

    @Override
    public void miscGoal() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.4));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, GSAttributesConfig.CrocofangServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, GSAttributesConfig.CrocofangServantDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, GSAttributesConfig.CrocofangServantArmor.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), GSAttributesConfig.CrocofangServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), GSAttributesConfig.CrocofangServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), GSAttributesConfig.CrocofangServantArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(TRIED_SPAWN, false);
        this.entityData.define(CHARGING, false);
        this.entityData.define(AUTO_MODE, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("AutoMode", this.isAutonomous());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("AutoMode")) {
            this.setAutonomous(pCompound.getBoolean("AutoMode"));
        }
    }

    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger() instanceof Summoned;
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
    }

    public double getPassengersRidingOffset() {
        return this.stunnedTime > 0 ? 1.15 : super.getPassengersRidingOffset() * 1.15;
    }

    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTO_MODE, autonomous);
        if (autonomous) {
            this.playSound(SoundEvents.ARROW_HIT_PLAYER);
            if (!this.isWandering()) {
                this.setWandering(true);
                this.setStaying(false);
            }
        }
    }

    public boolean isAutonomous() {
        return this.entityData.get(AUTO_MODE);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Mob mob){
                if (MobsConfig.ServantRideAutonomous.get()) {
                    return null;
                }
                return mob;
            } else if (entity instanceof LivingEntity
                    && this.notClientAttacking()
                    && !this.isAutonomous()) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }

    public boolean notClientAttacking(){
        return !this.clientAttacking;
    }

    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    public boolean hasPassenger(){
        return !this.getPassengers().isEmpty();
    }

    protected void doPlayerRide(Player player) {
        if (!this.level.isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4){
            this.clientAttacking = true;
        } else if (pId == 5){
            this.clientAttacking = false;
        } else if (pId == 102) {
            this.happyCool = 40;
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_AMBIENT.get(), 1.0F, 1.5F);
        }  else {
            super.handleEntityEvent(pId);
        }
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.hasPassenger()
                    && ((rider instanceof Player && !this.isAutonomous())
                    || (rider instanceof IServant servant && (servant.isStaying() || servant.isCommanded() || servant.isGuardingArea())))
                    && this.notClientAttacking()) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float speed = this.getSpeed();
                float f = rider.xxa * speed;
                float f1 = rider.zza * speed;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                if (this.isInWater() && this.getFluidTypeHeight(ForgeMod.WATER_TYPE.get()) > this.getFluidJumpThreshold() || this.isInLava() || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType) && height > this.getFluidJumpThreshold())) {
                    Vec3 vector3d = this.getDeltaMovement();
                    this.setDeltaMovement(vector3d.x, 0.04F, vector3d.z);
                    this.hasImpulse = true;
                    if (f1 > 0.0F) {
                        float f2 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
                        float f3 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
                        this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f2 * 0.04F), 0.0D, (double) (0.4F * f3 * 0.04F)));
                    }
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;

                this.calculateEntityAnimation(false);
            } else {
                super.travel(pTravelVector);
            }
        }
    }

    public float getStepHeight() {
        return 1.0F;
    }

    public SoundEvent getCelebrateSound() {
        return IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_AMBIENT.get();
    }

    protected @Nullable SoundEvent getAmbientSound() {
        return IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_DEATH.get();
    }

    public boolean isPersistenceRequired() {
        return true;
    }

    public void tick() {
        super.tick();

        AttributeInstance instance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (instance != null) {
            if (this.chargeTime <= 0 && this.stunnedTime <= 0) {
                instance.removeModifier(SPEED_PENALTY);
            } else {
                instance.removeModifier(SPEED_PENALTY);
                instance.addTransientModifier(SPEED_PENALTY);
            }
        }

        if (this.isAlive()) {
            if (!this.level.isClientSide){
                if (this.chargeTime > 0){
                    this.level.broadcastEntityEvent(this, (byte)4);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)5);
                }
            }
            if (this.happyCool > 0) {
                --this.happyCool;
            }
            DamageSource damageSource = this.getServantAttack();
            if (this.biteTime > 0) {
                --this.biteTime;
                if (this.biteTime == 11) {
                    float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
                    float radius2 = 2.0F;
                    double x = this.getX() + 0.800000011920929 * Math.sin((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius2 * Math.sin((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                    double z = this.getZ() + 0.800000011920929 * Math.cos((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius2 * Math.cos((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                    List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(x - 1.0, this.getY(), z - 1.0, x + 1.0, this.getY() + 1.0, z + 1.0));

                    for (LivingEntity caught : list) {
                        if (caught != this && caught.isAlive() && !MobUtil.areAllies(caught, this)) {
                            caught.hurt(damageSource, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                            caught.knockback((double) (f1 * 0.5F), (double) Mth.sin(this.getYRot() * 0.017453292F), (double) (-Mth.cos(this.getYRot() * 0.017453292F)));
                        }
                    }
                }
            }

            if (this.chargeTime > 0) {
                ++this.chargeTime;
                this.setYRot(this.getYHeadRot());
                this.yBodyRot = this.getYRot();
                if (this.getTarget() != null) {
                    LivingEntity t = this.getTarget();
                    double chargeX = this.getX() - t.getX();
                    double chargeY = this.getY() - t.getY();
                    double chargeZ = this.getZ() - t.getZ();
                    double charged = Math.sqrt(chargeX * chargeX + chargeY * chargeY + chargeZ * chargeZ);
                    float power = 3.8F;
                    double motionX = this.getDeltaMovement().x - chargeX / charged * (double)power * 0.2;
                    double motionZ = this.getDeltaMovement().z - chargeZ / charged * (double)power * 0.2;
                    if (this.chargeTime == 30) {
                        if (!this.level.isClientSide) {
                            this.setCharging(true);
                        }

                        this.setAnimationState(3);
                        this.setCharge(motionX, motionZ);
                    }

                    if (this.chargeTime == 31) {
                        GSMobUtil.mobFollowingSound(this.level(), this, IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_CHARGE.get(), 3.0F, 1.0F, false);
                    }

                    if (this.chargeTime > 30 && this.chargeTime <= 64) {
                        this.setDeltaMovement(this.chargeX, this.getDeltaMovement().y, this.chargeZ);

                        for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(15.0))) {
                            if (!MobUtil.areAllies(entity, this) && entity instanceof LivingEntity target && entity.isAlive()) {
                                double x = this.getX() - entity.getX();
                                double y = this.getY() - entity.getY();
                                double z = this.getZ() - entity.getZ();
                                double d = Math.sqrt(x * x + y * y + z * z);
                                if (this.distanceToSqr(entity) < 9.0) {
                                    if (entity.invulnerableTime <= 0) {
                                        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                                        entity.hurt(damageSource, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                                        entity.hurtMarked = true;
                                        entity.setDeltaMovement(-x / d * 2.0, -y / d * 2.0 + 0.5, -z / d * 2.0);
                                    }

                                    if (target.isBlocking()) {
                                        EntityUtil.disableShield(target, 100);
                                        if (target instanceof AbstractHauntedArmor armor){
                                            armor.disableShield(true);
                                        }
                                        this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.7F);
                                        this.hurt(damageSource, 4.0F);
                                        this.setAnimationState(4);
                                        if (!this.level.isClientSide) {
                                            this.setCharging(false);
                                        }

                                        this.chargeTime = 0;
                                        this.stunnedTime = 120;
                                    }
                                }
                            }
                        }

                        if (this.horizontalCollision) {
                            this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 0.7F);
                            this.hurt(this.damageSources().generic(), 4.0F);
                            this.setAnimationState(4);
                            if (!this.level.isClientSide) {
                                this.setCharging(false);
                            }

                            this.chargeTime = 0;
                            this.stunnedTime = 120;
                        }
                    }
                }

                if (this.chargeTime >= 70) {
                    this.chargeAnimationState.stop();
                    this.setAnimationState(0);
                    if (!this.level.isClientSide) {
                        this.setCharging(false);
                    }

                    this.chargeTime = 0;
                }
            }
        }

        if (this.stunnedTime > 0) {
            --this.stunnedTime;
        }

    }

    public void setCharge(double x, double z) {
        this.chargeX = x;
        this.chargeZ = z;
    }

    public boolean doHurtTarget(Entity p_21372_) {
        if (this.biteTime < 1 && this.chargeTime < 1 && this.stunnedTime < 1) {
            this.attackAnimationState.stop();
            this.setAnimationState(0);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_BITE.get(), 1.0F, 1.0F);
            this.biteTime = 20;
            this.setAnimationState(1);
        }

        return false;
    }

    public void setAnimationState(int input) {
        this.entityData.set(ANIMATION_STATE, input);
    }

    public AnimationState getAnimationState(String input) {
        if (Objects.equals(input, "attack")) {
            return this.attackAnimationState;
        } else if (Objects.equals(input, "precharge")) {
            return this.prechargeAnimationState;
        } else if (Objects.equals(input, "charge")) {
            return this.chargeAnimationState;
        } else {
            return Objects.equals(input, "stunned") ? this.stunnedAnimationState : new AnimationState();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ANIMATION_STATE.equals(p_21104_) && this.level().isClientSide) {
            switch (this.entityData.get(ANIMATION_STATE)) {
                case 0:
                case 1:
                    this.chargeAnimationState.stop();
                    this.attackAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.attackAnimationState.stop();
                    this.prechargeAnimationState.start(this.tickCount);
                    break;
                case 3:
                    this.prechargeAnimationState.stop();
                    this.chargeAnimationState.start(this.tickCount);
                    break;
                case 4:
                    this.chargeAnimationState.stop();
                    this.stunnedAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        return !this.getPassengers().contains(p_37849_.getEntity()) && super.hurt(p_37849_, p_37850_);
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!pPlayer.isCrouching() && this.stunnedTime <= 0) {
                if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer){
                    this.getFirstPassenger().stopRiding();
                    return InteractionResult.SUCCESS;
                } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)){
                    this.doPlayerRide(pPlayer);
                    return InteractionResult.SUCCESS;
                }
            } else if (pHand == InteractionHand.MAIN_HAND) {
                if (this.isFood(pPlayer.getMainHandItem()) && this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodProperties = itemstack.getFoodProperties(this);
                    if (foodProperties != null) {
                        this.heal((float) foodProperties.getNutrition());
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }

                        this.gameEvent(GameEvent.EAT, this);
                        this.eat(this.level, itemstack);
                        if (this.level instanceof ServerLevel serverLevel) {

                            for (int i = 0; i < 7; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02;
                                double d1 = this.random.nextGaussian() * 0.02;
                                double d2 = this.random.nextGaussian() * 0.02;
                                serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, d0, d1, d2, 0.5);
                            }
                        }
                        pPlayer.swing(pHand);
                        return InteractionResult.SUCCESS;
                    }
                } else if (itemstack.isEmpty() && this.happyCool <= 0){
                    this.happyCool = 40;
                    this.level.broadcastEntityEvent(this, (byte)102);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_AMBIENT.get(), 1.0F, 1.5F);
                    this.heal(1.0F);
                    if (this.level instanceof ServerLevel serverLevel){
                        for(int i = 0; i < 5; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02;
                            double d1 = this.random.nextGaussian() * 0.02;
                            double d2 = this.random.nextGaussian() * 0.02;
                            serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 1.0, this.getRandomZ(1.0), 0, d0, d1, d2, 1.0F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    class StunGoal extends Goal {
        public boolean canUse() {
            return CrocofangServant.this.stunnedTime > 0;
        }

        public boolean canContinueToUse() {
            return CrocofangServant.this.stunnedTime > 0;
        }

        public StunGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public void tick() {
            CrocofangServant.this.getNavigation().stop();
        }

        public void stop() {
            CrocofangServant.this.stunnedAnimationState.stop();
            CrocofangServant.this.setAnimationState(0);
        }
    }

    class ChargeGoal extends Goal {
        public boolean canUse() {
            return CrocofangServant.this.biteTime < 1 && CrocofangServant.this.getTarget() != null && CrocofangServant.this.hasLineOfSight(CrocofangServant.this.getTarget()) && CrocofangServant.this.random.nextFloat() * 75.0F < 0.9F && CrocofangServant.this.distanceToSqr(CrocofangServant.this.getTarget()) > 27.0 && CrocofangServant.this.stunnedTime < 1;
        }

        public boolean canContinueToUse() {
            return CrocofangServant.this.chargeTime > 0;
        }

        public void start() {
            CrocofangServant.this.attackAnimationState.stop();
            CrocofangServant.this.setAnimationState(2);
            CrocofangServant.this.playSound(IllageAndSpillageSoundEvents.ENTITY_CROCOFANG_PREPARE_CHARGE.get(), 3.0F, 1.0F);
            CrocofangServant.this.chargeTime = 1;
        }

        public ChargeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public void tick() {
            CrocofangServant.this.getNavigation().stop();
            if (CrocofangServant.this.getTarget() != null) {
                CrocofangServant.this.getLookControl().setLookAt(CrocofangServant.this.getTarget(), 100.0F, 100.0F);
            }
        }
    }

    class AttackGoal extends Goal {
        public boolean canUse() {
            return CrocofangServant.this.biteTime > 0;
        }

        public boolean canContinueToUse() {
            return CrocofangServant.this.biteTime > 0;
        }

        public AttackGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public void tick() {
            CrocofangServant.this.getNavigation().stop();
            if (CrocofangServant.this.getTarget() != null) {
                CrocofangServant.this.getLookControl().setLookAt(CrocofangServant.this.getTarget(), 100.0F, 100.0F);
            }
        }
    }
}
