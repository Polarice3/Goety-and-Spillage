package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.GSSpiritHandModel;
import com.Polarice3.goety_spillage.common.entities.ally.GSSpiritHand;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yellowbrossproductions.illageandspillage.client.model.SpiritHandModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class GSSpiritHandRenderer extends MobRenderer<GSSpiritHand, GSSpiritHandModel<GSSpiritHand>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/magispeller/magispeller_nothing.png");
    private final Random random = new Random();

    public GSSpiritHandRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSSpiritHandModel<>(renderManagerIn.bakeLayer(SpiritHandModel.LAYER_LOCATION)), 0.0F);
        this.addLayer(new GSSpiritHandLayer<>(this));
        this.addLayer(new GSSpiritHandEvilLayer<>(this));
    }

    protected void scale(GSSpiritHand p_115314_, PoseStack p_115315_, float p_115316_) {
        p_115315_.scale(p_115314_.isDeadOrDying() ? 1.0F - (float)p_115314_.deathTime / 20.0F : 1.0F, p_115314_.isDeadOrDying() ? 1.0F + (float)p_115314_.deathTime / 5.0F : 1.0F, p_115314_.isDeadOrDying() ? 1.0F - (float)p_115314_.deathTime / 20.0F : 1.0F);
    }

    protected float getFlipDegrees(GSSpiritHand p_115337_) {
        return 0.0F;
    }

    public Vec3 getRenderOffset(GSSpiritHand p_114483_, float p_114484_) {
        return p_114483_.getAttackType() == 2 ? new Vec3(this.random.nextGaussian() * 0.06, this.random.nextGaussian() * 0.06, this.random.nextGaussian() * 0.06) : new Vec3(0.0, (double)(-((float)p_114483_.deathTime / 5.0F) / 2.0F), 0.0);
    }

    public ResourceLocation getTextureLocation(GSSpiritHand p_110775_1_) {
        return TEXTURE;
    }

    public static class GSSpiritHandLayer<T extends LivingEntity> extends EyesLayer<T, GSSpiritHandModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/spiritcaller/spirit_hand.png"));

        public GSSpiritHandLayer(RenderLayerParent<T, GSSpiritHandModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_ instanceof GSSpiritHand s) {
                if (s.isGoodOrEvil()) {
                    super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
                }
            }

        }

        public RenderType renderType() {
            return LAYER;
        }
    }

    public static class GSSpiritHandEvilLayer<T extends LivingEntity> extends EyesLayer<T, GSSpiritHandModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/spiritcaller/spirit_hand.png"));

        public GSSpiritHandEvilLayer(RenderLayerParent<T, GSSpiritHandModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_ instanceof GSSpiritHand s) {
                if (!s.isGoodOrEvil()) {
                    super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
                }
            }

        }

        public RenderType renderType() {
            return LAYER;
        }
    }
}
