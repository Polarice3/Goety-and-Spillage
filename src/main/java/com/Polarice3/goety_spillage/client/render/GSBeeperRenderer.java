package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.common.entities.ally.factory.GSBeeper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yellowbrossproductions.illageandspillage.client.model.BeeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class GSBeeperRenderer extends MobRenderer<GSBeeper, BeeperModel<GSBeeper>> {
    private static final ResourceLocation BEEPER = new ResourceLocation("illageandspillage", "textures/entity/engineer/factory/beeper.png");
    private static final ResourceLocation BEEPING = new ResourceLocation("illageandspillage", "textures/entity/engineer/factory/beeping.png");

    public GSBeeperRenderer(EntityRendererProvider.Context context) {
        super(context, new BeeperModel<>(context.bakeLayer(BeeperModel.LAYER_LOCATION)), 0.3F);
        this.addLayer(new GSBeeperGlowLayer<>(this));
        this.addLayer(new GSBeeperBeepLayer<>(this));
    }

    protected void scale(GSBeeper entityLivingBaseIn, PoseStack matrixStackIn, float partialTickTime) {
        entityLivingBaseIn.setPartialTicks(partialTickTime);
        float creeperFlashIntensity = entityLivingBaseIn.getCreeperFlashIntensity(partialTickTime);
        float mathsThing = 1.0F + Mth.sin(creeperFlashIntensity * 100.0F) * creeperFlashIntensity * 0.01F;
        creeperFlashIntensity = Mth.clamp(creeperFlashIntensity, 0.0F, 1.0F);
        creeperFlashIntensity *= creeperFlashIntensity;
        creeperFlashIntensity *= creeperFlashIntensity;
        float multipliedByMathsThing = (1.0F + creeperFlashIntensity * 0.4F) * mathsThing;
        float dividedByMathsThing = (1.0F + creeperFlashIntensity * 0.1F) / mathsThing;
        matrixStackIn.scale(multipliedByMathsThing, dividedByMathsThing, multipliedByMathsThing);
    }

    protected float getWhiteOverlayProgress(GSBeeper livingEntityIn, float partialTicks) {
        float flashIntensity = livingEntityIn.getCreeperFlashIntensity(partialTicks);
        return (int)(flashIntensity * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(flashIntensity, 0.5F, 1.0F);
    }

    public ResourceLocation getTextureLocation(GSBeeper entity) {
        return entity.getCreeperFlashIntensity(entity.getPartialTicks()) > 0.0F ? BEEPING : BEEPER;
    }

    public static class GSBeeperGlowLayer<T extends LivingEntity> extends EyesLayer<T, BeeperModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/engineer/factory/beeper_glow.png"));

        public GSBeeperGlowLayer(RenderLayerParent<T, BeeperModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_ instanceof GSBeeper beeper) {
                if (!beeper.isInvisible() && beeper.getCreeperFlashIntensity(beeper.getPartialTicks()) <= 0.0F) {
                    super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
                }
            }

        }

        public RenderType renderType() {
            return LAYER;
        }
    }

    public static class GSBeeperBeepLayer<T extends LivingEntity> extends EyesLayer<T, BeeperModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/engineer/factory/beeping_glow.png"));

        public GSBeeperBeepLayer(RenderLayerParent<T, BeeperModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_ instanceof GSBeeper beeper) {
                if (!beeper.isInvisible() && beeper.getCreeperFlashIntensity(beeper.getPartialTicks()) > 0.0F) {
                    super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
                }
            }

        }

        public RenderType renderType() {
            return LAYER;
        }
    }
}
