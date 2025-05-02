package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.ally.undead.zombie.ZombieAbsorber;
import com.yellowbrossproductions.illageandspillage.client.model.AbsorberModel;
import com.yellowbrossproductions.illageandspillage.client.model.animation.AbsorberAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

import java.util.Random;

public class ZombieAbsorberModel<T extends Entity> extends AbsorberModel<T> {
    private final ModelPart waist;
    private final Random random = new Random();

    public ZombieAbsorberModel(ModelPart root) {
        super(root);
        this.waist = root.getChild("waist");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        ModelPart chest = this.waist.getChild("chest");
        if (entity instanceof ZombieAbsorber absorber) {
            this.animate(absorber.getAnimationState("attack"), AbsorberAnimation.ATTACK, ageInTicks, absorber.getAnimationSpeed());
            this.animate(absorber.getAnimationState("death"), AbsorberAnimation.DEATH, ageInTicks, absorber.getAnimationSpeed());
            if (absorber.deathTime >= 30 && absorber.deathTime < 44) {
                chest.x += (-0.5F + this.random.nextFloat()) * 0.8F;
                chest.y += (-0.5F + this.random.nextFloat()) * 0.8F;
                chest.z += (-0.5F + this.random.nextFloat()) * 0.8F;
            }
        }
    }
}
