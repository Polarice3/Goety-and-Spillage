package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.common.entities.projectiles.GSPumpkinBomb;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yellowbrossproductions.illageandspillage.client.model.PumpkinBombModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class GSPumpkinBombRenderer extends MobRenderer<GSPumpkinBomb, PumpkinBombModel<GSPumpkinBomb>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/pumpkin_bomb.png");
    private static final ResourceLocation GOOPY = new ResourceLocation("illageandspillage", "textures/entity/freakager/pumpkin_bomb_ragno.png");
    private final Random random = new Random();

    public GSPumpkinBombRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new PumpkinBombModel<>(renderManagerIn.bakeLayer(PumpkinBombModel.LAYER_LOCATION)), 0.5F);
    }

    protected void scale(GSPumpkinBomb p_114046_, PoseStack p_114047_, float p_114048_) {
        float $$3 = p_114046_.getSwelling(p_114048_);
        float $$4 = 1.0F + Mth.sin($$3 * 100.0F) * $$3 * 0.01F;
        $$3 = Mth.clamp($$3, 0.0F, 1.0F);
        $$3 *= $$3;
        $$3 *= $$3;
        float $$5 = (1.0F + $$3 * 0.4F) * $$4;
        float $$6 = (1.0F + $$3 * 0.1F) / $$4;
        p_114047_.scale($$5, $$6, $$5);
    }

    protected float getWhiteOverlayProgress(GSPumpkinBomb p_114043_, float p_114044_) {
        float $$2 = p_114043_.getSwelling(p_114044_);
        return (int)($$2 * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp($$2, 0.5F, 1.0F);
    }

    public Vec3 getRenderOffset(GSPumpkinBomb p_114483_, float p_114484_) {
        return p_114483_.getGoopy() ? new Vec3(this.random.nextGaussian() * 0.03, 0.0, this.random.nextGaussian() * 0.03) : super.getRenderOffset(p_114483_, p_114484_);
    }

    public ResourceLocation getTextureLocation(GSPumpkinBomb p_110775_1_) {
        return p_110775_1_.getGoopy() ? GOOPY : TEXTURE;
    }
}
