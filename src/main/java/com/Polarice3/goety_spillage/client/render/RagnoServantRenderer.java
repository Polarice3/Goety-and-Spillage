package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.RagnoServantModel;
import com.Polarice3.goety_spillage.common.entities.ally.RagnoServant;
import com.yellowbrossproductions.illageandspillage.client.model.RagnoModel;
import com.yellowbrossproductions.illageandspillage.config.IllageAndSpillageConfig;
import com.yellowbrossproductions.illageandspillage.entities.RagnoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class RagnoServantRenderer extends MobRenderer<RagnoServant, RagnoServantModel<RagnoServant>> {
    private final Random random = new Random();
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/freakager.png");
    private static final ResourceLocation ARACHNOPHOBE = new ResourceLocation("illageandspillage", "textures/entity/freakager/freakager_arachnophobe.png");
    private static final ResourceLocation CRAZY = new ResourceLocation("illageandspillage", "textures/entity/freakager/freakager_phase2.png");

    public RagnoServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RagnoServantModel<>(renderManagerIn.bakeLayer(RagnoServantModel.LAYER_LOCATION)), 1.6F);
    }

    public Vec3 getRenderOffset(RagnoServant p_114336_, float p_114337_) {
        float craziness = p_114336_.isCrazy() ? 2.0F : 1.0F;
        if (p_114336_.deathTime > 40){
            return new Vec3(0.0D, 2.2D, 0.0D);
        }
        return new Vec3(this.random.nextGaussian() * 0.02 * (double)craziness, 0.0 + Math.min((double)p_114336_.deathTime / 10.0, 1.0) * 2.2, this.random.nextGaussian() * 0.02 * (double)craziness);
    }

    protected float getFlipDegrees(RagnoServant p_115337_) {
        return 180.0F;
    }

    public ResourceLocation getTextureLocation(RagnoServant p_110775_1_) {
        return IllageAndSpillageConfig.arachnophobeMode.get() ? ARACHNOPHOBE : (p_110775_1_.isCrazy() ? CRAZY : TEXTURE);
    }
}
