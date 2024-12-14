package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundEngineer;
import com.yellowbrossproductions.illageandspillage.client.model.EngineerModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.EngineerAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BoundEngineerModel<T extends Entity> extends EngineerModel<T> {
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;
    private final ModelPart head;

    public BoundEngineerModel(ModelPart root) {
        super(root);
        ModelPart all = root.getChild("all");
        ModelPart body = all.getChild("body");
        this.left_arm = body.getChild("left_arm");
        this.right_arm = body.getChild("right_arm");
        this.left_leg = all.getChild("left_leg");
        this.right_leg = all.getChild("right_leg");
        this.head = body.getChild("head");
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof BoundEngineer engineer) {
            this.animate(engineer.getAnimationState("throw"), EngineerAnimation.THROW, ageInTicks, engineer.getAnimationSpeed());
            this.animate(engineer.getAnimationState("repair"), EngineerAnimation.REPAIR, ageInTicks, engineer.getAnimationSpeed());
            this.head.yRot = netHeadYaw * 0.017453292F;
            this.head.xRot = headPitch * 0.017453292F;
            this.right_leg.visible = false;
            this.left_leg.visible = false;
            if (this.riding) {
                if (engineer.getAnimationState() == 0) {
                    this.right_arm.xRot = -0.62831855F;
                    this.left_arm.xRot = -0.62831855F;
                    this.right_arm.yRot = 0.0F;
                    this.right_arm.zRot = 0.0F;
                    this.left_arm.yRot = 0.0F;
                    this.left_arm.zRot = 0.0F;
                }

                this.right_leg.xRot = -1.4137167F;
                this.right_leg.yRot = 0.31415927F;
                this.right_leg.zRot = 0.07853982F;
                this.left_leg.xRot = -1.4137167F;
                this.left_leg.yRot = -0.31415927F;
                this.left_leg.zRot = -0.07853982F;
            } else {
                if (engineer.getAnimationState() == 0) {
                    this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount * 0.5F;
                    this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
                    this.right_arm.yRot = 0.0F;
                    this.right_arm.zRot = 0.0F;
                    this.left_arm.yRot = 0.0F;
                    this.left_arm.zRot = 0.0F;
                }

                this.right_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
                this.right_leg.yRot = 0.0F;
                this.right_leg.zRot = 0.0F;
                this.left_leg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount * 0.5F;
                this.left_leg.yRot = 0.0F;
                this.left_leg.zRot = 0.0F;
            }
        }

    }
}
