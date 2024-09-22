package com.Polarice3.goety_spillage.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.goety_spillage.common.entities.IAttackMyOwner;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;
import java.util.List;

public class GSSpiritHand extends Summoned implements IAttackMyOwner {
    private static final EntityDataAccessor<Boolean> GOOD_OR_EVIL = SynchedEntityData.defineId(GSSpiritHand.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(GSSpiritHand.class, EntityDataSerializers.INT);
    private boolean attacking = false;
    private int attackTicks;
    private int actualAttackTicks;
    int power;
    double chargeX;
    double chargeY;
    double chargeZ;
    double targetX;
    double targetY;
    double targetZ;

    public GSSpiritHand(EntityType<? extends Summoned> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new AlwaysWatchTargetGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 50.0);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GOOD_OR_EVIL, true);
        this.entityData.define(ATTACK_TYPE, 0);
    }

    public void addAdditionalSaveData(CompoundTag p_21484_) {
        super.addAdditionalSaveData(p_21484_);
        p_21484_.putBoolean("IsGoodOrEvil", this.isGoodOrEvil());
    }

    public void readAdditionalSaveData(CompoundTag p_21450_) {
        super.readAdditionalSaveData(p_21450_);
        this.setGoodOrEvil(p_21450_.getBoolean("IsGoodOrEvil"));
    }

    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.setNoGravity(true);
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
        this.setDeltaMovement(this.getDeltaMovement().add((-0.5 + this.random.nextDouble()) / 20.0, (-0.5 + this.random.nextDouble()) / 20.0, (-0.5 + this.random.nextDouble()) / 20.0));
        boolean loot = CuriosFinder.hasWanting(this.getTrueOwner());
        LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
        if (this.getTarget() != null) {
            ++this.attackTicks;
            List<GSSpiritHand> list = this.level.getEntitiesOfClass(GSSpiritHand.class, this.getBoundingBox().inflate(100.0), GSSpiritHand::isAttacking);
            if (this.attackTicks > 100 && this.actualAttackTicks < 1 && this.random.nextInt(12) == 0) {
                if (list.isEmpty()) {
                    this.setAttacking(true);
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_SPIRITHAND_WARN.get(), 4.0F, this.getVoicePitch());
                    if (this.random.nextBoolean()) {
                        if (!this.level.isClientSide) {
                            this.setAttackType(1);
                        }
                    } else if (!this.level.isClientSide) {
                        this.setAttackType(2);
                    }
                } else {
                    this.attackTicks = 0;
                }
            }

            if (this.isAttacking()) {
                ++this.actualAttackTicks;
                if (this.getAttackType() == 1) {
                    if (this.isGoodOrEvil()) {
                        if (this.actualAttackTicks < 100) {
                            double x = this.getX() - this.getTarget().getX();
                            double y = this.getY() - (this.getTarget().getY() - 0.3);
                            double z = this.getZ() - this.getTarget().getZ();
                            double d = Math.sqrt(x * x + y * y + z * z);
                            float power = 0.2F;
                            double motionX = this.getDeltaMovement().x - x / d * (double)power * 0.2;
                            double motionY = this.getDeltaMovement().y - y / d * (double)power * 0.2;
                            double motionZ = this.getDeltaMovement().z - z / d * (double)power * 0.2;
                            if (this.distanceToSqr(this.getTarget()) > 1.5) {
                                this.setDeltaMovement(motionX, motionY, motionZ);
                            } else {
                                this.setDeltaMovement(this.getDeltaMovement().x / 2.0, this.getDeltaMovement().y / 2.0, this.getDeltaMovement().z / 2.0);
                            }
                        } else {
                            this.setDeltaMovement(0.0, 0.5 + (double)(this.power / 8), 0.0);
                            for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0))){
                                livingEntity.hurtMarked = true;
                                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 0.5 + (double)(this.power / 8), livingEntity.getDeltaMovement().z);
                                livingEntity.lerpMotion(livingEntity.getDeltaMovement().x, 0.5 + (double)(this.power / 8), livingEntity.getDeltaMovement().z);
                            }
                            if (this.actualAttackTicks > 120 - this.power * 2) {
                                this.attackTicks = 0;
                                this.actualAttackTicks = 0;
                                this.setAttacking(false);
                                if (!this.level.isClientSide) {
                                    this.setAttackType(0);
                                }
                            }
                        }
                    } else if (this.actualAttackTicks < 100) {
                        double x = this.getX() - this.getTarget().getX();
                        double y = this.getY() - (this.getTarget().getY() + (double)this.getTarget().getEyeHeight() + 1.0 + (double)this.actualAttackTicks / 25.0);
                        double z = this.getZ() - this.getTarget().getZ();
                        double d = Math.sqrt(x * x + y * y + z * z);
                        float power = 0.2F;
                        double motionX = this.getDeltaMovement().x - x / d * (double)power * 0.2;
                        double motionY = this.getDeltaMovement().y - y / d * (double)power * 0.2;
                        double motionZ = this.getDeltaMovement().z - z / d * (double)power * 0.2;
                        if (this.distanceToSqr(this.getTarget()) > 1.5) {
                            this.setDeltaMovement(motionX, motionY, motionZ);
                        } else {
                            this.setDeltaMovement(this.getDeltaMovement().x / 2.0, this.getDeltaMovement().y / 2.0, this.getDeltaMovement().z / 2.0);
                        }
                    } else {
                        this.setDeltaMovement(0.0, -0.7 + (double)(this.power / 8), 0.0);
                        for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0))){
                            livingEntity.hurtMarked = true;
                            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, -0.7 + (double)(this.power / 8), livingEntity.getDeltaMovement().z);
                            livingEntity.lerpMotion(livingEntity.getDeltaMovement().x, -0.7 + (double)(this.power / 8), livingEntity.getDeltaMovement().z);
                        }
                        if (this.actualAttackTicks > 120 - this.power * 2 || !this.level.getBlockState(this.blockPosition()).isAir()) {
                            this.attackTicks = 0;
                            this.actualAttackTicks = 0;
                            this.setAttacking(false);
                            if (!this.level.isClientSide) {
                                this.setAttackType(0);
                                ExplosionUtil.lootExplode(this.level, this.getTrueOwner(), this.getX(), this.getY(), this.getZ(), 2.5F, false, Explosion.BlockInteraction.NONE, lootMode);
                            }
                        }
                    }
                } else {
                    if (this.actualAttackTicks >= 100 && this.getTarget() != null && this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0) {
                        double x = this.getX() - this.getTarget().getX();
                        double y = this.getY() - this.getTarget().getY();
                        double z = this.getZ() - this.getTarget().getZ();
                        double d = Math.sqrt(x * x + y * y + z * z);
                        float power = 6.0F;
                        double motionX = this.getDeltaMovement().x - x / d * (double)power * 0.2;
                        double motionY = this.getDeltaMovement().y - y / d * (double)power * 0.2;
                        double motionZ = this.getDeltaMovement().z - z / d * (double)power * 0.2;
                        this.setTargetPosition(this.getTarget().getX(), this.getTarget().getY(), this.getTarget().getZ());
                        this.setCharge(motionX, motionY, motionZ);
                    }

                    if (this.chargeX != 0.0 && this.chargeY != 0.0 && this.chargeZ != 0.0) {
                        this.setDeltaMovement(this.chargeX, this.chargeY, this.chargeZ);
                        if (this.getX() - this.targetX < 1.0 && this.getX() - this.targetX > -1.0 && this.getY() - this.targetY < 1.0 && this.getY() - this.targetY > -1.0 && this.getZ() - this.targetZ < 1.0 && this.getZ() - this.targetZ > -1.0) {
                            this.actualAttackTicks = 141;
                        }
                    }

                    if (this.actualAttackTicks > 140) {
                        this.attackTicks = 0;
                        this.actualAttackTicks = 0;
                        this.setAttacking(false);
                        if (!this.level.isClientSide) {
                            this.setAttackType(0);
                            ExplosionUtil.lootExplode(this.level, this.getTrueOwner(), this.getX(), this.getY(), this.getZ(), 2.5F, false, Explosion.BlockInteraction.NONE, lootMode);
                        }

                        this.setCharge(0.0, 0.0, 0.0);
                        this.setTargetPosition(0.0, 0.0, 0.0);
                    }
                }
            }
        } else {
            this.attackTicks = 0;
            this.actualAttackTicks = 0;
            this.setAttacking(false);
            if (!this.level.isClientSide) {
                this.setAttackType(0);
            }
        }

        if (this.getTrueOwner() != null && this.getTarget() == null && !this.isAttacking()){
            if (this.distanceTo(this.getTrueOwner()) > 10.0D) {
                double x = this.getX() - this.getTrueOwner().getX();
                double y = this.getY() - this.getTrueOwner().getY();
                double z = this.getZ() - this.getTrueOwner().getZ();
                double d = Math.sqrt(x * x + y * y + z * z);
                float power = 0.2F;
                double motionX = this.getDeltaMovement().x - x / d * (double) power * 0.2;
                double motionY = this.getDeltaMovement().y - y / d * (double) power * 0.2;
                double motionZ = this.getDeltaMovement().z - z / d * (double) power * 0.2;
                if (this.distanceToSqr(this.getTrueOwner()) > 1.5D) {
                    this.setDeltaMovement(motionX, motionY, motionZ);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().x / 2.0, this.getDeltaMovement().y / 2.0, this.getDeltaMovement().z / 2.0);
                }
            }
        }

        if (this.level.getBlockState(this.blockPosition().below()) != Blocks.AIR.defaultBlockState()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.01, 0.0));
        } else if (this.getDeltaMovement().y > 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.01, 0.0));
        }
    }

    @Override
    public void lifeSpanDamage() {
        this.kill();
    }

    @Override
    public boolean canUpdateMove() {
        return false;
    }

    public boolean doHurtTarget(Entity p_21372_) {
        return false;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public boolean isPersistenceRequired() {
        return true;
    }

    public boolean isGoodOrEvil() {
        return this.entityData.get(GOOD_OR_EVIL);
    }

    public void setGoodOrEvil(boolean goodOrEvil) {
        this.entityData.set(GOOD_OR_EVIL, goodOrEvil);
    }

    public void setCharge(double x, double y, double z) {
        this.chargeX = x;
        this.chargeY = y;
        this.chargeZ = z;
    }

    public void setTargetPosition(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return null;
    }

    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    protected void doPush(Entity entityIn) {
    }

    public void push(Entity entityIn) {
    }

    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return p_21016_ == DamageSource.OUT_OF_WORLD && super.hurt(p_21016_, p_21017_);
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    public boolean isAttacking() {
        return this.attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int a) {
        this.entityData.set(ATTACK_TYPE, a);
    }

    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ != 60) {
            super.handleEntityEvent(p_21375_);
        }

    }

    class AlwaysWatchTargetGoal extends Goal {
        public AlwaysWatchTargetGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public boolean canUse() {
            return GSSpiritHand.this.getTarget() != null && !GSSpiritHand.this.isCommanded();
        }

        public boolean canContinueToUse() {
            return GSSpiritHand.this.getTarget() != null && !GSSpiritHand.this.isCommanded();
        }

        public void tick() {
            GSSpiritHand.this.getNavigation().stop();
            if (GSSpiritHand.this.getTarget() != null && GSSpiritHand.this.actualAttackTicks < 100) {
                GSSpiritHand.this.getLookControl().setLookAt(GSSpiritHand.this.getTarget(), 100.0F, 100.0F);
            }

            if (GSSpiritHand.this.actualAttackTicks >= 100) {
                GSSpiritHand.this.getLookControl().setLookAt(GSSpiritHand.this.targetX, GSSpiritHand.this.targetY, GSSpiritHand.this.targetZ, 100.0F, 100.0F);
            }
        }
    }
}
