package com.Polarice3.goety_spillage.common.entities.ally.factory;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.goety_spillage.common.entities.ai.FollowMasterOwnerGoal;
import com.yellowbrossproductions.illageandspillage.entities.IllagerAttack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class FactoryServant extends Summoned implements IllagerAttack {
    public FactoryServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new FollowMasterOwnerGoal<>(this, 1.0F, 2.0F, 100.0F));
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

    @Override
    public void tick() {
        super.tick();
        if (this.getTrueOwner() != null){
            if (this.getTrueOwner() instanceof IOwned owned){
                if (this.getTrueOwner().isDeadOrDying()){
                    if (owned.getTrueOwner() != null && owned.getTrueOwner().isAlive()) {
                        this.setTrueOwner(owned.getTrueOwner());
                    } else if (owned.getMasterOwner() != null) {
                        this.setTrueOwner(owned.getMasterOwner());
                    }
                }
            }
        }
    }
}
