package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.GSEyesoreModel;
import com.Polarice3.goety_spillage.common.entities.ally.GSEyesore;
import com.yellowbrossproductions.illageandspillage.client.model.EyesoreModel;
import com.yellowbrossproductions.illageandspillage.entities.EyesoreEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class GSEyesoreRenderer extends MobRenderer<GSEyesore, EyesoreModel<GSEyesore>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/eyesore.png");
    private static final ResourceLocation SCARED = new ResourceLocation("illageandspillage", "textures/entity/freakager/eyesore_shocked.png");

    public GSEyesoreRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSEyesoreModel<>(renderManagerIn.bakeLayer(EyesoreModel.LAYER_LOCATION)), 0.5F);
    }

    public Vec3 getRenderOffset(GSEyesore p_114483_, float p_114484_) {
        return p_114483_.isScared() ? new Vec3(p_114483_.getRandom().nextGaussian() * 0.015, 0.0, p_114483_.getRandom().nextGaussian() * 0.015) : super.getRenderOffset(p_114483_, p_114484_);
    }

    public ResourceLocation getTextureLocation(GSEyesore p_110775_1_) {
        return p_110775_1_.isScared() ? SCARED : TEXTURE;
    }
}