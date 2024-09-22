package com.Polarice3.goety_spillage.common.capabilities.spillage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpillageCapUpdatePacket {
    private final int entityID;
    private CompoundTag tag;

    public SpillageCapUpdatePacket(int id, CompoundTag tag) {
        this.entityID = id;
        this.tag = tag;
    }

    public SpillageCapUpdatePacket(LivingEntity living) {
        this.entityID = living.getId();
        living.getCapability(SpillageProvider.CAPABILITY, null).ifPresent((misc) -> {
            this.tag = SpillageCapHelper.save(new CompoundTag(), misc);
        });
    }

    public static void encode(SpillageCapUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityID);
        buffer.writeNbt(packet.tag);
    }

    public static SpillageCapUpdatePacket decode(FriendlyByteBuf buffer) {
        return new SpillageCapUpdatePacket(buffer.readInt(), buffer.readNbt());
    }

    public static void consume(SpillageCapUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel != null){
                Entity entity = clientLevel.getEntity(packet.entityID);
                if (entity != null) {
                    entity.getCapability(SpillageProvider.CAPABILITY).ifPresent((misc) -> {
                        SpillageCapHelper.load(packet.tag, misc);
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
