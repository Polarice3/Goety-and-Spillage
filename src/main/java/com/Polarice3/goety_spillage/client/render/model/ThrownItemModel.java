package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;

public class ThrownItemModel<T extends Entity> extends EntityModel<T> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(GoetySpillage.location("thrown_item"), "main");
    private final ModelPart root;
    private final ModelPart item;

    public ThrownItemModel(ModelPart root) {
        this.root = root;
        this.item = root.getChild("item");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("item", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 7.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 0, 0);
    }

    @Override
    public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {
        this.item.translateAndRotate(p_102109_);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.item.xRot = ageInTicks * 25.0F * 0.017453292F;
        this.item.visible = false;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
