package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.common.entities.ally.factory.GSSniper;
import com.yellowbrossproductions.illageandspillage.client.model.SniperModel;
import com.yellowbrossproductions.illageandspillage.client.render.layer.engineer.SniperGlowLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GSSniperRenderer extends MobRenderer<GSSniper, SniperModel<GSSniper>> {
    private static final ResourceLocation SNIPER = new ResourceLocation("illageandspillage", "textures/entity/engineer/factory/sniper.png");

    public GSSniperRenderer(EntityRendererProvider.Context context) {
        super(context, new SniperModel<>(context.bakeLayer(SniperModel.LAYER_LOCATION)), 0.3F);
        this.addLayer(new SniperGlowLayer<>(this));
    }

    protected float getFlipDegrees(GSSniper p_115337_) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(GSSniper entity) {
        return SNIPER;
    }
}
