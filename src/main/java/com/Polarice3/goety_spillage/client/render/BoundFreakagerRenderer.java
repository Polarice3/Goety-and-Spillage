package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.BoundFreakagerModel;
import com.Polarice3.goety_spillage.common.entities.ally.undead.bound.BoundFreakager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class BoundFreakagerRenderer extends MobRenderer<BoundFreakager, BoundFreakagerModel<BoundFreakager>> {
    private static final ResourceLocation TEXTURE = GoetySpillage.location("textures/entity/servants/bound_illager/bound_freakager.png");
    private static final ResourceLocation CASTING = GoetySpillage.location("textures/entity/servants/bound_illager/bound_freakager_casting.png");

    public BoundFreakagerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BoundFreakagerModel<>(renderManagerIn.bakeLayer(BoundFreakagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack p_116352_, MultiBufferSource p_116353_, int p_116354_, BoundFreakager p_116355_, float p_116356_, float p_116357_, float p_116358_, float p_116359_, float p_116360_, float p_116361_) {
                if (p_116355_.shouldShowArms()) {
                    super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_, p_116360_, p_116361_);
                }

            }
        });
    }

    public ResourceLocation getTextureLocation(BoundFreakager freakager) {
        if (freakager.shouldShowArms()){
            return CASTING;
        }
        return TEXTURE;
    }
}
