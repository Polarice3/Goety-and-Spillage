package com.Polarice3.goety_spillage.common.network.server;

import com.Polarice3.goety_spillage.client.audio.GSMobFollowingSoundPlayer;
import com.yellowbrossproductions.illageandspillage.util.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SMobFollowSoundPacket {
    private final int entityId;
    private final SoundEvent sound;
    private final float volume;
    private final float pitch;
    private final boolean loop;

    public SMobFollowSoundPacket(int entityId, SoundEvent sound, float volume, float pitch, boolean loop) {
        this.entityId = entityId;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.loop = loop;
    }

    public static void encode(SMobFollowSoundPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        ResourceLocation soundLocation = ForgeRegistries.SOUND_EVENTS.getKey(msg.sound);
        buf.writeResourceLocation(soundLocation == null ? new ResourceLocation("") : soundLocation);
        buf.writeFloat(msg.volume);
        buf.writeFloat(msg.pitch);
        buf.writeBoolean(msg.loop);
    }

    public static SMobFollowSoundPacket decode(FriendlyByteBuf buf) {
        return new SMobFollowSoundPacket(buf.readInt(), ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation()), buf.readFloat(), buf.readFloat(), buf.readBoolean());
    }

    public static void consume(SMobFollowSoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = ClientHelper.getLevel();
            if (level != null) {
                Entity entity = level.getEntity(msg.entityId);
                if (entity != null && msg.sound != null) {
                    GSMobFollowingSoundPlayer.playSound(level, entity, msg.sound, msg.volume, msg.pitch, msg.loop);
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }

}
