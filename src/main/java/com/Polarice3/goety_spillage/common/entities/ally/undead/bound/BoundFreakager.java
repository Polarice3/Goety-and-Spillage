package com.Polarice3.goety_spillage.common.entities.ally.undead.bound;

import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.RagnoServant;
import com.Polarice3.goety_spillage.common.entities.projectiles.*;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.entities.TrickOrTreatEntity;
import com.yellowbrossproductions.illageandspillage.init.ModEntityTypes;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import com.yellowbrossproductions.illageandspillage.util.ItemRegisterer;
import com.yellowbrossproductions.illageandspillage.util.PotionRegisterer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class BoundFreakager extends AbstractBoundIllager implements ICanBeAnimated {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(BoundFreakager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOW_ARMS = SynchedEntityData.defineId(BoundFreakager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SCYTHE = SynchedEntityData.defineId(BoundFreakager.class, EntityDataSerializers.BOOLEAN);
    public AnimationState bombsAnimationState = new AnimationState();
    public AnimationState axesAnimationState = new AnimationState();
    public AnimationState fastaxesAnimationState = new AnimationState();
    public AnimationState potionsAnimationState = new AnimationState();
    public AnimationState scytheAnimationState = new AnimationState();
    public AnimationState trickortreatAnimationState = new AnimationState();
    private int attackType;
    private int attackTicks;
    private int attackCooldown;
    private final int BOMBS_ATTACK = 1;
    private final int AXES_ATTACK = 2;
    private final int FAST_AXES_ATTACK = 3;
    private final int POTIONS_ATTACK = 4;
    private final int SCYTHE_ATTACK = 5;
    private final int TOT_ATTACK = 6;
    private int bombsCooldown;
    private int axesCooldown;
    private int potionsCooldown;
    private int scytheCooldown;
    private int trickOrTreatCooldown;
    private double potionThrowDistance;
    public boolean waitingForScythe;
    private final List<TrickOrTreatEntity> treats = new ArrayList<>();

    public BoundFreakager(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TrickOrTreatGoal());
        this.goalSelector.addGoal(0, new ScytheGoal());
        this.goalSelector.addGoal(0, new PotionsGoal());
        this.goalSelector.addGoal(0, new FastAxesGoal());
        this.goalSelector.addGoal(0, new AxesGoal());
        this.goalSelector.addGoal(0, new ThrowBombsGoal());
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AlwaysWatchTargetGoal());
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.MAX_HEALTH, GSAttributesConfig.BoundFreakagerHealth.get())
                .add(Attributes.ATTACK_DAMAGE, GSAttributesConfig.BoundFreakagerDamage.get())
                .add(Attributes.ARMOR, GSAttributesConfig.BoundFreakagerArmor.get())
                .add(Attributes.FOLLOW_RANGE, 50.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), GSAttributesConfig.BoundFreakagerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), GSAttributesConfig.BoundFreakagerDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), GSAttributesConfig.BoundFreakagerArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(SHOW_ARMS, false);
        this.entityData.define(SCYTHE, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("AttackCoolDown", this.attackCooldown);
        pCompound.putInt("BombsCoolDown", this.bombsCooldown);
        pCompound.putInt("PotionsCoolDown", this.potionsCooldown);
        pCompound.putInt("ScytheCoolDown", this.scytheCooldown);
        pCompound.putInt("ToTCoolDown", this.trickOrTreatCooldown);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("AttackCoolDown")) {
            this.attackCooldown = pCompound.getInt("AttackCoolDown");
        }
        if (pCompound.contains("BombsCoolDown")) {
            this.bombsCooldown = pCompound.getInt("BombsCoolDown");
        }
        if (pCompound.contains("PotionsCoolDown")) {
            this.potionsCooldown = pCompound.getInt("PotionsCoolDown");
        }
        if (pCompound.contains("ScytheCoolDown")) {
            this.scytheCooldown = pCompound.getInt("ScytheCoolDown");
        }
        if (pCompound.contains("ToTCoolDown")) {
            this.trickOrTreatCooldown = pCompound.getInt("ToTCoolDown");
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ANIMATION_STATE.equals(p_21104_) && this.level.isClientSide) {
            switch (this.entityData.get(ANIMATION_STATE)) {
                case 0, 1, 2:
                    this.stopAllAnimationStates();
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.bombsAnimationState.start(this.tickCount);
                    break;
                case 4:
                    this.stopAllAnimationStates();
                    this.axesAnimationState.start(this.tickCount);
                    break;
                case 5:
                    this.stopAllAnimationStates();
                    this.fastaxesAnimationState.start(this.tickCount);
                    break;
                case 6:
                    this.stopAllAnimationStates();
                    this.potionsAnimationState.start(this.tickCount);
                    break;
                case 7:
                    this.stopAllAnimationStates();
                    this.scytheAnimationState.start(this.tickCount);
                    break;
                case 8:
                    this.stopAllAnimationStates();
                    this.trickortreatAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    public void stopAllAnimationStates() {
        this.bombsAnimationState.stop();
        this.axesAnimationState.stop();
        this.fastaxesAnimationState.stop();
        this.potionsAnimationState.stop();
        this.scytheAnimationState.stop();
        this.trickortreatAnimationState.stop();
    }

    public float getVoicePitch() {
        return 0.45F;
    }

    public int xpReward() {
        return 20;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5), this.getY() + 0.5, this.getRandomZ(0.5), (0.5 - this.random.nextDouble()) * 0.15, 0.009999999776482582, (0.5 - this.random.nextDouble()) * 0.15);
            }
        }
        if (this.attackType > 0) {
            ++this.attackTicks;
        }

        if (this.attackCooldown > 0) {
            --this.attackCooldown;
        }

        if (this.attackType < 1) {
            if (this.bombsCooldown > 0) {
                --this.bombsCooldown;
            }

            if (this.axesCooldown > 0) {
                --this.axesCooldown;
            }

            if (this.potionsCooldown > 0) {
                --this.potionsCooldown;
            }

            if (this.scytheCooldown > 0) {
                --this.scytheCooldown;
            }

            if (this.trickOrTreatCooldown > 0) {
                --this.trickOrTreatCooldown;
            }
        }
        this.updateTreatList();
        this.distractAttackers();
        this.attackAI();
        if (this.getTarget() != null
                && !this.isPassenger()
                && !this.isStaying()
                && !this.isCommanded()
                && this.isNotAttacking()){
            if (this.getTarget().distanceTo(this) >= 10.0D){
                this.getNavigation().moveTo(this.getTarget(), 1.0F);
            }
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    public void attackAI(){
        if (this.isAlive()) {
            if (this.attackType == this.BOMBS_ATTACK && this.attackTicks == 20) {
                double throwSpeed = 0.7D;
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_CYMBAL.get(), 1.0F, 1.0F);

                for(int i = 0; i < 4; ++i) {
                    if (this.getHealth() < this.getMaxHealth() / 2.0F) {
                        GSSkullBomb bomb = GSEntityTypes.SKULL_BOMB.get().create(this.level);
                        if (bomb != null) {
                            bomb.setPos(this.getX(), this.getY() + 0.25D, this.getZ());
                            bomb.setTrueOwner(this);
                            if (i == 0) {
                                bomb.setDeltaMovement(-throwSpeed, 0.5D, -throwSpeed);
                            } else if (i == 1) {
                                bomb.setDeltaMovement(-throwSpeed, 0.5D, throwSpeed);
                            } else if (i == 2) {
                                bomb.setDeltaMovement(throwSpeed, 0.5D, -throwSpeed);
                            } else {
                                bomb.setDeltaMovement(throwSpeed, 0.53D, throwSpeed);
                            }

                            this.level.addFreshEntity(bomb);
                        }
                    } else {
                        GSPumpkinBomb bomb = GSEntityTypes.PUMPKIN_BOMB.get().create(this.level);
                        if (bomb != null) {
                            bomb.setPos(this.getX(), this.getY() + 0.25D, this.getZ());
                            bomb.setTrueOwner(this);
                            bomb.setTarget(this.getTarget());
                            if (i == 0) {
                                bomb.setDeltaMovement(-throwSpeed, 0.3D, -throwSpeed);
                            } else if (i == 1) {
                                bomb.setDeltaMovement(-throwSpeed, 0.3D, throwSpeed);
                            } else if (i == 2) {
                                bomb.setDeltaMovement(throwSpeed, 0.3D, -throwSpeed);
                            } else {
                                bomb.setDeltaMovement(throwSpeed, 0.3D, throwSpeed);
                            }

                            if (this.getTeam() != null) {
                                this.level.getScoreboard().addPlayerToTeam(bomb.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                            }

                            this.level.addFreshEntity(bomb);
                        }
                    }
                }
            }

            if (this.attackType == this.AXES_ATTACK) {
                if (this.attackTicks % 28 == 0) {
                    this.setAnimationState(0);
                    this.setAnimationState(4);
                }

                if (this.attackTicks % 28 == 2) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_THROW.get(), 2.0F, this.getVoicePitch());
                }

                if (this.getTarget() != null && this.attackTicks % 28 == 6) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (!this.level.isClientSide) {
                        this.throwAxe(true);
                    }
                }

                if (this.attackTicks % 28 == 14) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
                    this.setLeftHanded(true);
                }

                if (this.attackTicks % 28 == 16) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_THROW.get(), 2.0F, this.getVoicePitch());
                }

                if (this.getTarget() != null && this.attackTicks % 28 == 22) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (!this.level.isClientSide) {
                        this.throwAxe();
                    }
                }

                if (this.attackTicks % 28 == 27) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
                    this.setLeftHanded(false);
                }
            }

            if (this.attackType == this.FAST_AXES_ATTACK) {
                if (this.attackTicks % 12 == 0) {
                    this.setAnimationState(0);
                    this.setAnimationState(5);
                }

                if (this.attackTicks % 12 == 1) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_THROW.get(), 2.0F, this.getVoicePitch());
                }

                if (this.getTarget() != null && this.attackTicks % 12 == 3) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (!this.level.isClientSide) {
                        this.throwAxe();
                    }
                }

                if (this.attackTicks % 12 == 6) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
                    this.setLeftHanded(true);
                }

                if (this.attackTicks % 12 == 7) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_THROW.get(), 2.0F, this.getVoicePitch());
                }

                if (this.getTarget() != null && this.attackTicks % 12 == 10) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (!this.level.isClientSide) {
                        this.throwAxe();
                    }
                }

                if (this.attackTicks % 12 == 11) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
                    this.setLeftHanded(false);
                }
            }

            if (this.attackType == this.POTIONS_ATTACK) {
                if (this.attackTicks == 10) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SPIN.get(), 2.0F, 1.0F);
                    this.potionThrowDistance = 0.0D;
                }

                if (this.attackTicks >= 10 && this.attackTicks <= 50) {
                    this.makePotionParticles();
                }

                if (this.attackTicks >= 20 && this.attackTicks <= 50) {
                    this.potionThrowDistance += 0.02D;

                    for(int i = 0; i < 2; ++i) {
                        if (!this.level.isClientSide) {
                            DarkPotion potion = GSEntityTypes.DARK_POTION.get().create(this.level);
                            if (potion != null) {
                                potion.setPos(this.getX(), this.getY() + 2.0D, this.getZ());
                                potion.setOwner(this);
                                potion.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), PotionRegisterer.MUTATION.get()));
                                potion.setXRot(-20.0F);
                                potion.setDeltaMovement((-2.0D + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble()) * (this.potionThrowDistance / 4.0D), 1.0, (-2.0D + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble()) * (this.potionThrowDistance / 4.0D));
                                this.level.addFreshEntity(potion);
                            }
                        }
                    }
                }
            }

            if (this.attackType == this.SCYTHE_ATTACK) {
                if (this.getTarget() != null) {
                    if (this.attackTicks == 14) {
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_THROW.get(), 2.0F, this.getVoicePitch());
                    }

                    if (this.attackTicks == 16) {
                        this.waitingForScythe = true;
                        if (!this.level.isClientSide) {
                            this.setShowScythe(false);
                        }

                        FreakyScythe scythe = GSEntityTypes.FREAKY_SCYTHE.get().create(this.level);
                        if (scythe != null) {
                            scythe.setPos(this.getX(), this.getY() + 1.5D, this.getZ());
                            double x = scythe.getX() - this.getTarget().getX();
                            double y = scythe.getY() - this.getTarget().getY();
                            double z = scythe.getZ() - this.getTarget().getZ();
                            double d = Math.sqrt(x * x + y * y + z * z);
                            float power = 3.0F;
                            double motionX = -(x / d * (double) power * 0.2D);
                            double motionY = -(y / d * (double) power * 0.2D);
                            double motionZ = -(z / d * (double) power * 0.2D);
                            scythe.setAcceleration(motionX, motionY, motionZ);
                            scythe.halfHP = this.getHealth() < this.getMaxHealth() / 2.0F;
                            scythe.setGoFor(this.getTarget());
                            scythe.setShooter(this);
                            scythe.setDamage(GSAttributesConfig.BoundFreakagerScytheDamage.get().floatValue());
                            this.level.addFreshEntity(scythe);
                        }
                    }
                }
            }

            if (this.attackType == this.TOT_ATTACK && this.attackTicks == 21) {
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_CYMBAL.get(), 2.0F, 1.0F);
                int amount = 7;

                for(int i = 0; i < amount; ++i) {
                    TrickOrTreatEntity treat = ModEntityTypes.TrickOrTreat.get().create(this.level);
                    if (treat != null) {
                        treat.circleTime = i * 20;
                        treat.bounceTime = i;
                        treat.setPos(this.getX(), this.getY(), this.getZ());
                        treat.setOwner(this);
                        treat.setTreat(this.random.nextInt(5) + 1);
                        if (this.getTeam() != null) {
                            this.level.getScoreboard().addPlayerToTeam(treat.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                        }

                        this.circleTreat(treat, i, amount);
                        this.level.addFreshEntity(treat);
                        this.treats.add(treat);
                    }
                }
            }
        }
    }

    public void throwAxe(){
        this.throwAxe(false);
    }

    public void throwAxe(boolean first){
        if (this.getTarget() != null) {
            ThrownAxe projectile = GSEntityTypes.THROWN_AXE.get().create(this.level);
            if (projectile != null) {
                projectile.setPos(this.getX(), this.getY() + 1.0D, this.getZ());
                projectile.setYHeadRot(this.getYHeadRot());
                projectile.setYRot(this.getYHeadRot());
                double x = projectile.getX() - this.getTarget().getX();
                double y = projectile.getY() - this.getTarget().getY();
                if (first){
                    y = projectile.getY() - (this.getTarget().getY() + 1.5D);
                }
                double z = projectile.getZ() - this.getTarget().getZ();
                double d = Math.sqrt(x * x + y * y + z * z);
                float power = 3.5F;
                double motionX = -(x / d * (double) power * 0.2D);
                double motionY = -(y / d * (double) power * 0.2D);
                double motionZ = -(z / d * (double) power * 0.2D);
                projectile.setAcceleration(motionX, motionY, motionZ);
                projectile.setShooter(this);
                projectile.setDamage(GSAttributesConfig.BoundFreakagerAxeDamage.get().floatValue());
                this.level.addFreshEntity(projectile);
            }
        }
    }

    public void die(DamageSource p_37847_) {
        if (!this.treats.isEmpty()) {
            for (TrickOrTreatEntity treat : this.treats) {
                treat.kill();
            }
        }

        if (this.getVehicle() instanceof RagnoServant ragno && ragno.isAlive()) {
            ItemEntity bag = EntityType.ITEM.create(this.level);
            if (bag != null) {
                bag.setItem(ItemRegisterer.BAG_OF_HORRORS.get().getDefaultInstance());
                bag.setPos(this.getX(), this.getY(), this.getZ());
                bag.setDeltaMovement(0.0D, 0.6D, 0.0D);
                bag.setNeverPickUp();
                bag.setUnlimitedLifetime();
                bag.noPhysics = true;
                this.level.addFreshEntity(bag);
                ragno.item = bag;
                ragno.goCrazy();
            }
        } else {
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.spawnAtLocation(ItemRegisterer.BAG_OF_HORRORS.get().getDefaultInstance());
            }
        }

        super.die(p_37847_);
    }

    private void circleTreat(Entity entity, int number, int amount) {
        float TAU = 6.2831855F;
        float velocity = 0.5F;
        float yaw = (float)number * (TAU / (float)amount);
        float vy = 0.3F;
        float vx = velocity * Mth.cos(yaw);
        float vz = velocity * Mth.sin(yaw);
        entity.setDeltaMovement(vx, vy, vz);
    }

    public void updateTreatList() {
        if (!this.treats.isEmpty()) {
            for(int i = 0; i < this.treats.size(); ++i) {
                TrickOrTreatEntity clone = this.treats.get(i);
                if (!clone.isAlive()) {
                    this.treats.remove(i);
                    --i;
                }
            }
        }

    }

    public void distractAttackers() {
        if (!this.treats.isEmpty()) {
            List<Mob> list = this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(100.0));

            for (Mob attacker : list) {
                TrickOrTreatEntity treat = this.treats.get(this.random.nextInt(this.treats.size()));
                if (attacker.getLastHurtByMob() == this) {
                    attacker.setLastHurtByMob(treat);
                }

                if (attacker.getTarget() == this) {
                    attacker.setTarget(treat);
                }

                if (attacker instanceof Warden warden) {
                    if (warden.getTarget() == this) {
                        warden.increaseAngerAt(treat, AngerLevel.ANGRY.getMinimumAnger() + 100, false);
                        warden.setAttackTarget(treat);
                    }
                } else {
                    if (attacker.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent() && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() == this) {
                        attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, treat.getUUID(), 600L);
                        attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, treat, 600L);
                    }
                }
            }
        }

    }

    public void makePotionParticles() {
        if (this.level instanceof ServerLevel serverLevel){
            double d0 = (-0.5D + this.random.nextGaussian()) / 4.0D;
            double d1 = (-0.5D + this.random.nextGaussian()) / 4.0D;
            double d2 = (-0.5D + this.random.nextGaussian()) / 4.0D;
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getRandomX(1.0) + (-0.5 + this.random.nextDouble()) * 0.8, this.getRandomY() - (-0.5 + this.random.nextDouble()) * 0.4, this.getRandomZ(1.0) + (-0.5 + this.random.nextDouble()) * 0.8, 0, d0, d1, d2, 0.5F);
        }
    }

    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        return p_37849_ != DamageSource.IN_WALL && super.hurt(p_37849_, p_37850_);
    }

    protected SoundEvent getAmbientSound() {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_DEATH.get();
    }

    public void setAnimationState(int input) {
        this.entityData.set(ANIMATION_STATE, input);
    }

    public AnimationState getAnimationState(String input) {
        if (Objects.equals(input, "bombs")) {
            return this.bombsAnimationState;
        } else if (Objects.equals(input, "axes")) {
            return this.axesAnimationState;
        } else if (Objects.equals(input, "fastaxes")) {
            return this.fastaxesAnimationState;
        } else if (Objects.equals(input, "potions")) {
            return this.potionsAnimationState;
        } else if (Objects.equals(input, "scythe")) {
            return this.scytheAnimationState;
        } else {
            return Objects.equals(input, "trickortreat") ? this.trickortreatAnimationState : new AnimationState();
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_213386_1_, DifficultyInstance p_213386_2_, MobSpawnType p_213386_3_, @Nullable SpawnGroupData p_213386_4_, @Nullable CompoundTag p_213386_5_) {
        RandomSource randomsource = p_213386_1_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_213386_2_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_213386_2_);
        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance p_180481_1_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public boolean shouldShowArms() {
        return this.entityData.get(SHOW_ARMS);
    }

    public void setShowArms(boolean showArms) {
        this.entityData.set(SHOW_ARMS, showArms);
    }

    public boolean shouldShowScythe() {
        return this.entityData.get(SCYTHE);
    }

    public void setShowScythe(boolean scythe) {
        this.entityData.set(SCYTHE, scythe);
    }

    public boolean isNotAttacking(){
        return this.attackType == 0;
    }

    public boolean doesAttackMeetNormalRequirements() {
        return this.isNotAttacking() && this.getTarget() != null && this.hasLineOfSight(this.getTarget()) && this.attackCooldown < 1;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    class TrickOrTreatGoal extends Goal {
        public TrickOrTreatGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return BoundFreakager.this.doesAttackMeetNormalRequirements()
                    && BoundFreakager.this.random.nextInt(16) == 0
                    && BoundFreakager.this.trickOrTreatCooldown < 1
                    && BoundFreakager.this.getHealth() < BoundFreakager.this.getMaxHealth();
        }

        public void start() {
            BoundFreakager.this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_TRICKORTREAT.get(), 2.0F, BoundFreakager.this.getVoicePitch());
            BoundFreakager.this.setAnimationState(8);
            BoundFreakager.this.attackType = BoundFreakager.this.TOT_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 30;
        }

        public void tick() {
            BoundFreakager.this.getNavigation().stop();
            if (BoundFreakager.this.getTarget() != null) {
                BoundFreakager.this.getLookControl().setLookAt(BoundFreakager.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            BoundFreakager.this.attackTicks = 0;
            BoundFreakager.this.attackType = 0;
            BoundFreakager.this.setAnimationState(0);
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(false);
            }

            BoundFreakager.this.trickOrTreatCooldown = 900;
            BoundFreakager.this.attackCooldown = 100;
        }
    }

    class ScytheGoal extends Goal {
        public ScytheGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return BoundFreakager.this.doesAttackMeetNormalRequirements()
                    && BoundFreakager.this.random.nextInt(16) == 0
                    && BoundFreakager.this.scytheCooldown < 1;
        }

        public void start() {
            BoundFreakager.this.setAnimationState(7);
            BoundFreakager.this.attackType = BoundFreakager.this.SCYTHE_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowScythe(true);
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 24 || BoundFreakager.this.waitingForScythe;
        }

        public void tick() {
            BoundFreakager.this.getNavigation().stop();
            if (BoundFreakager.this.getTarget() != null) {
                BoundFreakager.this.getLookControl().setLookAt(BoundFreakager.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            BoundFreakager.this.attackTicks = 0;
            BoundFreakager.this.attackType = 0;
            BoundFreakager.this.setAnimationState(0);
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(false);
            }

            BoundFreakager.this.scytheCooldown = 200;
            BoundFreakager.this.attackCooldown = 100;
        }
    }

    class PotionsGoal extends Goal {
        public PotionsGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return BoundFreakager.this.doesAttackMeetNormalRequirements()
                    && BoundFreakager.this.random.nextInt(16) == 0
                    && BlockFinder.emptySquareSpace(BoundFreakager.this.level, BoundFreakager.this.blockPosition(), 13, true)
                    && BoundFreakager.this.potionsCooldown < 1;
        }

        public void start() {
            BoundFreakager.this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_REVEAL.get(), 2.0F, BoundFreakager.this.getVoicePitch());
            BoundFreakager.this.setAnimationState(6);
            BoundFreakager.this.attackType = BoundFreakager.this.POTIONS_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 60;
        }

        public void tick() {
            BoundFreakager.this.getNavigation().stop();
            if (BoundFreakager.this.getTarget() != null) {
                BoundFreakager.this.getLookControl().setLookAt(BoundFreakager.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            BoundFreakager.this.attackTicks = 0;
            BoundFreakager.this.attackType = 0;
            BoundFreakager.this.setAnimationState(0);
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(false);
            }

            BoundFreakager.this.potionsCooldown = 200;
            BoundFreakager.this.attackCooldown = 100;
        }
    }

    class FastAxesGoal extends Goal {
        public FastAxesGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return BoundFreakager.this.doesAttackMeetNormalRequirements()
                    && BoundFreakager.this.random.nextInt(16) == 0
                    && BoundFreakager.this.axesCooldown < 1
                    && BoundFreakager.this.getHealth() < BoundFreakager.this.getMaxHealth() / 2.0F;
        }

        public void start() {
            BoundFreakager.this.setAnimationState(5);
            BoundFreakager.this.attackType = BoundFreakager.this.FAST_AXES_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 60;
        }

        public void tick() {
            BoundFreakager.this.getNavigation().stop();
            if (BoundFreakager.this.getTarget() != null) {
                BoundFreakager.this.getLookControl().setLookAt(BoundFreakager.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            BoundFreakager.this.attackTicks = 0;
            BoundFreakager.this.attackType = 0;
            BoundFreakager.this.setAnimationState(0);
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(false);
            }

            BoundFreakager.this.axesCooldown = 200;
            BoundFreakager.this.attackCooldown = 100;
        }
    }

    class AxesGoal extends Goal {
        public AxesGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return BoundFreakager.this.doesAttackMeetNormalRequirements()
                    && BoundFreakager.this.random.nextInt(16) == 0
                    && BoundFreakager.this.axesCooldown < 1
                    && BoundFreakager.this.getHealth() >= BoundFreakager.this.getMaxHealth() / 2.0F;
        }

        public void start() {
            BoundFreakager.this.setAnimationState(4);
            BoundFreakager.this.attackType = BoundFreakager.this.AXES_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 82;
        }

        public void tick() {
            BoundFreakager.this.getNavigation().stop();
            if (BoundFreakager.this.getTarget() != null) {
                BoundFreakager.this.getLookControl().setLookAt(BoundFreakager.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            BoundFreakager.this.attackTicks = 0;
            BoundFreakager.this.attackType = 0;
            BoundFreakager.this.setAnimationState(0);
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(false);
            }

            BoundFreakager.this.axesCooldown = 200;
            BoundFreakager.this.attackCooldown = 100;
        }
    }

    class ThrowBombsGoal extends Goal {
        public ThrowBombsGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return BoundFreakager.this.doesAttackMeetNormalRequirements()
                    && BoundFreakager.this.random.nextInt(16) == 0
                    && BoundFreakager.this.bombsCooldown < 1;
        }

        public void start() {
            BoundFreakager.this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_PUMPKINBOMBS.get(), 2.0F, BoundFreakager.this.getVoicePitch());
            BoundFreakager.this.setAnimationState(3);
            BoundFreakager.this.attackType = BoundFreakager.this.BOMBS_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 30;
        }

        public void tick() {
            BoundFreakager.this.getNavigation().stop();
            if (BoundFreakager.this.getTarget() != null) {
                BoundFreakager.this.getLookControl().setLookAt(BoundFreakager.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            BoundFreakager.this.attackTicks = 0;
            BoundFreakager.this.attackType = 0;
            BoundFreakager.this.setAnimationState(0);
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(false);
            }

            BoundFreakager.this.bombsCooldown = 200;
            BoundFreakager.this.attackCooldown = 100;
        }
    }

    class AlwaysWatchTargetGoal extends Goal {
        public AlwaysWatchTargetGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public boolean canUse() {
            return BoundFreakager.this.getTarget() != null && !BoundFreakager.this.isCommanded();
        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.getTarget() != null && !BoundFreakager.this.isCommanded();
        }

        public void tick() {
            BoundFreakager.this.getNavigation().stop();
            if (BoundFreakager.this.getTarget() != null) {
                BoundFreakager.this.getLookControl().setLookAt(BoundFreakager.this.getTarget(), 100.0F, 100.0F);
            }
        }
    }

    class NoveltyGoal extends Goal {
        public NoveltyGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public boolean canUse() {
            return BoundFreakager.this.getTarget() == null
                    && BoundFreakager.this.isNotAttacking()
                    && !BoundFreakager.this.isCommanded()
                    && BoundFreakager.this.getTrueOwner() != null
                    && BoundFreakager.this.getTrueOwner().distanceTo(BoundFreakager.this) <= 16.0F
                    && BoundFreakager.this.hasLineOfSight(BoundFreakager.this.getTrueOwner())
                    && !BoundFreakager.this.getTrueOwner().hasLineOfSight(BoundFreakager.this);
        }

        public void tick() {
            BoundFreakager.this.getNavigation().stop();
            if (BoundFreakager.this.getTrueOwner() != null) {
                BoundFreakager.this.getLookControl().setLookAt(BoundFreakager.this.getTrueOwner(), 100.0F, 100.0F);
            }
        }
    }
}
