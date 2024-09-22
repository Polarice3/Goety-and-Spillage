package com.Polarice3.goety_spillage.common.network.client;

import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.goety_spillage.common.items.curios.FreakyRobeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CFreakyRobePacket {

    public static void encode(CFreakyRobePacket packet, FriendlyByteBuf buffer) {
    }

    public static CFreakyRobePacket decode(FriendlyByteBuf buffer) {
        return new CFreakyRobePacket();
    }

    public static void consume(CFreakyRobePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                ItemStack stack = CuriosFinder.findCurio(playerEntity, itemStack -> itemStack.getItem() instanceof FreakyRobeItem);

                if (stack != null && !stack.isEmpty()){
                    FreakyRobeItem.startAttack(playerEntity, stack);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
