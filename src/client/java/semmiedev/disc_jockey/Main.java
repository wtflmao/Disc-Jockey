package semmiedev.disc_jockey;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import semmiedev.disc_jockey.gui.hud.BlocksOverlay;
import semmiedev.disc_jockey.gui.screen.DiscJockeyScreen;
import semmiedev.disc_jockey.network.ClientHelloPacket;
import semmiedev.disc_jockey.network.ServerHelloPacket;

import java.io.File;
import java.util.ArrayList;

import static semmiedev.disc_jockey.CommonInitializer.MOD_ID;
import static semmiedev.disc_jockey.CommonInitializer.LOGGER;

public class Main implements ClientModInitializer {
    public static final ArrayList<ClientTickEvents.StartWorldTick> TICK_LISTENERS = new ArrayList<>();
    public static final Previewer PREVIEWER = new Previewer();
    public static final SongPlayer SONG_PLAYER = new SongPlayer();

    public static boolean serverModPresent = false;

    public static File songsFolder;
    public static Config config;
    public static ConfigHolder<Config> configHolder;

    @Override
    public void onInitializeClient() {
        configHolder = AutoConfig.register(Config.class, JanksonConfigSerializer::new);
        config = configHolder.getConfig();

        songsFolder = new File(FabricLoader.getInstance().getConfigDir()+File.separator+MOD_ID+File.separator+"songs");
        if (!songsFolder.isDirectory()) songsFolder.mkdirs();

        SongLoader.loadSongs();

        KeyBinding openScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(MOD_ID+".key_bind.open_screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.category."+MOD_ID));

        ClientTickEvents.START_CLIENT_TICK.register(new ClientTickEvents.StartTick() {
            private ClientWorld prevWorld;

            @Override
            public void onStartTick(MinecraftClient client) {
                if (prevWorld != client.world) {
                    PREVIEWER.stop();
                    SONG_PLAYER.stop();
                }
                prevWorld = client.world;

                if (openScreenKeyBind.wasPressed()) {
                    if (SongLoader.loadingSongs) {
                        client.inGameHud.getChatHud().addMessage(Text.translatable(Main.MOD_ID+".still_loading").formatted(Formatting.RED));
                        SongLoader.showToast = true;
                    } else {
                        client.setScreen(new DiscJockeyScreen());
                    }
                }
            }
        });

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            for (ClientTickEvents.StartWorldTick listener : TICK_LISTENERS) listener.onStartTick(world);
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            DiscjockeyCommand.register(dispatcher);
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            serverModPresent = false; // Reset on join
            ClientPlayNetworking.send(ClientHelloPacket.INSTANCE);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            serverModPresent = false;
            PREVIEWER.stop();
            SONG_PLAYER.stop();
        });

        ClientPlayNetworking.registerGlobalReceiver(ServerHelloPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                serverModPresent = true;
                LOGGER.info("Disc Jockey server mod detected. Enabling advanced playback features.");
            });
        });

        HudRenderCallback.EVENT.register(BlocksOverlay::render);
    }
}
