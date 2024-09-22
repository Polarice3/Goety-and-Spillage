package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.projectiles.IceChunk;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class MobProjectile extends Mob {
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;
    public LivingEntity shooter = null;
    public float damage;

    public MobProjectile(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.MAX_HEALTH, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource bullcrap) {
        if (!this.level.isClientSide) {
            this.discard();
        }

        return false;
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    public int getLifeSpan(){
        return 100;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Damage")) {
            this.damage = pCompound.getFloat("Damage");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.damage);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        this.makeParticles();
        this.setDeltaMovement(this.accelerationX, this.accelerationY, this.accelerationZ);
        if (!this.level.isClientSide) {
            if (this.tickCount >= this.getLifeSpan()) {
                this.discard();
            }
        }

        super.tick();
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
    }

    public void setAcceleration(double x, double y, double z) {
        this.accelerationX = x;
        this.accelerationY = y;
        this.accelerationZ = z;
    }

    public void makeParticles() {
        if (this.level instanceof ServerLevel serverLevel){
            this.setParticles(serverLevel);
        }
    }

    public void setParticles(ServerLevel serverLevel) {
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vector3d = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * 0.007499999832361937 * (double)inaccuracy, this.random.nextGaussian() * 0.007499999832361937 * (double)inaccuracy, this.random.nextGaussian() * 0.007499999832361937 * (double)inaccuracy).scale((double)velocity);
        this.setDeltaMovement(vector3d);
        float f = (float)vector3d.horizontalDistanceSqr();
        this.setYHeadRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875));
        this.setYRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 57.2957763671875));
        this.setXRot((float)(Mth.atan2(vector3d.y, (double)f) * 57.2957763671875));
        this.setYRot(this.yRotO);
        this.setXRot(this.xRotO);
    }

    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        float f = -Mth.sin(p_234612_3_ * 0.017453292F) * Mth.cos(p_234612_2_ * 0.017453292F);
        float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * 0.017453292F);
        float f2 = Mth.cos(p_234612_3_ * 0.017453292F) * Mth.cos(p_234612_2_ * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, p_234612_5_, p_234612_6_);
        Vec3 vector3d = p_234612_1_.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.onGround() ? 0.0 : vector3d.y, vector3d.z));
        this.accelerationX = this.getDeltaMovement().x;
        this.accelerationY = this.getDeltaMovement().y;
        this.accelerationZ = this.getDeltaMovement().z;
    }

    public void setShooter(LivingEntity shooter) {
        this.shooter = shooter;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0;
    }

    public boolean hurt(DamageSource source, float amount) {
        return (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) && super.hurt(source, amount);
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

    @Override
    public boolean isAttackable() {
        return false;
    }

    protected boolean canHitEntity(Entity entity) {
        if (!entity.isSpectator() && entity.isAlive() && entity.isPickable() && !entity.noPhysics && !(entity instanceof IceChunk)) {
            Entity owner = this.shooter;
            return owner == null || !owner.isPassengerOfSameVehicle(entity) && !MobUtil.areAllies(owner, entity);
        } else {
            return false;
        }
    }
}
