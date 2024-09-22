package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.common.entities.projectiles.ThrownAxe;
import com.yellowbrossproductions.illageandspillage.client.model.AxeModel;
import com.yellowbrossproductions.illageandspillage.entities.projectile.AxeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ThrownAxeRenderer extends MobRenderer<ThrownAxe, AxeModel<ThrownAxe>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/axe.png");

    public ThrownAxeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AxeModel<>(renderManagerIn.bakeLayer(AxeModel.LAYER_LOCATION)), 0.3F);
    }

    public ResourceLocation getTextureLocation(ThrownAxe p_110775_1_) {
        return TEXTURE;
    }
}
