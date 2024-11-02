package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.GSTotModel;
import com.Polarice3.goety_spillage.common.entities.ally.GSTot;
import com.yellowbrossproductions.illageandspillage.client.model.TrickOrTreatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class GSTotRenderer extends MobRenderer<GSTot, GSTotModel<GSTot>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/trickortreat.png");
    private static final ResourceLocation GOOPY = new ResourceLocation("illageandspillage", "textures/entity/freakager/trickortreat_ragno.png");

    public GSTotRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSTotModel(renderManagerIn.bakeLayer(TrickOrTreatModel.LAYER_LOCATION)), 0.6F);
    }

    protected float getFlipDegrees(GSTot p_115337_) {
        return 0.0F;
    }

    public Vec3 getRenderOffset(GSTot p_114483_, float p_114484_) {
        return p_114483_.getGoopy() ? new Vec3(p_114483_.getRandom().nextGaussian() * 0.03, 0.0, p_114483_.getRandom().nextGaussian() * 0.03) : super.getRenderOffset(p_114483_, p_114484_);
    }

    public ResourceLocation getTextureLocation(GSTot p_110775_1_) {
        return p_110775_1_.getGoopy() ? GOOPY : TEXTURE;
    }
}
