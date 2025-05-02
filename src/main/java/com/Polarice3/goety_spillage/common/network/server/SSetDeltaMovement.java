package com.Polarice3.goety_spillage.common.network.server;

import com.yellowbrossproductions.illageandspillage.util.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SSetDeltaMovement {
    public int mob;
    public double x;
    public double y;
    public double z;

    public SSetDeltaMovement(int id, double x, double y, double z) {
        this.mob = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(SSetDeltaMovement packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
        buffer.writeDouble(packet.x);
        buffer.writeDouble(packet.y);
        buffer.writeDouble(packet.z);
    }

    public static SSetDeltaMovement decode(FriendlyByteBuf buffer) {
        return new SSetDeltaMovement(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void consume(SSetDeltaMovement packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Level level = ClientHelper.getLevel();
            if (level != null){
                Entity entity = level.getEntity(packet.mob);
                if (entity != null) {
                    entity.setDeltaMovement(packet.x, packet.y, packet.z);
                    entity.hasImpulse = true;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
