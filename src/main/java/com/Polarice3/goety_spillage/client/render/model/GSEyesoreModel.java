package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.GSEyesore;
import com.yellowbrossproductions.illageandspillage.client.model.EyesoreModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.EyesoreAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class GSEyesoreModel<T extends Entity> extends EyesoreModel<T> {
    public GSEyesoreModel(ModelPart root) {
        super(root);
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof GSEyesore eyesore) {
            this.animate(eyesore.getAnimationState("slither"), EyesoreAnimation.SLITHER, ageInTicks, eyesore.getAnimationSpeed());
            this.animate(eyesore.getAnimationState("fly"), EyesoreAnimation.FLY, ageInTicks, eyesore.getAnimationSpeed());
        }

    }
}
