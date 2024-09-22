package com.Polarice3.goety_spillage.common.entities.ally;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.projectiles.GSPumpkinBomb;
import com.Polarice3.goety_spillage.common.entities.projectiles.WebProjectile;
import com.Polarice3.goety_spillage.common.network.GSNetwork;
import com.Polarice3.goety_spillage.common.network.server.SSetDeltaMovement;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.config.IllageAndSpillageConfig;
import com.yellowbrossproductions.illageandspillage.entities.CameraShakeEntity;
import com.yellowbrossproductions.illageandspillage.entities.TrickOrTreatEntity;
import com.yellowbrossproductions.illageandspillage.entities.VillagerSoulEntity;
import com.yellowbrossproductions.illageandspillage.init.ModEntityTypes;
import com.yellowbrossproductions.illageandspillage.util.EntityUtil;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import com.yellowbrossproductions.illageandspillage.util.ItemRegisterer;
import net.minecraft.core.BlockPos;
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
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RagnoServant extends Summoned implements PlayerRideableJumping, IAutoRideable, ICanBeAnimated {
    private static final UUID SPEED_PENALTY_UUID = UUID.fromString("5CD17A52-AB9A-42D3-A629-90FDE04B281E");
    private static final AttributeModifier SPEED_PENALTY = new AttributeModifier(SPEED_PENALTY_UUID, "STOP MOVING AROUND STUPID", -0.35, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CRAZY = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STUNNED = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(RagnoServant.class, EntityDataSerializers.INT);
    public AnimationState introAnimationState = new AnimationState();
    public AnimationState phaseAnimationState = new AnimationState();
    public AnimationState blockAnimationState = new AnimationState();
    public AnimationState webAnimationState = new AnimationState();
    public AnimationState leapAnimationState = new AnimationState();
    public AnimationState burrowAnimationState = new AnimationState();
    public AnimationState popupAnimationState = new AnimationState();
    public AnimationState chargeAnimationState = new AnimationState();
    public AnimationState coughAnimationState = new AnimationState();
    public AnimationState stunAnimationState = new AnimationState();
    private int attackTicks;
    private int attackCooldown;
    private final int WEB_ATTACK = 1;
    private final int LEAP_ATTACK = 2;
    private final int BURROW_ATTACK = 3;
    private final int CHARGE_ATTACK = 4;
    private final int COUGH_ATTACK = 5;
    private int webCooldown;
    private int leapCooldown;
    private int burrowCooldown;
    private int chargeCooldown;
    private int coughCooldown;
    int introTicks;
    int phaseTicks;
    public ItemEntity item = null;
    int blockTicks;
    boolean shouldHurtOnTouch;
    public boolean isPlayingIntro;
    public double chargeX;
    public double chargeZ;
    public boolean circleDirection;
    public boolean clientAttacking;
    protected boolean isJumping;
    protected float playerJumpPendingScale;
    public int circleTick;
    public int attacksUsed;
    public int stunTick;
    private boolean isBurrowing = false;
    public DamageSource deathBlow = DamageSource.GENERIC;

    public RagnoServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new StunGoal());
        this.goalSelector.addGoal(0, new WebGoal());
        this.goalSelector.addGoal(0, new LeapGoal());
        this.goalSelector.addGoal(0, new BurrowGoal());
        this.goalSelector.addGoal(0, new ChargeGoal());
        this.goalSelector.addGoal(0, new CoughGoal());
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AlwaysWatchTargetGoal());
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 0.6));
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
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(CRAZY, false);
        this.entityData.define(STUNNED, false);
        this.entityData.define(AUTO_MODE, false);
        this.entityData.define(ATTACK_TYPE, 0);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
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

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.CONVERSION || pReason == MobSpawnType.MOB_SUMMONED || this.getTrueOwner() != null){
            this.playIntro();
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

    public boolean isStunned() {
        return this.entityData.get(STUNNED);
    }

    public void setStunned(boolean stunned) {
        this.entityData.set(STUNNED, stunned);
    }

    public boolean isBurrowed(){
        return this.getCurrentAnimation() == 6 && this.isBurrowing;
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

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable() || this.isBurrowed() || this.isPlayingIntro();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return super.isInvulnerableTo(p_20122_) || this.isBurrowed() || this.isPlayingIntro();
    }

    @Override
    public boolean isAttackable() {
        return super.isAttackable() && !this.isBurrowed() && !this.isPlayingIntro();
    }

    public boolean canBeSeenByAnyone() {
        return super.canBeSeenByAnyone() && !this.isBurrowed() && !this.isPlayingIntro();
    }

    public void push(Entity p_21294_) {
        if (!this.isBurrowed()) {
            super.push(p_21294_);
        }
    }

    protected void pushEntities() {
        if (!this.isBurrowed()) {
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
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Mob mob){
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
        return super.isControlledByLocalInstance()
                && this.notClientAttacking()
                && (this.getControllingPassenger() == null
                || (!this.isAutonomous() && this.getControllingPassenger() instanceof Player)
                || this.getControllingPassenger() instanceof Mob);
    }

    @Override
    public boolean isVehicle() {
        if (this.getControllingPassenger() instanceof Mob){
            return super.isVehicle();
        } else {
            if (this.level.isClientSide){
                return super.isVehicle() && this.notClientAttacking();
            } else {
                return super.isVehicle() && this.isNotAttacking();
            }
        }
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

    public void goCrazy(){
        if (this.phaseTicks < 1){
            if (!this.level.isClientSide) {
                this.stopAttacking();
                this.level.broadcastEntityEvent(this, (byte) 9);
            }

            this.isPlayingIntro = true;
            this.setAnimationState(2);
            this.phaseTicks = 1;
        }
    }

    public void tick() {
        if (this.isPlayingIntro()) {
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        }

        AttributeInstance instance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (instance != null) {
            if (this.getAttackType() <= 0 && !this.isPlayingIntro() && !this.isStunned()) {
                instance.removeModifier(SPEED_PENALTY);
            } else {
                instance.removeModifier(SPEED_PENALTY);
                instance.addTransientModifier(SPEED_PENALTY);
            }
        }

        if (this.random.nextInt(200) == 0) {
            this.circleDirection = !this.circleDirection;
        }

        if (this.circleDirection) {
            ++this.circleTick;
        } else {
            --this.circleTick;
        }

        if (this.stunTick > 0) {
            this.getNavigation().stop();
            this.navigation.stop();
        }

        this.stopAttackersFromAttacking();
        if (this.blockTicks > 0) {
            --this.blockTicks;
        }

        if (this.introTicks > 0 && this.introTicks < 80) {
            ++this.introTicks;
        }

        if (this.introTicks == 21) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_ROAR.get(), 3.0F, 1.0F);
        }

        if (this.introTicks == 24) {
            CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.05F, 48, 20);
        }

        if (this.introTicks == 70) {
            this.isPlayingIntro = false;
            this.level.broadcastEntityEvent(this, (byte) 10);
            this.setAnimationState(0);
        }

        if (!this.level.isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }

        if (this.phaseTicks > 0 && this.phaseTicks < 90) {
            ++this.phaseTicks;
        }

        if (this.phaseTicks == 19) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_LEAP.get(), 2.0F, 1.0F);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_EAT.get(), 2.0F, 1.0F);
        }

        if (this.phaseTicks == 20 && this.item != null) {
            this.makeSpitParticles(this.item);
            this.item.discard();
        }

        if (this.phaseTicks == 27) {
            if (!this.level.isClientSide) {
                this.setCrazy(true);
                this.level.broadcastEntityEvent(this, (byte) 7);
            }

            CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.1F, 0, 20);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.0F);
        }

        if (this.phaseTicks == 43) {
            CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.05F, 0, 30);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.5F);
        }

        if (this.phaseTicks == 60) {
            CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.05F, 0, 30);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.4F);
        }

        if (this.phaseTicks == 50) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 0.7F);
        }

        if (this.phaseTicks == 80) {
            this.isPlayingIntro = false;
            this.level.broadcastEntityEvent(this, (byte) 10);
            this.attackTicks = 0;
            this.setAttackType(0);
            this.setAnimationState(0);
        }

        if (this.getAttackType() > 0) {
            ++this.attackTicks;
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
        }

        this.attackAI();

        if (this.isNotAttacking()
                && !this.isPlayingIntro()
                && this.getTarget() != null
                && !this.isStaying()
                && !this.isStunned()) {
            this.circleTarget(this.getTarget(), 10.0F, 0.8F, true, this.circleTick, 0.0F, 1.0F);
            this.lookAt(this.getTarget(), 100.0F, 100.0F);
            this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
        }

        if (this.attacksUsed >= IllageAndSpillageConfig.ragno_attackTimes.get() && !this.isStunned()) {
            this.setStunned(true);
        }

        if (this.isStunned()) {
            this.getNavigation().stop();
            ++this.stunTick;
            if (this.stunTick == 6) {
                this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_STUN.get(), 3.0F, 1.0F);
            }

            if (this.stunTick % 5 == 0) {
                this.makeSweatParticles();
            }
        }

        if (this.getTarget() != null
                && this.isAlive()
                && (double)this.distanceTo(this.getTarget()) < 8.0D * ((double)this.getTarget().getBbWidth() + 0.4D)
                && this.getAttackType() == 0
                && this.isOnGround()
                && !this.isStaying()
                && !this.isStunned()
                && !this.isPlayingIntro()) {
            double x = this.getX() - this.getTarget().getX();
            double y = this.getY() - this.getTarget().getY();
            double z = this.getZ() - this.getTarget().getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            this.setDeltaMovement(this.getDeltaMovement().subtract(-x / d * 0.08D, 0.0D, -z / d * 0.08D));
        }

        super.tick();
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
    }

    @Override
    public void mobSense() {
        if (!this.isBurrowed() && !this.isPlayingIntro()){
            super.mobSense();
        }
    }

    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        super.setDeltaMovement(p_20335_, p_20336_, p_20337_);
        if (!this.level.isClientSide){
            GSNetwork.sentToTrackingEntity(this, new SSetDeltaMovement(this.getId(), p_20335_, p_20336_, p_20337_));
        }
    }

    public void attackAI(){
        if (this.isAlive()) {
            DamageSource damageSource = DamageSource.mobAttack(this);
            if (this.getTrueOwner() != null){
                damageSource = ModDamageSource.summonAttack(this, this.getTrueOwner());
            }
            if (this.getAttackType() == this.WEB_ATTACK) {
                if (this.attackTicks == 4) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.5D, 0.0D));
                }

                if (this.attackTicks == 6 && this.getTarget() != null) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_WEB.get(), 2.0F, 1.0F);

                    for(int i = 0; i < 8; ++i) {
                        if (!this.level.isClientSide) {
                            WebProjectile projectile = GSEntityTypes.WEB.get().create(this.level);
                            if (projectile != null) {
                                projectile.setPos(this.getX(), this.getY() + 1.0D, this.getZ());
                                projectile.setYHeadRot(this.getYHeadRot());
                                projectile.setYRot(this.getYHeadRot());
                                double x = projectile.getX() - this.getTarget().getX();
                                double y = projectile.getY() - (this.getTarget().getY() + 1.5D);
                                double z = projectile.getZ() - this.getTarget().getZ();
                                double d = Math.sqrt(x * x + y * y + z * z);
                                double power = 2.5F;
                                double motionX = -(x / d * power * 0.2D);
                                double motionY = -(y / d * power * 0.2D);
                                double motionZ = -(z / d * power * 0.2D);
                                double randomX = (-0.5D + this.random.nextDouble()) / 8.0D;
                                double randomY = (-0.5D + this.random.nextDouble()) / 8.0D;
                                double randomZ = (-0.5D + this.random.nextDouble()) / 8.0D;
                                projectile.setAcceleration(motionX + randomX, motionY + randomY, motionZ + randomZ);
                                projectile.setShooter(this);
                                projectile.setDamage(this.getAttackValue() / 2.0F);
                                this.level.addFreshEntity(projectile);
                            }
                        }
                    }
                }

                if (this.attackTicks == 10) {
                    this.setDeltaMovement(0.0D, -1.0D, 0.0D);
                }
            }

            if (this.getAttackType() == this.LEAP_ATTACK) {
                double targetX = 0.0D;
                double targetY = 0.0D;
                double targetZ = 0.0D;
                if (this.getTarget() != null) {
                    double xPower = this.getX() - this.getTarget().getX();
                    double yPower = this.getY() - (this.getTarget().getY() + 1.5D);
                    double zPower = this.getZ() - this.getTarget().getZ();
                    double d = Math.sqrt(xPower * xPower + yPower * yPower + zPower * zPower);
                    float power = 6.5F;
                    targetX = -(xPower / d * (double)power * 0.2D);
                    targetY = -(yPower / d * (double)power * 0.02D);
                    targetZ = -(zPower / d * (double)power * 0.2D);
                }

                if (this.attackTicks == 6) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                }

                if (this.attackTicks == 17) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.9F);
                }

                if (this.attackTicks == 30) {
                    this.shouldHurtOnTouch = true;
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_LEAP.get(), 2.0F, this.getVoicePitch());
                    this.setDeltaMovement(targetX, targetY > 0.0D ? targetY + 0.2D : 0.2D, targetZ);
                }

                if (this.shouldHurtOnTouch) {
                    for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(15.0D))){
                        if (!MobUtil.areAllies(entity, this) && entity instanceof LivingEntity && entity.isAlive()) {
                            double x = this.getX() - entity.getX();
                            double y = this.getY() - entity.getY();
                            double z = this.getZ() - entity.getZ();
                            double d = Math.sqrt(x * x + y * y + z * z);
                            if (this.distanceToSqr(entity) < 9.0D) {
                                if (entity.invulnerableTime <= 0) {
                                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                                    entity.hurt(damageSource, this.getAttackValue());
                                    entity.hurtMarked = true;
                                    entity.setDeltaMovement(-x / d * 2.0D, -y / d * 2.0D + 0.5D, -z / d * 2.0D);
                                }

                                if (((LivingEntity)entity).isBlocking()) {
                                    EntityUtil.disableShield((LivingEntity)entity, 100);
                                }
                            }
                        }
                    }
                }
            }

            if (this.getAttackType() == this.BURROW_ATTACK) {
                if (this.attackTicks > 6 && this.attackTicks <= 30) {
                    this.playSound(SoundEvents.GRAVEL_BREAK, 2.0F, 0.7F);
                    this.makeBlockParticles(this.getBlockStateOn());
                    this.isBurrowing = true;
                }

                if (this.attackTicks >= 30 && this.getTarget() != null) {
                    this.clearFire();
                    if (this.attackTicks < 100) {
                        this.playSound(SoundEvents.STONE_BREAK, 2.0F, 0.5F);
                    }

                    double targetX = this.getX();
                    double targetY = this.getY();
                    double targetZ = this.getZ();
                    if (this.attackTicks < 100 && this.getTarget() != null) {
                        this.setInvisible(true);
                        targetX = this.getTarget().getX();
                        targetY = this.getTarget().getY();
                        targetZ = this.getTarget().getZ();
                    }

                    this.setPos(targetX, targetY, targetZ);
                    this.setDeltaMovement(0.0D, 0.0D, 0.0D);
                    if (this.attackTicks == 120) {
                        this.makeBlockParticles(this.getBlockStateOn());
                        this.setAnimationState(7);
                        this.setInvisible(false);
                        CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.05F, 0, 30);
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.6F);
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_SLAM.get(), 2.0F, 1.2F);
                        for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(15.0))){
                            if (!MobUtil.areAllies(entity, this) && entity instanceof LivingEntity && entity.isAlive() && entity != this) {
                                double x = this.getX() - entity.getX();
                                double y = this.getY() - entity.getY();
                                double z = this.getZ() - entity.getZ();
                                double d = Math.sqrt(x * x + y * y + z * z);
                                if (this.distanceToSqr(entity) < 9.0D && entity.invulnerableTime <= 0) {
                                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                                    entity.hurt(damageSource, this.getAttackValue());
                                    entity.hurtMarked = true;
                                    entity.setDeltaMovement(-x / d * 2.0D, -y / d * 2.0D + 0.8D, -z / d * 2.0D);
                                    entity.lerpMotion(-x / d * 2.0D, -y / d * 2.0D + 0.8D, -z / d * 2.0D);
                                }
                            }
                        }

                        this.setDeltaMovement(0.0D, 0.0D, 0.0D);
                    }
                }
            }

            if (this.getAttackType() == this.CHARGE_ATTACK) {
                if (this.attackTicks == 30 && this.getTarget() != null) {
                    double targetX = this.getX() - this.getTarget().getX();
                    double targetY = this.getY() - this.getTarget().getY();
                    double targetZ = this.getZ() - this.getTarget().getZ();
                    double d = Math.sqrt(targetX * targetX + targetY * targetY + targetZ * targetZ);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_CHARGE.get(), 2.0F, this.getVoicePitch());
                    float power = 4.5F;
                    double motionX = -(targetZ / d * (double)power * 0.2D);
                    this.chargeX = motionX;
                    this.chargeZ = motionX;
                }

                if (this.attackTicks > 30) {
                    this.setDeltaMovement(this.chargeX, this.getDeltaMovement().y, this.chargeZ);
                    for (Entity hit : this.level.getEntities(this, this.getBoundingBox().inflate(15.0D))){
                        if (!MobUtil.areAllies(hit, this) && hit instanceof LivingEntity && hit.isAlive() && hit != this) {
                            double x = this.getX() - hit.getX();
                            double y = this.getY() - hit.getY();
                            double z = this.getZ() - hit.getZ();
                            double d = Math.sqrt(x * x + y * y + z * z);
                            if (this.distanceToSqr(hit) < 9.0D && hit.invulnerableTime <= 0) {
                                this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                                hit.hurt(damageSource, this.getAttackValue());
                                hit.hurtMarked = true;
                                hit.setDeltaMovement(-x / d * 2.0D, -y / d * 2.0D + 1.2D, -z / d * 2.0D);
                                hit.lerpMotion(-x / d * 2.0D, -y / d * 2.0D + 1.2D, -z / d * 2.0D);
                            }
                        }
                    }
                }
            }

            if (this.getAttackType() == this.COUGH_ATTACK && this.attackTicks == 10 && this.getTarget() != null) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_COUGH.get(), 2.0F, 1.0F);
                CameraShakeEntity.cameraShake(this.level, this.position(), 50.0F, 0.06F, 0, 10);
                if (this.random.nextBoolean()) {
                    for(int i = 0; i < 4; ++i) {
                        if (!this.level.isClientSide) {
                            TrickOrTreatEntity treat = ModEntityTypes.TrickOrTreat.get().create(this.level);
                            if (treat != null) {
                                treat.setPos(this.getX(), this.getY(), this.getZ());
                                double x = this.getX() - this.getTarget().getX();
                                double y = this.getY() - this.getTarget().getY();
                                double z = this.getZ() - this.getTarget().getZ();
                                double d = Math.sqrt(x * x + y * y + z * z);
                                double power = 4.5F;
                                double motionX = -(x / d * power * 0.2D);
                                double motionY = -(y / d * power * 0.2D);
                                double motionZ = -(z / d * power * 0.2D);
                                treat.setDeltaMovement(motionX, motionY, motionZ);
                                treat.circleTime = i * 10;
                                treat.bounceTime = i;
                                treat.setTreat(this.random.nextInt(5) + 1);
                                if (this.getTeam() != null) {
                                    this.level.getScoreboard().addPlayerToTeam(treat.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                                }

                                treat.setOwner(this);
                                treat.setGoopy();
                                this.level.addFreshEntity(treat);
                            }
                        }
                    }
                } else {
                    for(int i = 0; i < 3; ++i) {
                        if (!this.level.isClientSide) {
                            GSPumpkinBomb treat = GSEntityTypes.PUMPKIN_BOMB.get().create(this.level);
                            if (treat != null) {
                                treat.setPos(this.getX(), this.getY(), this.getZ());
                                double x = this.getX() - this.getTarget().getX();
                                double y = this.getY() - this.getTarget().getY();
                                double z = this.getZ() - this.getTarget().getZ();
                                double d = Math.sqrt(x * x + y * y + z * z);
                                double power = 4.5F;
                                double motionX = -(x / d * power * 0.2D);
                                double motionY = -(y / d * power * 0.2D);
                                double motionZ = -(z / d * power * 0.2D);
                                if (this.getTeam() != null) {
                                    this.level.getScoreboard().addPlayerToTeam(treat.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                                }

                                treat.setDeltaMovement(motionX, motionY, motionZ);
                                treat.setTrueOwner(this);
                                treat.setTarget(this.getTarget());
                                treat.setGoopy();
                                this.level.addFreshEntity(treat);
                            }
                        }
                    }
                }
            }
        }
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int attackType) {
        this.entityData.set(ATTACK_TYPE, attackType);
    }

    public boolean isNotAttacking(){
        return this.getAttackType() <= 0;
    }

    public boolean notClientAttacking(){
        return !this.clientAttacking;
    }

    public void stopAttacking() {
        this.setAttackType(0);
    }

    public void die(DamageSource pSource) {
        List<TrickOrTreatEntity> treats = this.level.getEntitiesOfClass(TrickOrTreatEntity.class, this.getBoundingBox().inflate(40.0));
        if (!treats.isEmpty()) {
            for (TrickOrTreatEntity treat : treats) {
                if (treat.getOwner() == this) {
                    treat.kill();
                }
            }
        }

        this.deathBlow = pSource;
        this.clearFire();
        if (!this.level.isClientSide) {
            this.attackTicks = 0;
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

        if ((this.deathTime == 200 || (this.deathTime == 40 && this.deathBlow == DamageSource.OUT_OF_WORLD)) && !this.level.isClientSide()) {
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
        if (!this.isStunned() && !this.isStaying()) {
            int directionInt = direction ? -1 : 1;
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

    public void makeSweatParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 2; ++i) {
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
        if (this.getTrueOwner() != null && pSource.getEntity() == this.getTrueOwner() && MobsConfig.MinionsMasterImmune.get()){
            return false;
        } else if (this.hasPassenger() && pSource.getEntity() == this.getFirstPassenger()) {
            return false;
        } else if (this.isBurrowed()) {
            return false;
        } else {
            if (this.isAlive() && pSource != DamageSource.OUT_OF_WORLD) {
                boolean source;
                if (!this.isCrazy() && !this.isStunned()) {
                    source = !pSource.isBypassArmor();
                    if (source && this.blockTicks < 1 && (this.entityData.get(ANIMATION_STATE) == 0 || (Integer)this.entityData.get(ANIMATION_STATE) == 3)) {
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_BLOCK.get(), 2.0F, 1.0F);
                        this.setAnimationState(0);
                        this.setAnimationState(3);
                        this.blockTicks = 10;
                    }

                    if (pSource.getEntity() instanceof LivingEntity livingEntity && this.getLastHurtByMob() == null) {
                        this.setLastHurtByMob(livingEntity);
                    }

                    return false;
                }

                if (!this.isStunned()) {
                    source = !pSource.isBypassArmor();
                    if (source && this.getAttackType() == 0) {
                        if (this.blockTicks < 1 && (this.entityData.get(ANIMATION_STATE) == 0 || (Integer)this.entityData.get(ANIMATION_STATE) == 3)) {
                            this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_BLOCK.get(), 2.0F, 1.0F);
                            this.setAnimationState(0);
                            this.setAnimationState(3);
                            this.blockTicks = 10;
                        }

                        if (pSource.getEntity() instanceof LivingEntity livingEntity && this.getLastHurtByMob() == null) {
                            this.setLastHurtByMob(livingEntity);
                        }

                        return false;
                    }

                    pAmount /= 3.5F;
                }
            }

            return pSource != DamageSource.IN_WALL && super.hurt(pSource, pAmount);
        }
    }

    protected SoundEvent getAmbientSound() {
        return IllageAndSpillageSoundEvents.ENTITY_RAGNO_AMBIENT.get();
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
        return 1.9D;
    }

    public AnimationState getAnimationState(String input) {
        if (Objects.equals(input, "intro")) {
            return this.introAnimationState;
        } else if (Objects.equals(input, "phase")) {
            return this.phaseAnimationState;
        } else if (Objects.equals(input, "block")) {
            return this.blockAnimationState;
        } else if (Objects.equals(input, "web")) {
            return this.webAnimationState;
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
        } else {
            return Objects.equals(input, "stun") ? this.stunAnimationState : new AnimationState();
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
                    this.introAnimationState.start(this.tickCount);
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
                    this.stunAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    public void stopAllAnimationStates() {
        this.introAnimationState.stop();
        this.phaseAnimationState.stop();
        this.blockAnimationState.stop();
        this.webAnimationState.stop();
        this.leapAnimationState.stop();
        this.burrowAnimationState.stop();
        this.popupAnimationState.stop();
        this.chargeAnimationState.stop();
        this.coughAnimationState.stop();
        this.stunAnimationState.stop();
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

    public boolean doesAttackMeetNormalRequirements() {
        return this.getAttackType() == 0
                && this.getTarget() != null
                && this.hasLineOfSight(this.getTarget())
                && this.attackCooldown < 1
                && !this.isStunned()
                && !this.isPlayingIntro();
    }

    @Override
    public boolean isStaying() {
        return super.isStaying() || (this.getFirstPassenger() instanceof IServant servant && servant.isStaying());
    }

    public boolean isPlayingIntro(){
        return this.isPlayingIntro;
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.hasPassenger()
                    && ((rider instanceof Player && !this.isAutonomous())
                    || (rider instanceof IServant servant && (servant.isStaying() || servant.isCommanded() || servant.isPatrolling())))
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

                if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
                    float f2 = rider.xxa * 0.5F;
                    float f3 = rider.zza;
                    if (f3 <= 0.0F) {
                        f3 *= 0.25F;
                    }

                    Vec3 vec3 = new Vec3(f2, 0.0D, f3);
                    this.executeRidersJump(this.playerJumpPendingScale, vec3);
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;

                if (this.onGround) {
                    this.playerJumpPendingScale = 0.0F;
                    this.setIsJumping(false);
                }

                this.calculateEntityAnimation(this, false);
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
        } else if (pId == 8){
            this.setCrazy(false);
        } else if (pId == 9){
            this.isPlayingIntro = true;
        } else if (pId == 10){
            this.isPlayingIntro = false;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!pPlayer.level.isClientSide) {
            if (pPlayer == this.getTrueOwner() && !this.isBurrowed() && !this.isPlayingIntro()) {
                if (!pPlayer.isCrouching() && !this.isStunned()) {
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
                        this.level.addFreshEntity(bag);
                        this.item = bag;
                    }
                    this.stopAttacking();

                    this.isPlayingIntro = true;
                    this.setAnimationState(2);
                    this.phaseTicks = 1;
                    this.level.broadcastEntityEvent(this, (byte) 9);
                    this.level.broadcastEntityEvent(this, (byte) 6);
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
            this.hurt(DamageSource.STARVE, Float.MAX_VALUE);
        }
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setIsJumping(boolean jumping) {
        this.isJumping = jumping;
    }

    protected void executeRidersJump(float p_248808_, Vec3 p_275435_) {
        double d0 = 1.0D * (double)p_248808_ * (double)this.getBlockJumpFactor();
        double d1 = d0 + this.getJumpBoostPower();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d1, vec3.z);
        this.setIsJumping(true);
        this.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
        if (p_275435_.z > 0.0D) {
            float f = Mth.sin(this.getYRot() * ((float)Math.PI / 180F));
            float f1 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
            this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * f * p_248808_, 0.0D, 0.4F * f1 * p_248808_));
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
        return this.notClientAttacking() && !this.isPlayingIntro() && !this.isBurrowed();
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
            return RagnoServant.this.isStunned();
        }

        public void start() {
            RagnoServant.this.setAnimationState(10);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.stunTick <= 100;
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
            RagnoServant.this.attackTicks = 0;
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.attackCooldown = 40;
            RagnoServant.this.attacksUsed = 0;
            RagnoServant.this.stunTick = 0;
            RagnoServant.this.setStunned(false);
        }
    }

    class WebGoal extends Goal {
        public WebGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && RagnoServant.this.random.nextInt(16) == 0
                    && RagnoServant.this.webCooldown < 1;
        }

        public void start() {
            RagnoServant.this.setAnimationState(4);
            RagnoServant.this.setAttackType(RagnoServant.this.WEB_ATTACK);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.attackTicks <= 17
                    && RagnoServant.this.getAttackType() == RagnoServant.this.WEB_ATTACK;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            if (!RagnoServant.this.isPlayingIntro()) {
                RagnoServant.this.setAnimationState(0);
            }

            RagnoServant.this.attackTicks = 0;
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.webCooldown = 200;
            RagnoServant.this.attackCooldown = 20;
            if (RagnoServant.this.isCrazy() || !RagnoServant.this.hasPassenger()) {
                ++RagnoServant.this.attacksUsed;
            }

        }
    }

    class LeapGoal extends Goal {
        public LeapGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && RagnoServant.this.random.nextInt(16) == 0
                    && RagnoServant.this.leapCooldown < 1
                    && RagnoServant.this.getTarget() != null
                    && !RagnoServant.this.isStaying()
                    && (double)RagnoServant.this.distanceTo(RagnoServant.this.getTarget()) < 12.0;
        }

        public void start() {
            RagnoServant.this.playSound(IllageAndSpillageSoundEvents.ENTITY_RAGNO_PREPARECHARGE.get(), 2.0F, 1.0F);
            RagnoServant.this.setAnimationState(5);
            RagnoServant.this.setAttackType(RagnoServant.this.LEAP_ATTACK);
        }

        public boolean canContinueToUse() {
            return (RagnoServant.this.attackTicks <= 40 || !RagnoServant.this.isOnGround()) && RagnoServant.this.getAttackType() == RagnoServant.this.LEAP_ATTACK;
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            if (!RagnoServant.this.isPlayingIntro()) {
                RagnoServant.this.setAnimationState(0);
            }

            RagnoServant.this.attackTicks = 0;
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.shouldHurtOnTouch = false;
            RagnoServant.this.leapCooldown = 100;
            RagnoServant.this.attackCooldown = 20;
            if (RagnoServant.this.isCrazy() || !RagnoServant.this.hasPassenger()) {
                ++RagnoServant.this.attacksUsed;
            }

        }
    }

    class BurrowGoal extends Goal {
        public BurrowGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && RagnoServant.this.random.nextInt(16) == 0
                    && RagnoServant.this.burrowCooldown < 1
                    && !RagnoServant.this.isStaying()
                    && !RagnoServant.this.hasPassenger();
        }

        public void start() {
            RagnoServant.this.setAnimationState(6);
            RagnoServant.this.setAttackType(RagnoServant.this.BURROW_ATTACK);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.attackTicks <= 138;
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
            RagnoServant.this.attackTicks = 0;
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.burrowCooldown = 160;
            RagnoServant.this.attackCooldown = 20;
            ++RagnoServant.this.attacksUsed;
            RagnoServant.this.setInvisible(false);
            RagnoServant.this.isBurrowing = false;
        }
    }

    class ChargeGoal extends Goal {
        public ChargeGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && RagnoServant.this.random.nextInt(16) == 0
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
            return RagnoServant.this.attackTicks <= 70;
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
            RagnoServant.this.attackTicks = 0;
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.chargeCooldown = 160;
            RagnoServant.this.attackCooldown = 20;
            ++RagnoServant.this.attacksUsed;
        }
    }

    class CoughGoal extends Goal {
        public CoughGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return RagnoServant.this.doesAttackMeetNormalRequirements()
                    && RagnoServant.this.random.nextInt(16) == 0
                    && RagnoServant.this.coughCooldown < 1
                    && RagnoServant.this.isCrazy();
        }

        public void start() {
            RagnoServant.this.setAnimationState(9);
            RagnoServant.this.setAttackType(RagnoServant.this.COUGH_ATTACK);
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.attackTicks <= 70;
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
            RagnoServant.this.attackTicks = 0;
            RagnoServant.this.setAttackType(0);
            RagnoServant.this.coughCooldown = 200;
            RagnoServant.this.attackCooldown = 20;
            ++RagnoServant.this.attacksUsed;
        }
    }

    class AlwaysWatchTargetGoal extends Goal {
        public AlwaysWatchTargetGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public boolean canUse() {
            return RagnoServant.this.isPlayingIntro();
        }

        public boolean canContinueToUse() {
            return RagnoServant.this.isPlayingIntro();
        }

        public void tick() {
            RagnoServant.this.getNavigation().stop();
            RagnoServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (RagnoServant.this.getTarget() != null) {
                RagnoServant.this.getLookControl().setLookAt(RagnoServant.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            RagnoServant.this.isPlayingIntro = false;
            RagnoServant.this.level.broadcastEntityEvent(RagnoServant.this, (byte) 10);
            RagnoServant.this.setAnimationState(0);
        }
    }
}
