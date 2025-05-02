package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.GSFactoryModel;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yellowbrossproductions.illageandspillage.client.model.FactoryModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class GSFactoryRenderer extends MobRenderer<GSFactory, GSFactoryModel<GSFactory>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/engineer/factory.png");
    private static final ResourceLocation INACTIVE = GoetySpillage.location("textures/entity/servants/factory_inactive.png");

    public GSFactoryRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSFactoryModel<>(renderManagerIn.bakeLayer(FactoryModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new GSFactoryGlowLayer<>(this));
    }

    protected float getFlipDegrees(GSFactory factory) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(GSFactory factory) {
        if (!factory.isActive()){
            return INACTIVE;
        }
        return TEXTURE;
    }

    public static class GSFactoryGlowLayer<T extends LivingEntity> extends EyesLayer<T, GSFactoryModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/engineer/factory_glow.png"));
        private static final RenderType INACTIVE = RenderType.eyes(GoetySpillage.location("textures/entity/servants/factory_inactive_glow.png"));

        public GSFactoryGlowLayer(RenderLayerParent<T, GSFactoryModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible()) {
                if (p_116986_ instanceof GSFactory factory){
                    if (factory.isActive()) {
                        super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
                    } else {
                        VertexConsumer vertexconsumer = p_116984_.getBuffer(INACTIVE);
                        this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
            }
        }

        public RenderType renderType() {
            return LAYER;
        }
    }

}
