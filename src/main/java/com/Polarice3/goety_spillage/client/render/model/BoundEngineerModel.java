package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundEngineer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yellowbrossproductions.illageandspillage.client.model.EngineerModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.EngineerAnimation;
import com.yellowbrossproductions.illageandspillage.entities.EngineerEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Calendar;

public class BoundEngineerModel<T extends Entity> extends EngineerModel<T> implements HierarchicalArmor {
    public final ModelPart all;
    public final ModelPart body;
    public final ModelPart left_arm;
    public final ModelPart right_arm;
    public final ModelPart left_leg;
    public final ModelPart right_leg;
    public final ModelPart head;
    public final ModelPart hat;

    public BoundEngineerModel(ModelPart root) {
        super(root);
        this.all = root.getChild("all");
        this.body = this.all.getChild("body");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.left_leg = this.all.getChild("left_leg");
        this.right_leg = this.all.getChild("right_leg");
        this.head = this.body.getChild("head");
        this.hat = this.head.getChild("hat");
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof BoundEngineer engineer) {
            this.animate(engineer.getAnimationState("throw"), EngineerAnimation.THROW, ageInTicks, engineer.getAnimationSpeed());
            this.animate(engineer.getAnimationState("repair"), EngineerAnimation.REPAIR, ageInTicks, engineer.getAnimationSpeed());
            Calendar calendar = Calendar.getInstance();
            this.body.getChild("head").getChild("hat").getChild("birthday").visible = calendar.get(2) == 1 && calendar.get(5) < 8;
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
        } else if (entity instanceof EngineerEntity engineer) {
            this.animate(engineer.getAnimationState("throw"), EngineerAnimation.THROW, ageInTicks, engineer.getAnimationSpeed());
            this.animate(engineer.getAnimationState("repair"), EngineerAnimation.REPAIR, ageInTicks, engineer.getAnimationSpeed());
            Calendar calendar = Calendar.getInstance();
            this.body.getChild("head").getChild("hat").getChild("birthday").visible = calendar.get(2) == 1 && calendar.get(5) < 8;
            this.head.yRot = netHeadYaw * 0.017453292F;
            this.head.xRot = headPitch * 0.017453292F;
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
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack headItem = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
            this.hat.visible = headItem.isEmpty();
        }
    }

    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
        this.all.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(0, 0.0F, 0);
    }

    @Override
    public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
        this.all.translateAndRotate(poseStack);
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(0.0F, -(12F / 16F), 0.0F);
        poseStack.scale(1.05F, 1.05F, 1.05F);
    }

    @Override
    public void translateToLeg(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
    }

    @Override
    public void translateToArms(ModelPart modelPart, PoseStack poseStack) {
        this.all.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        modelPart.translateAndRotate(poseStack);
        poseStack.scale(1.05F, 1.05F, 1.05F);
    }

    public Iterable<ModelPart> rightHandArmors() {
        return ImmutableList.of(this.right_arm);
    }

    public Iterable<ModelPart> leftHandArmors() {
        return ImmutableList.of(this.left_arm);
    }

    public Iterable<ModelPart> rightLegPartArmors() {
        return ImmutableList.of(this.right_leg);
    }

    public Iterable<ModelPart> leftLegPartArmors() {
        return ImmutableList.of(this.left_leg);
    }

    public Iterable<ModelPart> bodyPartArmors() {
        return ImmutableList.of(this.body);
    }

    public Iterable<ModelPart> headPartArmors() {
        return ImmutableList.of(this.head);
    }
}
