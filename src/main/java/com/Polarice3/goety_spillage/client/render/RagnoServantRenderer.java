package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.RagnoServantModel;
import com.Polarice3.goety_spillage.common.entities.ally.RagnoServant;
import com.yellowbrossproductions.illageandspillage.client.model.RagnoModel;
import com.yellowbrossproductions.illageandspillage.config.IllageAndSpillageConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class RagnoServantRenderer extends MobRenderer<RagnoServant, RagnoServantModel<RagnoServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/ragno.png");
    private static final ResourceLocation PAIN = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/pain.png");
    private static final ResourceLocation SCREAM = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/scream.png");
    private static final ResourceLocation INSANE = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/insane.png");
    private static final ResourceLocation WOUNDED = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/wounded.png");
    private static final ResourceLocation TEXTURE_ARACH = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/arachnophobe/ragno.png");
    private static final ResourceLocation PAIN_ARACH = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/arachnophobe/pain.png");
    private static final ResourceLocation SCREAM_ARACH = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/arachnophobe/scream.png");
    private static final ResourceLocation WOUNDED_ARACH = new ResourceLocation("illageandspillage", "textures/entity/freakager/ragno/arachnophobe/wounded.png");

    public RagnoServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RagnoServantModel<>(renderManagerIn.bakeLayer(RagnoModel.LAYER_LOCATION)), 1.6F);
    }

    public Vec3 getRenderOffset(RagnoServant p_114336_, float p_114337_) {
        float craziness = (float)p_114336_.getShakeMultiplier() / 10.0F;
        return new Vec3(p_114336_.getRandom().nextGaussian() * 0.02 * (double)craziness, 0.0, p_114336_.getRandom().nextGaussian() * 0.02 * (double)craziness);
    }

    protected float getFlipDegrees(RagnoServant p_115337_) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(RagnoServant p_110775_1_) {
        return switch (p_110775_1_.getRagnoFace()) {
            case 1 -> IllageAndSpillageConfig.arachnophobeMode.get() ? PAIN_ARACH : PAIN;
            case 2 -> IllageAndSpillageConfig.arachnophobeMode.get() ? SCREAM_ARACH : SCREAM;
            case 3 -> IllageAndSpillageConfig.arachnophobeMode.get() ? SCREAM_ARACH : INSANE;
            case 4 -> IllageAndSpillageConfig.arachnophobeMode.get() ? WOUNDED_ARACH : WOUNDED;
            default -> IllageAndSpillageConfig.arachnophobeMode.get() ? TEXTURE_ARACH : TEXTURE;
        };
    }
}
