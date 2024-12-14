package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.factory.GSHinder;
import com.yellowbrossproductions.illageandspillage.client.model.HinderModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.HinderAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class GSHinderModel<T extends Entity> extends HinderModel<T> {
    private final ModelPart thingy2;
    private final ModelPart thingy;
    private final ModelPart head;
    private final ModelPart base;

    public GSHinderModel(ModelPart root) {
        super(root);
        this.thingy2 = root.getChild("thingy2");
        this.thingy = root.getChild("thingy");
        this.head = root.getChild("head");
        this.base = root.getChild("base");
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof GSHinder hinder) {
            this.animate(hinder.getAnimationState("intro"), HinderAnimation.SPAWN, ageInTicks, hinder.getAnimationSpeed());
            this.animate(hinder.getAnimationState("idle"), HinderAnimation.IDLE, ageInTicks, hinder.getAnimationSpeed());
            if (hinder.isInMotion()) {
                this.base.xRot = ageInTicks * 25.0F * 0.017453292F;
                this.base.yRot = ageInTicks * 15.0F * 0.017453292F;
                this.base.zRot = ageInTicks * 20.0F * 0.017453292F;
                this.head.visible = false;
                this.thingy.visible = false;
                this.thingy2.visible = false;
            } else {
                this.base.xRot = 0.0F;
                this.base.yRot = 0.0F;
                this.base.zRot = 0.0F;
                this.head.visible = true;
                this.thingy.visible = true;
                this.thingy2.visible = true;
            }
        }

    }
}
