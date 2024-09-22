package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.GSIllagerSoulModel;
import com.Polarice3.goety_spillage.common.entities.projectiles.GSIllagerSoul;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yellowbrossproductions.illageandspillage.client.model.IllagerSoulModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class GSIllagerSoulRenderer extends MobRenderer<GSIllagerSoul, GSIllagerSoulModel<GSIllagerSoul>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/magispeller/magispeller_nothing.png");

    public GSIllagerSoulRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSIllagerSoulModel<>(renderManagerIn.bakeLayer(IllagerSoulModel.LAYER_LOCATION)), 0.0F);
        this.addLayer(new IllagerSoulAngelLayer<>(this));
        this.addLayer(new IllagerSoulDevilLayer<>(this));
    }

    protected void scale(GSIllagerSoul p_114046_, PoseStack p_114047_, float p_114048_) {
        float f = p_114046_.getSwelling(p_114048_);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f3 = (f + 0.01F) / f1;
        p_114047_.scale(1.0F, f3, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(GSIllagerSoul p_114482_) {
        return TEXTURE;
    }

    public static class IllagerSoulAngelLayer<T extends LivingEntity> extends EyesLayer<T, GSIllagerSoulModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/spiritcaller/soul_angel.png"));

        public IllagerSoulAngelLayer(RenderLayerParent<T, GSIllagerSoulModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
            if (p_225628_4_ instanceof GSIllagerSoul soul) {
                if (soul.isAngelOrDevil()) {
                    super.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
                }
            }

        }

        public RenderType renderType() {
            return LAYER;
        }
    }

    public static class IllagerSoulDevilLayer<T extends LivingEntity> extends EyesLayer<T, GSIllagerSoulModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/spiritcaller/soul_devil.png"));

        public IllagerSoulDevilLayer(RenderLayerParent<T, GSIllagerSoulModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
            if (p_225628_4_ instanceof GSIllagerSoul soul) {
                if (!soul.isAngelOrDevil()) {
                    super.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
                }
            }

        }

        public RenderType renderType() {
            return LAYER;
        }
    }
}
