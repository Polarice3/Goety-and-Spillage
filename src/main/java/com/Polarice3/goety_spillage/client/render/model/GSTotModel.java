package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.GSTot;
import com.yellowbrossproductions.illageandspillage.client.model.TrickOrTreatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class GSTotModel<T extends Entity> extends TrickOrTreatModel<T> {
    private final ModelPart treat1;
    private final ModelPart treat2;
    private final ModelPart treat3;
    private final ModelPart treat4;
    private final ModelPart treat5;
    private final ModelPart treat6;

    public GSTotModel(ModelPart root) {
        super(root);
        this.treat1 = root.getChild("treat1");
        this.treat2 = root.getChild("treat2");
        this.treat3 = root.getChild("treat3");
        this.treat4 = root.getChild("treat4");
        this.treat5 = root.getChild("treat5");
        this.treat6 = root.getChild("treat6");
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.treat1.resetPose();
        this.treat2.resetPose();
        this.treat3.resetPose();
        this.treat4.resetPose();
        this.treat5.resetPose();
        this.treat6.resetPose();
        if (entity instanceof GSTot treat) {
            this.treat1.visible = treat.getTreat() == 1;
            this.treat2.visible = treat.getTreat() == 2;
            this.treat3.visible = treat.getTreat() == 3;
            this.treat4.visible = treat.getTreat() == 4;
            this.treat5.visible = treat.getTreat() == 5;
            this.treat6.visible = treat.getTreat() == 6;
            if (treat.getBounce()) {
                this.treat1.yRot = netHeadYaw * 0.017453292F;
                this.treat1.xRot = 1.5707F + headPitch * 0.017453292F;
                this.treat2.yRot = netHeadYaw * 0.017453292F;
                this.treat2.xRot = 1.5707F + headPitch * 0.017453292F;
                this.treat3.yRot = netHeadYaw * 0.017453292F;
                this.treat3.xRot = 1.5707F + headPitch * 0.017453292F;
                this.treat4.yRot = netHeadYaw * 0.017453292F;
                this.treat4.xRot = 1.5707F + headPitch * 0.017453292F;
                this.treat5.yRot = netHeadYaw * 0.017453292F;
                this.treat5.xRot = 1.5707F + headPitch * 0.017453292F;
                this.treat6.yRot = netHeadYaw * 0.017453292F;
                this.treat6.xRot = 1.5707F + headPitch * 0.017453292F;
            }
        }

    }
}
