package semmiedev.disc_jockey;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import semmiedev.disc_jockey.network.PlayNotePacket;
import semmiedev.disc_jockey.network.ClientHelloPacket;
import semmiedev.disc_jockey.network.ServerHelloPacket;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Permissions.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ServerDiscJockeyCommand.register(dispatcher);
        });

        ServerPlayNetworking.registerGlobalReceiver(ClientHelloPacket.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayNetworking.send(context.player(), ServerHelloPacket.INSTANCE);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PlayNotePacket.ID, (payload, context) -> {
            context.server().execute(() -> {
                if (!Permissions.ALLOWED_PLAYERS.contains(context.player().getUuid())) return;

                BlockPos pos = payload.pos();
                int midiPitch = payload.pitch();
                ServerWorld world = context.player().getServerWorld();

                // Determine instrument from the block at the given position
                BlockState blockState = world.getBlockState(pos.down());
                NoteBlockInstrument instrument = blockState.getInstrument();

                // True pitch calculation (from NBS specification: (pitch - 45) / 50)
                // Minecraft pitch range is 0.5 to 2.0. MIDI pitch 69 (A4) should be 1.0f.
                // We use 2^((midi_pitch - 69) / 12) to get the frequency multiplier from A4.
                float pitch = (float) Math.pow(2.0, (midiPitch - 69.0) / 12.0);

                // Ensure pitch is within Minecraft's playable range.
                pitch = Math.max(0.5f, Math.min(2.0f, pitch));
                
                Vec3d particlePos = pos.toCenterPos();

                // Broadcast sound and particle effects to all players in the dimension
                world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_PLAY.value(), SoundCategory.RECORDS, 1.0f, pitch);
                world.spawnParticles(ParticleTypes.NOTE, particlePos.x, particlePos.y, particlePos.z, 0, (midiPitch % 25) / 24.0, 0.0, 0.0, 1.0);
            });
        });
    }
} 