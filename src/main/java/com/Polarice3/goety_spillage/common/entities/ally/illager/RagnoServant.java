package com.Polarice3.goety_spillage.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CSetDeltaMovement;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.GSTot;
import com.Polarice3.goety_spillage.common.entities.ally.undead.GSFunnybone;
import com.Polarice3.goety_spillage.common.entities.projectiles.GSPumpkinBomb;
import com.Polarice3.goety_spillage.common.entities.projectiles.GSWebNet;
import com.Polarice3.goety_spillage.common.entities.projectiles.WebProjectile;
import com.Polarice3.goety_spillage.common.entities.util.DarkEffectCloud;
import com.Polarice3.goety_spillage.common.network.GSNetwork;
import com.Polarice3.goety_spillage.common.network.server.SSetDeltaMovement;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.entities.CameraShakeEntity;
import com.yellowbrossproductions.illageandspillage.entities.VillagerSoulEntity;
import com.yellowbrossproductions.illageandspillage.init.ModEntityTypes;
import com.yellowbrossproductions.illageandspillage.particle.ParticleRegisterer;
import com.yellowbrossproductions.illageandspillage.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class RagnoServant extends RaiderServant implements PlayerRideableJumping, IAutoRideable, ICanBeAnimated {
    private static final UUID SPEED_PENALTY_UUID = UUID.fromString("5CD17A52-AB9A-42D3-A629-90FDE04B281E");
    private static final AttributeModifier SPEED_PENALTY = new AttributeModifier(SPEED_PENALTY_UUID, "STOP MOVING AROUND STUPID", -0.35, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CRAZY = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STUNNED = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> BURROWING = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GRABBING = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STUN_HEALTH= SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TICKS = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RAGNO_FACE = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHAKE_MULTIPLIER = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FRAME = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    public AnimationState intro1AnimationState = new AnimationState();
    public AnimationState intro2AnimationState = new AnimationState();
    public AnimationState phaseAnimationState = new AnimationState();
    public AnimationState blockAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState webAnimationState = new AnimationState();
    public AnimationState webNetAnimationState = new AnimationState();
    public AnimationState pullInAnimationState = new AnimationState();
    public AnimationState netSlamAnimationState = new AnimationState();
    public AnimationState jumpAnimationState = new AnimationState();
    public AnimationState landAnimationState = new AnimationState();
    public AnimationState leapAnimationState = new AnimationState();
    public AnimationState burrowAnimationState = new AnimationState();
    public AnimationState popupAnimationState = new AnimationState();
    public AnimationState chargeAnimationState = new AnimationState();
    public AnimationState coughAnimationState = new AnimationState();
    public AnimationState stunAnimationState = new AnimationState();
    public AnimationState fallAnimationState = new AnimationState();
    public AnimationState grabAnimationState = new AnimationState();
    public AnimationState breathAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();
    public LivingEntity entityToStareAt;
    private int attackCooldown;
    private final int WEB_ATTACK = 1;
    private final int LEAP_ATTACK = 2;
    private final int BURROW_ATTACK = 3;
    private final int CHARGE_ATTACK = 4;
    private final int COUGH_ATTACK = 5;
    private final int ATTACK_ATTACK = 6;
    private final int JUMP_ATTACK = 7;
    private final int WEB_NET_ATTACK = 8;
    private final int BREATH_ATTACK = 9;
    private int webCooldown;
    private int webNetCooldown;
    private int jumpCooldown;
    private int leapCooldown;
    private int burrowCooldown;
    private int chargeCooldown;
    private int coughCooldown;
    private int breathCooldown;
    int introTicks;
    int phaseTicks;
    public ItemEntity item = null;
    int blockTicks;
    boolean shouldHurtOnTouch;
    public boolean isPlayingIntro;
    public boolean isPlayingPhase;
    public boolean waitingForWeb;
    public int followupTicks;
    public double chargeX;
    public double chargeZ;
    public boolean circleDirection;
    public boolean clientAttacking;
    protected boolean isJumping;
    protected float playerJumpPendingScale;
    public int circleTick;
    public int stunTick;
    private boolean hasNotBeenStunned = true;
    public DamageSource deathBlow = this.damageSources().generic();

    public RagnoServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new StunGoal());
        this.goalSelector.addGoal(0, new BreathGoal());
        this.goalSelector.addGoal(0, new WebGoal());
        this.goalSelector.addGoal(0, new LeapGoal());
        this.goalSelector.addGoal(0, new WebNetGoal());
        this.goalSelector.addGoal(0, new BurrowGoal());
        this.goalSelector.addGoal(0, new ChargeGoal());
        this.goalSelector.addGoal(0, new CoughGoal());
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public boolean doHurtTarget(Entity p_21372_) {
        return false;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, GSAttributesConfig.RagnoServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, GSAttributesConfig.RagnoServantDamage.get())
                .add(Attributes.ARMOR, GSAttributesConfig.RagnoServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 50.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), GSAttributesConfig.RagnoServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), GSAttributesConfig.RagnoServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), GSAttributesConfig.RagnoServantArmor.get());
    }

    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger() instanceof Summoned;
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
    }

    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(RAGNO_FACE, 0);
        this.entityData.define(FRAME, 0);
        this.entityData.define(SHAKE_MULTIPLIER, 0);
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(CRAZY, false);
        this.entityData.define(BURROWING, false);
        this.entityData.define(GRABBING, false);
        this.entityData.define(STUN_HEALTH, this.getMaxStunHealth());
        this.entityData.define(STUNNED, false);
        this.entityData.define(AUTO_MODE, false);
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(ATTACK_TICKS, 0);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasNotBeenStunned", this.hasNotBeenStunned);
        pCompound.putInt("face", this.getRagnoFace());
        pCompound.putInt("shake", this.getShakeMultiplier());
        pCompound.putInt("IntroTicks", this.introTicks);
        pCompound.putInt("AttackCoolDown", this.attackCooldown);
        pCompound.putInt("WebCoolDown", this.webCooldown);
        pCompound.putInt("LeapCoolDown", this.leapCooldown);
        pCompound.putInt("BurrowCoolDown", this.burrowCooldown);
        pCompound.putInt("ChargeCoolDown", this.chargeCooldown);
        pCompound.putInt("CoughCoolDown", this.coughCooldown);
        pCompound.putBoolean("Crazy", this.isCrazy());
        pCompound.putBoolean("AutoMode", this.isAutonomous());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.hasNotBeenStunned = pCompound.getBoolean("hasNotBeenStunned");
        this.setRagnoFace(pCompound.getInt("face"));
        this.setShakeMultiplier(pCompound.getInt("shake"));
        if (pCompound.contains("IntroTicks")) {
            this.introTicks = pCompound.getInt("IntroTicks");
        }
        if (pCompound.contains("AttackCoolDown")) {
            this.attackCooldown = pCompound.getInt("AttackCoolDown");
        }
        if (pCompound.contains("WebCoolDown")) {
            this.webCooldown = pCompound.getInt("WebCoolDown");
        }
        if (pCompound.contains("LeapCoolDown")) {
            this.leapCooldown = pCompound.getInt("LeapCoolDown");
        }
        if (pCompound.contains("BurrowCoolDown")) {
            this.burrowCooldown = pCompound.getInt("BurrowCoolDown");
        }
        if (pCompound.contains("ChargeCoolDown")) {
            this.chargeCooldown = pCompound.getInt("ChargeCoolDown");
        }
        if (pCompound.contains("CoughCoolDown")) {
            this.coughCooldown = pCompound.getInt("CoughCoolDown");
        }
        if (pCompound.contains("Crazy")) {
            this.setCrazy(pCompound.getBoolean("Crazy"));
        }
        if (pCompound.contains("AutoMode")) {
            this.setAutonomous(pCompound.getBoolean("AutoMode"));
        }
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof RagnoServant;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return GSSpellConfig.RagnoLimit.get();
    }

    public void setHealth(float p_21154_) {
        float healthValue = p_21154_ - this.getHealth();
        if (healthValue > 0.0F || ((this.isCrazy() || this.getPassengers().isEmpty()) && !this.isBurrowing() && !this.isGrabbing()) || healthValue <= -1.0E12F) {
            if (this.isCrazy() && !this.level.isClientSide) {
                if (this.getHealth() + healthValue > this.getMaxHealth() / 2.0F) {
                    this.setShakeMultiplier(20);
                    this.setRagnoFace(3);
                } else {
                    this.setShakeMultiplier(40);
                    this.setRagnoFace(4);
                }
            }

            super.setHealth(p_21154_);
        }

    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.CONVERSION || pReason == MobSpawnType.MOB_SUMMONED || (this.getTrueOwner() != null && pReason != MobSpawnType.SPAWN_EGG)){
            this.playIntro();
        } else {
            this.setShakeMultiplier(10);
        }
        return spawnGroupData;
    }

    public int xpReward() {
        return 40;
    }

    public float getStepHeight() {
        return 2.0F;
    }

    public boolean isCrazy() {
        return this.entityData.get(CRAZY);
    }

    public void setCrazy(boolean crazy) {
        this.entityData.set(CRAZY, crazy);
    }

    public boolean isBurrowing() {
        return this.entityData.get(BURROWING);
    }

    public void setBurrowing(boolean burrowing) {
        this.entityData.set(BURROWING, burrowing);
    }

    public boolean isGrabbing() {
        return this.entityData.get(GRABBING);
    }

    public void setGrabbing(boolean grabbing) {
        this.entityData.set(GRABBING, grabbing);
    }

    public int getRagnoFace() {
        return this.entityData.get(RAGNO_FACE);
    }

    public void setRagnoFace(int face) {
        this.entityData.set(RAGNO_FACE, face);
    }

    public int getShakeMultiplier() {
        return this.entityData.get(SHAKE_MULTIPLIER);
    }

    public void setShakeMultiplier(int shake) {
        this.entityData.set(SHAKE_MULTIPLIER, shake);
    }

    public int getFrame() {
        return this.entityData.get(FRAME);
    }

    public void setFrame(int frame) {
        this.entityData.set(FRAME, frame);
    }

    public boolean isStunned() {
        return this.entityData.get(STUNNED);
    }

    public void setStunned(boolean stunned) {
        if (stunned && this.hasNotBeenStunned) {
            this.hasNotBeenStunned = false;
        }

        this.entityData.set(STUNNED, stunned);
    }

    protected PathNavigation createNavigation(Level p_33802_) {
        return new WallClimberNavigation(this, p_33802_);
    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean p_33820_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_33820_) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 &= -2;
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIMATION_STATE);
    }

    public boolean isPickable() {
        return !this.isBurrowing() && !this.isGrabbing() && super.isPickable();
    }

    public boolean isAttackable() {
        return !this.isBurrowing() && !this.isGrabbing() && super.isAttackable();
    }

    public boolean attackable() {
        return !this.isBurrowing() && !this.isGrabbing() && super.attackable();
    }

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable() || this.isBurrowing() || this.isPlayingIntro();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return super.isInvulnerableTo(p_20122_) || this.isBurrowing() || this.isPlayingIntro();
    }

    public boolean canBeSeenByAnyone() {
        return super.canBeSeenByAnyone() && !this.isBurrowing() && !this.isPlayingIntro();
    }

    public void push(Entity p_21294_) {
        if (this.entityData.get(ANIMATION_STATE) != 6) {
            super.push(p_21294_);
        }

    }

    protected void pushEntities() {
        if (this.entityData.get(ANIMATION_STATE) != 6) {
            super.pushEntities();
        }

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

    public float getAttackValue(){
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi() && !this.isCrazy()) {
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

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return p_21197_.getEffect() != EffectRegisterer.MUTATION.get() && super.canBeAffected(p_21197_);
    }

    public boolean halfHealth() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    public void goCrazy(){
        if (this.isAlive() && this.phaseTicks < 1){
            if (!this.level.isClientSide) {
                this.stopAttacking();
                this.level.broadcastEntityEvent(this, (byte) 11);
                this.setStunned(false);
            }

            this.isPlayingPhase = true;
            this.setAnimationState(2);
            this.phaseTicks = 1;
        }
    }

    public void tick() {
        if (this.isPlayingIntro) {
            if (this.entityToStareAt != null) {
                this.getLookControl().setLookAt(this.entityToStareAt, 100.0F, 100.0F);
            }

            this.getNavigation().stop();
            this.getMoveControl().strafe(0.0F, 0.0F);
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);

            ++this.introTicks;
            if (this.introTicks - 1 == 11) {
                this.setShakeMultiplier(0);
                this.setAnimationState(11);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.2F, 0, 10);
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.5F);
            }

            if (this.introTicks - 12 == 10) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_ROAR.get(), 3.0F, 1.0F);
            }

            if (this.introTicks - 12 >= 10 && this.introTicks - 12 < 15) {
                this.setShakeMultiplier(this.getShakeMultiplier() + 6);
            }

            if (this.introTicks - 12 == 13) {
                this.setRagnoFace(2);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 50.0F, 0.05F, 48, 20);
            }

            if (this.introTicks - 12 == 54) {
                this.setRagnoFace(1);
                this.setShakeMultiplier(10);
            }

            if (this.introTicks - 12 == 73) {
                this.setRagnoFace(0);
            }

            if (this.introTicks - 12 == 90) {
                this.isPlayingIntro = false;
                this.level.broadcastEntityEvent(this, (byte) 10);
                this.introTicks = 0;
                this.setAnimationState(0);
            }
        }

        if (this.getFrame() == 10 && !this.isPlayingIntro && !this.isPlayingPhase && !this.isBurrowing() && !this.isGrabbing()) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_WEB.get(), 0.5F, this.getVoicePitch());
            if (this.getRagnoFace() == 0) {
                this.setRagnoFace(1);
            }
        }

        if (this.getFrame() == 12 && this.getRagnoFace() == 1) {
            this.setRagnoFace(0);
        }

        if (this.getFrame() > 12 && !this.isPlayingPhase && this.isAlive() && this.random.nextInt(30) == 0) {
            this.setFrame(0);
        }

        if (!this.level.isClientSide) {
            this.setFrame(this.getFrame() + 1);
        }

        AttributeInstance instance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (instance != null) {
            if (this.getAttackType() <= 0 && !this.isPlayingIntro && !this.isPlayingPhase && !this.isStunned()) {
                instance.removeModifier(SPEED_PENALTY);
            } else {
                instance.removeModifier(SPEED_PENALTY);
                instance.addTransientModifier(SPEED_PENALTY);
            }
        }

        if (this.random.nextInt(200) == 0) {
            this.circleDirection = !this.circleDirection;
        }

        this.circleTick += this.circleDirection ? 1 : -1;
        if (this.stunTick > 0) {
            this.getNavigation().stop();
            this.navigation.stop();
        }

        this.stopAttackersFromAttacking();
        if (this.blockTicks > 0) {
            --this.blockTicks;
        }

        if (!this.level.isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }

        if (this.phaseTicks > 0 && this.isAlive()) {
            this.getPassengers().forEach(Entity::stopRiding);
            ++this.phaseTicks;
        }

        if (this.phaseTicks == 20) {
            this.level.addFreshEntity(this.item);
        }

        if (this.phaseTicks == 25) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_LEAP.get(), 2.0F, 1.0F);
        }

        if (this.phaseTicks == 26 && this.item != null) {
            this.makeSpitParticles(this.item);
            this.item.discard();
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_EAT.get(), 2.0F, 1.0F);
        }

        if (this.phaseTicks == 30) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_BLOCK.get(), 2.0F, 1.5F);
        }

        if (this.phaseTicks == 32 || this.phaseTicks == 40) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_EAT.get(), 2.0F, 1.0F);
        }

        if (this.phaseTicks == 50) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 0.7F);
        }

        if (this.phaseTicks == 74) {
            if (!this.level.isClientSide) {
                this.setCrazy(true);
                this.setRagnoFace(3);
                this.setShakeMultiplier(20);
                this.level.broadcastEntityEvent(this, (byte) 7);
            }

            CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.1F, 0, 20);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SCREECH.get(), 2.0F, 1.0F);
        }

        if (this.phaseTicks >= 84) {
            this.getPassengers().forEach(Entity::stopRiding);
            this.isPlayingPhase = false;
            if (!this.level.isClientSide) {
                this.level.broadcastEntityEvent(this, (byte) 12);
            }
            this.setStunHealth(this.getMaxStunHealth());
            this.setAttackTicks(0);
            this.setAttackType(0);
            this.setAnimationState(0);
            this.phaseTicks = 0;
        }

        if (this.getAttackType() > 0) {
            this.setAttackTicks(this.getAttackTicks() + 1);
            if (!this.level.isClientSide) {
                this.level.broadcastEntityEvent(this, (byte) 4);
            }
        } else {
            if (!this.level.isClientSide) {
                this.level.broadcastEntityEvent(this, (byte) 5);
            }
        }

        if (this.attackCooldown > 0) {
            --this.attackCooldown;
        }

        if (this.getAttackType() < 1) {
            if (this.webCooldown > 0) {
                --this.webCooldown;
            }

            if (this.webNetCooldown > 0) {
                --this.webNetCooldown;
            }

            if (this.jumpCooldown > 0) {
                --this.jumpCooldown;
            }

            if (this.leapCooldown > 0) {
                --this.leapCooldown;
            }

            if (this.burrowCooldown > 0) {
                --this.burrowCooldown;
            }

            if (this.chargeCooldown > 0) {
                --this.chargeCooldown;
            }

            if (this.coughCooldown > 0) {
                --this.coughCooldown;
            }

            if (this.breathCooldown > 0) {
                --this.breathCooldown;
            }
        }

        this.attackAI();

        if (this.isNotAttacking()
                && !this.isPlayingIntro()
                && this.getTarget() != null
                && !this.isStaying()
                && this.getControllingPassenger() == null
                && !this.isStunned()) {
            this.circleTarget(this.getTarget(), 10.0F, 0.8F, true, this.circleTick, 0.0F, 1.0F);
            this.lookAt(this.getTarget(), 100.0F, 100.0F);
            this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
        }

        if (this.getStunHealth() <= 0 && !this.isStunned()) {
            this.setStunned(true);
        }

        this.regenerateStunHealth();
        if (this.getStunHealth() <= this.getMaxStunHealth() / 3 && !this.isStunned() && !this.isBurrowing() && this.random.nextInt(4) == 0) {
            this.makeSweatParticles(1);
        }

        if (this.isStunned()) {
            this.getNavigation().stop();
            ++this.stunTick;
            if (this.stunTick == 6) {
                this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_STUN.get(), 3.0F, 1.0F);
            }

            if (this.stunTick % 5 == 0) {
                this.makeSweatParticles(2);
            }

            this.navigation.stop();
        }

        if (this.getTarget() != null && this.isAlive() && (double)this.distanceTo(this.getTarget()) < 8.0 * ((double)this.getTarget().getBbWidth() + 0.4) && this.getAttackType() == 0 && this.onGround() && !this.isStunned() && !this.isPlayingIntro && !this.isPlayingPhase) {
            double deltaX = this.getX() - this.getTarget().getX();
            double extraX = this.getY() - this.getTarget().getY();
            double extraZ = this.getZ() - this.getTarget().getZ();
            double deltaZ = Math.sqrt(deltaX * deltaX + extraX * extraX + extraZ * extraZ);
            this.setDeltaMovement(this.getDeltaMovement().subtract(-deltaX / deltaZ * 0.08, 0.0, -extraZ / deltaZ * 0.08));
        }

        if (this.isCrazy() && this.halfHealth() && !this.isBurrowing() && this.random.nextInt(5) == 0) {
            this.makePassiveMutationParticles();
        }

        super.tick();
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int attackType) {
        this.entityData.set(ATTACK_TYPE, attackType);
    }

    public int getAttackTicks() {
        return this.entityData.get(ATTACK_TICKS);
    }

    public void setAttackTicks(int attackTicks) {
        this.entityData.set(ATTACK_TICKS, attackTicks);
    }

    public void stopAttacking() {
        this.setAttackType(0);
    }

    @Override
    public void mobSense() {
        if (!this.isBurrowing() && !this.isPlayingIntro()){
            super.mobSense();
        }
    }

    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        super.setDeltaMovement(p_20335_, p_20336_, p_20337_);
        if (!this.level.isClientSide){
            if (this.isAttacking()) {
                GSNetwork.sentToTrackingEntity(this, new SSetDeltaMovement(this.getId(), p_20335_, p_20336_, p_20337_));
            }
        }
    }

    public void attackAI(){
        if (this.isAlive()) {
            DamageSource damageSource = this.damageSources().mobAttack(this);
            if (this.getTrueOwner() != null){
                damageSource = ModDamageSource.summonAttack(this, this.getTrueOwner());
            }
            if (this.getAttackType() == WEB_ATTACK && this.isCrazy()) {
                if (this.getAttackTicks() == 4) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.5, 0.0));
                }

                if (this.getAttackTicks() == 7 && this.getTarget() != null) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_WEB.get(), 2.0F, 1.0F);

                    for(int i = 0; i < 8; ++i) {
                        if (!this.level.isClientSide) {
                            WebProjectile projectile = GSEntityTypes.WEB.get().create(this.level);
                            if (projectile != null){
                                projectile.setPos(this.getX(), this.getY() + 1.0, this.getZ());
                                projectile.setYHeadRot(this.getYHeadRot());
                                projectile.setYRot(this.getYHeadRot());
                                double deltaX = projectile.getX() - this.getTarget().getX();
                                double deltaY = projectile.getY() - (this.getTarget().getY() + 1.5);
                                double deltaZ = projectile.getZ() - this.getTarget().getZ();
                                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                                float power = 2.5F;
                                double motionX = -(deltaX / distance * (double)power * 0.2);
                                double motionY = -(deltaY / distance * (double)power * 0.2);
                                double motionZ = -(deltaZ / distance * (double)power * 0.2);
                                double randomX = (-0.5 + this.random.nextDouble()) / 8.0;
                                double randomY = (-0.5 + this.random.nextDouble()) / 8.0;
                                double randomZ = (-0.5 + this.random.nextDouble()) / 8.0;
                                projectile.setAcceleration(motionX + randomX, motionY + randomY, motionZ + randomZ);
                                projectile.setShooter(this);
                                this.level.addFreshEntity(projectile);
                            }
                        }
                    }
                }

                if (this.getAttackTicks() == 10) {
                    this.setDeltaMovement(0.0, -1.0, 0.0);
                }
            }
            if (!this.isCrazy() && ((this.doesAttackMeetNormalRequirements() && this.random.nextInt(16) == 0 && this.webCooldown < 1) || getAttackType() == this.WEB_ATTACK)) {
                if (this.getAttackTicks() == 0) {
                    this.setAnimationState(4);
                    this.setAttackType(WEB_ATTACK);
                }

                this.getNavigation().stop();
                this.getMoveControl().strafe(0.0F, 0.0F);
                if (this.getTarget() != null) {
                    this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                }

                this.getMoveControl().strafe(0.0F, 0.0F);
                this.navigation.stop();

                if (this.getAttackTicks() == 7 && this.getTarget() != null) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_WEB.get(), 2.0F, 1.0F);

                    for(int i = 0; i < 8; ++i) {
                        if (!this.level.isClientSide) {
                            WebProjectile projectile = GSEntityTypes.WEB.get().create(this.level);
                            if (projectile != null){
                                projectile.setPos(this.getX(), this.getY() + 1.0, this.getZ());
                                projectile.setYHeadRot(this.getYHeadRot());
                                projectile.setYRot(this.getYHeadRot());
                                double deltaX = projectile.getX() - this.getTarget().getX();
                                double deltaY = projectile.getY() - (this.getTarget().getY() + 1.5);
                                double deltaZ = projectile.getZ() - this.getTarget().getZ();
                                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                                float power = 2.5F;
                                double motionX = -(deltaX / distance * (double)power * 0.2);
                                double motionY = -(deltaY / distance * (double)power * 0.2);
                                double motionZ = -(deltaZ / distance * (double)power * 0.2);
                                double randomX = (-0.5 + this.random.nextDouble()) / 8.0;
                                double randomY = (-0.5 + this.random.nextDouble()) / 8.0;
                                double randomZ = (-0.5 + this.random.nextDouble()) / 8.0;
                                projectile.setAcceleration(motionX + randomX, motionY + randomY, motionZ + randomZ);
                                projectile.setShooter(this);
                                this.level.addFreshEntity(projectile);
                            }
                        }
                    }
                }

                if (this.getAttackTicks() > 20) {
                    this.setAnimationState(0);
                    this.setAttackTicks(0);
                    this.setAttackType(0);
                    this.webCooldown = 200;
                }
            }

            if (this.getAttackType() == WEB_NET_ATTACK) {
                if (this.getAttackTicks() == 15 && this.getTarget() != null) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_WEB.get(), 2.0F, 1.0F);
                    this.waitingForWeb = true;
                    GSWebNet webNet = GSEntityTypes.WEB_NET.get().create(this.level);
                    if (webNet != null){
                        webNet.setPos(this.getX(), this.getY() + 1.5, this.getZ());
                        webNet.setYHeadRot(this.getYHeadRot());
                        webNet.setYRot(this.getYHeadRot());
                        double deltaX = webNet.getX() - this.getTarget().getX();
                        double deltaY = webNet.getY() - (this.getTarget().getY() + 1.0);
                        double deltaZ = webNet.getZ() - this.getTarget().getZ();
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        float power = 10.0F;
                        double motionX = -(deltaX / distance * (double)power * 0.2);
                        double motionY = -(deltaY / distance * (double)power * 0.2);
                        double motionZ = -(deltaZ / distance * (double)power * 0.2);
                        webNet.setAcceleration(motionX, motionY, motionZ);
                        if (!this.level.isClientSide) {
                            float radius2 = -2.0F;
                            double randomX = this.getX() + 0.8D * Math.sin((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius2 * Math.sin((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                            double randomY = this.getZ() + 0.8D * Math.cos((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius2 * Math.cos((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                            webNet.setAttachPoint(randomX, this.getY() + 1.0, randomY);
                        }

                        webNet.setShooter(this);
                        this.level.addFreshEntity(webNet);
                    }
                }

                if (this.followupTicks == 1) {
                    this.setAnimationState(17);
                }

                if (this.followupTicks > 0) {
                    ++this.followupTicks;
                }

                if (this.followupTicks == 7) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.5F);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_LEAP.get(), 2.0F, this.getVoicePitch());
                    CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.2F, 0, 30);
                    if (!this.level.isClientSide) {
                        float angle = 2.0F;
                        double extraX = this.getX() + 0.8D * Math.sin((double)(-this.getYRot()) * Math.PI / 180.0) + (double)angle * Math.sin((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                        double extraZ = this.getZ() + 0.8D * Math.cos((double)(-this.getYRot()) * Math.PI / 180.0) + (double)angle * Math.cos((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(extraX - 5.0, this.getY(), extraZ - 5.0, extraX + 5.0, this.getY() + 5.0, extraZ + 5.0));

                        for (LivingEntity entity2 : list) {
                            if (!MobUtil.areAllies(entity2, this) && entity2.isAlive()) {
                                double deltaX = this.getX() - entity2.getX();
                                double deltaY = this.getY() - entity2.getY();
                                double deltaZ = this.getZ() - entity2.getZ();
                                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity2)) {
                                    entity2.hurtMarked = true;
                                    entity2.hurt(damageSource, 20.0F);
                                    entity2.setDeltaMovement(entity2.getDeltaMovement().add(-deltaX / distance * 5.0 - entity2.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), -deltaY / distance * 2.0 - entity2.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), -deltaZ / distance * 5.0 - entity2.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                                    if (entity2.isBlocking()) {
                                        EntityUtil.disableShield(entity2, 400);
                                        if (entity2 instanceof AbstractHauntedArmor armor){
                                            armor.disableShield(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (this.getAttackType() == LEAP_ATTACK) {
                double x = 0.0;
                double y = 0.0;
                double z = 0.0;
                if (this.getTarget() != null) {
                    double deltaX = this.getX() - this.getTarget().getX();
                    double deltaY = this.getY() - (this.getTarget().getY() + 1.5);
                    double deltaZ = this.getZ() - this.getTarget().getZ();
                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                    float power = 10.0F;
                    x = -(deltaX / distance * (double)power * 0.2);
                    y = -(deltaY / distance * (double)power * 0.06);
                    z = -(deltaZ / distance * (double)power * 0.2);
                }

                if (this.getAttackTicks() == 19) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                }

                if (this.getAttackTicks() == 27) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                }

                if (this.getAttackTicks() == 37) {
                    this.shouldHurtOnTouch = true;
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_LEAP.get(), 2.0F, this.getVoicePitch());
                    this.setDeltaMovement(x, y > 0.0 ? y + 0.2 : 0.2, z);
                }

                if (this.shouldHurtOnTouch) {
                    for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(15.0D),
                            livingEntity -> !MobUtil.areAllies(livingEntity, this) && livingEntity.isAlive())){
                        double deltaX = this.getX() - entity.getX();
                        double deltaY = this.getY() - entity.getY();
                        double deltaZ = this.getZ() - entity.getZ();
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        if (this.distanceToSqr(entity) < 9.0) {
                            if (entity.invulnerableTime <= 0) {
                                this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                                entity.hurt(damageSource, 8.0F);
                                entity.hurtMarked = true;
                                entity.setDeltaMovement(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 0.5, -deltaZ / distance * 2.0);
                            }

                            if (entity.isBlocking()) {
                                EntityUtil.disableShield(entity, 100);
                                if (entity instanceof AbstractHauntedArmor armor){
                                    armor.disableShield(true);
                                }
                            }
                        }
                    }
                }
            }

            if ((!this.isCrazy() && this.getFirstPassenger() instanceof LivingEntity livingEntity && MobUtil.healthIsHalved(livingEntity) || this.isCrazy() && this.halfHealth()) && (this.doesAttackMeetNormalRequirements() && this.getRandom().nextInt(16) == 0 && this.jumpCooldown < 1 || this.getAttackType() == JUMP_ATTACK)) {
                if (this.getAttackTicks() == 0) {
                    this.setAttackType(JUMP_ATTACK);
                    this.setAnimationState(14);
                }

                this.getNavigation().stop();
                this.getMoveControl().strafe(0.0F, 0.0F);
                if (this.getTarget() != null) {
                    this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                }

                this.getMoveControl().strafe(0.0F, 0.0F);
                this.navigation.stop();
                if (this.getAttackTicks() == 7 && this.getTarget() != null) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 1.0F);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_LEAP.get(), 2.0F, this.getVoicePitch());
                    if (this.isCrazy()) {
                        this.setDeltaMovement((this.getTarget().getX() - this.getX()) * 0.15, 1.5, (this.getTarget().getZ() - this.getZ()) * 0.15);
                    } else if (this.distanceToSqr(this.getTarget()) <= 100.0) {
                        this.setDeltaMovement((-4.0 + this.random.nextDouble() * 3.0) * 1.1, 1.5, (-4.0 + this.random.nextDouble() * 3.0) * 1.1);
                    } else {
                        double deltaX = this.getX() - this.getTarget().getX();
                        double deltaY = this.getY() - this.getTarget().getY();
                        double deltaZ = this.getZ() - this.getTarget().getZ();
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        float radius = 18.0F;
                        double motionX = -(deltaX / distance * (double)radius * 0.2);
                        double motionZ = -(deltaZ / distance * (double)radius * 0.2);
                        this.setDeltaMovement(motionX + (-2.0 + this.random.nextDouble() * 5.0) * 1.1, 1.5, motionZ + (-2.0 + this.random.nextDouble() * 5.0) * 1.1);
                    }
                }

                if (this.getAttackTicks() >= 7) {
                    for (LivingEntity hit : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(15.0D),
                            livingEntity -> !MobUtil.areAllies(livingEntity, this) && livingEntity.isAlive())){
                        double deltaX = this.getX() - hit.getX();
                        double deltaY = this.getY() - hit.getY();
                        double deltaZ = this.getZ() - hit.getZ();
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        if (this.distanceToSqr(hit) < 9.0 && hit.invulnerableTime <= 0) {
                            this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                            hit.hurt(damageSource, 8.0F);
                            hit.hurtMarked = true;
                            hit.setDeltaMovement(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 1.2, -deltaZ / distance * 2.0);
                            hit.lerpMotion(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 1.2, -deltaZ / distance * 2.0);
                        }
                    }
                }

                if (this.getAttackTicks() > 107 || this.getAttackTicks() > 8 && this.onGround()) {
                    for (LivingEntity hit : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(15.0D),
                            livingEntity -> !MobUtil.areAllies(livingEntity, this) && livingEntity.isAlive())){
                        double deltaX = this.getX() - hit.getX();
                        double deltaY = this.getY() - hit.getY();
                        double deltaZ = this.getZ() - hit.getZ();
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        if (this.distanceToSqr(hit) < 36.0 && hit.invulnerableTime <= 0) {
                            this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                            hit.hurt(damageSource, 8.0F);
                            hit.hurtMarked = true;
                            hit.setDeltaMovement(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 1.2, -deltaZ / distance * 2.0);
                            hit.lerpMotion(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 1.2, -deltaZ / distance * 2.0);
                        }
                    }

                    CameraShakeEntity.cameraShake(this.level(), this.position(), 50.0F, 0.05F, 0, 30);
                    EntityUtil.makeCircleParticles(this.level(), this, ParticleTypes.LARGE_SMOKE, 100, 1.0, 1.0F);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.5F);
                    this.setAnimationState(15);
                    this.setAttackTicks(0);
                    if (this.isCrazy()) {
                        this.loseStunHealth(5, false);
                    }

                    this.attackCooldown = 10;
                    this.jumpCooldown = 80;
                    this.setAttackType(0);
                }
            }

            if (!this.isCrazy() && (this.doesAttackMeetNormalRequirements() && this.getTarget() != null && this.distanceToSqr(this.getTarget()) < 49.0 || this.getAttackType() == ATTACK_ATTACK)) {
                if (this.getAttackTicks() == 0) {
                    this.setAttackType(ATTACK_ATTACK);
                    this.setAnimationState(12);
                }

                this.getNavigation().stop();
                this.getMoveControl().strafe(0.0F, 0.0F);
                if (this.getTarget() != null) {
                    this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                }
                if (this.getAttackTicks() == 4) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 1.0F);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_LEAP.get(), 1.0F, this.getVoicePitch());
                    if (!this.level.isClientSide) {
                        float radius = 2.0F;
                        double deltaX = this.getX() + 0.8D * Math.sin((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius * Math.sin((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                        double deltaZ = this.getZ() + 0.8D * Math.cos((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius * Math.cos((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(deltaX - 2.0, this.getY(), deltaZ - 2.0, deltaX + 2.0, this.getY() + 2.0, deltaZ + 2.0));

                        for (LivingEntity entity : list){
                            if (!MobUtil.areAllies(entity, this) && entity.isAlive()) {
                                double motionX = this.getX() - entity.getX();
                                double motionY = this.getY() - entity.getY();
                                double motionZ = this.getZ() - entity.getZ();
                                double distance = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
                                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity)) {
                                    entity.hurtMarked = true;
                                    entity.hurt(damageSource, 6.0F);
                                    entity.setDeltaMovement(entity.getDeltaMovement().add(-motionX / distance * 5.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), -motionY / distance * 0.3 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), -motionZ / distance * 5.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                                    if (entity.isBlocking()) {
                                        EntityUtil.disableShield(entity, 100);
                                        if (entity instanceof AbstractHauntedArmor armor){
                                            armor.disableShield(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (this.getAttackTicks() > 10) {
                    this.setAnimationState(0);
                    this.setAttackTicks(0);
                    this.attackCooldown = 25;
                    this.setAttackType(0);
                }
            }

            if (!this.isPlayingIntro && !this.isPlayingPhase && !this.isCrazy() && this.isStunned()) {
                if (this.stunTick == 1) {
                    this.setAnimationState(13);
                }

                this.getNavigation().stop();
                this.getMoveControl().strafe(0.0F, 0.0F);
                if (this.getTarget() != null && this.isCrazy()) {
                    this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                }

                this.getMoveControl().strafe(0.0F, 0.0F);
                this.navigation.stop();
                if (this.stunTick > 120) {
                    if (!this.isPlayingPhase) {
                        this.setAnimationState(0);
                    }

                    this.setAttackTicks(0);
                    this.setAttackType(0);
                    this.setStunHealth(this.getMaxStunHealth());
                    this.stunTick = 0;
                    this.setStunned(false);
                }
            }

            if (this.getAttackType() == this.BURROW_ATTACK) {
                if (this.getAttackTicks() > 6 && this.getAttackTicks() <= 30) {
                    this.playSound(SoundEvents.GRAVEL_BREAK, 2.0F, 0.7F);
                    this.makeBlockParticles(this.getBlockStateOn());
                    this.setBurrowing(true);
                }

                if (this.getAttackTicks() >= 30) {
                    this.clearFire();
                    if (this.getAttackTicks() < (this.halfHealth() ? 40 : 100)) {
                        this.playSound(SoundEvents.STONE_BREAK, 2.0F, 0.5F);
                    }

                    Entity target = this.getTarget();
                    if (this.getAttackTicks() < (this.halfHealth() ? 40 : 100) && target != null) {
                        this.setInvisible(true);
                        double targetX = target.getX();
                        double targetZ = target.getZ();
                        double d0 = Math.min(target.getY(), this.getY());
                        double d1 = Math.max(target.getY(), this.getY());
                        this.setPos(this.getBurrowPosition(targetX, targetZ, d0, d1));
                    }

                    this.setDeltaMovement(0.0, 0.0, 0.0);

                    if (this.getAttackTicks() == (this.halfHealth() ? 49 : 119)) {
                        this.setAnimationState(7);
                    }

                    if (this.getAttackTicks() == (this.halfHealth() ? 50 : 120)) {
                        this.makeBlockParticles(this.getBlockStateOn());
                        this.setInvisible(false);
                        CameraShakeEntity.cameraShake(this.level(), this.position(), 50.0F, 0.05F, 0, 30);
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.6F);
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.2F);
                        for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(15.0D))) {
                            if (!MobUtil.areAllies(this, entity) && entity instanceof LivingEntity livingEntity && entity.isAlive() && entity != this) {
                                double deltaX = this.getX() - entity.getX();
                                double deltaY = this.getY() - entity.getY();
                                double deltaZ = this.getZ() - entity.getZ();
                                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                                if (this.distanceToSqr(entity) < 9.0 && entity.invulnerableTime <= 0) {
                                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                                    entity.hurt(damageSource, 8.0F);
                                    entity.hurtMarked = true;
                                    entity.setDeltaMovement(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 0.8, -deltaZ / distance * 2.0);
                                    entity.lerpMotion(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 0.8, -deltaZ / distance * 2.0);
                                    if (livingEntity.isBlocking()) {
                                        EntityUtil.disableShield(livingEntity, 100);
                                        if (livingEntity instanceof AbstractHauntedArmor armor){
                                            armor.disableShield(true);
                                        }
                                    }
                                }
                            }
                        }
                        this.setDeltaMovement(0.0, 0.0, 0.0);
                    }
                }
            }

            if (this.isCrazy()) {
                if (this.getAttackType() == CHARGE_ATTACK) {
                    if (this.getAttackTicks() == 6) {
                        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                    }

                    if (this.getAttackTicks() == 26 && this.getTarget() != null) {
                        if (this.halfHealth()) {
                            DarkEffectCloud cloud = new DarkEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
                            cloud.setRadius(3.0F);
                            cloud.setRadiusOnUse(-0.5F);
                            cloud.setWaitTime(10);
                            cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());
                            cloud.setPotion(PotionRegisterer.MUTATION.get());
                            cloud.setOwner(this);
                            this.level.addFreshEntity(cloud);
                        }

                        this.chargeX = 0.0;
                        this.chargeZ = 0.0;
                        double deltaX = this.getX() - this.getTarget().getX();
                        double deltaY = this.getY() - this.getTarget().getY();
                        double deltaZ = this.getZ() - this.getTarget().getZ();
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_CHARGE.get(), 2.0F, this.getVoicePitch());
                        float angle = 4.5F;
                        double extraX = -(deltaX / distance * (double)angle * 0.2);
                        double extraZ = -(deltaZ / distance * (double)angle * 0.2);
                        this.chargeX = extraX;
                        this.chargeZ = extraZ;
                    }

                    if (this.getAttackTicks() > 26) {
                        if (this.halfHealth() && (this.getAttackTicks() - 26) % 5 == 0) {
                            DarkEffectCloud cloud = new DarkEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
                            cloud.setRadius(3.0F);
                            cloud.setRadiusOnUse(-0.5F);
                            cloud.setWaitTime(10);
                            cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());
                            cloud.setPotion(PotionRegisterer.MUTATION.get());
                            cloud.setOwner(this);
                            this.level.addFreshEntity(cloud);
                        }

                        this.setDeltaMovement(this.chargeX, this.getDeltaMovement().y, this.chargeZ);
                        for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(15.0D),
                                livingEntity -> !MobUtil.areAllies(livingEntity, this) && livingEntity.isAlive())){
                            double deltaX = this.getX() - entity.getX();
                            double deltaY = this.getY() - entity.getY();
                            double deltaZ = this.getZ() - entity.getZ();
                            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                            if (this.distanceToSqr(entity) < 9.0 && entity.invulnerableTime <= 0) {
                                this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                                entity.hurt(damageSource, 8.0F);
                                entity.hurtMarked = true;
                                entity.setDeltaMovement(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 1.2, -deltaZ / distance * 2.0);
                                entity.lerpMotion(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 1.2, -deltaZ / distance * 2.0);
                            }
                        }
                    }
                }
            }

            if (!this.isPlayingIntro && !this.isPlayingPhase && !this.isCrazy() && (this.doesAttackMeetNormalRequirements() && this.getTarget() != null && this.distanceToSqr(this.getTarget()) > 1225.0 && this.chargeCooldown < 1 || this.getAttackType() == CHARGE_ATTACK)) {
                if (this.getAttackTicks() == 0) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 0.9F);
                    this.setAnimationState(8);
                    this.setAttackType(CHARGE_ATTACK);
                }

                this.getNavigation().stop();
                this.getMoveControl().strafe(0.0F, 0.0F);
                if (this.getTarget() != null) {
                    this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                }
                if (this.getAttackTicks() == 6) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                }

                if (!this.level.isClientSide && this.getAttackTicks() == 26 && this.getTarget() != null) {
                    this.chargeX = 0.0;
                    this.chargeZ = 0.0;
                    double deltaX = this.getX() - this.getTarget().getX();
                    double deltaY = this.getY() - this.getTarget().getY();
                    double deltaZ = this.getZ() - this.getTarget().getZ();
                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_CHARGE.get(), 2.0F, this.getVoicePitch());
                    float angle = 4.5F;
                    double extraX = -(deltaX / distance * (double)angle * 0.2);
                    double extraZ = -(deltaY / distance * (double)angle * 0.2);
                    this.chargeX = extraX;
                    this.chargeZ = extraZ;
                }

                if (!this.level.isClientSide && this.getAttackTicks() > 26) {
                    this.setDeltaMovement(this.chargeX, this.getDeltaMovement().y, this.chargeZ);

                    for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(15.0D), livingEntity -> !MobUtil.areAllies(livingEntity, this) && livingEntity.isAlive())) {
                        double deltaX = this.getX() - entity.getX();
                        double deltaY = this.getY() - entity.getY();
                        double deltaZ = this.getZ() - entity.getZ();
                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        if (this.distanceToSqr(entity) < 9.0 && entity.invulnerableTime <= 0) {
                            this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                            entity.hurt(damageSource, 8.0F);
                            entity.hurtMarked = true;
                            entity.setDeltaMovement(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 1.2, -deltaZ / distance * 2.0);
                            entity.lerpMotion(-deltaX / distance * 2.0, -deltaY / distance * 2.0 + 1.2, -deltaZ / distance * 2.0);
                        }
                    }
                }

                if (this.getAttackTicks() > 96) {
                    this.setAnimationState(0);
                    this.setAttackTicks(0);
                    this.setAttackType(0);
                    this.chargeX = 0.0;
                    this.chargeZ = 0.0;
                    this.chargeCooldown = 40;
                    this.attackCooldown = 20;
                }
            }

            if (this.getAttackType() == COUGH_ATTACK && this.getAttackTicks() == 10 && this.getTarget() != null) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_COUGH.get(), 2.0F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 50.0F, 0.06F, 0, 10);
                if (this.random.nextBoolean()) {
                    for(int i = 0; i < 4; ++i) {
                        if (!this.level.isClientSide) {
                            GSTot treat = GSEntityTypes.TRICK_OR_TREAT.get().create(this.level);
                            if (treat != null) {
                                treat.setPos(this.getX(), this.getY(), this.getZ());
                                double deltaX = this.getX() - this.getTarget().getX();
                                double deltaY = this.getY() - this.getTarget().getY();
                                double deltaZ = this.getZ() - this.getTarget().getZ();
                                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                                float power = 4.5F;
                                double motionX = -(deltaX / distance * (double) power * 0.2);
                                double motionY = -(deltaY / distance * (double) power * 0.2);
                                double motionZ = -(deltaZ / distance * (double) power * 0.2);
                                treat.setDeltaMovement(motionX, motionY, motionZ);
                                treat.circleTime = i * 10;
                                treat.bounceTime = i;
                                treat.setTreat(this.random.nextInt(6) + 1);
                                if (this.getTeam() != null) {
                                    this.level.getScoreboard().addPlayerToTeam(treat.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                                }

                                treat.setTrueOwner(this);
                                treat.setGoopy();
                                this.level.addFreshEntity(treat);
                            }
                        }
                    }
                } else {
                    for(int i = 0; i < 3; ++i) {
                        if (!this.level.isClientSide) {
                            if (this.halfHealth()) {
                                GSFunnybone funnybone = GSEntityTypes.FUNNYBONE.get().create(this.level());
                                if (funnybone != null) {
                                    funnybone.setPos(this.getX(), this.getY(), this.getZ());
                                    double deltaX = this.getX() - this.getTarget().getX();
                                    double deltaY = this.getY() - this.getTarget().getY();
                                    double deltaZ = this.getZ() - this.getTarget().getZ();
                                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                                    float power = 4.5F;
                                    double motionX = -(deltaX / distance * (double) power * 0.2);
                                    double motionY = -(deltaY / distance * (double) power * 0.2);
                                    double motionZ = -(deltaZ / distance * (double) power * 0.2);
                                    if (this.getTeam() != null) {
                                        this.level.getScoreboard().addPlayerToTeam(funnybone.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                                    }

                                    funnybone.setDeltaMovement(motionX, motionY, motionZ);
                                    funnybone.setTrueOwner(this);
                                    funnybone.setTarget(this.getTarget());
                                    funnybone.setGoopy(true);
                                    this.level.addFreshEntity(funnybone);
                                }
                            } else {
                                GSPumpkinBomb bomb = GSEntityTypes.PUMPKIN_BOMB.get().create(this.level);
                                if (bomb != null) {
                                    bomb.setPos(this.getX(), this.getY(), this.getZ());
                                    double deltaX = this.getX() - this.getTarget().getX();
                                    double deltaY = this.getY() - this.getTarget().getY();
                                    double deltaZ = this.getZ() - this.getTarget().getZ();
                                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                                    float power = 4.5F;
                                    double motionX = -(deltaX / distance * (double) power * 0.2);
                                    double motionY = -(deltaY / distance * (double) power * 0.2);
                                    double motionZ = -(deltaZ / distance * (double) power * 0.2);
                                    if (this.getTeam() != null) {
                                        this.level.getScoreboard().addPlayerToTeam(bomb.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                                    }

                                    bomb.setDeltaMovement(motionX, motionY, motionZ);
                                    bomb.setTrueOwner(this);
                                    bomb.setTarget(this.getTarget());
                                    bomb.setGoopy();
                                    this.level.addFreshEntity(bomb);
                                }
                            }
                        }
                    }
                }
            }

            if (this.canUseBreath() || this.getAttackType() == BREATH_ATTACK) {
                this.getNavigation().stop();
                this.getMoveControl().strafe(0.0F, 0.0F);
                if (this.getTarget() != null && !this.isGrabbing()) {
                    this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                }
                if (this.getAttackTicks() == 0) {
                    this.setAnimationState(18);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 0.9F);
                    this.setAttackType(BREATH_ATTACK);
                }

                if (this.getAttackTicks() == 30) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_BLOCK.get(), 2.0F, 1.0F);
                    if (!this.level().isClientSide) {
                        float radius2 = 2.0F;
                        double targetX = this.getX() + 0.800000011920929 * Math.sin((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius2 * Math.sin((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                        double deltaX = this.getZ() + 0.800000011920929 * Math.cos((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius2 * Math.cos((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(targetX - 2.0, this.getY(), deltaX - 2.0, targetX + 2.0, this.getY() + 2.0, deltaX + 2.0), (predicate) -> {
                            return !MobUtil.areAllies(predicate, this) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(predicate);
                        });

                        for (LivingEntity entity : list){
                            entity.hurt(damageSource, 2.0F);
                            if (!this.isGrabbing() && entity.isAlive() && entity.startRiding(this, true)) {
                                this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_LEAP.get(), 2.0F, this.getVoicePitch());
                                this.setGrabbing(true);
                            }
                        }
                    }
                }

                if (this.getAttackTicks() == 40 && this.isGrabbing()) {
                    this.setAnimationState(19);
                }

                if (this.getAttackTicks() == 53) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SCREECH.get(), 2.0F, 0.75F);
                    CameraShakeEntity.cameraShake(this.level(), this.position(), 50.0F, 0.05F, 48, 20);
                }

                if (this.getAttackTicks() >= 53 && this.getAttackTicks() <= 109) {
                    this.makeBreath();
                }

                if (this.getAttackTicks() == 109 && !this.getPassengers().isEmpty()) {
                    this.getPassengers().forEach(Entity::stopRiding);
                }

                if (this.isGrabbing() ? this.getAttackTicks() > 125 : this.getAttackTicks() > 45) {
                    this.setAnimationState(0);
                    this.setAttackTicks(0);
                    this.setAttackType(0);
                    this.loseStunHealth(this.isGrabbing() ? 10 : 5, false);
                    this.setGrabbing(false);
                    this.breathCooldown = 100;
                    this.attackCooldown = 20;
                }
            }
        }
    }

    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.isCrazy() || this.phaseTicks >= 1 && this.phaseTicks <= 250) {
            super.positionRider(passenger, moveFunction);
        } else {
            float radius = 3.0F;
            float angle = 0.017453292F * this.yBodyRot;
            double x = this.getX() + (double)(radius * Mth.sin((float)(Math.PI + (double)angle)));
            double z = this.getZ() + (double)(radius * Mth.cos(angle));
            moveFunction.accept(passenger, x, this.getY() + 0.75, z);
        }

    }

    public boolean shouldRiderSit() {
        return !this.isCrazy();
    }

    private Vec3 getBurrowPosition(double p_32673_, double p_32674_, double p_32675_, double p_32676_) {
        BlockPos blockpos = BlockPos.containing(p_32673_, p_32676_, p_32674_);
        boolean flag = false;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(p_32675_) - 1);

        return flag ? new Vec3(p_32673_, (double)blockpos.getY(), p_32674_) : this.position();
    }

    public boolean isNotAttacking(){
        return this.getAttackType() <= 0;
    }

    public boolean isAttacking(){
        return this.getAttackType() > 0;
    }

    public boolean notClientAttacking(){
        return !this.clientAttacking;
    }

    public void die(DamageSource pSource) {
        List<GSTot> treats = this.level.getEntitiesOfClass(GSTot.class, this.getBoundingBox().inflate(40.0));
        if (!treats.isEmpty()) {
            for (GSTot treat : treats) {
                if (treat.getTrueOwner() == this) {
                    treat.kill();
                }
            }
        }

        this.stopAttacking();
        this.setBurrowing(false);
        this.setGrabbing(false);
        this.deathBlow = pSource;
        this.clearFire();
        if (!this.level.isClientSide) {
            this.setAttackTicks(0);
            this.setAnimationState(21);
            this.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
        }

        if (this.lastHurtByPlayerTime > 0) {
            this.lastHurtByPlayerTime = 10000;
        }

    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 100) {
            VillagerSoulEntity soul = ModEntityTypes.VillagerSoul.get().create(this.level);
            if (soul != null) {
                soul.setPos(this.getX(), this.getY() + 1.0D, this.getZ());
                soul.setDeltaMovement(0.0D, 0.3D, 0.0D);
                if (this.getLastHurtByMob() != null) {
                    soul.setTarget(this.getLastHurtByMob());
                } else if (this.getTrueOwner() != null){
                    soul.setTarget(this.getTrueOwner());
                }
                this.level.addFreshEntity(soul);
            }
        }

        if ((this.deathTime == 200 || (this.deathTime == 40 && (this.deathBlow.is(DamageTypes.FELL_OUT_OF_WORLD)) || this.deathBlow.is(DamageTypes.GENERIC_KILL))) && !this.level.isClientSide()) {
            super.die(this.deathBlow);
            if (this.isCrazy()){
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    this.spawnAtLocation(ItemRegisterer.BAG_OF_HORRORS.get().getDefaultInstance());
                }
            }
            this.level.broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }

    }

    public boolean fireImmune() {
        return super.fireImmune() && !this.isDeadOrDying();
    }

    private void circleTarget(Entity target, float radius, float speed, boolean direction, int circleFrame, float offset, float moveSpeedMultiplier) {
        if (!this.isStunned()) {
            int directionInt = 1;
            double t = (double)(directionInt * circleFrame) * 0.5 * (double)speed / (double)radius + (double)offset;
            Vec3 movePos = target.position().add((double)radius * Math.cos(t), 0.0, (double)radius * Math.sin(t));
            this.getNavigation().moveTo(movePos.x(), movePos.y(), movePos.z(), (double)(speed * moveSpeedMultiplier));
        }

    }

    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_CRAWL.get(), 0.5F, 1.0F);
    }

    public void makeBlockParticles(BlockState blockstate) {
        if (this.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 12; ++i) {
                double d0 = -0.5D + this.random.nextGaussian();
                double d1 = -0.5D + this.random.nextGaussian();
                double d2 = -0.5D + this.random.nextGaussian();
                ParticleOptions block = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
                serverLevel.sendParticles(block, this.getRandomX(0.5D), this.getY(), this.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public void makeSweatParticles(int quantity) {
        if (this.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < quantity; ++i) {
                double d0 = -0.5D + this.random.nextGaussian();
                double d1 = -0.5D + this.random.nextGaussian();
                double d2 = -0.5D + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.SPLASH, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public void makeSpitParticles(Entity caught) {
        if (this.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 12; ++i) {
                double d0 = -0.5D + this.random.nextGaussian();
                double d1 = -0.5D + this.random.nextGaussian();
                double d2 = -0.5D + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.SPLASH, caught.getRandomX(0.5D), caught.getRandomY(), caught.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public void makePassiveMutationParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleRegisterer.MUTATION_DRIP_PARTICLES.get(), this.getRandomX(0.6), this.getRandomY() + (this.isAlive() ? 0.75 : 0.0), this.getRandomZ(0.6), 1, 0.0F, 0.0F, 0.0F, 0.0F);
        }
    }

    public void makeBreath() {
        if (this.level instanceof ServerLevel serverLevel) {
            double coneAngleDegrees = 50.0;
            double coneAngleRadians = Math.toRadians(coneAngleDegrees);
            double maxDistance = 8.0;
            List<LivingEntity> entitiesInRange = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(maxDistance), (predicate) -> {
                return predicate != this && !MobUtil.areAllies(predicate, this) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(predicate);
            });
            for (LivingEntity livingEntity : entitiesInRange){
                Vec3 toEntity = livingEntity.position().subtract(this.position());
                double distance = toEntity.length();
                if (!(distance > maxDistance)) {
                    Vec3 toEntityNormalized = toEntity.normalize();
                    double dotProduct = this.getLookAngle().dot(toEntityNormalized);
                    double angle = Math.acos(dotProduct);
                    if (angle <= coneAngleRadians / 2.0) {
                        livingEntity.addEffect(new MobEffectInstance(EffectRegisterer.MUTATION.get(), 600));
                        livingEntity.hurt(ModDamageSource.indirectEntityDamageSource(this.level, DamageTypesRegisterer.MUTATION, livingEntity, this), 4.0F);
                    }
                }
            }

            float radius2 = 1.0F;

            for(int i = 0; i < 7; ++i) {
                double x = this.getX() + 0.8D * Math.sin((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius2 * Math.sin((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                double z = this.getZ() + 0.8D * Math.cos((double)(-this.getYRot()) * Math.PI / 180.0) + (double)radius2 * Math.cos((double)(-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double)(-this.getXRot()) * Math.PI / 180.0);
                double y = this.getY() + 1.5;
                double coneAngle = (double)this.random.nextInt(40, 60);
                double randomYawOffset = (this.random.nextDouble() - 0.5) * coneAngle;
                double randomPitchOffset = (this.random.nextDouble() - 0.5) * coneAngle / 2.0;
                double yaw = Math.toRadians((double)(-this.getYRot()) + randomYawOffset);
                double pitch = Math.toRadians((double)(-this.getXRot()) + randomPitchOffset);
                double velocityX = -Math.cos(pitch) * Math.sin(yaw);
                double velocityY = -Math.sin(pitch);
                double velocityZ = -Math.cos(pitch) * Math.cos(yaw);
                double speed = this.random.nextDouble() + this.random.nextDouble();
                if (speed < 0.4) {
                    speed += this.random.nextDouble();
                }

                Vec3 velocity = (new Vec3(-velocityX, -velocityY, -velocityZ)).scale(speed);
                if (this.random.nextInt(10) == 0) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 0, velocity.x, velocity.y, velocity.z, 0.5F);
                } else {
                    serverLevel.sendParticles(this.random.nextBoolean() ? ParticleRegisterer.MUTATION_PARTICLES.get() : ParticleRegisterer.MUTATION_PARTICLES2.get(), x, y, z, 0, velocity.x, velocity.y, velocity.z, 0.5F);
                }
            }
        }
    }

    public void stopAttackersFromAttacking() {
        List<Mob> list = this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(100.0F));
        if (this.getFirstPassenger() instanceof LivingEntity livingEntity
                && livingEntity.isAlive()
                && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)
                && !this.isCrazy()) {
            for (Mob attacker : list) {
                if (attacker.getLastHurtByMob() == this) {
                    attacker.setLastHurtByMob(livingEntity);
                }

                if (attacker.getTarget() == this) {
                    attacker.setTarget(livingEntity);
                }

                if (attacker instanceof Warden warden) {
                    if (warden.getTarget() == this) {
                        warden.increaseAngerAt(livingEntity, AngerLevel.ANGRY.getMinimumAnger() + 100, false);
                        warden.setAttackTarget(livingEntity);
                    }
                } else {
                    if (attacker.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent() && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() == this) {
                        attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, livingEntity.getUUID(), 600L);
                        attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, livingEntity, 600L);
                    }
                }
            }
        }

    }

    public boolean startRiding(Entity p_20330_) {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.getTrueOwner() != null && pSource.getEntity() == this.getTrueOwner() && MobsConfig.ServantsMasterImmune.get()){
            return false;
        } else if (this.hasPassenger() && pSource.getEntity() == this.getFirstPassenger() && !this.isCrazy()) {
            return false;
        } else if ((this.isBurrowing() || this.isGrabbing()) && !pSource.is(DamageTypes.FELL_OUT_OF_WORLD) && !pSource.is(DamageTypes.GENERIC_KILL)) {
            return false;
        } else {
            if (pSource.is(DamageTypes.STARVE)){
                return super.hurt(pSource, pAmount);
            }
            boolean crazy = this.isCrazy() || this.getPassengers().isEmpty();
            if (this.isAlive() && !pSource.is(DamageTypes.FELL_OUT_OF_WORLD) && !pSource.is(DamageTypes.GENERIC_KILL) && (!crazy || EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(pSource.getEntity()))) {
                boolean source;
                if (!crazy || this.isPlayingPhase) {
                    source = !pSource.is(DamageTypeTags.BYPASSES_ARMOR);
                    if (!this.isStunned() && source && this.blockTicks < 1 && (this.entityData.get(ANIMATION_STATE) == 0 || this.entityData.get(ANIMATION_STATE) == 3 || this.entityData.get(ANIMATION_STATE) == 15)) {
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_BLOCK.get(), 2.0F, 1.0F);
                        this.setAnimationState(0);
                        this.setAnimationState(3);
                        this.blockTicks = 10;
                        this.loseStunHealth((int)pAmount, true);
                    }

                    if (pSource.getEntity() instanceof LivingEntity livingEntity && this.getLastHurtByMob() == null) {
                        this.setLastHurtByMob(livingEntity);
                    }

                    return false;
                }

                if (crazy && (this.stunTick < 10 || this.stunTick >= 105)) {
                    source = !pSource.is(DamageTypeTags.BYPASSES_ARMOR);
                    if (!this.isStunned() && source && this.getAttackType() == 0) {
                        if (this.blockTicks < 1 && (this.entityData.get(ANIMATION_STATE) == 0 || this.entityData.get(ANIMATION_STATE) == 3 || this.entityData.get(ANIMATION_STATE) == 15)) {
                            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_BLOCK.get(), 2.0F, 1.0F);
                            this.setAnimationState(0);
                            this.setAnimationState(3);
                            this.blockTicks = 10;
                            this.loseStunHealth((int)pAmount, true);
                        }

                        if (pSource.getEntity() instanceof LivingEntity livingEntity && this.getLastHurtByMob() == null) {
                            this.setLastHurtByMob(livingEntity);
                        }

                        return false;
                    }

                    pAmount /= 3.5F;
                }
            }

            return !pSource.is(DamageTypes.IN_WALL) && super.hurt(pSource, pAmount);
        }
    }

    public SoundEvent getCelebrateSound() {
        return this.entityData.get(ANIMATION_STATE) == 6 ? null : IllageAndSpillageSoundEvents.ENTITY_RAGNO_AMBIENT.get();
    }

    protected SoundEvent getAmbientSound() {
        return this.entityData.get(ANIMATION_STATE) == 6 ? null : IllageAndSpillageSoundEvents.ENTITY_RAGNO_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return IllageAndSpillageSoundEvents.ENTITY_RAGNO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_RAGNO_DEATH.get();
    }

    public boolean isPersistenceRequired() {
        return true;
    }

    public double getPassengersRidingOffset() {
        if (this.getAttackType() == JUMP_ATTACK) {
            return this.getAttackTicks() >= 7 ? 3.15 : 1.9;
        } else {
            return this.stunTick > 6 && this.stunTick < 115 ? 1.7 : 1.9;
        }
    }

    public AnimationState getAnimationState(String input) {
        if (Objects.equals(input, "intro1")) {
            return this.intro1AnimationState;
        } else if (Objects.equals(input, "intro2")) {
            return this.intro2AnimationState;
        } else if (Objects.equals(input, "phase")) {
            return this.phaseAnimationState;
        } else if (Objects.equals(input, "block")) {
            return this.blockAnimationState;
        } else if (Objects.equals(input, "web")) {
            return this.webAnimationState;
        } else if (Objects.equals(input, "webNet")) {
            return this.webNetAnimationState;
        } else if (Objects.equals(input, "pullIn")) {
            return this.pullInAnimationState;
        } else if (Objects.equals(input, "netSlam")) {
            return this.netSlamAnimationState;
        } else if (Objects.equals(input, "jump")) {
            return this.jumpAnimationState;
        } else if (Objects.equals(input, "land")) {
            return this.landAnimationState;
        } else if (Objects.equals(input, "leap")) {
            return this.leapAnimationState;
        } else if (Objects.equals(input, "burrow")) {
            return this.burrowAnimationState;
        } else if (Objects.equals(input, "popup")) {
            return this.popupAnimationState;
        } else if (Objects.equals(input, "charge")) {
            return this.chargeAnimationState;
        } else if (Objects.equals(input, "cough")) {
            return this.coughAnimationState;
        } else if (Objects.equals(input, "attack")) {
            return this.attackAnimationState;
        } else if (Objects.equals(input, "stun")) {
            return this.stunAnimationState;
        } else if (Objects.equals(input, "fall")) {
            return this.fallAnimationState;
        } else if (Objects.equals(input, "grab")) {
            return this.grabAnimationState;
        } else if (Objects.equals(input, "breath")) {
            return this.breathAnimationState;
        } else {
            return Objects.equals(input, "death") ? this.deathAnimationState : new AnimationState();
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
                    this.intro1AnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.phaseAnimationState.start(this.tickCount);
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.blockAnimationState.start(this.tickCount);
                    break;
                case 4:
                    this.stopAllAnimationStates();
                    this.webAnimationState.start(this.tickCount);
                    break;
                case 5:
                    this.stopAllAnimationStates();
                    this.leapAnimationState.start(this.tickCount);
                    break;
                case 6:
                    this.stopAllAnimationStates();
                    this.burrowAnimationState.start(this.tickCount);
                    break;
                case 7:
                    this.stopAllAnimationStates();
                    this.popupAnimationState.start(this.tickCount);
                    break;
                case 8:
                    this.stopAllAnimationStates();
                    this.chargeAnimationState.start(this.tickCount);
                    break;
                case 9:
                    this.stopAllAnimationStates();
                    this.coughAnimationState.start(this.tickCount);
                    break;
                case 10:
                    this.stopAllAnimationStates();
                    this.fallAnimationState.start(this.tickCount);
                    break;
                case 11:
                    this.stopAllAnimationStates();
                    this.intro2AnimationState.start(this.tickCount);
                    break;
                case 12:
                    this.stopAllAnimationStates();
                    this.attackAnimationState.start(this.tickCount);
                    break;
                case 13:
                    this.stopAllAnimationStates();
                    this.stunAnimationState.start(this.tickCount);
                    break;
                case 14:
                    this.stopAllAnimationStates();
                    this.jumpAnimationState.start(this.tickCount);
                    break;
                case 15:
                    this.stopAllAnimationStates();
                    this.landAnimationState.start(this.tickCount);
                    break;
                case 16:
                    this.stopAllAnimationStates();
                    this.webNetAnimationState.start(this.tickCount);
                    break;
                case 17:
                    this.stopAllAnimationStates();
                    this.netSlamAnimationState.start(this.tickCount);
                    break;
                case 18:
                    this.stopAllAnimationStates();
                    this.grabAnimationState.start(this.tickCount);
                    break;
                case 19:
                    this.stopAllAnimationStates();
                    this.breathAnimationState.start(this.tickCount);
                    break;
                case 20:
                    this.stopAllAnimationStates();
                    this.pullInAnimationState.start(this.tickCount);
                    break;
                case 21:
                    this.stopAllAnimationStates();
                    this.deathAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    public void stopAllAnimationStates() {
        this.intro1AnimationState.stop();
        this.phaseAnimationState.stop();
        this.blockAnimationState.stop();
        this.attackAnimationState.stop();
        this.webAnimationState.stop();
        this.webNetAnimationState.stop();
        this.pullInAnimationState.stop();
        this.netSlamAnimationState.stop();
        this.jumpAnimationState.stop();
        this.landAnimationState.stop();
        this.leapAnimationState.stop();
        this.burrowAnimationState.stop();
        this.popupAnimationState.stop();
        this.chargeAnimationState.stop();
        this.coughAnimationState.stop();
        this.stunAnimationState.stop();
        this.fallAnimationState.stop();
        this.grabAnimationState.stop();
        this.breathAnimationState.stop();
        this.deathAnimationState.stop();
    }

    public void playIntro() {
        this.setAnimationState(1);
        this.introTicks = 1;
        this.isPlayingIntro = true;
        if (!this.level.isClientSide){
            this.level.broadcastEntityEvent(this, (byte) 9);
        }
    }

    public void setAnimationState(int input) {
        this.entityData.set(ANIMATION_STATE, input);
    }

    public int getStunHealth() {
        return this.entityData.get(STUN_HEALTH);
    }

    public void setStunHealth(int stunHealth) {
        this.entityData.set(STUN_HEALTH, stunHealth);
    }

    public void regenerateStunHealth() {
        if (!this.level.isClientSide && !this.isStunned() && this.getStunHealth() < this.getMaxStunHealth()) {
            int rate;
            if (this.isCrazy()) {
                rate = this.halfHealth() ? 10 : 15;
            } else {
                rate = 40;
            }

            if (this.isBurrowing()) {
                rate *= 2;
            }

            if (this.tickCount % rate == 0) {
                this.setStunHealth(this.getStunHealth() + 1);
            }
        }

    }

    public void loseStunHealth(int amount, boolean canStun) {
        if (!this.level.isClientSide) {
            int trueAmount = Math.min(amount, 30);
            this.setStunHealth(canStun ? this.getStunHealth() - trueAmount : this.getStunHealth() - Math.min(this.getStunHealth() - 1, trueAmount));
            this.makeSweatParticles(trueAmount);
        }

    }

    public int getMaxStunHealth() {
        if (this.isCrazy()) {
            return this.halfHealth() ? 70 : 90;
        } else {
            return 50;
        }
    }

    public boolean doesAttackMeetNormalRequirements() {
        return this.isNotAttacking()
                && this.getTarget() != null
                && this.hasLineOfSight(this.getTarget())
                && this.attackCooldown < 1
                && !this.isStunned()
                && !this.isPlayingIntro();
    }

    public boolean canUseBreath() {
        return this.getAttackType() == 0
                && this.getTarget() != null
                && this.hasLineOfSight(this.getTarget())
                && this.attackCooldown < 1
                && !this.isStunned()
                && !this.isPlayingIntro
                && !this.isPlayingPhase
                && this.halfHealth()
                && this.breathCooldown < 1
                && this.isCrazy()
                && this.distanceToSqr(this.getTarget()) < 36.0;
    }

    @Override
    public boolean isStaying() {
        return super.isStaying() || (this.getFirstPassenger() instanceof IServant servant && servant.isStaying() && !MobsConfig.ServantRideAutonomous.get());
    }

    public boolean isPlayingIntro(){
        return this.isPlayingIntro;
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.hasPassenger()
                    && !this.isCrazy()
                    && ((rider instanceof Player && !this.isAutonomous())
                    || (rider instanceof IServant servant && (servant.isStaying() || servant.isCommanded() || servant.isGuardingArea())))
                    && this.notClientAttacking() && !this.isPlayingIntro()) {
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

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4){
            this.clientAttacking = true;
        } else if (pId == 5){
            this.clientAttacking = false;
        } else if (pId == 6){
            this.setAnimationState(2);
            this.phaseTicks = 1;
        } else if (pId == 7){
            this.setCrazy(true);
            this.setRagnoFace(3);
            this.setShakeMultiplier(20);
        } else if (pId == 8){
            this.setCrazy(false);
        } else if (pId == 9){
            this.isPlayingIntro = true;
        } else if (pId == 10){
            this.isPlayingIntro = false;
        } else if (pId == 11){
            this.isPlayingPhase = true;
            this.setAnimationState(2);
            this.phaseTicks = 1;
        } else if (pId == 12){
            this.isPlayingPhase = false;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!pPlayer.level.isClientSide) {
            if (pPlayer == this.getTrueOwner() && !this.isBurrowing() && !this.isPlayingIntro()) {
                if (!pPlayer.isCrouching() && !this.isStunned() && !this.isCrazy()) {
                    if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer){
                        this.getFirstPassenger().stopRiding();
                        return InteractionResult.SUCCESS;
                    } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)){
                        this.doPlayerRide(pPlayer);
                        return InteractionResult.SUCCESS;
                    }
                } else if (pHand == InteractionHand.MAIN_HAND && this.isFood(pPlayer.getMainHandItem()) && this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodProperties = pPlayer.getMainHandItem().getFoodProperties(this);
                    if (foodProperties != null) {
                        this.heal((float) foodProperties.getNutrition());
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getMainHandItem().shrink(1);
                        }
                        this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), 0.5F);
                        this.gameEvent(GameEvent.EAT, this);
                        return InteractionResult.SUCCESS;
                    } else {
                        return InteractionResult.PASS;
                    }
                } else if (!this.isCrazy() && this.phaseTicks < 1 && pPlayer.getMainHandItem().is(ItemRegisterer.BAG_OF_HORRORS.get())){
                    pPlayer.getMainHandItem().shrink(1);
                    ItemEntity bag = EntityType.ITEM.create(this.level);
                    if (bag != null) {
                        bag.setItem(ItemRegisterer.BAG_OF_HORRORS.get().getDefaultInstance());
                        bag.setPos(this.getX(), this.getY(), this.getZ());
                        bag.setDeltaMovement(0.0, 0.6, 0.0);
                        bag.setNeverPickUp();
                        bag.setUnlimitedLifetime();
                        bag.noPhysics = true;
                        this.item = bag;
                    }
                    this.stopAttacking();

                    this.isPlayingPhase = true;
                    this.setAnimationState(2);
                    this.phaseTicks = 1;
                    this.level.broadcastEntityEvent(this, (byte) 11);
                } else if (this.isCrazy() && !this.isDeadOrDying() && this.isNotAttacking() && this.getTarget() == null){
                    ItemEntity bag = EntityType.ITEM.create(this.level);
                    if (bag != null) {
                        bag.setItem(ItemRegisterer.BAG_OF_HORRORS.get().getDefaultInstance());
                        bag.setPos(this.getX(), this.getY(), this.getZ());
                        double x = this.getX() - this.getTrueOwner().getX();
                        double y = this.getY() - this.getTrueOwner().getY();
                        double z = this.getZ() - this.getTrueOwner().getZ();
                        double d = Math.sqrt(x * x + y * y + z * z);
                        double power = 4.5F;
                        double motionX = -(x / d * power * 0.2);
                        double motionY = -(y / d * power * 0.2);
                        double motionZ = -(z / d * power * 0.2);
                        bag.setDeltaMovement(motionX, motionY, motionZ);
                        this.level.addFreshEntity(bag);
                    }
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_COUGH.get());
                    this.setCrazy(false);
                    this.setRagnoFace(0);
                    this.setShakeMultiplier(10);
                    this.level.broadcastEntityEvent(this, (byte) 8);
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        FoodProperties foodProperties = p_30440_.getFoodProperties(this);
        return item.isEdible() && foodProperties != null && foodProperties.isMeat();
    }

    @Override
    public boolean canUpdateMove() {
        return !(this.getControllingPassenger() instanceof Mob) && !this.isPlayingIntro();
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            this.hurt(this.damageSources().starve(), Float.MAX_VALUE);
        }
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setIsJumping(boolean jumping) {
        this.isJumping = jumping;
    }

    protected void tickRidden(Player p_278233_, Vec3 p_275693_) {
        super.tickRidden(p_278233_, p_275693_);
        if (p_278233_.isLocalPlayer()) {
            if (this.onGround()) {
                this.setIsJumping(false);
                if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
                    this.executeRidersJump(this.playerJumpPendingScale, p_275693_);
                }

                this.playerJumpPendingScale = 0.0F;
            }
        }
    }

    protected void executeRidersJump(float p_248808_, Vec3 p_275435_) {
        double d0 = 1.0D * (double)p_248808_ * (double)this.getBlockJumpFactor();
        double d1 = d0 + this.getJumpBoostPower();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d1, vec3.z);
        ModNetwork.sendToServer(new CSetDeltaMovement(this.getId(), vec3.x, d1, vec3.z));
        this.setIsJumping(true);
        this.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
        if (p_275435_.z > 0.0D) {
            float f = Mth.sin(this.getYRot() * ((float)Math.PI / 180F));
            float f1 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
            Vec3 vec31 = this.getDeltaMovement().add(-0.4F * f * p_248808_, 0.0D, 0.4F * f1 * p_248808_);
            this.setDeltaMovement(vec31);
            ModNetwork.sendToServer(new CSetDeltaMovement(this.getId(), vec31.x, vec31.y, vec31.z));
        }

    }

    @Override
    public void onPlayerJump(int p_21696_) {
        if (p_21696_ < 0) {
            p_21696_ = 0;
        }

        if (p_21696_ >= 90) {
            this.playerJumpPendingScale = 1.0F;
        } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * (float)p_21696_ / 90.0F;
        }
    }

    @Override
    public boolean canJump() {
        return this.notClientAttacking()
                && !this.isPlayingIntro()
                && !this.isBurrowing();
    }

    @Override
    public void handleStartJump(int p_21695_) {
    }

    @Override
    public void handleStopJump() {
    }

    class StunGoal extends Goal {
        public StunGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.isStunned()
                    && !RagnoServant.this.isPlayingPhase;
        }

        public void start() {
            RagnoServant.this.setAnimationState(10);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.stunTick <= 114;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null && RagnoServant.this.isCrazy()) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.setAnimationState(0);
            RagnoServant.this.setAttackTicks(0);
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.attackCooldown = 20;
            RagnoServant.this.setStunHealth(RagnoServant.this.getMaxStunHealth());
            RagnoServant.this.stunTick = 0;
            RagnoServant.this.setStunned(false);
        }
    }

    class BreathGoal extends Goal {
        public BreathGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && RagnoServant.this.halfHealth()
                    && RagnoServant.this.breathCooldown < 1
                    && RagnoServant.this.isCrazy()
                    && RagnoServant.this.getTarget() != null
                    && RagnoServant.this.distanceToSqr(RagnoServant.this.getTarget()) < 36.0;
        }

        public void start() {
            RagnoServant.this.setAnimationState(18);
            RagnoServant.this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 0.9F);
            RagnoServant.this.setAttackType(BREATH_ATTACK);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.isGrabbing() ? RagnoServant.this.getAttackTicks() <= 109 : RagnoServant.this.getAttackTicks() <= 35;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null && !RagnoServant.this.isGrabbing()) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.setAnimationState(0);
            RagnoServant.this.setAttackTicks(0);
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.loseStunHealth(RagnoServant.this.isGrabbing() ? 10 : 5, false);
            RagnoServant.this.setGrabbing(false);
            RagnoServant.this.breathCooldown = 100;
            RagnoServant.this.attackCooldown = 20;
        }
    }

    class WebGoal extends Goal {
        public WebGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && (RagnoServant.this.halfHealth() || RagnoServant.this.random.nextInt(16) == 0)
                    && RagnoServant.this.webCooldown < 1;
        }

        public void start() {
            RagnoServant.this.setAnimationState(4);
            RagnoServant.this.setAttackType(WEB_ATTACK);
        }

        public boolean canContinueToUse() {
            if (RagnoServant.this.getAttackTicks() <= 20) {
                return RagnoServant.this.getAttackType() == WEB_ATTACK;
            }

            return false;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.setAnimationState(0);
            RagnoServant.this.setAttackTicks(0);
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.loseStunHealth(5, false);
            RagnoServant.this.webCooldown = 200;
            RagnoServant.this.attackCooldown = 20;
        }
    }

    class LeapGoal extends Goal {
        public LeapGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && (RagnoServant.this.halfHealth() || RagnoServant.this.random.nextInt(16) == 0)
                    && RagnoServant.this.leapCooldown < 1
                    && RagnoServant.this.getTarget() != null
                    && !RagnoServant.this.isStaying()
                    && RagnoServant.this.distanceToSqr(RagnoServant.this.getTarget()) < 144.0;
        }

        public void start() {
            RagnoServant.this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 1.0F);
            RagnoServant.this.setAnimationState(5);
            RagnoServant.this.setAttackType(LEAP_ATTACK);
        }

        public boolean canContinueToUse() {
            if (RagnoServant.this.getAttackTicks() < 130) {
                if (RagnoServant.this.getAttackTicks() <= 47 || !RagnoServant.this.onGround()) {
                    return RagnoServant.this.getAttackType() == LEAP_ATTACK;
                }

            }
            return false;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.setAnimationState(0);
            RagnoServant.this.setAttackTicks(0);
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.loseStunHealth(7, false);
            RagnoServant.this.shouldHurtOnTouch = false;
            RagnoServant.this.leapCooldown = 100;
            RagnoServant.this.attackCooldown = 20;
        }
    }

    class WebNetGoal extends Goal {
        public WebNetGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && RagnoServant.this.isCrazy()
                    && (RagnoServant.this.halfHealth() || RagnoServant.this.random.nextInt(16) == 0)
                    && RagnoServant.this.webNetCooldown < 1
                    && RagnoServant.this.getTarget() != null
                    && RagnoServant.this.distanceToSqr(RagnoServant.this.getTarget()) > 121.0;
        }

        public void start() {
            RagnoServant.this.setAnimationState(16);
            RagnoServant.this.setAttackType(WEB_NET_ATTACK);
            RagnoServant.this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 1.25F);
        }

        public boolean canContinueToUse() {
            if (RagnoServant.this.getAttackTicks() <= 16 || RagnoServant.this.waitingForWeb || RagnoServant.this.followupTicks > 0 && RagnoServant.this.followupTicks <= 20) {
                return RagnoServant.this.getAttackType() == WEB_NET_ATTACK;
            }
            return false;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null && (RagnoServant.this.getAttackTicks() < 16 || RagnoServant.this.getEntityData().get(RagnoServant.ANIMATION_STATE) == 20 || RagnoServant.this.followupTicks > 0)) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.setAttackTicks(0);
            RagnoServant.this.setAnimationState(0);
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.webNetCooldown = 200;
            RagnoServant.this.loseStunHealth(RagnoServant.this.followupTicks > 0 ? 7 : 5, false);
            RagnoServant.this.attackCooldown = 20;
            RagnoServant.this.followupTicks = 0;
        }
    }

    class BurrowGoal extends Goal {
        public BurrowGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && RagnoServant.this.onGround()
                    && (RagnoServant.this.halfHealth() || RagnoServant.this.getRandom().nextInt(16) == 0)
                    && RagnoServant.this.burrowCooldown < 1
                    && !RagnoServant.this.isStaying()
                    && !RagnoServant.this.hasPassenger();
        }

        public void start() {
            RagnoServant.this.setAnimationState(6);
            RagnoServant.this.setAttackType(RagnoServant.this.BURROW_ATTACK);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.getAttackTicks() <= (RagnoServant.this.halfHealth() ? 68 : 138);
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.setAnimationState(0);
            RagnoServant.this.setAttackTicks(0);
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.loseStunHealth(RagnoServant.this.halfHealth() ? 5 : 10, false);
            RagnoServant.this.burrowCooldown = 160;
            RagnoServant.this.attackCooldown = 20;
            RagnoServant.this.setInvisible(false);
            RagnoServant.this.setBurrowing(false);
        }
    }

    class ChargeGoal extends Goal {
        public ChargeGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && (RagnoServant.this.halfHealth() || RagnoServant.this.random.nextInt(16) == 0)
                    && RagnoServant.this.chargeCooldown < 1
                    && !RagnoServant.this.isStaying()
                    && RagnoServant.this.isCrazy();
        }

        public void start() {
            RagnoServant.this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 0.9F);
            RagnoServant.this.setAnimationState(8);
            RagnoServant.this.setAttackType(RagnoServant.this.CHARGE_ATTACK);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.getAttackTicks() <= 66;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.setAnimationState(0);
            RagnoServant.this.setAttackTicks(0);
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.loseStunHealth(10, false);
            RagnoServant.this.chargeCooldown = 160;
            RagnoServant.this.attackCooldown = 20;
        }
    }

    class CoughGoal extends Goal {
        public CoughGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && (RagnoServant.this.halfHealth() || RagnoServant.this.random.nextInt(16) == 0)
                    && RagnoServant.this.coughCooldown < 1
                    && RagnoServant.this.isCrazy();
        }

        public void start() {
            RagnoServant.this.setAnimationState(9);
            RagnoServant.this.setAttackType(RagnoServant.this.COUGH_ATTACK);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.halfHealth() ? RagnoServant.this.getAttackTicks() <= 20 : RagnoServant.this.getAttackTicks() <= 70;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.setAnimationState(0);
            RagnoServant.this.setAttackTicks(0);
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.loseStunHealth(5, false);
            RagnoServant.this.coughCooldown = 200;
            RagnoServant.this.attackCooldown = 20;
        }
    }

}
