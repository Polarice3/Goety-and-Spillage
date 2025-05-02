package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.GSHinderModel;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSHinder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yellowbrossproductions.illageandspillage.client.model.HinderModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class GSHinderRenderer extends MobRenderer<GSHinder, GSHinderModel<GSHinder>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/engineer/hinder.png");
    private static final ResourceLocation HEALING = new ResourceLocation("illageandspillage", "textures/entity/engineer/hinder_heal.png");
    private static final ResourceLocation COOLING = GoetySpillage.location("textures/entity/servants/hinder_cool.png");

    public GSHinderRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSHinderModel<>(renderManagerIn.bakeLayer(HinderModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new GSHinderGlowLayer<>(this));
    }

    protected float getFlipDegrees(GSHinder p_115337_) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(GSHinder p_110775_1_) {
        if (p_110775_1_.isCooling()){
            return COOLING;
        }
        return p_110775_1_.isHealing() ? HEALING : TEXTURE;
    }

    public static class GSHinderGlowLayer<T extends LivingEntity> extends EyesLayer<T, GSHinderModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/engineer/hinder_glow.png"));
        private static final RenderType HEALING = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/engineer/hinder_heal_glow.png"));
        private static final RenderType COOLING = RenderType.eyes(GoetySpillage.location("textures/entity/servants/hinder_cool_glow.png"));

        public GSHinderGlowLayer(RenderLayerParent<T, GSHinderModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_ instanceof GSHinder hinder) {
                if (!hinder.isInvisible()){
                    VertexConsumer vertexconsumer;
                    if (hinder.isCooling()){
                        vertexconsumer = p_116984_.getBuffer(COOLING);
                    } else if (!hinder.isHealing()) {
                        vertexconsumer = p_116984_.getBuffer(this.renderType());
                    } else {
                        vertexconsumer = p_116984_.getBuffer(HEALING);
                    }
                    this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }

        }

        public RenderType renderType() {
            return LAYER;
        }
    }
}
