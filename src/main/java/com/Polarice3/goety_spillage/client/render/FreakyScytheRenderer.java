package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.common.entities.projectiles.FreakyScythe;
import com.yellowbrossproductions.illageandspillage.client.model.ScytheModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class FreakyScytheRenderer extends MobRenderer<FreakyScythe, ScytheModel<FreakyScythe>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/scythe.png");

    public FreakyScytheRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ScytheModel<>(renderManagerIn.bakeLayer(ScytheModel.LAYER_LOCATION)), 0.3F);
    }

    protected int getBlockLightLevel(FreakyScythe p_114496_, BlockPos p_114497_) {
        return 15;
    }

    public ResourceLocation getTextureLocation(FreakyScythe p_110775_1_) {
        return TEXTURE;
    }
}
