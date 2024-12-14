package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.CrocofangServantModel;
import com.Polarice3.goety_spillage.common.entities.ally.CrocofangServant;
import com.yellowbrossproductions.illageandspillage.client.model.CrocofangModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CrocofangServantRenderer extends MobRenderer<CrocofangServant, CrocofangServantModel<CrocofangServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/crocofang.png");

    public CrocofangServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CrocofangServantModel<>(renderManagerIn.bakeLayer(CrocofangModel.LAYER_LOCATION)), 1.1F);
    }

    public ResourceLocation getTextureLocation(CrocofangServant p_110775_1_) {
        return TEXTURE;
    }
}
