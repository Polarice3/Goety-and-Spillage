package com.Polarice3.goety_spillage.common.items.curios;

import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class FreakyHatItem extends SingleStackItem {

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;
        ChatFormatting secondary = ChatFormatting.BLUE;

        if (stack.getItem() instanceof FreakyHatItem) {
            tooltip.add(Component.translatable("info.goety_spillage.freaky_hat").withStyle(main));
            tooltip.add(Component.translatable("info.goety_spillage.freaky_hat_axe").withStyle(secondary));
        }
    }
}
