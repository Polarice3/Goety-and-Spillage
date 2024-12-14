package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.client.render.model.GSImpModel;
import com.Polarice3.goety_spillage.common.entities.projectiles.GSImp;
import com.yellowbrossproductions.illageandspillage.client.model.ImpModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class GSImpRenderer extends MobRenderer<GSImp, GSImpModel<GSImp>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/magispeller/magispeller_nothing.png");

    public GSImpRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GSImpModel<>(renderManagerIn.bakeLayer(ImpModel.LAYER_LOCATION)), 0.0F);
        this.addLayer(new GSImpLayer<>(this));
    }

    public boolean shouldRender(GSImp imp, Frustum p_115469_, double p_115470_, double p_115471_, double p_115472_) {
        return imp.tickCount >= 1 + imp.getWaitTime() && super.shouldRender(imp, p_115469_, p_115470_, p_115471_, p_115472_);
    }

    public ResourceLocation getTextureLocation(GSImp p_110775_1_) {
        return TEXTURE;
    }

    public static class GSImpLayer<T extends LivingEntity> extends EyesLayer<T, GSImpModel<T>> {
        private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/spiritcaller/imp.png"));

        public GSImpLayer(RenderLayerParent<T, GSImpModel<T>> p_i226039_1_) {
            super(p_i226039_1_);
        }

        public RenderType renderType() {
            return LAYER;
        }
    }
}
