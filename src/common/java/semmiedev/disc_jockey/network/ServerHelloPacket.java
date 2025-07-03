package semmiedev.disc_jockey.network;

import net.fabricmc.fabric.api.networking.v1.CustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import semmiedev.disc_jockey.CommonInitializer;

public record ServerHelloPacket() implements CustomPayload {
    public static final ServerHelloPacket INSTANCE = new ServerHelloPacket();
    public static final CustomPayload.Id<ServerHelloPacket> ID = new CustomPayload.Id<>(new Identifier(CommonInitializer.MOD_ID, "server_hello"));
    public static final PacketCodec<PacketByteBuf, ServerHelloPacket> CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
} 