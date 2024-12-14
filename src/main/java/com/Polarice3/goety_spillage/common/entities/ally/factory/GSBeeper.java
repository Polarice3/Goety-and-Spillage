package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MobUtil;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.EnumSet;

public class GSBeeper extends FactoryServant {
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(GSBeeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(GSBeeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IGNITED = SynchedEntityData.defineId(GSBeeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> PARTIAL_TICKS = SynchedEntityData.defineId(GSBeeper.class, EntityDataSerializers.FLOAT);
    public int lastActiveTime;
    public int timeSinceIgnited;
    public int fuseTime = 30;
    private float explosionRadius = 1.2F;

    public GSBeeper(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BeeperSwellGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        boolean flag = super.causeFallDamage(distance, damageMultiplier, source);
        this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + distance * 1.5F);
        if (this.timeSinceIgnited > this.fuseTime - 5) {
            this.timeSinceIgnited = this.fuseTime - 5;
        }

        return flag;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, -1);
        this.entityData.define(POWERED, false);
        this.entityData.define(IGNITED, false);
        this.entityData.define(PARTIAL_TICKS, 1.0F);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("Fuse", (short)this.fuseTime);
        compound.putFloat("ExplosionRadius", this.explosionRadius);
        compound.putBoolean("Ignited", this.hasIgnited());
        if ((Boolean)this.entityData.get(POWERED)) {
            compound.putBoolean("Powered", true);
        }

    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Fuse", 99)) {
            this.fuseTime = compound.getShort("Fuse");
        }

        if (compound.contains("ExplosionRadius", 99)) {
            this.explosionRadius = compound.getFloat("ExplosionRadius");
        }

        if (compound.getBoolean("Ignited")) {
            this.ignite();
        }

        this.entityData.set(POWERED, compound.getBoolean("Powered"));
    }

    public float getPartialTicks() {
        return this.entityData.get(PARTIAL_TICKS);
    }

    public void setPartialTicks(float partialTicks) {
        this.entityData.set(PARTIAL_TICKS, partialTicks);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height * 0.8F;
    }

    protected void explode() {
        if (!this.level.isClientSide()) {
            this.dead = true;
            this.makeFireParticles();
            boolean loot = CuriosFinder.hasWanting(this.getMasterOwner());
            LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
            ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), this.explosionRadius, false, Explosion.BlockInteraction.KEEP, lootMode);
            this.discard();
            this.spawnLingeringCloud();
            for (LivingEntity hit : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.75), living -> !MobUtil.areAllies(this, living))) {
                hit.setSecondsOnFire(5);
            }
        }

    }

    public void makeFireParticles() {
        if (this.level instanceof ServerLevel serverLevel){
            for(int i = 0; i < 15; ++i) {
                double d0 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d1 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d2 = (-0.5 + this.random.nextGaussian()) / 4.0;
                serverLevel.sendParticles(ParticleTypes.FLAME, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public void tick() {
        if (this.isAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            if (this.hasIgnited()) {
                this.setCreeperState(1);
            }

            int i = this.getCreeperState();
            if (i > 0 && this.timeSinceIgnited == 0) {
                this.playSound(SoundEvents.TNT_PRIMED, 1.0F, 0.5F);
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_ENGINEER_BEEPER_BEEP.get(), 1.0F, 1.0F);
            }

            this.timeSinceIgnited += i;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= this.fuseTime) {
                this.timeSinceIgnited = this.fuseTime;
                this.explode();
            }
        }

        super.tick();
    }

    public boolean doHurtTarget(Entity p_21372_) {
        return true;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
            this.level.playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level.isClientSide()) {
                this.ignite();
                itemstack.hurtAndBreak(1, player, (p_213625_1_) -> {
                    p_213625_1_.broadcastBreakEvent(hand);
                });
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    public float getCreeperFlashIntensity(float partialTicks) {
        return Mth.lerp(partialTicks, (float)this.lastActiveTime, (float)this.timeSinceIgnited) / (float)(this.fuseTime - 2);
    }

    public int getCreeperState() {
        return this.entityData.get(STATE);
    }

    public void setCreeperState(int state) {
        this.entityData.set(STATE, state);
    }

    public boolean hasIgnited() {
        return this.entityData.get(IGNITED);
    }

    public void ignite() {
        this.entityData.set(IGNITED, true);
    }

    protected void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloudentity = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            areaeffectcloudentity.setRadius(1.0F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());

            for (MobEffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new MobEffectInstance(effectinstance));
            }

            this.level.addFreshEntity(areaeffectcloudentity);
        }

    }

    private static class BeeperSwellGoal extends Goal {
        private final GSBeeper swellingBeeper;
        private LivingEntity beeperAttackTarget;

        public BeeperSwellGoal(GSBeeper entityIn) {
            this.swellingBeeper = entityIn;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            LivingEntity livingEntity = this.swellingBeeper.getTarget();
            return this.swellingBeeper.getCreeperState() > 0 || livingEntity != null && this.swellingBeeper.distanceToSqr(livingEntity) < 4.0;
        }

        public void start() {
            this.beeperAttackTarget = this.swellingBeeper.getTarget();
        }

        public void stop() {
            this.beeperAttackTarget = null;
        }

        public void tick() {
            if (this.beeperAttackTarget == null) {
                this.swellingBeeper.setCreeperState(-1);
            } else if (this.swellingBeeper.distanceToSqr(this.beeperAttackTarget) > 6.25) {
                this.swellingBeeper.setCreeperState(-1);
            } else if (!this.swellingBeeper.getSensing().hasLineOfSight(this.beeperAttackTarget)) {
                this.swellingBeeper.setCreeperState(-1);
            } else {
                this.swellingBeeper.setCreeperState(1);
                this.swellingBeeper.getNavigation().moveTo(this.beeperAttackTarget, 0.8);
            }

        }
    }
}
