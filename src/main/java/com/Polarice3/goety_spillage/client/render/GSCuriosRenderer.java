package com.Polarice3.goety_spillage.client.render;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.WearRenderer;
import com.Polarice3.Goety.client.render.model.DarkRobeModel;
import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.client.render.model.FreakyHatModel;
import com.Polarice3.goety_spillage.common.items.GSItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class GSCuriosRenderer {
    public static String folderPath = "textures/models/curios/";

    public static ResourceLocation render(String textureName){
        return GoetySpillage.location(folderPath + textureName);
    }

    public static void register() {
        CuriosRendererRegistry.register(GSItems.FREAKY_HAT.get(), () -> new WearRenderer(render("freaky_hat.png"), new FreakyHatModel(bakeLayer(FreakyHatModel.LAYER_LOCATION))));
        CuriosRendererRegistry.register(GSItems.FREAKY_ROBE.get(), () -> new WearRenderer(render("freaky_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }
}
