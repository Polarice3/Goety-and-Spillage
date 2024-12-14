package com.Polarice3.goety_spillage.common.entities.ai;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import java.util.EnumSet;

public class FollowMasterOwnerGoal<T extends Mob & IServant> extends Goal {
    private final T summonedEntity;
    private LivingEntity owner;
    private final double followSpeed;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float minDist;
    private final float maxDist;
    private float oldWaterCost;

    public FollowMasterOwnerGoal(T summonedEntity, double followSpeed, float minimumDistance, float maximumDistance) {
        this.summonedEntity = summonedEntity;
        this.followSpeed = followSpeed;
        this.navigation = summonedEntity.getNavigation();
        this.minDist = minimumDistance;
        this.maxDist = maximumDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(summonedEntity.getNavigation() instanceof GroundPathNavigation) && !(summonedEntity.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMasterOwnerGoal");
        }
    }

    public boolean canUse() {
        if (this.summonedEntity.getTrueOwner() != null) {
            LivingEntity owner = this.summonedEntity.getMasterOwner();
            if (owner == null) {
                return false;
            }

            if (owner.isSpectator()) {
                return false;
            }

            if (!(this.summonedEntity.distanceToSqr(owner) <= (double)(this.minDist * this.minDist)) && !(this.summonedEntity.distanceToSqr(owner) >= (double)(this.maxDist * this.maxDist))) {
                if (this.summonedEntity.getTarget() != null) {
                    return false;
                }

                this.owner = owner;
                return true;
            }

            return false;
        }

        return false;
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (!(this.summonedEntity.distanceToSqr(this.owner) <= (double)(this.minDist * this.minDist)) && !(this.summonedEntity.distanceToSqr(this.owner) >= (double)(this.maxDist * this.maxDist))) {
            return this.summonedEntity.getTarget() == null;
        } else {
            return false;
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.summonedEntity.getPathfindingMalus(BlockPathTypes.WATER);
        this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.summonedEntity.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.summonedEntity.isLeashed() && !this.summonedEntity.isPassenger()) {
                this.navigation.moveTo(this.owner, this.followSpeed);
            }
        }

    }
}
