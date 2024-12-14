package com.Polarice3.goety_spillage.client.render.model;

import com.Polarice3.goety_spillage.common.entities.projectiles.GSSkullBomb;
import com.yellowbrossproductions.illageandspillage.client.model.SkullBombModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class GSSkullBombModel<T extends Entity> extends SkullBombModel<T> {
    private final ModelPart skull;
    private final ModelPart smallerskull;

    public GSSkullBombModel(ModelPart root) {
        super(root);
        this.skull = root.getChild("skull");
        this.smallerskull = root.getChild("smallerskull");
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof GSSkullBomb skull) {
            this.skull.visible = !skull.isSmall();
            this.smallerskull.visible = skull.isSmall();
        }

        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
