package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.projectiles.GSImp;
import com.yellowbrossproductions.illageandspillage.client.model.ImpModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class GSImpModel<T extends Entity> extends ImpModel<T> {
    private final ModelPart body;
    private final ModelPart block;

    public GSImpModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.block = root.getChild("block");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f3 = ageInTicks / 60.0F;
        this.body.getChild("head").yRot = netHeadYaw * 0.017453292F;
        this.body.getChild("head").xRot = headPitch * 0.017453292F;
        if (entity instanceof GSImp imp) {
            this.block.x = -0.5F + imp.getRandom().nextFloat();
            this.block.z = -0.5F + imp.getRandom().nextFloat();
        }

        if (entity instanceof GSImp imp) {
            if (imp.getStage() == 1) {
                this.body.visible = false;
                this.block.visible = true;
            } else {
                this.body.visible = true;
                this.block.visible = false;
            }

            if (imp.getStage() == 3) {
                this.body.getChild("left_arm").xRot = -2.0944F;
                this.body.getChild("left_arm").zRot = -1.7453F;
                this.body.getChild("head").y = Mth.sin(f3 * 40.0F) * 0.5F;
            } else {
                this.body.getChild("left_arm").xRot = 0.0F;
                this.body.getChild("left_arm").zRot = -0.6109F;
                this.body.getChild("head").y = 0.0F;
            }

            if (imp.getStage() == 2) {
                this.body.yRot = ageInTicks * 25.0F * 0.017453292F;
            } else {
                this.body.yRot = 0.0F;
            }

            if (imp.getStage() == 4) {
                this.body.xRot = 3.14159F;
            } else {
                this.body.xRot = 0.0F;
            }
        }

    }
}
