package com.Polarice3.goety_spillage.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.config.GSAttributesConfig;
import com.yellowbrossproductions.illageandspillage.Config;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class IgniterServant extends AbstractIllagerServant {
    public static final int FIREBALLS_TO_OVERHEAT = 25;
    public static final int COOLDOWN_TIME = 300;
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(IgniterServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TORCH_BURNT_OUT = SynchedEntityData.defineId(IgniterServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> FIREBALLS_SHOT = SynchedEntityData.defineId(IgniterServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> COOLDOWN_TICKS = SynchedEntityData.defineId(IgniterServant.class, EntityDataSerializers.FLOAT);
    private LivingEntity shootTarget;
    private int shootTicks;
    private int fireballTimer;

    public IgniterServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new ShootFireballsGoal());
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 0.8D, 1.0D, (p_234199_0_) -> {
            return this.isOverheated() && this.getTarget() == p_234199_0_;
        }));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, new NaturalAttackGoal<>(this, Sheep.class, 10, false, false, (p_234199_0_) -> {
            return p_234199_0_ instanceof Sheep sheep && sheep.getColor() == DyeColor.PINK;
        }));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, GSAttributesConfig.IgniterServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, GSAttributesConfig.IgniterServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), GSAttributesConfig.IgniterServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), GSAttributesConfig.IgniterServantArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(TORCH_BURNT_OUT, false);
        this.entityData.define(FIREBALLS_SHOT, 0.0F);
        this.entityData.define(COOLDOWN_TICKS, 0.0F);
    }

    public boolean isAlliedBurning(Entity target) {
        if (this.getTrueOwner() != null) {
            return (target == this.getTrueOwner() || MobUtil.getOwner(target) == this.getTrueOwner());
        } else {
            return MobUtil.areAllies(this, target);
        }
    }

    public LivingEntity getShootTarget() {
        return this.shootTarget;
    }

    public void setShootTarget(@Nullable LivingEntity target) {
        this.shootTarget = target;
    }

    public void tick() {
        super.tick();
        if (this.isAlive()) {
            if (this.level instanceof ServerLevel serverLevel) {
                int i = serverLevel.getServer().getTickCount() + this.getId();
                if (i % 2 != 0 && this.tickCount > 1) {
                    this.findTarget();
                }
                if (this.getShootTarget() == null) {
                    if (this.getTarget() != null && this.getTarget().isAlive()) {
                        this.setShootTarget(this.getTarget());
                    }
                }
            }
            if (this.isAttacking()) {
                ++this.shootTicks;
            } else {
                this.shootTicks = 0;
            }

            if (this.getFireballsShot() > 0.0F) {
                if (!this.isAttacking()) {
                    ++this.fireballTimer;
                    if (this.fireballTimer > 20) {
                        this.fireballTimer = 0;
                        this.setFireballsShot(this.getFireballsShot() - 1.0F);
                    }
                } else {
                    this.fireballTimer = 0;
                }
            }

            if (this.getFireballsShot() > 25.0F) {
                this.setCooldownTicks(300.0F);
                this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 1.0F, 0.8F);
                this.setFireballsShot(0.0F);
            }

            if (this.getCooldownTicks() / 300.0F > 0.6F || (this.isOverheated() || !this.isOverheated() && this.getFireballsShot() / 25.0F > 0.6F) && this.random.nextInt(5) == 0) {
                this.makeOverheatParticles();
            }

            this.setCooldownTicks(this.getCooldownTicks() - 1.0F);
            if (this.getCooldownTicks() < 0.0F) {
                this.setCooldownTicks(0.0F);
            }

            if (this.shootTicks >= 4) {
                this.playSound(SoundEvents.DISPENSER_LAUNCH, 1.0F, 1.0F);
                if (this.getShootTarget() != null) {
                    if (this.isTorchBurntOut()) {
                        this.shootSnowball(this.getShootTarget());
                    } else {
                        this.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
                        this.shootFireball(this.getShootTarget());
                        this.setFireballsShot(this.getFireballsShot() + 1.0F);
                    }
                }

                this.shootTicks = 0;
            }

            if (!this.level().isClientSide) {
                if (this.getShootTarget() != null) {
                    if (this.isAlliedBurning(this.getShootTarget())) {
                        this.setTorchBurntOut(true);
                        if (!this.getShootTarget().isOnFire()) {
                            this.setShootTarget(null);
                        }
                    } else {
                        this.setTorchBurntOut(false);
                    }
                }
            }

        }

    }

    protected AABB getTargetSearchArea(double p_26069_) {
        return this.getBoundingBox().inflate(p_26069_, 4.0D, p_26069_);
    }

    protected void findTarget() {
        this.shootTarget = this.level.getNearestEntity(this.level.getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(this.getAttributeValue(Attributes.FOLLOW_RANGE)), (p_148152_) -> {
            return true;
        }), TargetingConditions.forNonCombat().range(this.getAttributeValue(Attributes.FOLLOW_RANGE))
                .selector(livingEntity -> livingEntity.isOnFire() && this.isAlliedBurning(livingEntity)), this, this.getX(), this.getEyeY(), this.getZ());
    }

    public void makeOverheatParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, this.getRandomX(0.15) + -0.5 + this.random.nextDouble(), this.getY(this.random.nextDouble() / 2.0) + 1.0, this.getRandomZ(0.15) + -0.5 + this.random.nextDouble(), 1, 0, 0, 0, 0);
        }
    }

    public void shootSnowball(LivingEntity p_82196_1_) {
        Snowball snowballentity = new Snowball(this.level(), this){
            protected void onHitEntity(EntityHitResult p_37404_) {
                super.onHitEntity(p_37404_);
                Entity entity = p_37404_.getEntity();
                if (this.getOwner() instanceof IgniterServant servant) {
                    if (servant.isAlliedBurning(entity)) {
                        entity.clearFire();
                    }
                }
            }
        };
        double d0 = p_82196_1_.getEyeY() - 1.1D;
        double d1 = p_82196_1_.getX() - this.getX();
        double d2 = d0 - snowballentity.getY();
        double d3 = p_82196_1_.getZ() - this.getZ();
        float f = (float)(Math.sqrt(d1 * d1 + d3 * d3) * 0.2D);
        snowballentity.setPos(snowballentity.getX(), this.getY(0.5), snowballentity.getZ());
        snowballentity.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
        this.level().addFreshEntity(snowballentity);
    }

    public void shootFireball(LivingEntity target) {
        double d0 = this.distanceToSqr(target);
        double d1 = target.getX() - this.getX();
        double d2 = target.getY(0.5) - this.getY(0.5);
        double d3 = target.getZ() - this.getZ();
        float f = (float)(Math.sqrt(Math.sqrt(d0)) * 0.5);
        ModFireball fireballentity = new ModFireball(this.level(), this, d1 + this.getRandom().nextGaussian() * (double)f, d2, d3 + this.getRandom().nextGaussian() * (double)f);
        fireballentity.setPos(fireballentity.getX(), this.getY(0.5), fireballentity.getZ());
        fireballentity.setDangerous(Config.CommonConfig.igniter_canBurnBlocks.get());
        this.level().addFreshEntity(fireballentity);
    }

    public SoundEvent getCelebrateSound() {
        return IllageAndSpillageSoundEvents.ENTITY_IGNITER_CELEBRATE.get();
    }

    protected SoundEvent getAmbientSound() {
        return IllageAndSpillageSoundEvents.ENTITY_IGNITER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return IllageAndSpillageSoundEvents.ENTITY_IGNITER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IllageAndSpillageSoundEvents.ENTITY_IGNITER_DEATH.get();
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isTorchBurntOut() {
        return this.entityData.get(TORCH_BURNT_OUT);
    }

    public void setTorchBurntOut(boolean burnt) {
        this.entityData.set(TORCH_BURNT_OUT, burnt);
    }

    public float getFireballsShot() {
        return this.entityData.get(FIREBALLS_SHOT);
    }

    public void setFireballsShot(float fireballsShot) {
        this.entityData.set(FIREBALLS_SHOT, fireballsShot);
    }

    public float getCooldownTicks() {
        return this.entityData.get(COOLDOWN_TICKS);
    }

    public void setCooldownTicks(float cooldownTicks) {
        this.entityData.set(COOLDOWN_TICKS, cooldownTicks);
    }

    public boolean isOverheated() {
        return this.getCooldownTicks() > 0.0F;
    }

    public boolean doHurtTarget(Entity p_70652_1_) {
        return false;
    }

    public boolean killedEntity(ServerLevel level, LivingEntity entity) {
        if (entity instanceof Sheep && ((Sheep)entity).getColor() == DyeColor.PINK && this.getTarget() == entity) {
            this.playSound(this.getCelebrateSound(), 1.0F, 1.0F);
        }

        return super.killedEntity(level, entity);
    }

    class ShootFireballsGoal extends Goal {
        public ShootFireballsGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return IgniterServant.this.getShootTarget() != null
                    && IgniterServant.this.distanceToSqr(IgniterServant.this.getShootTarget()) < 90.0
                    && IgniterServant.this.hasLineOfSight(IgniterServant.this.getShootTarget())
                    && !IgniterServant.this.isOverheated();
        }

        public void start() {
            IgniterServant.this.setAttacking(true);
            IgniterServant.this.playSound(SoundEvents.LEVER_CLICK, 1.0F, 0.6F);
        }

        public boolean canContinueToUse() {
            return IgniterServant.this.getShootTarget() != null
                    && IgniterServant.this.distanceToSqr(IgniterServant.this.getShootTarget()) < 90.0
                    && IgniterServant.this.getShootTarget().isAlive()
                    && IgniterServant.this.hasLineOfSight(IgniterServant.this.getShootTarget())
                    && !IgniterServant.this.isOverheated();
        }

        public void tick() {
            IgniterServant.this.getNavigation().stop();
            if (IgniterServant.this.getShootTarget() != null) {
                IgniterServant.this.getLookControl().setLookAt(IgniterServant.this.getShootTarget(), 30.0F, 30.0F);
            }

            IgniterServant.this.navigation.stop();
        }

        public void stop() {
            IgniterServant.this.setAttacking(false);
            IgniterServant.this.playSound(SoundEvents.LEVER_CLICK, 1.0F, 0.5F);
        }
    }
}
