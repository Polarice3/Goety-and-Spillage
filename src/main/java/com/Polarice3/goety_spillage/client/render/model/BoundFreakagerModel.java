package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundFreakager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yellowbrossproductions.illageandspillage.client.model.animation.FreakagerAnimation;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;

import java.util.Random;

public class BoundFreakagerModel<T extends Entity> extends HierarchicalModel<T> implements HeadedModel, ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(GoetySpillage.location("bound_freakager"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart arms;
    private final ModelPart scythe;
    private final Random random = new Random();

    public BoundFreakagerModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.arms = this.body.getChild("arms");
        this.scythe = this.right_arm.getChild("scythe");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 14.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 3.5F, 0.3F));
        PartDefinition arms_rotation = arms.addOrReplaceChild("arms_rotation", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, 0.0F, -2.05F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(40, 38).addBox(-4.0F, 4.0F, -2.05F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.05F, -0.7505F, 0.0F, 0.0F));
        arms_rotation.addOrReplaceChild("arms_flipped", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(4.0F, -24.0F, -2.05F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 24.0F, 0.0F));
        body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        right_arm.addOrReplaceChild("scythe", CubeListBuilder.create().texOffs(144, 148).addBox(0.0F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(-0.1F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(-0.2F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(-0.3F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(-0.4F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(-0.5F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(0.1F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(0.2F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(0.3F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(0.4F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(144, 148).addBox(0.5F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 14.0F, -2.0F, 2.3562F, 0.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(32, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.5F, 0.1745F, 0.0F, 0.0F));
        hat.addOrReplaceChild("hat_littlepiece", CubeListBuilder.create().texOffs(116, 0).addBox(-1.5F, -5.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.5236F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 192, 196);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof BoundFreakager freakager) {
            this.animate(freakager.getAnimationState("bombs"), FreakagerAnimation.BOMBS, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("axes"), FreakagerAnimation.AXES, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("fastaxes"), FreakagerAnimation.FASTAXES, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("potions"), FreakagerAnimation.POTIONS, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("scythe"), FreakagerAnimation.SCYTHE, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("trickortreat"), FreakagerAnimation.TRICKORTREAT, ageInTicks, freakager.getAnimationSpeed());
            this.head.yRot += netHeadYaw * ((float)Math.PI / 180F);
            this.head.xRot += headPitch * ((float)Math.PI / 180F);
            this.left_arm.visible = freakager.shouldShowArms();
            this.right_arm.visible = freakager.shouldShowArms();
            this.arms.visible = !freakager.shouldShowArms();
            this.scythe.visible = freakager.shouldShowScythe();
            if (freakager.getHealth() < freakager.getMaxHealth() / 2.0F) {
                this.head.x += (-0.5F + this.random.nextFloat()) * 1.2F;
                this.head.y += (-0.5F + this.random.nextFloat()) * 1.2F;
                this.head.z += (-0.5F + this.random.nextFloat()) * 1.2F;
            }
        }
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public ModelPart root() {
        return this.root;
    }

    public ModelPart getHead() {
        return this.head;
    }

    private ModelPart getArm(HumanoidArm p_191216_1_) {
        return p_191216_1_ == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
    }

    public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {
        this.root().translateAndRotate(p_102109_);
        this.body.translateAndRotate(p_102109_);
        this.getArm(p_102108_).translateAndRotate(p_102109_);
    }
}
