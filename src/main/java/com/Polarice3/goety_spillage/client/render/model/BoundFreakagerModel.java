package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundFreakager;
import com.yellowbrossproductions.illageandspillage.client.model.FreakagerModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.FreakagerAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BoundFreakagerModel<T extends Entity> extends FreakagerModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(GoetySpillage.location("bound_freakager"), "main");
    private final ModelPart all;

    public BoundFreakagerModel(ModelPart root) {
        super(root);
        this.all = root.getChild("all");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        ModelPart head = this.all.getChild("body").getChild("head");
        ModelPart left_arm = this.all.getChild("body").getChild("left_arm");
        ModelPart right_arm = this.all.getChild("body").getChild("right_arm");
        ModelPart arms = this.all.getChild("body").getChild("arms");
        ModelPart scythe = this.all.getChild("body").getChild("right_arm").getChild("scythe");
        if (entity instanceof BoundFreakager freakager) {
            this.all.getChild("right_leg2").visible = false;
            this.all.getChild("left_leg2").visible = false;
            this.all.getChild("body").getChild("right_leg").visible = false;
            this.all.getChild("body").getChild("left_leg").visible = false;
            this.animate(freakager.getAnimationState("intro1"), FreakagerAnimation.INTRO1, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("intro2"), FreakagerAnimation.INTRO2, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("intro3"), FreakagerAnimation.INTRO3, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("bombs"), FreakagerAnimation.BOMBS, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("minions"), FreakagerAnimation.MINIONS, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("axes_start"), FreakagerAnimation.AXES_START, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("axes_normal"), FreakagerAnimation.AXES_NORMAL, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("axes_angry"), FreakagerAnimation.AXES_ANGRY, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("potions"), FreakagerAnimation.POTIONS, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("scythe"), FreakagerAnimation.SCYTHE, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("catch"), FreakagerAnimation.CATCH, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("trickortreat"), FreakagerAnimation.TRICKORTREAT, ageInTicks, freakager.getAnimationSpeed());
            this.animate(freakager.getAnimationState("phase"), FreakagerAnimation.PHASE, ageInTicks, freakager.getAnimationSpeed());
            head.yRot += netHeadYaw * 0.017453292F;
            head.xRot += headPitch * 0.017453292F;
            left_arm.visible = freakager.shouldShowArms();
            right_arm.visible = freakager.shouldShowArms();
            arms.visible = !freakager.shouldShowArms();
            scythe.visible = freakager.shouldShowScythe();
            float f4 = ageInTicks / 60.0F;
            float multiplier = 0.1F;
            ModelPart var10000 = head.getChild("hat").getChild("hat_littlepiece2");
            var10000.zRot += Mth.cos(f4 * 5.0F) * multiplier;
            if (freakager.halfHealth() && freakager.isAlive()) {
                head.x += (-0.5F + freakager.getRandom().nextFloat()) * 1.2F;
                head.y += (-0.5F + freakager.getRandom().nextFloat()) * 1.2F;
                head.z += (-0.5F + freakager.getRandom().nextFloat()) * 1.2F;
            }
        }
    }
}
