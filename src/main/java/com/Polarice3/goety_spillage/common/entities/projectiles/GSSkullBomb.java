package com.Polarice3.goety_spillage.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.goety_spillage.common.entities.GSEntityTypes;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class GSSkullBomb extends GSBomb {
    private static final EntityDataAccessor<Boolean> IS_SMALL = SynchedEntityData.defineId(GSSkullBomb.class, EntityDataSerializers.BOOLEAN);

    public GSSkullBomb(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SMALL, false);
    }

    @Override
    public int getMaxSwell() {
        return 40;
    }

    public void tick() {
        this.oldSwell = this.swell++;
        if (this.swell >= this.getMaxSwell()) {
            this.swell = this.getMaxSwell();
        }

        super.tick();
        if (this.tickCount == 1) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SHIVER.get(), 1.0F, 1.0F);
        }

        if (this.tickCount == 33) {
            this.playSound(IllageAndSpillageSoundEvents.ENTITY_FREAKAGER_SKULLBOMB_EXPLODE.get(), 3.0F, 1.0F);
        }

        if (this.tickCount >= 40) {
            this.explode();
        }

        if (this.onGround()) {
            this.setDeltaMovement((-0.5D + this.random.nextDouble()) * 0.6D, 0.3D, (-0.5D + this.random.nextDouble()) * 0.6D);
        }

    }

    private void explode() {
        if (!this.level.isClientSide) {
            float f = this.isSmall() ? 1.5F : 2.5F;
            this.dead = true;
            boolean loot = CuriosFinder.hasWanting(this.getMasterOwner());
            LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
            ExplosionUtil.lootExplode(this.level, this.getTrueOwner(), this.getX(), this.getY(), this.getZ(), f, false, Explosion.BlockInteraction.KEEP, lootMode);
            if (!this.isSmall()) {
                for (int i = 0; i < 4; ++i){
                    GSSkullBomb skullBomb = GSEntityTypes.SKULL_BOMB.get().create(this.level);
                    if (skullBomb != null) {
                        skullBomb.setPos(this.getX(), this.getY() + 0.25, this.getZ());
                        skullBomb.setSmall(true);
                        double burst = 0.3D;
                        if (i == 0){
                            skullBomb.setDeltaMovement(-burst, burst, -burst);
                        } else if (i == 1){
                            skullBomb.setDeltaMovement(-burst, burst, burst);
                        } else if (i == 2){
                            skullBomb.setDeltaMovement(burst, burst, -burst);
                        } else {
                            skullBomb.setDeltaMovement(burst, burst, burst);
                        }
                        if (this.getTrueOwner() != null) {
                            skullBomb.setTrueOwner(this.getTrueOwner());
                        }
                        this.level.addFreshEntity(skullBomb);
                    }
                }
            }

            this.discard();
        }

    }

    public void aiStep() {
        if (this.level.isClientSide) {
            for(int i = 0; i < 2; ++i) {
                double d0 = (-0.5D + this.random.nextGaussian()) / 12.0D;
                double d1 = (-0.5D + this.random.nextGaussian()) / 12.0D;
                double d2 = (-0.5D + this.random.nextGaussian()) / 12.0D;
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0D), this.getRandomY() + 0.25D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        }

        super.aiStep();
    }

    public boolean isSmall() {
        return this.entityData.get(IS_SMALL);
    }

    public void setSmall(boolean small) {
        this.entityData.set(IS_SMALL, small);
    }
}
