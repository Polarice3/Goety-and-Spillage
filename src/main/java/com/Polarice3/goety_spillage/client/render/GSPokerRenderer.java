package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.common.entities.ally.factory.GSPoker;
import com.yellowbrossproductions.illageandspillage.client.model.PokerModel;
import com.yellowbrossproductions.illageandspillage.client.render.layer.engineer.PokerGlowLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class GSPokerRenderer extends MobRenderer<GSPoker, PokerModel<GSPoker>> {
    private static final ResourceLocation POKER = new ResourceLocation("illageandspillage", "textures/entity/engineer/factory/poker.png");

    public GSPokerRenderer(EntityRendererProvider.Context context) {
        super(context, new PokerModel<>(context.bakeLayer(PokerModel.LAYER_LOCATION)), 0.3F);
        this.addLayer(new PokerGlowLayer<>(this));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(GSPoker entity) {
        return POKER;
    }
}
