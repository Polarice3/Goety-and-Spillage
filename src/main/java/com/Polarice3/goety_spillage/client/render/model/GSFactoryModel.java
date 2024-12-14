package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.factory.GSFactory;
import com.yellowbrossproductions.illageandspillage.client.model.FactoryModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.FactoryAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class GSFactoryModel<T extends Entity> extends FactoryModel<T> {
    public GSFactoryModel(ModelPart root) {
        super(root);
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof GSFactory factory) {
            this.animate(factory.getAnimationState("intro"), FactoryAnimation.INTRO, ageInTicks, factory.getAnimationSpeed());
            this.animate(factory.getAnimationState("spin"), FactoryAnimation.SPIN, ageInTicks, factory.getAnimationSpeed());
        }

    }
}
