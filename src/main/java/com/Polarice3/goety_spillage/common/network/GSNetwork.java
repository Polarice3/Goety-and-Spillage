package com.Polarice3.goety_spillage.common.network;

import com.Polarice3.goety_spillage.GoetySpillage;
import com.Polarice3.goety_spillage.common.capabilities.spillage.SpillageCapUpdatePacket;
import com.Polarice3.goety_spillage.common.network.client.CFreakyRobePacket;
import com.Polarice3.goety_spillage.common.network.client.CSetDeltaMovement;
import com.Polarice3.goety_spillage.common.network.server.SSetDeltaMovement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class GSNetwork {
    public static SimpleChannel INSTANCE;
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(GoetySpillage.MOD_ID, "channel"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), CFreakyRobePacket.class, CFreakyRobePacket::encode, CFreakyRobePacket::decode, CFreakyRobePacket::consume);
        INSTANCE.registerMessage(nextID(), CSetDeltaMovement.class, CSetDeltaMovement::encode, CSetDeltaMovement::decode, CSetDeltaMovement::consume);
        INSTANCE.registerMessage(nextID(), SSetDeltaMovement.class, SSetDeltaMovement::encode, SSetDeltaMovement::decode, SSetDeltaMovement::consume);
        INSTANCE.registerMessage(nextID(), SpillageCapUpdatePacket.class, SpillageCapUpdatePacket::encode, SpillageCapUpdatePacket::decode, SpillageCapUpdatePacket::consume);
    }

    public static <MSG> void sendTo(Player player, MSG msg) {
        GSNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), msg);
    }

    public static <MSG> void sendToServer(MSG msg) {
        GSNetwork.INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sentToTrackingChunk(LevelChunk chunk, MSG msg) {
        GSNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
    }

    public static <MSG> void sentToTrackingEntity(Entity entity, MSG msg) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> {
            return entity;
        }), msg);
    }

    public static <MSG> void sentToTrackingEntityAndPlayer(Entity entity, MSG msg) {
        GSNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static <MSG> void sendToALL(MSG msg) {
        GSNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
