package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.GSChagrinModel;
import com.Polarice3.goety_spillage.common.entities.ally.factory.GSChagrin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yellowbrossproductions.illageandspillage.client.model.ChagrinSentryModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public class GSChagrinRenderer extends MobRenderer<GSChagrin, GSChagrinModel<GSChagrin>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/engineer/chagrin_sentry.png");

    public GSChagrinRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSChagrinModel<>(renderManagerIn.bakeLayer(ChagrinSentryModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new GSChagrinGlowLayer<>(this));
    }

    protected float getFlipDegrees(GSChagrin p_115337_) {
        return 0.0F;
    }

    protected void setupRotations(GSChagrin entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        if (!entity.hasPose(Pose.SLEEPING)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }

        entity.setPartialTicks(partialTicks);
        if (entity.deathTime > 0) {
            float v = ((float)entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            v = Mth.sqrt(v);
            if (v > 1.0F) {
                v = 1.0F;
            }

            poseStack.mulPose(Axis.ZP.rotationDegrees(v * this.getFlipDegrees(entity)));
        } else if (isEntityUpsideDown(entity)) {
            poseStack.translate(0.0F, entity.getBbHeight() + 0.1F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }

    }

    public ResourceLocation getTextureLocation(GSChagrin p_110775_1_) {
        return TEXTURE;
    }

    public class GSChagrinGlowLayer<T extends LivingEntity> extends EyesLayer<T, GSChagrinModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/engineer/chagrin_sentry_glow.png"));

        public GSChagrinGlowLayer(RenderLayerParent<T, GSChagrinModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_ instanceof GSChagrin sentry) {
                if (!sentry.isInvisible()) {
                    if (sentry.getStunTicks() > 0 && sentry.getStunTicks() % 2 == 0 && sentry.getStunTicks() < 190 && sentry.getRandom().nextBoolean()) {
                        super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
                    } else if (sentry.getStunTicks() >= 190 || sentry.getStunTicks() == 0) {
                        super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
                    }
                }
            }

        }

        public RenderType renderType() {
            return LAYER;
        }
    }
}
