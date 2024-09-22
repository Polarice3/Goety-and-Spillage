package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.GSSpiritHand;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yellowbrossproductions.illageandspillage.client.model.SpiritHandModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class GSSpiritHandModel<T extends Entity> extends SpiritHandModel<T> {
    private final ModelPart hand1;
    private final ModelPart hand2;

    public GSSpiritHandModel(ModelPart root) {
        super(root);
        this.hand1 = root.getChild("hand1");
        this.hand2 = root.getChild("hand2");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof GSSpiritHand hand) {
            this.hand1.xRot = headPitch * 0.017453292F * -1.0F;
            this.hand2.xRot = headPitch * 0.017453292F * -1.0F;
            if (hand.getAttackType() == 1) {
                this.hand2.xRot = 1.5708F;
                this.hand2.zRot = -3.1416F;
                this.hand2.z = -9.0F;
                this.hand1.zRot = 1.5708F;
                this.hand1.xRot = -1.5708F;
                this.hand1.getChild("finger1").xRot = -1.5708F;
                this.hand1.getChild("finger1").zRot = 0.0F;
                this.hand1.getChild("finger2").xRot = -1.5708F;
                this.hand1.getChild("finger2").zRot = 0.0F;
                this.hand1.getChild("finger3").xRot = -1.5708F;
                this.hand1.getChild("finger3").zRot = 0.0F;
                this.hand1.getChild("finger4").xRot = -1.5708F;
                this.hand1.getChild("finger4").zRot = 0.0F;
                this.hand1.getChild("finger5").xRot = -1.5708F;
                this.hand1.getChild("finger5").zRot = 1.5708F;
                this.hand1.getChild("finger1").getChild("fingersmaller1").xRot = -1.5708F;
                this.hand1.getChild("finger2").getChild("fingersmaller2").xRot = -1.5708F;
                this.hand1.getChild("finger3").getChild("fingersmaller3").xRot = -1.5708F;
                this.hand1.getChild("finger4").getChild("fingersmaller4").xRot = -1.5708F;
                this.hand1.getChild("finger5").getChild("fingersmaller5").xRot = -1.5708F;
                this.hand1.getChild("finger1").getChild("fingersmaller1").getChild("fingersmallest1").xRot = 0.0F;
                this.hand1.getChild("finger2").getChild("fingersmaller2").getChild("fingersmallest2").xRot = 0.0F;
                this.hand1.getChild("finger3").getChild("fingersmaller3").getChild("fingersmallest3").xRot = 0.0F;
                this.hand1.getChild("finger4").getChild("fingersmaller4").getChild("fingersmallest4").xRot = 0.0F;
                this.hand1.getChild("finger5").getChild("fingersmaller5").getChild("fingersmallest5").xRot = 0.0F;
            } else if (hand.getAttackType() == 2) {
                this.hand2.xRot = -1.5708F + headPitch * 0.017453292F * -1.0F;
                this.hand2.zRot = -3.1416F;
                this.hand2.z = -4.0F;
                this.hand1.zRot = -3.1416F;
                this.hand1.xRot = -1.5708F + headPitch * 0.017453292F * -1.0F;
                this.hand1.getChild("finger1").xRot = -1.5708F;
                this.hand1.getChild("finger1").zRot = 0.0F;
                this.hand1.getChild("finger2").xRot = -1.5708F;
                this.hand1.getChild("finger2").zRot = 0.0F;
                this.hand1.getChild("finger3").xRot = -1.5708F;
                this.hand1.getChild("finger3").zRot = 0.0F;
                this.hand1.getChild("finger4").xRot = -1.5708F;
                this.hand1.getChild("finger4").zRot = 0.0F;
                this.hand1.getChild("finger5").xRot = -1.5708F;
                this.hand1.getChild("finger5").zRot = 1.5708F;
                this.hand1.getChild("finger1").getChild("fingersmaller1").xRot = -1.5708F;
                this.hand1.getChild("finger2").getChild("fingersmaller2").xRot = -1.5708F;
                this.hand1.getChild("finger3").getChild("fingersmaller3").xRot = -1.5708F;
                this.hand1.getChild("finger4").getChild("fingersmaller4").xRot = -1.5708F;
                this.hand1.getChild("finger5").getChild("fingersmaller5").xRot = -1.5708F;
                this.hand1.getChild("finger1").getChild("fingersmaller1").getChild("fingersmallest1").xRot = 0.0F;
                this.hand1.getChild("finger2").getChild("fingersmaller2").getChild("fingersmallest2").xRot = 0.0F;
                this.hand1.getChild("finger3").getChild("fingersmaller3").getChild("fingersmallest3").xRot = 0.0F;
                this.hand1.getChild("finger4").getChild("fingersmaller4").getChild("fingersmallest4").xRot = 0.0F;
                this.hand1.getChild("finger5").getChild("fingersmaller5").getChild("fingersmallest5").xRot = 0.0F;
            } else {
                this.hand2.xRot = headPitch * 0.017453292F * -1.0F;
                this.hand2.zRot = -3.1416F;
                this.hand2.z = -4.0F;
                this.hand1.xRot = headPitch * 0.017453292F * -1.0F;
                this.hand1.zRot = -3.1416F;
                this.hand1.getChild("finger1").xRot = 0.0F;
                this.hand1.getChild("finger1").zRot = 0.0873F;
                this.hand1.getChild("finger2").xRot = 0.0F;
                this.hand1.getChild("finger2").zRot = 0.0F;
                this.hand1.getChild("finger3").xRot = 0.0F;
                this.hand1.getChild("finger3").zRot = -0.0873F;
                this.hand1.getChild("finger4").xRot = 0.0F;
                this.hand1.getChild("finger4").zRot = -0.2618F;
                this.hand1.getChild("finger5").xRot = 0.0F;
                this.hand1.getChild("finger5").zRot = 0.6981F;
                this.hand1.getChild("finger1").getChild("fingersmaller1").xRot = -0.4363F;
                this.hand1.getChild("finger2").getChild("fingersmaller2").xRot = -0.2618F;
                this.hand1.getChild("finger3").getChild("fingersmaller3").xRot = -0.4363F;
                this.hand1.getChild("finger4").getChild("fingersmaller4").xRot = -0.4363F;
                this.hand1.getChild("finger5").getChild("fingersmaller5").xRot = -0.7854F;
                this.hand1.getChild("finger1").getChild("fingersmaller1").getChild("fingersmallest1").xRot = -0.4363F;
                this.hand1.getChild("finger2").getChild("fingersmaller2").getChild("fingersmallest2").xRot = -0.2618F;
                this.hand1.getChild("finger3").getChild("fingersmaller3").getChild("fingersmallest3").xRot = -0.6109F;
                this.hand1.getChild("finger4").getChild("fingersmaller4").getChild("fingersmallest4").xRot = -0.7854F;
                this.hand1.getChild("finger5").getChild("fingersmaller5").getChild("fingersmallest5").xRot = -0.7854F;
            }

            this.hand1.visible = !hand.isGoodOrEvil();
            this.hand2.visible = hand.isGoodOrEvil();
        }

    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.hand1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.hand2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
