package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.client.render.animation.RagnoServantAnimations;
import com.Polarice3.goety_spillage.common.entities.ally.illager.RagnoServant;
import com.yellowbrossproductions.illageandspillage.client.model.RagnoModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.RagnoAnimation;
import com.yellowbrossproductions.illageandspillage.client.model.animation.RagnoAnimation2;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import java.util.Calendar;

public class RagnoServantModel<T extends Entity> extends RagnoModel<T> {
    private final ModelPart all;

    public RagnoServantModel(ModelPart root) {
        super(root);
        this.all = root.getChild("all");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        ModelPart head = this.all.getChild("neck").getChild("ragno_head");
        ModelPart neck = this.all.getChild("neck");
        ModelPart abdomen = this.all.getChild("ragno_body").getChild("abdomen");
        ModelPart leg1 = this.all.getChild("leg1");
        ModelPart leg2 = this.all.getChild("leg2");
        ModelPart leg3 = this.all.getChild("leg3");
        ModelPart leg4 = this.all.getChild("leg4");
        ModelPart leg5 = this.all.getChild("leg5");
        ModelPart leg6 = this.all.getChild("leg6");
        ModelPart leg7 = this.all.getChild("leg7");
        ModelPart leg8 = this.all.getChild("leg8");
        if (entity instanceof RagnoServant ragno) {
            float craziness = (float)ragno.getShakeMultiplier() * 4.0F;
            this.animate(ragno.getAnimationState("intro1"), RagnoAnimation.INTRO1, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("intro2"), RagnoAnimation.INTRO2, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("phase"), RagnoServantAnimations.PHASE, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("block"), RagnoAnimation.BLOCK, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("attack"), RagnoAnimation.ATTACK, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("web"), RagnoAnimation.WEB, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("webNet"), RagnoAnimation2.WEB_NET, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("pullIn"), RagnoAnimation2.PULL_IN, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("netSlam"), RagnoAnimation.PULL_SLAM, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("jump"), RagnoAnimation.JUMP, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("land"), RagnoAnimation.LAND, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("leap"), RagnoAnimation.LEAP, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("burrow"), RagnoAnimation.BURROW, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("popup"), RagnoAnimation.POPUP, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("charge"), RagnoAnimation.CHARGE, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("cough"), RagnoAnimation.COUGH, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("stun"), RagnoAnimation.STUN, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("fall"), RagnoAnimation.FALL, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("grab"), RagnoAnimation2.GRAB, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("breath"), RagnoAnimation2.BREATH, ageInTicks, ragno.getAnimationSpeed());
            this.animate(ragno.getAnimationState("death"), RagnoAnimation2.DEATH, ageInTicks, ragno.getAnimationSpeed());
            Calendar calendar = Calendar.getInstance();
            head.getChild("birthday").visible = calendar.get(2) == 1 && calendar.get(5) < 8;
            if (!ragno.isAlive()) {
                this.all.yRot = (float)Math.toRadians(180.0);
            } else {
                this.all.yRot = 0.0F;
            }

            head.yRot += netHeadYaw * 0.017453292F;
            head.xRot += headPitch * 0.017453292F;
            RandomSource random = ragno.getRandom();
            head.yRot += (-0.5F + random.nextFloat()) * 0.08F * craziness * 0.017453292F;
            head.xRot += (-0.5F + random.nextFloat()) * 0.08F * craziness * 0.017453292F;
            head.zRot += (-0.5F + random.nextFloat()) * 0.08F * craziness * 0.017453292F;
            neck.yRot += (-0.5F + random.nextFloat()) * 0.04F * craziness * 0.017453292F;
            neck.xRot += (-0.5F + random.nextFloat()) * 0.04F * craziness * 0.017453292F;
            abdomen.yRot += (-0.5F + random.nextFloat()) * 0.16F * craziness * 0.017453292F;
            abdomen.xRot += (-0.5F + random.nextFloat()) * 0.08F * craziness * 0.017453292F;
            abdomen.zRot += (-0.5F + random.nextFloat()) * 0.08F * craziness * 0.017453292F;
            if (ragno.getFrame() >= 10 && ragno.getFrame() <= 12) {
                float $$9 = ageInTicks / 60.0F;
                float $$10 = 15.0F;
                float $$11 = 20.0F;
                head.yRot += Mth.cos($$9 * 40.0F * $$10) * $$11 * 0.017453292F;
                head.xRot += Mth.cos($$9 * 20.0F * $$10) * $$11 / 8.0F * 0.017453292F;
                head.zRot += Mth.cos($$9 * 60.0F * $$10) * $$11 * 0.017453292F;
                neck.yRot += Mth.cos($$9 * 30.0F * $$10) * $$11 * 0.017453292F;
                neck.xRot += Mth.cos($$9 * 50.0F * $$10) * $$11 / 8.0F * 0.017453292F;
            }

            if (ragno.getAttackType() != 7) {
                float $$9 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
                float $$10 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * limbSwingAmount;
                float $$11 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * limbSwingAmount;
                float $$12 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 4.712389F) * 0.4F) * limbSwingAmount;
                float $$13 = Math.abs(Mth.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
                float $$14 = Math.abs(Mth.sin(limbSwing * 0.6662F + 3.1415927F) * 0.4F) * limbSwingAmount;
                float $$15 = Math.abs(Mth.sin(limbSwing * 0.6662F + 1.5707964F) * 0.4F) * limbSwingAmount;
                float $$16 = Math.abs(Mth.sin(limbSwing * 0.6662F + 4.712389F) * 0.4F) * limbSwingAmount;
                leg8.yRot += $$9;
                leg4.yRot -= $$9;
                leg7.yRot += $$10;
                leg3.yRot -= $$10;
                leg6.yRot += $$11;
                leg2.yRot -= $$11;
                leg5.yRot += $$12;
                leg1.yRot -= $$12;
                leg8.zRot += $$13;
                leg4.zRot -= $$13;
                leg7.zRot += $$14;
                leg3.zRot -= $$14;
                leg6.zRot += $$15;
                leg2.zRot -= $$15;
                leg5.zRot += $$16;
                leg1.zRot -= $$16;
            } else {
                leg8.yRot = 0.0F;
                leg4.yRot = 0.0F;
                leg7.yRot = 0.0F;
                leg3.yRot = 0.0F;
                leg6.yRot = 0.0F;
                leg2.yRot = 0.0F;
                leg5.yRot = 0.0F;
                leg1.yRot = 0.0F;
                leg8.zRot = 0.0F;
                leg4.zRot = 0.0F;
                leg7.zRot = 0.0F;
                leg3.zRot = 0.0F;
                leg6.zRot = 0.0F;
                leg2.zRot = 0.0F;
                leg5.zRot = 0.0F;
                leg1.zRot = 0.0F;
            }
        }

    }
}
