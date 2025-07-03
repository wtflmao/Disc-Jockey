package semmiedev.disc_jockey.network;

import net.fabricmc.fabric.api.networking.v1.CustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import semmiedev.disc_jockey.CommonInitializer;

public record PlayNotePacket(BlockPos pos, int pitch) implements CustomPayload {
    public static final CustomPayload.Id<PlayNotePacket> ID = new CustomPayload.Id<>(new Identifier(CommonInitializer.MOD_ID, "play_note"));
    public static final PacketCodec<PacketByteBuf, PlayNotePacket> CODEC = PacketCodec.of(PlayNotePacket::write, PlayNotePacket::new);

    private PlayNotePacket(PacketByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }

    private void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.pitch);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
} 