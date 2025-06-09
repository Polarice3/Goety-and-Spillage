package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.illager.IgniterServant;
import com.yellowbrossproductions.illageandspillage.client.model.IgniterModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class IgniterServantModel<T extends Entity> extends IgniterModel<T> {
    private final ModelPart head;
    private final ModelPart right_arm;
    private final ModelPart dispenser;

    public IgniterServantModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.right_arm = root.getChild("right_arm");
        this.dispenser = root.getChild("dispenser");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity instanceof IgniterServant igniter) {
            this.head.getChild("mask_on").visible = igniter.isAttacking();
            this.head.getChild("mask_off").visible = !igniter.isAttacking();
            this.dispenser.getChild("torch_lit").visible = !igniter.isTorchBurntOut();
            this.dispenser.getChild("torch_burnt").visible = igniter.isTorchBurntOut();
            if (igniter.isAttacking()) {
                this.right_arm.xRot = -0.6981F;
                this.dispenser.getChild("lever_handle").zRot = 2.3562F;
                this.head.getChild("birthday").xRot = 0.0F;
            } else {
                this.right_arm.xRot = -2.1817F;
                this.dispenser.getChild("lever_handle").zRot = 0.7854F;
                this.head.getChild("birthday").xRot = (float)Math.toRadians(-20.0);
            }
        }
    }
}
