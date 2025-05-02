package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.common.entities.IAttackMyOwner;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class GSImp extends Owned implements IllagerAttack, IAttackMyOwner {
    private static final EntityDataAccessor<Integer> STAGE = SynchedEntityData.defineId(GSImp.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAIT = SynchedEntityData.defineId(GSImp.class, EntityDataSerializers.INT);
    int power;

    public GSImp(EntityType<? extends Owned> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 50.0);
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return false;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STAGE, 1);
        this.entityData.define(WAIT, 0);
    }

    public void aiStep() {
        if (this.tickCount >= 1 + this.getWaitTime() && this.getStage() == 1 && !this.isInvisible() && this.level().isClientSide && this.random.nextBoolean()) {
            this.level().addParticle(ParticleTypes.FLAME, this.getRandomX(1.0), this.getRandomY() + 0.2, this.getRandomZ(1.0), 0.0, 0.3, 0.0);
        }

        if (this.tickCount > 44 + this.getWaitTime() && this.tickCount < 48 + this.getWaitTime() && this.level().isClientSide) {
            for(int i = 0; i < 75; ++i) {
                this.level().addParticle(ParticleTypes.FLAME, this.getRandomX(1.0), this.getRandomY() + 0.2, this.getRandomZ(1.0), 0.0, this.random.nextDouble() + 0.1, 0.0);
            }
        }

        super.aiStep();
    }

    public void tick() {
        if (this.getStage() != 1 && this.getStage() != 3 && this.getStage() != 5) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
        } else {
            this.setDeltaMovement(0.0, 0.0, 0.0);
        }

        this.noPhysics = true;
        super.tick();
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
        this.setInvulnerable(true);
        if (this.tickCount == 1 + this.getWaitTime()) {
            this.setInvisible(false);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_BLOCKALERT.get(), 2.0F, 1.0F);
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_EARTHRUMBLE.get(), 2.0F, 1.0F);
        }

        if (this.tickCount >= 46 + this.getWaitTime() && this.getStage() == 1) {
            if (!this.level.isClientSide) {
                this.setStage(2);
            }

            this.setDeltaMovement(0.0, 0.8, 0.0);
            this.playSound(SoundEvents.FIRECHARGE_USE, 2.0F, 1.0F);

            for (Entity entity : this.level.getEntities(this, this.getBoundingBox())) {
                if (!MobUtil.areAllies(this.getOwner(), entity) && entity instanceof LivingEntity && entity.isAlive()) {
                    entity.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 4.0F + (float) this.power);
                    entity.setSecondsOnFire(8);
                }
            }
        }

        if (this.tickCount >= 59 + this.getWaitTime() && this.getStage() == 2) {
            if (this.random.nextInt(3) == 0) {
                if (!this.level.isClientSide) {
                    this.setStage(3);
                }

                this.playSound(IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_IMPLAUGH.get(), 2.0F, this.getVoicePitch());
            } else if (!this.level.isClientSide) {
                this.setStage(5);
            }
        }

        if ((this.tickCount >= 75 + this.getWaitTime() && this.getStage() == 3 || this.getStage() == 5) && !this.level().isClientSide) {
            this.setStage(4);
        }

        if (this.tickCount >= 95 + this.getWaitTime() && this.getStage() == 4) {
            this.discard();
        }

        if (this.getTarget() != null && this.getStage() > 1) {
            this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
        }
    }

    public int getStage() {
        return this.entityData.get(STAGE);
    }

    public void setStage(int stage) {
        this.entityData.set(STAGE, stage);
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getWaitTime() {
        return this.entityData.get(WAIT);
    }

    public void setWaitTime(int waitTime) {
        this.entityData.set(WAIT, waitTime);
    }

    public boolean hurt(DamageSource source, float p_21017_) {
        return (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) && super.hurt(source, p_21017_);
    }

    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    protected void doPush(Entity entityIn) {
    }

    public void push(Entity entityIn) {
    }
}
