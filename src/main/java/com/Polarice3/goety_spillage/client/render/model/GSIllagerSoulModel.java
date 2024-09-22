package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.projectiles.GSIllagerSoul;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yellowbrossproductions.illageandspillage.client.model.IllagerSoulModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class GSIllagerSoulModel<T extends Entity> extends IllagerSoulModel<T> {
    private final ModelPart body;

    public GSIllagerSoulModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof GSIllagerSoul soul) {
            if (soul.isCharging()) {
                this.body.getChild("head").xRot = -1.5708F;
                this.body.getChild("head").yRot = 0.0F;
                this.body.xRot = 1.5708F + headPitch * 0.017453292F;
                this.body.getChild("right_arm").zRot = 0.0F;
                this.body.getChild("left_arm").zRot = 0.0F;
            } else {
                this.body.getChild("head").yRot = netHeadYaw * 0.017453292F;
                this.body.getChild("head").xRot = headPitch * 0.017453292F;
                this.body.xRot = 0.0F;
                this.body.getChild("right_arm").zRot = -0.2618F;
                this.body.getChild("left_arm").zRot = 0.2618F;
            }
        }

    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
