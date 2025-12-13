package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.BoundEngineerModel;
import com.Polarice3.goety_spillage.common.entities.ally.illager.EngineerServant;
import com.yellowbrossproductions.illageandspillage.client.model.EngineerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EngineerServantRenderer extends MobRenderer<EngineerServant, BoundEngineerModel<EngineerServant>> {
    private static final ResourceLocation TEXTURE = GoetySpillage.location("textures/entity/servants/illager/engineer.png");

    public EngineerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BoundEngineerModel<>(renderManagerIn.bakeLayer(EngineerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
    }

    public ResourceLocation getTextureLocation(EngineerServant p_110775_1_) {
        return TEXTURE;
    }
}
