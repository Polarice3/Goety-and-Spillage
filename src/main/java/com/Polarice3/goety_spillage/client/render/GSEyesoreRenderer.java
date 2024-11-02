package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.GSEyesoreModel;
import com.Polarice3.goety_spillage.common.entities.ally.GSEyesore;
import com.yellowbrossproductions.illageandspillage.client.model.EyesoreModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GSEyesoreRenderer extends MobRenderer<GSEyesore, EyesoreModel<GSEyesore>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/eyesore.png");

    public GSEyesoreRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSEyesoreModel<>(renderManagerIn.bakeLayer(EyesoreModel.LAYER_LOCATION)), 0.5F);
    }

    public ResourceLocation getTextureLocation(GSEyesore p_110775_1_) {
        return TEXTURE;
    }
}