package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.goety_spillage.common.entities.IAttackMyOwner;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class GSIllagerSoul extends Owned implements IAttackMyOwner {
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(GSIllagerSoul.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANGEL_OR_DEVIL = SynchedEntityData.defineId(GSIllagerSoul.class, EntityDataSerializers.BOOLEAN);
    private int attackTicks;
    double chargeX;
    double chargeY;
    double chargeZ;
    double targetX;
    double targetY;
    double targetZ;
    private int chargeTime;
    private int oldSwell;
    private int swell;
    private final int maxSwell = 15;

    public GSIllagerSoul(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new LookAtTargetGoal());
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
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
        this.entityData.define(CHARGING, false);
        this.entityData.define(ANGEL_OR_DEVIL, false);
    }

    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return false;
    }

    public void tick() {
        this.noPhysics = true;
        boolean loot = CuriosFinder.hasWanting(this.getTrueOwner());
        LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
        if (this.getTarget() != null) {
            ++this.attackTicks;
            if (this.attackTicks > 50 && this.random.nextInt(6) == 0 && this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0) {
                LivingEntity entity = this.getTarget();
                double x = this.getX() - entity.getX();
                double y = this.getY() - entity.getY();
                double z = this.getZ() - entity.getZ();
                double d = Math.sqrt(x * x + y * y + z * z);
                float power = 3.5F;
                double motionX = this.getDeltaMovement().x - x / d * (double)power * 0.2;
                double motionY = this.getDeltaMovement().y - y / d * (double)power * 0.2;
                double motionZ = this.getDeltaMovement().z - z / d * (double)power * 0.2;
                this.playSound(IllageAndSpillageSoundEvents.ENTITY_SPIRITCALLER_SOULSCREAM.get(), 3.0F, this.getVoicePitch());
                this.setTargetPosition(entity.getX(), entity.getY(), entity.getZ());
                this.setCharge(motionX, motionY, motionZ);
            }
        }

        if (this.chargeX != 0.0 && this.chargeY != 0.0 && this.chargeZ != 0.0) {
            ++this.chargeTime;
            this.setDeltaMovement(this.chargeX, this.chargeY, this.chargeZ);
            if (!this.level.isClientSide) {
                this.setCharging(true);
            }

            if (this.getX() - this.targetX < 1.0 && this.getX() - this.targetX > -1.0 && this.getY() - this.targetY < 1.0 && this.getY() - this.targetY > -1.0 && this.getZ() - this.targetZ < 1.0 && this.getZ() - this.targetZ > -1.0 || this.chargeTime > 60) {
                if (!this.level.isClientSide) {
                    ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), 2.0F, false, Explosion.BlockInteraction.KEEP, lootMode);
                }

                this.kill();
            }
        } else {
            this.chargeTime = 0;
            if (!this.level.isClientSide) {
                this.setCharging(false);
            }
        }

        if (this.tickCount > 140) {
            if (!this.level.isClientSide) {
                ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), 2.0F, false, Explosion.BlockInteraction.KEEP, lootMode);
            }

            this.kill();
        }

        super.tick();
        this.setNoGravity(true);
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
        this.setInvulnerable(true);
        this.oldSwell = this.swell++;
        if (this.swell >= this.maxSwell) {
            this.swell = this.maxSwell;
        }

        if (this.getTrueOwner() instanceof Mob mob) {
            this.setTarget(mob.getTarget());
        }

        if (!this.isCharging()) {
            if (this.level.getBlockState(this.blockPosition().below()) != Blocks.AIR.defaultBlockState()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.04, 0.0));
            } else if (this.getDeltaMovement().y > 0.0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
            }
        }
    }

    public float getSwelling(float p_32321_) {
        return Mth.lerp(p_32321_, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean charge) {
        this.entityData.set(CHARGING, charge);
    }

    public boolean isAngelOrDevil() {
        return this.entityData.get(ANGEL_OR_DEVIL);
    }

    public void setAngelOrDevil(boolean what) {
        this.entityData.set(ANGEL_OR_DEVIL, what);
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

    public boolean ignoreExplosion() {
        return true;
    }

    public boolean hurt(DamageSource source, float p_21017_) {
        return (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) && super.hurt(source, p_21017_);
    }

    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
        this.deathTime = 19;
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return null;
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    class LookAtTargetGoal extends Goal {
        public LookAtTargetGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        public boolean canUse() {
            return GSIllagerSoul.this.getTarget() != null;
        }

        public boolean canContinueToUse() {
            return true;
        }

        public void tick() {
            if (GSIllagerSoul.this.getTarget() != null && !GSIllagerSoul.this.isCharging()) {
                GSIllagerSoul.this.getLookControl().setLookAt(GSIllagerSoul.this.getTarget(), 100.0F, 100.0F);
            }

            if (GSIllagerSoul.this.isCharging()) {
                GSIllagerSoul.this.getLookControl().setLookAt(GSIllagerSoul.this.targetX, GSIllagerSoul.this.targetY, GSIllagerSoul.this.targetZ, 100.0F, 100.0F);
            }

        }
    }
}
