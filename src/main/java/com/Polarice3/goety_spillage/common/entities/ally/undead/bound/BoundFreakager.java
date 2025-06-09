package com.Polarice3.goety_spillage.common.entities.ally.undead.bound;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.Polarice3.goety_spillage.common.entities.ally.GSEyesore;
import com.Polarice3.goety_spillage.common.entities.ally.GSTot;
import com.Polarice3.goety_spillage.common.entities.ally.illager.RagnoServant;
import com.Polarice3.goety_spillage.common.entities.ally.undead.GSFunnybone;
import com.Polarice3.goety_spillage.common.entities.projectiles.*;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.Polarice3.goety_spillage.config.GSMobsConfig;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class BoundFreakager extends AbstractBoundIllager implements ICanBeAnimated {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(BoundFreakager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOW_ARMS = SynchedEntityData.defineId(BoundFreakager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SCYTHE = SynchedEntityData.defineId(BoundFreakager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FREAKAGER_FACE = SynchedEntityData.defineId(BoundFreakager.class, EntityDataSerializers.INT);
    public AnimationState laughAnimationState = new AnimationState();
    public AnimationState bombsAnimationState = new AnimationState();
    public AnimationState minionsAnimationState = new AnimationState();
    public AnimationState intro1AnimationState = new AnimationState();
    public AnimationState intro2AnimationState = new AnimationState();
    public AnimationState intro3AnimationState = new AnimationState();
    public AnimationState axesStartAnimationState = new AnimationState();
    public AnimationState axesNormalAnimationState = new AnimationState();
    public AnimationState angryAxesAnimationState = new AnimationState();
    public AnimationState potionsAnimationState = new AnimationState();
    public AnimationState scytheAnimationState = new AnimationState();
    public AnimationState catchAnimationState = new AnimationState();
    public AnimationState trickortreatAnimationState = new AnimationState();
    public AnimationState phaseAnimationState = new AnimationState();
    private int attackType;
    private int attackTicks;
    private int attackCooldown;
    private static final int BOMBS_ATTACK = 1;
    private static final int AXES_ATTACK = 2;
    private static final int ANGRY_AXES_ATTACK = 3;
    private static final int POTIONS_ATTACK = 4;
    private static final int SCYTHE_ATTACK = 5;
    private static final int TRICKORTREAT_ATTACK = 6;
    private static final int MINIONS_ATTACK = 7;
    private int bombsCooldown;
    private int minionsCooldown;
    private int axesCooldown;
    private int potionsCooldown;
    private int scytheCooldown;
    private int trickOrTreatCooldown;
    private double potionThrowDistance;
    public boolean waitingForScythe;
    public int catchTicks;
    private final List<GSTot> treats = new ArrayList<>();

    public BoundFreakager(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TrickOrTreatGoal());
        this.goalSelector.addGoal(0, new ScytheGoal());
        this.goalSelector.addGoal(0, new PotionsGoal());
        this.goalSelector.addGoal(0, new AngryAxesGoal());
        this.goalSelector.addGoal(0, new AxesGoal());
        this.goalSelector.addGoal(0, new ThrowBombsGoal());
        this.goalSelector.addGoal(0, new ThrowMinionsGoal());
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
        this.entityData.define(FREAKAGER_FACE, 0);
        this.entityData.define(SHOW_ARMS, false);
        this.entityData.define(SCYTHE, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("AttackCoolDown", this.attackCooldown);
        pCompound.putInt("MinionsCoolDown", this.minionsCooldown);
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
        if (pCompound.contains("MinionsCoolDown")) {
            this.minionsCooldown = pCompound.getInt("MinionsCoolDown");
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

    @Override
    protected boolean isSunSensitive() {
        return GSMobsConfig.BoundFreakagerSun.get();
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return effectInstance.getEffect() != EffectRegisterer.MUTATION.get() && super.canBeAffected(effectInstance);
    }

    public boolean halfHealth() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    public boolean hasFewEnoughMinions() {
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(100.0), (predicate) -> {
            return predicate.isAlive() && predicate instanceof IServant servant && (servant instanceof GSEyesore || servant instanceof GSFunnybone) && servant.getTrueOwner() == this;
        });
        return list.size() < 3;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ANIMATION_STATE.equals(p_21104_) && this.level().isClientSide) {
            switch (this.entityData.get(ANIMATION_STATE)) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.minionsAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.laughAnimationState.start(this.tickCount);
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.bombsAnimationState.start(this.tickCount);
                    break;
                case 4:
                    this.stopAllAnimationStates();
                    this.axesNormalAnimationState.start(this.tickCount);
                    break;
                case 5:
                    this.stopAllAnimationStates();
                    this.angryAxesAnimationState.start(this.tickCount);
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
                    break;
                case 9:
                    this.stopAllAnimationStates();
                    this.intro1AnimationState.start(this.tickCount);
                    break;
                case 10:
                    this.stopAllAnimationStates();
                    this.intro2AnimationState.start(this.tickCount);
                    break;
                case 11:
                    this.stopAllAnimationStates();
                    this.intro3AnimationState.start(this.tickCount);
                    break;
                case 12:
                    this.stopAllAnimationStates();
                    this.axesStartAnimationState.start(this.tickCount);
                    break;
                case 13:
                    this.stopAllAnimationStates();
                    this.catchAnimationState.start(this.tickCount);
                    break;
                case 14:
                    this.stopAllAnimationStates();
                    this.phaseAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    public void stopAllAnimationStates() {
        this.intro1AnimationState.stop();
        this.intro2AnimationState.stop();
        this.intro3AnimationState.stop();
        this.laughAnimationState.stop();
        this.bombsAnimationState.stop();
        this.minionsAnimationState.stop();
        this.axesStartAnimationState.stop();
        this.axesNormalAnimationState.stop();
        this.angryAxesAnimationState.stop();
        this.potionsAnimationState.stop();
        this.scytheAnimationState.stop();
        this.catchAnimationState.stop();
        this.trickortreatAnimationState.stop();
        this.phaseAnimationState.stop();
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

            if (this.minionsCooldown > 0 && this.hasFewEnoughMinions()) {
                --this.minionsCooldown;
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

        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();

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
            if (this.attackType == BOMBS_ATTACK) {
                if (this.attackTicks == 20) {
                    double y = 0.7;
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_CYMBAL.get(), 1.0F, 1.0F);
                    this.setFreakagerFace(1);

                    for(int i = 0; i < 4; ++i) {
                        if (this.halfHealth()) {
                            GSSkullBomb s1 = GSEntityTypes.SKULL_BOMB.get().create(this.level);
                            if (s1 != null) {
                                s1.setPos(this.getX(), this.getY() + 0.25, this.getZ());
                                s1.setTrueOwner(this);
                                if (i == 0) {
                                    s1.setDeltaMovement(-y, 0.5, -y);
                                } else if (i == 1) {
                                    s1.setDeltaMovement(-y, 0.5, y);
                                } else if (i == 2) {
                                    s1.setDeltaMovement(y, 0.5, -y);
                                } else {
                                    s1.setDeltaMovement(y, 0.53, y);
                                }
                                this.level.addFreshEntity(s1);
                            }
                        } else {
                            GSPumpkinBomb s1 = GSEntityTypes.PUMPKIN_BOMB.get().create(this.level);
                            if (s1 != null) {
                                s1.setPos(this.getX(), this.getY() + 0.25, this.getZ());
                                s1.setTrueOwner(this);
                                s1.setTarget(this.getTarget());
                                if (i == 0) {
                                    s1.setDeltaMovement(-y, 0.3, -y);
                                } else if (i == 1) {
                                    s1.setDeltaMovement(-y, 0.3, y);
                                } else if (i == 2) {
                                    s1.setDeltaMovement(y, 0.3, -y);
                                } else {
                                    s1.setDeltaMovement(y, 0.3, y);
                                }

                                if (this.getTeam() != null) {
                                    this.level.getScoreboard().addPlayerToTeam(s1.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                                }

                                this.level.addFreshEntity(s1);
                            }
                        }
                    }
                }

                if (this.attackTicks == 30) {
                    this.setFreakagerFace(0);
                }
            }

            if (this.attackType == MINIONS_ATTACK) {
                if (this.attackTicks == 10) {
                    this.setFreakagerFace(1);
                }

                if (this.attackTicks == 25) {
                    this.setFreakagerFace(0);
                }

                if (this.attackTicks == 40) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_CYMBAL.get(), 1.0F, 1.0F);
                    this.setFreakagerFace(1);

                    for(int i = 0; i < 5; ++i) {
                        if (this.halfHealth()) {
                            GSEyesore entity = GSEntityTypes.EYESORE.get().create(this.level);
                            if (entity != null) {
                                entity.setPos(this.getX(), this.getY() + 0.25, this.getZ());
                                entity.setTrueOwner(this);
                                entity.setTarget(this.getTarget());
                                entity.setFlying(true);
                                entity.setDeltaMovement((double) (-2 + this.random.nextInt(5)) * 0.4, 0.6, (double) (-2 + this.random.nextInt(5)) * 0.4);
                                if (this.getTeam() != null) {
                                    this.level.getScoreboard().addPlayerToTeam(entity.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                                }

                                this.level.addFreshEntity(entity);
                            }
                        } else {
                            GSFunnybone entity = GSEntityTypes.FUNNYBONE.get().create(this.level);
                            if (entity != null) {
                                entity.setPos(this.getX(), this.getY() + 0.25, this.getZ());
                                entity.setTrueOwner(this);
                                entity.setTarget(this.getTarget());
                                entity.setFlying(true);
                                entity.setDeltaMovement((double) (-2 + this.random.nextInt(5)) * 0.4, 0.6, (double) (-2 + this.random.nextInt(5)) * 0.4);
                                if (this.getTeam() != null) {
                                    this.level.getScoreboard().addPlayerToTeam(entity.getStringUUID(), this.level.getScoreboard().getPlayerTeam(this.getTeam().getName()));
                                }

                                this.level.addFreshEntity(entity);
                            }
                        }
                    }
                }

                if (this.attackTicks == 45) {
                    this.setFreakagerFace(0);
                }
            }

            if (this.attackType == AXES_ATTACK) {
                if (this.attackTicks == 4) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
                    this.setItemSlot(EquipmentSlot.OFFHAND, Items.IRON_AXE.getDefaultInstance());
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_AXE_DRAW.get(), 2.0F, 1.0F);
                    this.setFreakagerFace(1);
                }

                if (this.attackTicks >= 21) {
                    this.setFreakagerFace(0);
                    int trueAttackTicks = this.attackTicks - 21;
                    if (trueAttackTicks % 26 == 0) {
                        this.setAnimationState(0);
                        this.setAnimationState(4);
                    }

                    if (trueAttackTicks % 26 == 0 || trueAttackTicks % 26 == 12) {
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_THROW.get(), 2.0F, this.getVoicePitch());
                    }

                    if (trueAttackTicks % 26 == 0 || trueAttackTicks % 26 == 12) {
                        if (trueAttackTicks % 26 == 12) {
                            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        } else {
                            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                        }

                        if (!this.level.isClientSide && this.getTarget() != null) {
                            double x = this.getX() - this.getTarget().getX();
                            double y = this.getY() + 1.0 - (this.getTarget().getY() + (double)(this.getTarget().getEyeHeight() / 2.0F));
                            double z = this.getZ() - this.getTarget().getZ();
                            ThrownAxe projectile = new ThrownAxe(this.level, this, -x, -y, -z);
                            projectile.moveTo(this.getX(), this.getY() + 1.0, this.getZ());
                            projectile.setRot(this);
                            projectile.setOwner(this);
                            projectile.setDamage(GSAttributesConfig.BoundFreakagerAxeDamage.get().floatValue());
                            this.level.addFreshEntity(projectile);
                        }
                    }

                    if (trueAttackTicks % 26 == 6 || trueAttackTicks % 26 == 18) {
                        if (trueAttackTicks % 26 == 6) {
                            this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
                        } else {
                            this.setItemSlot(EquipmentSlot.OFFHAND, Items.IRON_AXE.getDefaultInstance());
                        }
                    }
                }
            }
            if (this.attackType == ANGRY_AXES_ATTACK) {
                if (this.attackTicks == 4) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
                    this.setItemSlot(EquipmentSlot.OFFHAND, Items.IRON_AXE.getDefaultInstance());
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_AXE_DRAW.get(), 2.0F, 1.0F);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_ANGRYAXES.get(), 4.0F, this.getVoicePitch());
                    this.setFreakagerFace(3);
                }

                if (this.attackTicks >= 40 && this.attackTicks < 96) {
                    this.setFreakagerFace(4);
                    if ((this.attackTicks - 40) % 7 == 0) {
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_VILLAGERWAVE.get(), 3.0F, this.getVoicePitch());
                    }

                    if (!this.level.isClientSide && this.getTarget() != null) {
                        float f = this.yBodyRot * 0.017453292F * 0.25F;
                        float f1 = Mth.cos(f);
                        float f2 = Mth.sin(f);
                        Vec3 vec3;
                        if (this.attackTicks % 2 == 0) {
                            vec3 = new Vec3(this.getX() + (double)f1 * 0.6, this.getY() + 0.7, this.getZ() + (double)f2 * 0.6);
                        } else {
                            vec3 = new Vec3(this.getX() - (double)f1 * 0.6, this.getY() + 0.7, this.getZ() - (double)f2 * 0.6);
                        }

                        double x = vec3.x - this.getTarget().getX();
                        double y = vec3.y - (this.getTarget().getY() + (double)(this.getTarget().getEyeHeight() / 2.0F));
                        double z = vec3.z - this.getTarget().getZ();
                        ThrownAxe projectile = new ThrownAxe(this.level, this, -x, -y, -z);
                        projectile.moveTo(vec3);
                        projectile.setRot(this);
                        projectile.shoot(-x, -y, -z, 1.0F, 20.0F);
                        projectile.setOwner(this);
                        projectile.setDamage(GSAttributesConfig.BoundFreakagerAxeDamage.get().floatValue());
                        this.level.addFreshEntity(projectile);
                    }
                }

                if (this.attackTicks == 96) {
                    this.setFreakagerFace(3);
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }

                if (this.attackTicks == 110) {
                    this.setFreakagerFace(2);
                }
            }

            if (this.attackType == POTIONS_ATTACK) {
                if (this.attackTicks == 5) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_REVEAL.get(), 2.0F, this.getVoicePitch());
                    this.setFreakagerFace(1);
                }

                if (this.attackTicks == 20) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SPIN.get(), 2.0F, 1.0F);
                    this.setFreakagerFace(0);
                    this.potionThrowDistance = 0.0;
                }

                if (this.attackTicks >= 20 && this.attackTicks <= 60) {
                    this.makePotionParticles();
                    this.potionThrowDistance += 0.02;

                    for(int trueAttackTicks = 0; trueAttackTicks < 2; ++trueAttackTicks) {
                        if (!this.level.isClientSide) {
                            DarkPotion potion = GSEntityTypes.DARK_POTION.get().create(this.level);
                            if (potion != null) {
                                potion.setPos(this.getX(), this.getY() + 2.0, this.getZ());
                                potion.setOwner(this);
                                int lingerChance = this.halfHealth() ? this.getRandom().nextInt(0, 15) : 1;
                                potion.setItem(PotionUtils.setPotion(new ItemStack(lingerChance == 0 ? ItemRegisterer.DARK_LINGER.get() : ItemRegisterer.DARK_SPLASH.get()), PotionRegisterer.MUTATION.get()));
                                potion.setXRot(-20.0F);
                                if (this.halfHealth()) {
                                    potion.shoot(-2.0 + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble(), 5.0 + this.random.nextDouble() + this.random.nextDouble(), -2.0 + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble(), 0.75F, 10.0F);
                                    potion.setDeltaMovement(potion.getDeltaMovement().add(0.0, 0.5, 0.0));
                                } else {
                                    potion.setDeltaMovement((-2.0 + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble()) * (this.potionThrowDistance / 4.0), 1.0, (-2.0 + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble() + this.random.nextDouble()) * (this.potionThrowDistance / 4.0));
                                }

                                this.level.addFreshEntity(potion);
                            }
                        }
                    }
                }
            }

            if (this.attackType == SCYTHE_ATTACK) {
                if (this.attackTicks == 3) {
                    this.setShowScythe(true);
                    this.setFreakagerFace(1);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_AXE_DRAW.get(), 2.0F, 1.0F);
                }

                if (this.attackTicks == 13) {
                    this.setFreakagerFace(0);
                }

                if (this.attackTicks == 21) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_THROW.get(), 2.0F, this.getVoicePitch());
                }

                if (this.attackTicks == 23 && this.getTarget() != null) {
                    this.waitingForScythe = true;
                    if (!this.level.isClientSide) {
                        this.setShowScythe(false);
                    }

                    FreakyScythe scythe = GSEntityTypes.FREAKY_SCYTHE.get().create(this.level);
                    if (scythe != null){
                        scythe.setPos(this.getX(), this.getY() + 1.5, this.getZ());
                        double x = scythe.getX() - this.getTarget().getX();
                        double y = scythe.getY() - (this.getTarget().getY() + 1.0);
                        double z = scythe.getZ() - this.getTarget().getZ();
                        double d = Math.sqrt(x * x + y * y + z * z);
                        float power = 3.0F;
                        double motionX = -(x / d * (double)power * 0.2);
                        double motionY = -(y / d * (double)power * 0.2);
                        double motionZ = -(z / d * (double)power * 0.2);
                        scythe.setAcceleration(motionX, motionY, motionZ);
                        scythe.halfHP = this.halfHealth();
                        scythe.setGoFor(this.getTarget());
                        scythe.setShooter(this);
                        scythe.setDamage(GSAttributesConfig.BoundFreakagerScytheDamage.get().floatValue());
                        this.level.addFreshEntity(scythe);
                    }
                }

                if (this.attackTicks > 23 && !this.waitingForScythe) {
                    if (this.getAnimationState() != 13) {
                        this.setAnimationState(13);
                        this.setShowScythe(true);
                        this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SCYTHE_CATCH.get(), 1.5F, this.getVoicePitch());
                    }

                    ++this.catchTicks;
                }

                if (this.catchTicks == 9) {
                    this.setShowScythe(false);
                }

                if (this.attackTicks >= 314) {
                    this.waitingForScythe = false;
                }
            }

            if (this.attackType == TRICKORTREAT_ATTACK) {
                if (this.attackTicks == 21) {
                    this.setFreakagerFace(1);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_CYMBAL.get(), 2.0F, 1.0F);
                    int amount = 7;

                    for(int i = 0; i < amount; ++i) {
                        GSTot treat = GSEntityTypes.TRICK_OR_TREAT.get().create(this.level);
                        if (treat != null) {
                            treat.circleTime = i * 20;
                            treat.bounceTime = i;
                            treat.setPos(this.getX(), this.getY(), this.getZ());
                            treat.setTrueOwner(this);
                            treat.setTreat(this.random.nextInt(6) + 1);
                            this.circleTreat(treat, i, amount);
                            this.level.addFreshEntity(treat);
                            this.treats.add(treat);
                        }
                    }
                }

                if (this.attackTicks == 31) {
                    this.setFreakagerFace(0);
                }
            }
        }
    }

    public void die(DamageSource p_37847_) {
        if (!this.treats.isEmpty()) {
            for (GSTot treat : this.treats) {
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

    public int getFreakagerFace() {
        return this.entityData.get(FREAKAGER_FACE);
    }

    public void setFreakagerFace(int face) {
        if (!this.level.isClientSide) {
            this.entityData.set(FREAKAGER_FACE, face);
        }

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
                GSTot clone = this.treats.get(i);
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
                GSTot treat = this.treats.get(this.random.nextInt(this.treats.size()));
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

    public boolean hurt(DamageSource source, float amount) {
        if (this.getVehicle() instanceof RagnoServant && !((RagnoServant)this.getVehicle()).isStunned() && (!(source.getEntity() instanceof Player) || !((Player)source.getEntity()).getAbilities().instabuild)) {
            amount = (float)((double)amount / 3.5);
        }

        return !source.is(DamageTypes.IN_WALL) && super.hurt(source, amount);
    }

    public SoundEvent getCelebrateSound() {
        return IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_LAUGH.get();
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

    public int getAnimationState() {
        return this.entityData.get(ANIMATION_STATE);
    }

    public AnimationState getAnimationState(String input) {
        if (Objects.equals(input, "intro1")) {
            return this.intro1AnimationState;
        } else if (Objects.equals(input, "intro2")) {
            return this.intro2AnimationState;
        } else if (Objects.equals(input, "intro3")) {
            return this.intro3AnimationState;
        } else if (Objects.equals(input, "axes_start")) {
            return this.axesStartAnimationState;
        } else if (Objects.equals(input, "axes_normal")) {
            return this.axesNormalAnimationState;
        } else if (Objects.equals(input, "laugh")) {
            return this.laughAnimationState;
        } else if (Objects.equals(input, "bombs")) {
            return this.bombsAnimationState;
        } else if (Objects.equals(input, "minions")) {
            return this.minionsAnimationState;
        } else if (Objects.equals(input, "axes_angry")) {
            return this.angryAxesAnimationState;
        } else if (Objects.equals(input, "potions")) {
            return this.potionsAnimationState;
        } else if (Objects.equals(input, "scythe")) {
            return this.scytheAnimationState;
        } else if (Objects.equals(input, "catch")) {
            return this.catchAnimationState;
        } else if (Objects.equals(input, "trickortreat")) {
            return this.trickortreatAnimationState;
        } else {
            return Objects.equals(input, "phase") ? this.phaseAnimationState : new AnimationState();
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
            BoundFreakager.this.attackType = TRICKORTREAT_ATTACK;
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
            BoundFreakager.this.attackType = SCYTHE_ATTACK;
            BoundFreakager.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            if (!BoundFreakager.this.level.isClientSide) {
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
            BoundFreakager.this.catchTicks = 0;
            BoundFreakager.this.attackType = 0;
            BoundFreakager.this.setAnimationState(0);
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(false);
                BoundFreakager.this.setShowScythe(false);
            }

            BoundFreakager.this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
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
            BoundFreakager.this.setAnimationState(6);
            BoundFreakager.this.attackType = POTIONS_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 75;
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

    class AngryAxesGoal extends Goal {
        public AngryAxesGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return BoundFreakager.this.doesAttackMeetNormalRequirements()
                    && BoundFreakager.this.random.nextInt(16) == 0
                    && BoundFreakager.this.axesCooldown < 1
                    && BoundFreakager.this.halfHealth();
        }

        public void start() {
            BoundFreakager.this.setAnimationState(5);
            BoundFreakager.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            BoundFreakager.this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            BoundFreakager.this.attackType = ANGRY_AXES_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
                BoundFreakager.this.setFreakagerFace(2);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 115;
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
                BoundFreakager.this.setFreakagerFace(0);
            }

            BoundFreakager.this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
            BoundFreakager.this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
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
            BoundFreakager.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            BoundFreakager.this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            BoundFreakager.this.attackType = AXES_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
                BoundFreakager.this.setFreakagerFace(2);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 100;
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

            BoundFreakager.this.setLeftHanded(false);
            BoundFreakager.this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
            BoundFreakager.this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
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
            BoundFreakager.this.attackType = BOMBS_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 40;
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
            BoundFreakager.this.attackCooldown = 90;
        }
    }

    class ThrowMinionsGoal extends Goal {
        public ThrowMinionsGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return BoundFreakager.this.doesAttackMeetNormalRequirements()
                    && BoundFreakager.this.random.nextInt(16) == 0
                    && BoundFreakager.this.minionsCooldown < 1
                    && BoundFreakager.this.hasFewEnoughMinions();
        }

        public void start() {
            BoundFreakager.this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_MINIONS.get(), 2.0F, BoundFreakager.this.getVoicePitch());
            BoundFreakager.this.setAnimationState(1);
            BoundFreakager.this.attackType = MINIONS_ATTACK;
            if (!BoundFreakager.this.level.isClientSide) {
                BoundFreakager.this.setShowArms(true);
            }

        }

        public boolean canContinueToUse() {
            return BoundFreakager.this.attackTicks <= 50;
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

            BoundFreakager.this.minionsCooldown = 400;
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
