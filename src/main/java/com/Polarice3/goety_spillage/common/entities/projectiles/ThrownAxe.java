package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.yellowbrossproductions.illageandspillage.util.EntityUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class ThrownAxe extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(ThrownAxe.class, EntityDataSerializers.FLOAT);
    private boolean canExplode = false;
    public float damage = 8.0F;

    public ThrownAxe(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public ThrownAxe(double p_36818_, double p_36819_, double p_36820_, double p_36821_, double p_36822_, double p_36823_, Level p_36824_) {
        super(GSEntityTypes.THROWN_AXE.get(), p_36818_, p_36819_, p_36820_, p_36821_, p_36822_, p_36823_, p_36824_);
    }

    public ThrownAxe(LivingEntity p_36827_, double p_36828_, double p_36829_, double p_36830_, Level p_36831_) {
        super(GSEntityTypes.THROWN_AXE.get(), p_36827_, p_36828_, p_36829_, p_36830_, p_36831_);
        this.setOwner(p_36827_);
    }

    public ThrownAxe(Level p_181151_, LivingEntity p_181152_, double p_181153_, double p_181154_, double p_181155_) {
        super(GSEntityTypes.THROWN_AXE.get(), p_181152_, p_181153_, p_181154_, p_181155_, p_181151_);
        this.setOwner(p_181152_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(Y_ROT, 0.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setDamage(compound.getFloat("Damage"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Damage", this.getDamage());
    }

    public float getYRot() {
        return this.entityData.get(Y_ROT);
    }

    public void setYRot(float f) {
        this.entityData.set(Y_ROT, f);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    public void tick() {
        this.setInvulnerable(true);
        if (this.canExplode) {
            this.explode(1.0);
            if (!this.level.isClientSide) {
                this.discard();
            }
        }

        this.makeParticles();
        if (this.tickCount >= 100 && !this.level.isClientSide) {
            this.discard();
        }

        super.tick();
    }

    protected boolean shouldBurn() {
        return false;
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    protected void onHitEntity(EntityHitResult p_37259_) {
        super.onHitEntity(p_37259_);
        Entity attacker = this.getOwner() != null ? this.getOwner() : this;
        boolean canHurt = attacker == this || !MobUtil.areAllies(p_37259_.getEntity(), this.getOwner());
        if (canHurt) {
            this.canExplode = true;
        }

    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!(p_37406_ instanceof EntityHitResult)) {
            this.canExplode = true;
        }

    }

    public void makeParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            double d0 = -0.5D + this.random.nextGaussian();
            double d1 = -0.5D + this.random.nextGaussian();
            double d2 = -0.5D + this.random.nextGaussian();
            serverLevel.sendParticles(ParticleTypes.CRIT, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
        }
    }

    public void makeExplodeParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 10; ++i) {
                double d0 = -0.5 + this.random.nextGaussian();
                double d1 = -0.5 + this.random.nextGaussian();
                double d2 = -0.5 + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.POOF, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 1, d0, d1, d2, 0.5F);
            }

            for(int i = 0; i < 20; ++i) {
                double d0 = -0.5 + this.random.nextGaussian();
                double d1 = -0.5 + this.random.nextGaussian();
                double d2 = -0.5 + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.CRIT, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 1, d0, d1, d2, 0.5F);
            }

            for(int i = 0; i < 6; ++i) {
                double d0 = -0.5 + this.random.nextGaussian();
                double d1 = -0.5 + this.random.nextGaussian();
                double d2 = -0.5 + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 1, d0, d1, d2, 0.5F);
            }
        }
    }

    public boolean isPickable() {
        return false;
    }

    public boolean isAttackable() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        return source.is(DamageTypes.GENERIC_KILL) && super.hurt(source, amount);
    }

    public void setRot(Entity shooter){
        this.setYRot(shooter.getYHeadRot());
    }

    private void explode(double size) {
        List<Entity> list = this.level.getEntities(this, new AABB(this.getX() - size, this.getY() - size, this.getZ() - size, this.getX() + size, this.getY() + size, this.getZ() + size), Entity::isAlive);
        Entity attacker = this.getOwner() != null ? this.getOwner() : this;
        this.makeExplodeParticles();
        this.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 2.0F, 1.0F);

        for (Entity entity : list){
            if (entity instanceof LivingEntity living) {
                if (entity != attacker && !MobUtil.areAllies(entity, attacker) && entity.isAlive() && !entity.isInvulnerable() && !entity.isSpectator()) {
                    living.hurt(this.damageSources().thrown(this, attacker), this.getDamage());
                    living.invulnerableTime = 0;
                    EntityUtil.disableShield(living, 200);
                    if (entity instanceof AbstractHauntedArmor armor){
                        if (armor.isBlocking()) {
                            armor.disableShield(true);
                        }
                    }
                }
            }
        }
    }
}
