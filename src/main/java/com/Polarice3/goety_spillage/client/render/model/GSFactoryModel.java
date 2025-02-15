package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.factory.GSFactory;
import com.yellowbrossproductions.illageandspillage.client.model.FactoryModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.FactoryAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

import java.util.Calendar;

public class GSFactoryModel<T extends Entity> extends FactoryModel<T> {
    private final ModelPart head;

    public GSFactoryModel(ModelPart root) {
        super(root);
        ModelPart all = root.getChild("all");
        ModelPart fold2 = all.getChild("fold2");
        this.head = fold2.getChild("head");
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof GSFactory factory) {
            this.animate(factory.getAnimationState("intro"), FactoryAnimation.INTRO, ageInTicks, factory.getAnimationSpeed());
            this.animate(factory.getAnimationState("spin"), FactoryAnimation.SPIN, ageInTicks, factory.getAnimationSpeed());
            Calendar calendar = Calendar.getInstance();
            this.head.getChild("birthday").visible = calendar.get(2) == 1 && calendar.get(5) < 8;
        }

    }
}
