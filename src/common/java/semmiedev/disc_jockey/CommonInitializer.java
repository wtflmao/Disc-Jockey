package semmiedev.disc_jockey;

import net.fabricmc.api.ModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import semmiedev.disc_jockey.network.PlayNotePacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import semmiedev.disc_jockey.network.ClientHelloPacket;
import semmiedev.disc_jockey.network.ServerHelloPacket;

public class CommonInitializer implements ModInitializer {
    public static final String MOD_ID = "disc_jockey";
    public static final MutableText NAME = Text.literal("Disc Jockey");
    public static final Logger LOGGER = LoggerFactory.getLogger("Disc Jockey");

    @Override
    public void onInitialize() {
        // This code runs on both client and server environments.
        // It's used for registering common things like network channels and packets.
        PayloadTypeRegistry.playC2S().register(PlayNotePacket.ID, PlayNotePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ClientHelloPacket.ID, ClientHelloPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ServerHelloPacket.ID, ServerHelloPacket.CODEC);
    }
} 