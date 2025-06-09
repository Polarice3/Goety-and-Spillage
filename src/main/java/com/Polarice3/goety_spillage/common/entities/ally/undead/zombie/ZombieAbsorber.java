package com.Polarice3.goety_spillage.common.entities.ally.undead.zombie;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.Polarice3.goety_spillage.config.GSMobsConfig;
import com.Polarice3.goety_spillage.config.GSSpellConfig;
import com.yellowbrossproductions.illageandspillage.Config;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ICanBeAnimated;
import com.yellowbrossproductions.illageandspillage.util.EntityUtil;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ZombieAbsorber extends Summoned implements ICanBeAnimated {
    private static final EntityDataAccessor<Integer> ATTACK_TICKS = SynchedEntityData.defineId(ZombieAbsorber.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(ZombieAbsorber.class, EntityDataSerializers.INT);
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public ZombieAbsorber(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new AttackGoal());
        this.goalSelector.addGoal(3, new ModMeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.15F)
                .add(Attributes.MAX_HEALTH, GSAttributesConfig.ZombieAbsorberHealth.get())
                .add(Attributes.ATTACK_DAMAGE, GSAttributesConfig.ZombieAbsorberDamage.get())
                .add(Attributes.ARMOR, GSAttributesConfig.ZombieAbsorberArmor.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), GSAttributesConfig.ZombieAbsorberHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), GSAttributesConfig.ZombieAbsorberDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), GSAttributesConfig.ZombieAbsorberArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_TICKS, 0);
        this.entityData.define(ANIMATION_STATE, 0);
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof ZombieAbsorber;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return GSSpellConfig.ZombieAbsorberLimit.get();
    }

    @Override
    public int xpReward() {
        return 10;
    }

    protected boolean isSunSensitive() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public boolean canBeRidden(LivingEntity livingEntity) {
        if (livingEntity instanceof ZombieServant servant){
            if (servant.isBaby()){
                if (!this.isVehicle()) {
                    if (servant.getTrueOwner() == this.getTrueOwner()) {
                        return true;
                    }
                }
            }
        }
        return super.canBeRidden(livingEntity);
    }

    public void tick() {
        super.tick();
        if (this.hurtTime >= 3) {
            this.hurtTime = 2;
        }

        if (this.isAlive() && this.getAttackAnimationTick() > 0) {
            if (!this.level().isClientSide) {
                this.setAttackAnimationTick(this.getAttackAnimationTick() - 1);
            }

            this.setYRot(this.getYHeadRot());
            this.yBodyRot = this.getYRot();
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
            if (this.getTarget() != null) {
                this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
            }

            if (this.getAttackAnimationTick() == 10) {
                this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 1.0F);
                float radius2 = 1.5F;
                double x = this.getX() + 0.8F * Math.sin((double) (-this.getYRot()) * Math.PI / 180.0) + (double) radius2 * Math.sin((double) (-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double) (-this.getXRot()) * Math.PI / 180.0);
                double z = this.getZ() + 0.8F * Math.cos((double) (-this.getYRot()) * Math.PI / 180.0) + (double) radius2 * Math.cos((double) (-this.yHeadRot) * Math.PI / 180.0) * Math.cos((double) (-this.getXRot()) * Math.PI / 180.0);
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(x - 2.0, this.getY(), z - 2.0, x + 2.0, this.getY() + 2.0, z + 2.0));
                DamageSource damageSource = this.damageSources().mobAttack(this);
                if (this.getTrueOwner() != null){
                    damageSource = ModDamageSource.summonAttack(this, this.getTrueOwner());
                }
                for (LivingEntity caught : list) {
                    if (caught != this && !MobUtil.areAllies(this, caught) && caught.isAlive()) {
                        caught.hurt(damageSource, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        caught.lerpMotion(caught.getDeltaMovement().x, caught.getDeltaMovement().y + 0.3D, caught.getDeltaMovement().z);
                        caught.setDeltaMovement(caught.getDeltaMovement().add(0.0D, 0.3D, 0.0D));
                        EntityUtil.disableShield(caught, 200);
                        if (caught instanceof AbstractHauntedArmor armor){
                            if (armor.isBlocking()) {
                                armor.disableShield(true);
                            }
                        }
                    }
                }
            }
        }

        if (this.getFirstPassenger() instanceof ZombieServant servant && servant.isBaby()){
            if (this.getTarget() != null){
                if (MobUtil.healthIsHalved(this)){
                    double d0 = this.getTarget().getX() - this.getX();
                    double d1 = this.getTarget().getY(0.3333333333333333) - this.getY() - (double)this.getTarget().getBbHeight() / 2.0;
                    double d2 = this.getTarget().getZ() - this.getZ();
                    double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
                    MobUtil.shoot(servant, d0, d1 + d3 * 0.2D, d2, 1.6F, 1.0F);
                    servant.setTarget(this.getTarget());
                    servant.stopRiding();
                }
            }
        }

    }

    @Override
    public void healServant() {
        if (GSMobsConfig.ZombieAbsorberHeal.get()) {
            super.healServant();
        }
    }

    public int getAttackAnimationTick() {
        return this.entityData.get(ATTACK_TICKS);
    }

    public void setAttackAnimationTick(int attackAnimationTick) {
        this.entityData.set(ATTACK_TICKS, attackAnimationTick);
    }

    public void die(DamageSource p_37847_) {
        this.setAnimationState(2);
        super.die(p_37847_);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 30) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_ABSORBER_COLLAPSE.get(), 1.0F, 1.0F);
        }

        if (this.deathTime == 90 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }

    }

    public float getStepHeight() {
        return 1.0F;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!Config.CommonConfig.absorber_damageMode.get()) {
            if (!source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.GENERIC_KILL)) {
                amount = 1.0F;
            }
        } else if (!source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.GENERIC_KILL) && amount > 1.0F) {
            amount = 1.0F;
        }

        boolean flag = super.hurt(source, amount);

        if (flag) {
            this.setNoHealTime(MathHelper.secondsToTicks(GSMobsConfig.ZombieAbsorberHealTime.get()));
        }

        return flag;
    }

    protected float getStandingEyeHeight(Pose p_21131_, EntityDimensions p_21132_) {
        return 2.5625F;
    }

    protected SoundEvent getAmbientSound() {
        return IllageAndSpillageSoundEvents.ENTITY_ABSORBER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return IllageAndSpillageSoundEvents.ENTITY_ABSORBER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_ABSORBER_DEATH.get();
    }

    public float getVoicePitch() {
        return this.isDeadOrDying() ? 1.0F : super.getVoicePitch() - 0.25F;
    }

    public boolean doHurtTarget(Entity p_21372_) {
        if (this.getAttackAnimationTick() < 1) {
            this.attackAnimationState.stop();
            this.setAnimationState(0);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_ABSORBER_ATTACK.get(), 1.0F, 0.75F);
            if (!this.level().isClientSide) {
                this.setAttackAnimationTick(30);
            }

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
        } else {
            return Objects.equals(input, "death") ? this.deathAnimationState : new AnimationState();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ANIMATION_STATE.equals(p_21104_) && this.level().isClientSide) {
            switch (this.entityData.get(ANIMATION_STATE)) {
                case 0:
                default:
                    break;
                case 1:
                    this.attackAnimationState.start(this.tickCount);
                    break;
                case 2:
                    this.attackAnimationState.stop();
                    this.deathAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
        Item item = itemstack.getItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (item == Items.ROTTEN_FLESH && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.heal(2.0F);
                Level var6 = this.level;
                if (var6 instanceof ServerLevel serverLevel) {

                    for(int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02;
                        double d1 = this.random.nextGaussian() * 0.02;
                        double d2 = this.random.nextGaussian() * 0.02;
                        serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, d0, d1, d2, 0.5);
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    class AttackGoal extends Goal {
        public boolean canUse() {
            return ZombieAbsorber.this.getAttackAnimationTick() > 0;
        }

        public boolean canContinueToUse() {
            return ZombieAbsorber.this.getAttackAnimationTick() > 0;
        }

        public AttackGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public void tick() {
            ZombieAbsorber.this.getNavigation().stop();
            ZombieAbsorber.this.navigation.stop();
        }
    }
}
