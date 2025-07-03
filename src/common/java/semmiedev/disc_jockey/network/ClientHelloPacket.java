package semmiedev.disc_jockey.network;

import net.fabricmc.fabric.api.networking.v1.CustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import semmiedev.disc_jockey.CommonInitializer;

public record ClientHelloPacket() implements CustomPayload {
    public static final ClientHelloPacket INSTANCE = new ClientHelloPacket();
    public static final CustomPayload.Id<ClientHelloPacket> ID = new CustomPayload.Id<>(new Identifier(CommonInitializer.MOD_ID, "client_hello"));
    public static final PacketCodec<PacketByteBuf, ClientHelloPacket> CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
} 