package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.GSEyesore;
import com.yellowbrossproductions.illageandspillage.client.model.EyesoreModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.EyesoreAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

import java.util.Calendar;

public class GSEyesoreModel<T extends Entity> extends EyesoreModel<T> {
    private final ModelPart eye;

    public GSEyesoreModel(ModelPart root) {
        super(root);
        this.eye = root.getChild("eye");
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof GSEyesore eyesore) {
            Calendar calendar = Calendar.getInstance();
            this.eye.getChild("birthday").visible = calendar.get(2) == 1 && calendar.get(5) < 8;
            this.animate(eyesore.getAnimationState("slither"), EyesoreAnimation.SLITHER, ageInTicks, eyesore.getAnimationSpeed());
            this.animate(eyesore.getAnimationState("fly"), EyesoreAnimation.FLY, ageInTicks, eyesore.getAnimationSpeed());
        }

    }
}
