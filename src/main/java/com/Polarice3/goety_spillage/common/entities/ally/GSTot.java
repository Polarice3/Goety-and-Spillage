package com.Polarice3.goety_spillage.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.yellowbrossproductions.illageandspillage.util.EffectRegisterer;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import com.yellowbrossproductions.illageandspillage.util.ItemRegisterer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GSTot extends Summoned {
    private static final EntityDataAccessor<Integer> TREAT = SynchedEntityData.defineId(GSTot.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> BOUNCE = SynchedEntityData.defineId(GSTot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GOOPY = SynchedEntityData.defineId(GSTot.class, EntityDataSerializers.BOOLEAN);
    public boolean distract = false;
    public int circleTime;
    public int bounceTime;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;

    public GSTot(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.MAX_HEALTH, 8.0F)
                .add(Attributes.ATTACK_DAMAGE, 5.0F)
                .add(Attributes.FOLLOW_RANGE, 50.0F);
    }

    public float getStepHeight() {
        return 2.0F;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TREAT, 1);
        this.entityData.define(BOUNCE, false);
        this.entityData.define(GOOPY, false);
    }

    public boolean getBounce() {
        return this.entityData.get(BOUNCE);
    }

    public void setBounce() {
        this.entityData.set(BOUNCE, true);
    }

    public boolean getGoopy() {
        return this.entityData.get(GOOPY);
    }

    public void setGoopy() {
        this.entityData.set(GOOPY, true);
    }

    public void addAdditionalSaveData(CompoundTag p_21484_) {
        super.addAdditionalSaveData(p_21484_);
        p_21484_.putInt("Treat", this.getTreat());
        p_21484_.putBoolean("Distract", this.distract);
    }

    public void readAdditionalSaveData(CompoundTag p_21450_) {
        super.readAdditionalSaveData(p_21450_);
        this.setTreat(p_21450_.getInt("Treat"));
        this.distract = p_21450_.getBoolean("Distract");
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return p_21197_.getEffect() != EffectRegisterer.MUTATION.get() && super.canBeAffected(p_21197_);
    }

    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    public void tick() {
        super.tick();
        if (this.tickCount % 15 == 0) {
            ++this.circleTime;
        }

        if (this.distract) {
            this.distractAttackers();
        }

        if (this.getTrueOwner() != null) {
            if ((double)this.distanceTo(this.getTrueOwner()) > 30.0) {
                this.getNavigation().moveTo(this.getTrueOwner(), 2.0);
            } else {
                this.circleOwner(this.getTrueOwner(), this.circleTime, Mth.cos((float)this.tickCount / 15.0F));
            }

            this.getLookControl().setLookAt(this.getTrueOwner(), 100.0F, 100.0F);
            int timeLimit = 300 + this.bounceTime * 20;
            if (this.tickCount >= timeLimit) {
                if (this.tickCount == timeLimit) {
                    if (!this.level().isClientSide) {
                        this.setBounce();
                    }

                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_TRICKORTREAT_BOUNCE.get(), 2.0F, 1.9F);
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.6, 0.0));
                    this.makeTreatParticles();
                }

                if (this.tickCount >= timeLimit + 10) {
                    LivingEntity entity = this.getTrueOwner();
                    double x = this.getX() - entity.getX();
                    double y = this.getY() - (entity.getY() + 2.2);
                    double z = this.getZ() - entity.getZ();
                    double d = Math.sqrt(x * x + y * y + z * z);
                    float power = 5.0F;
                    double motionX = -(x / d * (double)power * 0.2);
                    double motionY = -(y / d * (double)power * 0.2);
                    double motionZ = -(z / d * (double)power * 0.2);
                    this.setAcceleration(motionX, motionY, motionZ);
                    this.noPhysics = true;
                    this.setDeltaMovement(this.accelerationX, this.accelerationY, this.accelerationZ);
                    this.makeTreatParticles();
                    if (this.distanceToSqr(entity) < 6.0) {
                        entity.heal(5.0F);
                        this.makeHealParticles(entity);
                        this.kill();
                    }
                }
            }
        }

        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
    }

    protected float getStandingEyeHeight(Pose p_21131_, EntityDimensions p_21132_) {
        return 1.06F;
    }

    public void setAcceleration(double x, double y, double z) {
        this.accelerationX = x;
        this.accelerationY = y;
        this.accelerationZ = z;
    }

    public void makeTreatParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 10; ++i) {
                double d0 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d1 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d2 = (-0.5 + this.random.nextGaussian()) / 4.0;
                serverLevel.sendParticles(this.getParticle(), this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public void makeHealParticles(Entity caught) {
        if (this.level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 6; ++i) {
                double d0 = -0.5 + this.random.nextGaussian();
                double d1 = -0.5 + this.random.nextGaussian();
                double d2 = -0.5 + this.random.nextGaussian();
                serverLevel.sendParticles(ParticleTypes.HEART, caught.getRandomX(0.5), caught.getRandomY(), caught.getRandomZ(0.5), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public void distractAttackers() {
        if (this.distract) {
            if (this.getTrueOwner() != null) {
                List<Mob> list = this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(100.0D));
                for (Mob attacker : list) {
                    if (attacker.getLastHurtByMob() == this.getTrueOwner()) {
                        attacker.setLastHurtByMob(this);
                    }

                    if (attacker.getTarget() == this.getTrueOwner()) {
                        attacker.setTarget(this);
                    }

                    if (attacker instanceof Warden warden) {
                        if (warden.getTarget() == this.getTrueOwner()) {
                            warden.increaseAngerAt(this, AngerLevel.ANGRY.getMinimumAnger() + 100, false);
                            warden.setAttackTarget(this);
                        }
                    } else {
                        if (attacker.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent() && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() == this.getTrueOwner()) {
                            attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, this.getUUID(), 600L);
                            attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, this, 600L);
                        }
                    }
                }
            }
        }
    }

    public void resetTargeters() {
        List<Mob> list = this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(100.0));
        if (this.getTrueOwner() != null) {
            for (Mob attacker : list){
                if (attacker.getLastHurtByMob() == this && this.getTrueOwner() != null) {
                    attacker.setLastHurtByMob(this.getOwner());
                }

                if (attacker.getTarget() == this && this.getTrueOwner() != null) {
                    attacker.setTarget(this.getTrueOwner());
                }

                if (attacker instanceof Warden warden) {
                    if (warden.getTarget() == this) {
                        warden.increaseAngerAt(this.getTrueOwner(), AngerLevel.ANGRY.getMinimumAnger() + 100, false);
                        warden.setAttackTarget(this.getTrueOwner());
                    }
                } else {
                    try {
                        if (attacker.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent() && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() == this) {
                            attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, this.getTrueOwner().getUUID(), 600L);
                            attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, this.getTrueOwner(), 600L);
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }

    }

    public int getTreat() {
        return this.entityData.get(TREAT);
    }

    public void setTreat(int t) {
        this.entityData.set(TREAT, t);
    }

    protected void pushEntities() {
    }

    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
        this.deathTime = 19;
        this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_TRICKORTREAT_DESTROY.get(), 1.5F, 1.0F);
        this.makeTreatDestroyedParticles();
        this.resetTargeters();
    }

    private void circleOwner(Entity target, int circleFrame, float offset) {
        int directionInt = 1;
        double t = (double)(directionInt * circleFrame) * 0.5 * 1.0 / 8.0 + (double)offset;
        Vec3 movePos = target.position().add(8.0 * Math.cos(t), 0.0, 8.0 * Math.sin(t));
        this.getNavigation().moveTo(movePos.x(), movePos.y(), movePos.z(), 1.0);
    }

    private ParticleOptions getParticle() {
        ItemStack a = ItemRegisterer.TREAT1.get().getDefaultInstance();
        ItemStack b = ItemRegisterer.TREAT2.get().getDefaultInstance();
        ItemStack c = ItemRegisterer.TREAT3.get().getDefaultInstance();
        ItemStack d = ItemRegisterer.TREAT4.get().getDefaultInstance();
        ItemStack e = ItemRegisterer.TREAT5.get().getDefaultInstance();
        ItemStack f = ItemRegisterer.TREAT6.get().getDefaultInstance();
        return switch (this.getTreat()) {
            case 2 -> new ItemParticleOption(ParticleTypes.ITEM, b);
            case 3 -> new ItemParticleOption(ParticleTypes.ITEM, c);
            case 4 -> new ItemParticleOption(ParticleTypes.ITEM, d);
            case 5 -> new ItemParticleOption(ParticleTypes.ITEM, e);
            case 6 -> new ItemParticleOption(ParticleTypes.ITEM, f);
            default -> new ItemParticleOption(ParticleTypes.ITEM, a);
        };
    }

    public void makeTreatDestroyedParticles() {
        if (this.level instanceof ServerLevel serverLevel){
            for(int i = 0; i < 15; ++i) {
                double d0 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d1 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d2 = (-0.5 + this.random.nextGaussian()) / 4.0;
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
            }

            for(int i = 0; i < 150; ++i) {
                double d0 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d1 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d2 = (-0.5 + this.random.nextGaussian()) / 4.0;
                serverLevel.sendParticles(this.getParticle(), this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
            }
        }
    }

}
