package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.CrocofangServant;
import com.yellowbrossproductions.illageandspillage.client.model.CrocofangModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.CrocofangAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CrocofangServantModel<T extends Entity> extends CrocofangModel<T> {
    private final ModelPart bone;
    public CrocofangServantModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof CrocofangServant crocofang) {
            this.animate(crocofang.getAnimationState("attack"), CrocofangAnimation.BITE, ageInTicks, crocofang.getAnimationSpeed());
            this.animate(crocofang.getAnimationState("precharge"), CrocofangAnimation.PRECHARGE, ageInTicks, crocofang.getAnimationSpeed());
            this.animate(crocofang.getAnimationState("charge"), CrocofangAnimation.CHARGE, ageInTicks, crocofang.getAnimationSpeed());
            this.animate(crocofang.getAnimationState("stunned"), CrocofangAnimation.STUNNED, ageInTicks, crocofang.getAnimationSpeed());
            ModelPart var10000 = this.bone.getChild("body").getChild("head");
            var10000.yRot += netHeadYaw * 0.017453292F;
            var10000 = this.bone.getChild("body").getChild("head");
            var10000.xRot += headPitch * 0.017453292F * -1.0F;
            if (!crocofang.isCharging()) {
                var10000 = this.bone.getChild("body").getChild("legs").getChild("leg1");
                var10000.xRot += Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
                var10000 = this.bone.getChild("body").getChild("legs").getChild("leg3");
                var10000.xRot += Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
                var10000 = this.bone.getChild("body").getChild("legs").getChild("leg2");
                var10000.xRot += Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount * 0.5F;
                var10000 = this.bone.getChild("body").getChild("legs").getChild("leg4");
                var10000.xRot += Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount * 0.5F;
            }
        }

    }
}
