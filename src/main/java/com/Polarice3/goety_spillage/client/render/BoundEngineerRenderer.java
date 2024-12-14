package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.BoundEngineerModel;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundEngineer;
import com.yellowbrossproductions.illageandspillage.client.model.EngineerModel;
import com.yellowbrossproductions.illageandspillage.client.render.layer.HeadItemLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BoundEngineerRenderer extends MobRenderer<BoundEngineer, BoundEngineerModel<BoundEngineer>> {
    private static final ResourceLocation TEXTURE = GoetySpillage.location("textures/entity/servants/bound_illager/bound_engineer.png");

    public BoundEngineerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BoundEngineerModel<>(renderManagerIn.bakeLayer(EngineerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new HeadItemLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(BoundEngineer p_110775_1_) {
        return TEXTURE;
    }
}
