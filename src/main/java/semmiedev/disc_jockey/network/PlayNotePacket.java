package semmiedev.disc_jockey.network;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import semmiedev.disc_jockey.CommonInitializer;

public record PlayNotePacket(BlockPos pos, int pitch) implements CustomPayload {
    public static final CustomPayload.Id<PlayNotePacket> ID = new CustomPayload.Id<>(Identifier.of(CommonInitializer.MOD_ID, "play_note"));
    public static final PacketCodec<RegistryByteBuf, PlayNotePacket> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, PlayNotePacket::pos,
            PacketCodecs.VAR_INT, PlayNotePacket::pitch,
            PlayNotePacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}