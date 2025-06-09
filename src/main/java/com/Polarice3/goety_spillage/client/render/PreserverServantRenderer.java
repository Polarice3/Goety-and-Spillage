package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.PreserverServantModel;
import com.Polarice3.goety_spillage.common.entities.ally.illager.PreserverServant;
import com.yellowbrossproductions.illageandspillage.client.model.PreserverModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class PreserverServantRenderer extends MobRenderer<PreserverServant, PreserverServantModel<PreserverServant>> {
    private static final ResourceLocation TEXTURE = GoetySpillage.location("textures/entity/servants/illager/preserver.png");

    public PreserverServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new PreserverServantModel<>(renderManagerIn.bakeLayer(PreserverModel.LAYER_LOCATION)), 0.6F);
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(PreserverServant p_110775_1_) {
        return TEXTURE;
    }
}
