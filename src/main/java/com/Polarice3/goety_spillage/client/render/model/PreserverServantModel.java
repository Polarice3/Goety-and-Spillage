package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.illager.PreserverServant;
import com.yellowbrossproductions.illageandspillage.client.model.PreserverModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.Calendar;

public class PreserverServantModel<T extends Entity> extends PreserverModel<T> {
    private final ModelPart body;

    public PreserverServantModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity instanceof PreserverServant preserver) {
            Calendar calendar = Calendar.getInstance();
            this.body.getChild("head").getChild("birthday").visible = calendar.get(2) == 1 && calendar.get(5) < 8;
            if (!preserver.isTryingToProtect()) {
                this.body.getChild("head").yRot = netHeadYaw * 0.017453292F;
                this.body.getChild("head").xRot = headPitch * 0.017453292F;
                this.body.y = -Math.abs(Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F) * 4.0F + 6.0F;
                this.body.xRot = -Math.abs(Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F) * 0.1F;
                this.body.getChild("left_leg").xRot = Mth.cos(limbSwing * 1.6F) * 1.4F * limbSwingAmount * 0.2F;
                this.body.getChild("right_leg").xRot = Mth.cos(limbSwing * 1.6F + 3.1415927F) * 1.4F * limbSwingAmount * 0.2F;
            } else {
                float multiplier = 0.1F * (float)Math.min(preserver.getJumpAnimationTick(), 10);
                this.body.yRot = netHeadYaw * 0.017453292F * multiplier;
                this.body.xRot = (headPitch * 0.017453292F + 1.5707F) * multiplier;
            }
        }

    }
}
