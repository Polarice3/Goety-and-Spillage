package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.factory.GSChagrin;
import com.yellowbrossproductions.illageandspillage.client.model.ChagrinSentryModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.ChagrinSentryAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

import java.util.Calendar;

public class GSChagrinModel<T extends Entity> extends ChagrinSentryModel<T> {
    private final ModelPart locker;
    private final ModelPart upperBody;
    private final ModelPart head;
    private final ModelPart arm1;
    private final ModelPart arm2;
    private final ModelPart slab;

    public GSChagrinModel(ModelPart root) {
        super(root);
        ModelPart sentry = root.getChild("sentry");
        this.locker = sentry.getChild("locker");
        this.upperBody = sentry.getChild("upper_body");
        this.head = this.upperBody.getChild("head");
        this.arm1 = this.upperBody.getChild("arm1");
        this.arm2 = this.upperBody.getChild("arm2");
        this.slab = sentry.getChild("slab");
    }

    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity instanceof GSChagrin sentry) {
            Calendar calendar = Calendar.getInstance();
            this.head.getChild("birthday").visible = calendar.get(2) == 1 && calendar.get(5) < 8;

            float deltaYaw;
            for(deltaYaw = sentry.yBodyRot - sentry.yBodyRotO; deltaYaw < -180.0F; deltaYaw += 360.0F) {
            }

            while(deltaYaw >= 180.0F) {
                deltaYaw -= 360.0F;
            }

            this.upperBody.yRot = (sentry.yBodyRotO + deltaYaw * sentry.getPartialTicks()) * 0.017453292F;
            this.head.xRot = headPitch * 0.017453292F;
            this.arm1.xRot = headPitch * 0.017453292F;
            this.arm2.xRot = headPitch * 0.017453292F;
            this.slab.setRotation(0.0F, 0.0F, 0.0F);
            this.animate(sentry.getAnimationState("intro"), ChagrinSentryAnimation.INTRO, ageInTicks, sentry.getAnimationSpeed());
            this.animate(sentry.getAnimationState("shooting"), ChagrinSentryAnimation.SHOOT, ageInTicks, sentry.getAnimationSpeed());
            this.animate(sentry.getAnimationState("stun"), ChagrinSentryAnimation.POWER_DOWN, ageInTicks, sentry.getAnimationSpeed());
            this.locker.visible = sentry.shouldShowLocker();
            if (sentry.isInMotion()) {
                this.locker.xRot = ageInTicks * 25.0F * 0.017453292F;
                this.locker.yRot = ageInTicks * 15.0F * 0.017453292F;
                this.locker.zRot = ageInTicks * 20.0F * 0.017453292F;
                this.upperBody.visible = false;
                this.slab.visible = false;
            } else {
                this.locker.xRot = 0.0F;
                this.locker.yRot = 0.0F;
                this.locker.zRot = 0.0F;
                this.upperBody.visible = true;
                this.slab.visible = true;
            }
        }

    }
}
