package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.GSFunnyboneModel;
import com.Polarice3.goety_spillage.common.entities.ally.undead.GSFunnybone;
import com.yellowbrossproductions.illageandspillage.client.model.FunnyboneModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class GSFunnyboneRenderer extends MobRenderer<GSFunnybone, GSFunnyboneModel<GSFunnybone>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/funnybone.png");
    private static final ResourceLocation GOOPY = new ResourceLocation("illageandspillage", "textures/entity/freakager/funnybone_ragno.png");

    public GSFunnyboneRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSFunnyboneModel<>(renderManagerIn.bakeLayer(FunnyboneModel.LAYER_LOCATION)), 0.4F);
    }

    public Vec3 getRenderOffset(GSFunnybone p_114483_, float p_114484_) {
        return p_114483_.isGoopy() ? new Vec3(p_114483_.getRandom().nextGaussian() * 0.03, -0.15, p_114483_.getRandom().nextGaussian() * 0.03) : new Vec3(0.0, -0.15, 0.0);
    }

    public ResourceLocation getTextureLocation(GSFunnybone p_110775_1_) {
        return p_110775_1_.isGoopy() ? GOOPY : TEXTURE;
    }
}
