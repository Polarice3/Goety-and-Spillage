package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.undead.GSFunnybone;
import com.yellowbrossproductions.illageandspillage.client.model.FunnyboneModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.FunnyboneAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.Calendar;

public class GSFunnyboneModel<T extends Entity> extends FunnyboneModel<T> {
    private final ModelPart all;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart spine;
    private final ModelPart ribs;
    private final ModelPart head;
    private final ModelPart arm3;
    private final ModelPart arm4;
    private final ModelPart finger3;
    private final ModelPart finger4;
    private final ModelPart arm1;
    private final ModelPart arm2;
    private final ModelPart finger1;
    private final ModelPart finger2;
    private final ModelPart bone_weapon;

    public GSFunnyboneModel(ModelPart root) {
        super(root);
        this.all = root.getChild("all");
        this.leg1 = this.all.getChild("leg1");
        this.leg2 = this.leg1.getChild("leg2");
        this.leg3 = this.all.getChild("leg3");
        this.leg4 = this.leg3.getChild("leg4");
        this.spine = this.all.getChild("spine");
        this.ribs = this.spine.getChild("ribs");
        this.head = this.ribs.getChild("head");
        this.arm3 = this.ribs.getChild("arm3");
        this.arm4 = this.arm3.getChild("arm4");
        this.finger3 = this.arm4.getChild("finger3");
        this.finger4 = this.arm4.getChild("finger4");
        this.arm1 = this.ribs.getChild("arm1");
        this.arm2 = this.arm1.getChild("arm2");
        this.finger1 = this.arm2.getChild("finger1");
        this.finger2 = this.arm2.getChild("finger2");
        this.bone_weapon = this.arm2.getChild("bone_weapon");
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof GSFunnybone funnybone) {
            this.animate(funnybone.getAnimationState("idle"), FunnyboneAnimation.IDLE, ageInTicks, funnybone.getAnimationSpeed());
            this.animate(funnybone.getAnimationState("run"), FunnyboneAnimation.RUN, ageInTicks, funnybone.getAnimationSpeed());
            this.animate(funnybone.getAnimationState("fly"), FunnyboneAnimation.FLY, ageInTicks, funnybone.getAnimationSpeed());
            this.animate(funnybone.getAnimationState("spawn"), FunnyboneAnimation.SPAWN, ageInTicks, funnybone.getAnimationSpeed());
            this.animate(funnybone.getAnimationState("throw"), FunnyboneAnimation.THROW, ageInTicks, funnybone.getAnimationSpeed());
            Calendar calendar = Calendar.getInstance();
            this.head.getChild("birthday").visible = calendar.get(2) == 1 && calendar.get(5) < 8;
            this.bone_weapon.visible = funnybone.shouldShowBone();
            if (!funnybone.isFlying()) {
                this.head.yRot = netHeadYaw * 0.017453292F;
                this.head.xRot = headPitch * 0.017453292F - 0.7F;
            } else {
                this.head.xRot = -70.0F;
                this.head.yRot = 0.0F;
            }

            this.head.xScale = 1.25F;
            this.head.yScale = 1.25F;
            this.head.zScale = 1.25F;
            this.spine.xScale = 0.75F;
            this.spine.yScale = 0.75F;
            this.spine.zScale = 0.75F;
            this.leg1.xScale = 0.75F;
            this.leg1.yScale = 0.75F;
            this.leg1.zScale = 0.75F;
            this.leg3.xScale = 0.75F;
            this.leg3.yScale = 0.75F;
            this.leg3.zScale = 0.75F;
            if (funnybone.idleAnimationState.isStarted()) {
                this.arm3.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount * 0.5F;
                this.arm1.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
                this.leg3.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 1.0F - 0.5F;
                this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount * 1.0F - 1.0F;
            }

            if (!funnybone.isThrowing) {
                float moveX = (float)(funnybone.getX() - funnybone.xo);
                float moveZ = (float)(funnybone.getZ() - funnybone.zo);
                float speed = Mth.sqrt(moveX * moveX + moveZ * moveZ);
                if ((double)speed > 0.2) {
                    if (!funnybone.runAnimationState.isStarted() && this.isNotAnimating(funnybone)) {
                        funnybone.idleAnimationState.stop();
                        funnybone.runAnimationState.start(funnybone.tickCount);
                    }
                } else if (!funnybone.idleAnimationState.isStarted() && this.isNotAnimating(funnybone)) {
                    funnybone.runAnimationState.stop();
                    funnybone.idleAnimationState.start(funnybone.tickCount);
                } else if (!this.isNotAnimating(funnybone)) {
                    funnybone.idleAnimationState.stop();
                    funnybone.runAnimationState.stop();
                }
            }
        }

    }

    private boolean isNotAnimating(GSFunnybone funnybone) {
        return !funnybone.flyAnimationState.isStarted() && !funnybone.spawnAnimationState.isStarted() && !funnybone.throwAnimationState.isStarted();
    }
}
