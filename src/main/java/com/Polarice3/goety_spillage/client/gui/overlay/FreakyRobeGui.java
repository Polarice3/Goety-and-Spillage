package com.Polarice3.goety_spillage.client.gui.overlay;

import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.items.curios.FreakyRobeItem;
import com.Polarice3.goety_spillage.config.GSMainConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class FreakyRobeGui {
    public static final IGuiOverlay OVERLAY = FreakyRobeGui::drawHUD;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar(){
        return minecraft.player != null && CuriosFinder.hasCurio(minecraft.player, item -> item.getItem() instanceof FreakyRobeItem);
    }

    public static void drawHUD(ForgeGui gui, GuiGraphics ms, float partialTicks, int screenWidth, int screenHeight) {
        if(!shouldDisplayBar()) {
            return;
        }
        ItemStack robe = CuriosFinder.findCurio(minecraft.player, item -> item.getItem() instanceof FreakyRobeItem);
        if (robe.isEmpty()){
            return;
        }
        int i = ((screenWidth - 16) / 2) + GSMainConfig.FreakyGuiHorizontal.get();
        int j = (screenHeight - 68) + GSMainConfig.FreakyGuiVertical.get();
        int cool = 0;
        int attack = 0;
        if (!robe.isEmpty()){
            cool = FreakyRobeItem.getAttackCooldown(robe);
            attack = FreakyRobeItem.getAttackTick(robe);
        }
        ResourceLocation location = GoetySpillage.location("textures/gui/freaky_robe/ready.png");

        if (attack > 0){
            location = GoetySpillage.location("textures/gui/freaky_robe/work.png");
        }
        if (cool > 0){
            if (cool > 50){
                location = GoetySpillage.location("textures/gui/freaky_robe/cool_1.png");
            } else {
                location = GoetySpillage.location("textures/gui/freaky_robe/cool_2.png");
            }
        }

        ms.blit(location, i, j, 0, 0, 16,16, 16, 16);
    }
}
