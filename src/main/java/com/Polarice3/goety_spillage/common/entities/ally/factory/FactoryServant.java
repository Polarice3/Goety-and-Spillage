package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.EntityFinder;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class FactoryServant extends Summoned implements IllagerAttack {
    protected static final EntityDataAccessor<Optional<UUID>> FACTORY_UUID = SynchedEntityData.defineId(FactoryServant.class, EntityDataSerializers.OPTIONAL_UUID);

    public FactoryServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FACTORY_UUID, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Factory")) {
            this.setFactoryID(compound.getUUID("Factory"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getFactory() != null) {
            compound.putUUID("Factory", this.getFactory().getUUID());
        }
    }

    @Nullable
    public LivingEntity getFactory() {
        UUID uuid = this.getFactoryID();
        return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(this.level, uuid);
    }

    @Nullable
    public UUID getFactoryID() {
        return this.entityData.get(FACTORY_UUID).orElse(null);
    }

    public void setFactoryID(@Nullable UUID p_184754_1_) {
        this.entityData.set(FACTORY_UUID, Optional.ofNullable(p_184754_1_));
    }

    public void setFactory(@Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setFactoryID(livingEntity.getUUID());
        }
    }

    @Override
    public boolean canUpdateMove() {
        return false;
    }

    @Override
    public int xpReward() {
        return 0;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
    }
}
