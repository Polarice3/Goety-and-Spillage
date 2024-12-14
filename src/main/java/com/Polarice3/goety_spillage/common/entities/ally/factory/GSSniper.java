package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class GSSniper extends FactoryServant implements RangedAttackMob {
    private int attackTicks;

    public GSSniper(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0)
                .add(Attributes.FOLLOW_RANGE, 50.0);
    }

    @Override
    public void followGoal() {
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    public void die(DamageSource p_37847_) {
        this.makeDeathParticles();
        this.deathTime = 19;
        if (!this.isAlive()) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 0.5F, 3.0F);
        }

        super.die(p_37847_);
    }

    public void makeDeathParticles() {
        if (this.level instanceof ServerLevel serverLevel){
            for(int i = 0; i < 15; ++i) {
                double d0 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d1 = (-0.5 + this.random.nextGaussian()) / 4.0;
                double d2 = (-0.5 + this.random.nextGaussian()) / 4.0;
                serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, Items.IRON_BARS.getDefaultInstance()), this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    public void tick() {
        this.setNoGravity(true);
        ++this.attackTicks;
        LivingEntity flyTo = this.getTarget() != null ? this.getTarget() : (this.getOwner() != null && this.getMasterOwner() != null && this.getMasterOwner().isAlive() ? this.getMasterOwner() : null);
        if (flyTo != null) {
            if (this.attackTicks > 40 && flyTo == this.getTarget() && this.distanceToSqr(flyTo) <= 400.0 && this.hasLineOfSight(flyTo)) {
                this.playSound(SoundEvents.DISPENSER_LAUNCH, 1.0F, 1.0F);
                this.performRangedAttack(this.getTarget(), 1.0F);
                this.attackTicks = 0;
            }

            this.getLookControl().setLookAt(flyTo, 100.0F, 100.0F);
            double x = this.getX() - flyTo.getX();
            double y = this.getY() - flyTo.getY();
            double z = this.getZ() - flyTo.getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            float power = 0.08F;
            double motionX = this.getDeltaMovement().x - x / d * (double)power * 0.2;
            double motionY = this.getDeltaMovement().y - y / d * (double)power * 0.2;
            double motionZ = this.getDeltaMovement().z - z / d * (double)power * 0.2;
            if (this.distanceToSqr(flyTo) > 120.0) {
                this.setDeltaMovement(motionX, motionY, motionZ);
            }

            if (this.level.getBlockState(this.blockPosition().below(5)) == Blocks.AIR.defaultBlockState()
                    && this.level.getBlockState(this.blockPosition().below(4)) == Blocks.AIR.defaultBlockState()
                    && this.level.getBlockState(this.blockPosition().below(3)) == Blocks.AIR.defaultBlockState()
                    && this.level.getBlockState(this.blockPosition().below(2)) == Blocks.AIR.defaultBlockState()
                    && this.level.getBlockState(this.blockPosition().below(1)) == Blocks.AIR.defaultBlockState()) {
                if (this.getDeltaMovement().y > 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.01, 0.0));
                }
            } else {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.04, 0.0));
            }
        } else if (this.level.getBlockState(this.blockPosition().below(1)) != Blocks.AIR.defaultBlockState()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.04, 0.0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.01, 0.0));
        }

        super.tick();
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        AbstractArrow abstractarrowentity = this.getArrow(Items.BOW.getDefaultInstance(), p_33318_);
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrowentity = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
        }

        double d0 = p_33317_.getX() - this.getX();
        double d1 = p_33317_.getY(0.3333333333333333) - abstractarrowentity.getY();
        double d2 = p_33317_.getZ() - this.getZ();
        double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
        abstractarrowentity.setBaseDamage(1.0);
        abstractarrowentity.setPos(this.getX(), this.getY() + 0.5, this.getZ());
        abstractarrowentity.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, 1.0F);
        this.level.addFreshEntity(abstractarrowentity);
    }

    protected AbstractArrow getArrow(ItemStack p_213624_1_, float p_213624_2_) {
        return ProjectileUtil.getMobArrow(this, p_213624_1_, p_213624_2_);
    }

    public boolean doHurtTarget(Entity p_21372_) {
        return true;
    }
}
