package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.VillagerVictimModel;
import com.Polarice3.goety_spillage.common.entities.neutral.VillagerVictim;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.WanderingTrader;

public class VillagerVictimRenderer extends MobRenderer<VillagerVictim, VillagerVictimModel<VillagerVictim>> {
    private static final ResourceLocation TEXTURE = GoetySpillage.location("textures/entity/servants/villager_victim/victim.png");
    private static final ResourceLocation TRADER = GoetySpillage.location("textures/entity/servants/villager_victim/trader/victim.png");

    public VillagerVictimRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new VillagerVictimModel<>(renderManagerIn.bakeLayer(VillagerVictimModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new VillaFaceLayer<>(this, renderManagerIn.getModelSet()));
        this.addLayer(new VillaProfLayer<>(this, renderManagerIn.getResourceManager(), "villager"));
    }

    @Override
    public ResourceLocation getTextureLocation(VillagerVictim victim) {
        if (victim.isTrader()){
            return TRADER;
        }
        return TEXTURE;
    }

    protected void scale(VillagerVictim p_116375_, PoseStack p_116376_, float p_116377_) {
        float f = 0.9375F;
        p_116376_.scale(f, f, f);
    }

    public static class VillaProfLayer<T extends LivingEntity & VillagerDataHolder, M extends EntityModel<T> & VillagerHeadModel> extends VillagerProfessionLayer<T, M> {

        public VillaProfLayer(RenderLayerParent<T, M> p_174550_, ResourceManager p_174551_, String p_174552_) {
            super(p_174550_, p_174551_, p_174552_);
        }

        @Override
        public void render(PoseStack p_117646_, MultiBufferSource p_117647_, int p_117648_, T p_117649_, float p_117650_, float p_117651_, float p_117652_, float p_117653_, float p_117654_, float p_117655_) {
            if (p_117649_ instanceof VillagerVictim victim && !victim.isTrader()) {
                super.render(p_117646_, p_117647_, p_117648_, p_117649_, p_117650_, p_117651_, p_117652_, p_117653_, p_117654_, p_117655_);
            }
        }
    }

    public static class VillaFaceLayer<T extends LivingEntity> extends RenderLayer<T, VillagerVictimModel<T>> {
        private final EntityModel<T> model1;

        public VillaFaceLayer(RenderLayerParent<T, VillagerVictimModel<T>> p_i226039_1_, EntityModelSet crap) {
            super(p_i226039_1_);
            this.model1 = new VillagerVictimModel<>(crap.bakeLayer(VillagerVictimModel.LAYER_LOCATION));
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn instanceof VillagerVictim victim && victim.getVillagerFace() != 0 && !victim.isInvisible()) {
                this.getParentModel().copyPropertiesTo(this.model1);
                this.model1.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
                this.model1.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                ResourceLocation texture = GoetySpillage.location("textures/entity/servants/villager_victim/intro_" + victim.getVillagerFace() + ".png");
                if (victim.isTrader()){
                    texture = GoetySpillage.location("textures/entity/servants/villager_victim/trader/intro_" + victim.getVillagerFace() + ".png");
                }
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(texture));
                this.model1.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
