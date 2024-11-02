package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.common.entities.projectiles.ThrownAxe;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yellowbrossproductions.illageandspillage.client.model.AxeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ThrownAxeRenderer extends EntityRenderer<ThrownAxe> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/freakager/axe.png");
    private final AxeModel<Entity> model;

    public ThrownAxeRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        this.model = new AxeModel<>(p_174008_.bakeLayer(AxeModel.LAYER_LOCATION));
    }

    public void render(ThrownAxe axe, float p_116485_, float p_116486_, PoseStack p_116487_, MultiBufferSource p_116488_, int p_116489_) {
        p_116487_.pushPose();
        p_116487_.scale(1.0F, 1.0F, 1.0F);
        float f = Mth.lerp(p_116486_, axe.yRotO, axe.getYRot());
        f = Mth.wrapDegrees(f);
        float f1 = Mth.lerp(p_116486_, axe.xRotO, axe.getXRot());
        VertexConsumer vertexconsumer = p_116488_.getBuffer(this.model.renderType(this.getTextureLocation(axe)));
        float f7 = this.getBob(axe, p_116486_);
        this.model.setupAnim(axe, f, 0.0F, f7, f, f1);
        this.model.renderToBuffer(p_116487_, vertexconsumer, p_116489_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        p_116487_.popPose();
        super.render(axe, p_116485_, p_116486_, p_116487_, p_116488_, p_116489_);
    }

    protected float getBob(Entity p_115305_, float p_115306_) {
        return (float)p_115305_.tickCount + p_115306_;
    }

    public ResourceLocation getTextureLocation(ThrownAxe axeEntity) {
        return TEXTURE;
    }
}
