package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundFreakager;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FreakyScythe extends MobProjectile {
    public LivingEntity goFor = null;
    private boolean shouldReturn = false;
    public boolean halfHP = false;

    public FreakyScythe(EntityType<? extends MobProjectile> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
        this.damage = 5.0F;
    }

    public int getLifeSpan(){
        return 300;
    }

    public void tick() {
        this.setNoGravity(true);
        LivingEntity attacker = this.shooter != null ? this.shooter : this;
        List<Entity> list = this.level.getEntities(this, new AABB(this.getX() - 0.4, this.getY() - 0.4, this.getZ() - 0.4, this.getX() + 0.4, this.getY() + 0.4, this.getZ() + 0.4), Entity::isAlive);
        for (Entity entity : list) {
            if (entity instanceof LivingEntity living) {
                boolean canHurt = attacker instanceof Mob ? !MobUtil.areAllies(living, attacker) : living != this.shooter;
                if (canHurt && entity.isAlive() && !entity.isInvulnerable() && !entity.isSpectator()) {
                    living.hurt(this.damageSources().thrown(living, attacker), 5.0F);
                }
            }
        }

        if (this.tickCount % 4 == 0) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SCYTHE_SPIN.get(), 1.0F, 1.0F);
        }

        if (this.tickCount > 40 && !this.halfHP) {
            this.shouldReturn = true;
        } else if (this.tickCount > 160) {
            this.shouldReturn = true;
        }

        if (!this.halfHP) {
            if (!this.level.getBlockState(this.blockPosition().above()).isAir()) {
                this.shouldReturn = true;
            }
        } else if (this.tickCount > 120 && !this.level.getBlockState(this.blockPosition().above()).isAir()) {
            this.shouldReturn = true;
        }

        if (this.shouldReturn && this.shooter != null) {
            double x = this.getX() - this.shooter.getX();
            double y = this.getY() - (this.shooter.getY() + 2.2);
            double z = this.getZ() - this.shooter.getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            float power = 3.0F;
            double motionX = -(x / d * (double)power * 0.2);
            double motionY = -(y / d * (double)power * 0.2);
            double motionZ = -(z / d * (double)power * 0.2);
            this.setAcceleration(motionX, motionY, motionZ);
            if (this.distanceToSqr(this.shooter) < 6.0) {
                if (this.shooter instanceof BoundFreakager boundFreakager) {
                    boundFreakager.waitingForScythe = false;
                }

                if (!this.level.isClientSide) {
                    this.discard();
                }
            }
        }

        if (this.halfHP && (this.tickCount == 40 || this.tickCount == 80 || this.tickCount == 120)) {
            double x = this.getX() - this.goFor.getX();
            double y = this.getY() - (this.goFor.getY() + 1.5);
            double z = this.getZ() - this.goFor.getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            float power = 3.0F;
            double motionX = -(x / d * (double)power * 0.2);
            double motionY = -(y / d * (double)power * 0.2);
            double motionZ = -(z / d * (double)power * 0.2);
            this.setAcceleration(motionX, motionY, motionZ);
        }

        if (this.shooter != null && !this.shooter.isAlive()) {
            this.discard();
        }

        super.tick();
    }

    public void setParticles(ServerLevel serverLevel) {
        double d0 = -0.5D + this.random.nextGaussian();
        double d1 = -0.5D + this.random.nextGaussian();
        double d2 = -0.5D + this.random.nextGaussian();
        serverLevel.sendParticles(ParticleTypes.SMOKE, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0, d0, d1, d2, 0.5F);
    }

    @Override
    public void remove(RemovalReason p_146834_) {
        if (this.shooter instanceof BoundFreakager freakager){
            if (freakager.waitingForScythe){
                freakager.waitingForScythe = false;
            }
        }
        super.remove(p_146834_);
    }

    public void setGoFor(LivingEntity goFor) {
        this.goFor = goFor;
    }
}
