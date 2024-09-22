package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class GSPumpkinBomb extends GSBomb {
    private static final EntityDataAccessor<Boolean> GOOPY = SynchedEntityData.defineId(GSPumpkinBomb.class, EntityDataSerializers.BOOLEAN);
    private int jumpTicks;
    private boolean isSwell;

    public GSPumpkinBomb(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GOOPY, false);
    }

    public boolean getGoopy() {
        return this.entityData.get(GOOPY);
    }

    public void setGoopy() {
        this.entityData.set(GOOPY, true);
    }

    public void tick() {
        if (this.jumpTicks >= 100) {
            this.isSwell = true;
        }

        if (this.isSwell) {
            this.oldSwell = this.swell++;
            if (this.swell == 1) {
                this.playSound(SoundEvents.TNT_PRIMED, 1.0F, 0.5F);
            }

            if (this.swell >= this.getMaxSwell()) {
                this.swell = this.getMaxSwell();
                this.explode();
            }
        }

        if (this.tickCount > 20) {
            if (this.isOnGround()) {
                ++this.jumpTicks;
            }

            if (this.getTarget() != null) {
                this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
                if (this.jumpTicks % 20 == 0 && this.isOnGround()) {
                    this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_PUMPKINBOMB_BOING.get(), 1.0F, 1.0F);
                    if (!this.level.isClientSide) {
                        this.setDeltaMovement((this.getTarget().getX() - this.getX()) * 0.4 * 0.16, 0.5, (this.getTarget().getZ() - this.getZ()) * 0.4 * 0.16);
                    }
                }
            }
        }

        super.tick();
    }

    @Override
    public int getMaxSwell() {
        return 30;
    }

    private void explode() {
        this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_PUMPKINBOMB_EXPLODE.get(), 3.0F, 1.0F);
        if (!this.level.isClientSide) {
            this.dead = true;
            boolean loot = CuriosFinder.hasWanting(this.getMasterOwner());
            LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
            ExplosionUtil.lootExplode(this.level, this.getTrueOwner(), this.getX(), this.getY(), this.getZ(), 4.0F, false, Explosion.BlockInteraction.NONE, lootMode);
            this.discard();
        }

    }

    public void aiStep() {
        if (this.level.isClientSide && this.tickCount >= 60) {
            for(int i = 0; i < 2; ++i) {
                double d0 = (-0.5 + this.random.nextGaussian()) / 12.0;
                double d1 = (-0.5 + this.random.nextGaussian()) / 12.0;
                double d2 = (-0.5 + this.random.nextGaussian()) / 12.0;
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0), this.getRandomY() + 0.25, this.getRandomZ(1.0), d0, d1, d2);
            }
        }

        super.aiStep();
    }
}
