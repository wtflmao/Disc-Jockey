package semmiedev.disc_jockey.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import semmiedev.disc_jockey.Main;
import semmiedev.disc_jockey.Note;
import semmiedev.disc_jockey.Song;
import semmiedev.disc_jockey.SongLoader;
import semmiedev.disc_jockey.gui.hud.BlocksOverlay;
import semmiedev.disc_jockey.gui.SongListWidget;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscJockeyScreen extends Screen {
    private final Screen parent;
    private SongListWidget songListWidget;
    private final Map<Song, SongListWidget.SongEntry> songEntries = new HashMap<>();

    public DiscJockeyScreen() {
        this(null);
    }

    public DiscJockeyScreen(Screen parent) {
        super(Text.literal(Main.NAME));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        songListWidget = new SongListWidget(client, width, height - 60, 30, 20);
        addSelectableChild(songListWidget);

        songEntries.clear();
        SongListWidget.SongEntry selectedEntry = null;
        for (Song song : SongLoader.SONGS) {
            SongListWidget.SongEntry entry = new SongListWidget.SongEntry(song, songListWidget.children().size());
            songEntries.put(song, entry);
            if (entry.song == Main.SONG_PLAYER.song) {
                selectedEntry = entry;
            }
        }

        sortSongs();
        songListWidget.setSelected(selectedEntry);


        ButtonWidget playButton = ButtonWidget.builder(Text.translatable(Main.MOD_ID + ".gui.play"), button -> {
            SongListWidget.SongEntry entry = songListWidget.getSelectedOrNull();
            if (entry != null) {
                Main.SONG_PLAYER.start(entry.song);
            }
        }).dimensions(width / 2 - 102, height - 52, 68, 20).build();
        addDrawableChild(playButton);

        ButtonWidget stopButton = ButtonWidget.builder(Text.translatable(Main.MOD_ID + ".gui.stop"), button -> {
            Main.SONG_PLAYER.stop();
            Main.PREVIEWER.stop();
        }).dimensions(width / 2 - 102, height - 32, 68, 20).build();
        addDrawableChild(stopButton);

        ButtonWidget previewButton = ButtonWidget.builder(Text.translatable(Main.MOD_ID + ".gui.preview"), button -> {
            SongListWidget.SongEntry entry = songListWidget.getSelectedOrNull();
            if (entry != null) Main.PREVIEWER.start(entry.song);
        }).dimensions(width / 2 - 34, height - 52, 68, 20).build();
        addDrawableChild(previewButton);

        ButtonWidget favoriteButton = ButtonWidget.builder(Text.translatable(Main.MOD_ID + ".gui.favorite"), button -> {
            SongListWidget.SongEntry entry = songListWidget.getSelectedOrNull();
            if (entry != null) {
                entry.favorite = !entry.favorite;
                if (entry.favorite) Main.config.favorites.add(entry.song.fileName);
                else Main.config.favorites.remove(entry.song.fileName);
                sortSongs();
            }
        }).dimensions(width / 2 + 34, height - 52, 68, 20).build();
        addDrawableChild(favoriteButton);

        ButtonWidget openFolderButton = ButtonWidget.builder(Text.translatable(Main.MOD_ID + ".gui.open_folder"), button -> {
            Util.getOperatingSystem().open(Main.songsFolder);
        }).dimensions(width - 110, 8, 100, 20).build();
        addDrawableChild(openFolderButton);

        ButtonWidget hideWarningButton = ButtonWidget.builder(Text.translatable(Main.MOD_ID + ".gui.hide_warning"), button -> {
            Main.config.hideWarning = !Main.config.hideWarning;
            button.setMessage(Text.translatable(Main.MOD_ID + ".gui.hide_warning"));
        }).dimensions(width / 2 + 106, height - 52, 120, 20).build();
        addDrawableChild(hideWarningButton);
    }

    private void sortSongs() {
        SongListWidget.SongEntry selected = songListWidget.getSelectedOrNull();
        songListWidget.children().clear();
        int favoriteIndex = 0;
        for (Song song : SongLoader.SONGS) {
            if (Main.config.favorites.contains(song.fileName)) {
                songListWidget.children().add(favoriteIndex++, songEntries.get(song));
            } else {
                songListWidget.children().add(songEntries.get(song));
            }
        }
        songListWidget.setSelected(selected);
    }

    @Override
    public void close() {
        if (parent == null) super.close();
        else client.setScreen(parent);
        new Thread(() -> Main.configHolder.save()).start();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderDarkening(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        songListWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
    }
}
