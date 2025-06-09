package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.IgniterServantModel;
import com.Polarice3.goety_spillage.common.entities.ally.illager.IgniterServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yellowbrossproductions.illageandspillage.client.model.IgniterModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class IgniterServantRenderer extends MobRenderer<IgniterServant, IgniterServantModel<IgniterServant>> {
    private static final ResourceLocation TEXTURE = GoetySpillage.location("textures/entity/servants/illager/igniter.png");

    public IgniterServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new IgniterServantModel<>(renderManagerIn.bakeLayer(IgniterModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new OverheatLayer<>(this));
        this.addLayer(new TorchLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(IgniterServant p_114482_) {
        return TEXTURE;
    }

    public static class OverheatLayer<T extends LivingEntity> extends RenderLayer<T, IgniterServantModel<T>> {
        private static final RenderType LAYER1 = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/igniter/heat_layer1.png"));
        private static final RenderType LAYER2 = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/igniter/heat_layer2.png"));
        private static final RenderType LAYER3 = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/igniter/heat_layer3.png"));
        private static final RenderType LAYER4 = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/igniter/heat_layer4.png"));
        private static final RenderType LAYER5 = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/igniter/heat_layer5.png"));

        public OverheatLayer(RenderLayerParent<T, IgniterServantModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public RenderType getLayer(float percent, IgniterServant igniter) {
            if (igniter.isOverheated()) {
                if (percent <= 0.2F) {
                    return LAYER1;
                }

                if (percent > 0.2F && percent <= 0.4F) {
                    return LAYER2;
                }

                if (percent > 0.4F && percent <= 0.6F) {
                    return LAYER3;
                }

                if (percent > 0.6F && percent <= 0.8F) {
                    return LAYER4;
                }

                if (percent > 0.8F) {
                    return LAYER5;
                }
            } else {
                if (percent > 0.2F && percent <= 0.4F) {
                    return LAYER1;
                }

                if (percent > 0.4F && percent <= 0.6F) {
                    return LAYER2;
                }

                if (percent > 0.6F && percent <= 0.8F) {
                    return LAYER3;
                }

                if (percent > 0.8F) {
                    return LAYER4;
                }
            }

            return LAYER1;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity instanceof IgniterServant igniter) {
                if (!igniter.isInvisible()) {
                    float percent = igniter.isOverheated() ? igniter.getCooldownTicks() / 300.0F : igniter.getFireballsShot() / 25.0F;
                    if (percent > 0.2F || igniter.isOverheated()) {
                        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.getLayer(percent, igniter));
                        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
            }

        }
    }

    public static class TorchLayer<T extends LivingEntity> extends EyesLayer<T, IgniterServantModel<T>> {
        private static final RenderType LAYER = RenderType.entityTranslucentEmissive(new ResourceLocation("illageandspillage", "textures/entity/igniter/torch_layer.png"));

        public TorchLayer(RenderLayerParent<T, IgniterServantModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public RenderType renderType() {
            return LAYER;
        }

        public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
            if (!p_225628_4_.isInvisible()) {
                super.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
            }

        }
    }
}
