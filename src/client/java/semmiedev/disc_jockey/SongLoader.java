package semmiedev.disc_jockey;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import semmiedev.disc_jockey.gui.SongListWidget;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SongLoader {
    public static final ArrayList<Song> SONGS = new ArrayList<>();
    public static final ArrayList<String> SONG_SUGGESTIONS = new ArrayList<>();
    public static volatile boolean loadingSongs;
    public static volatile boolean showToast;

    public static void loadSongs() {
        if (loadingSongs) return;
        new Thread(() -> {
            loadingSongs = true;
            SONGS.clear();
            SONG_SUGGESTIONS.clear();
            SONG_SUGGESTIONS.add("Songs are loading, please wait");
            for (File file : Main.songsFolder.listFiles()) {
                if (!file.isFile()) continue;

                Song song = null;
                try {
                    String fileName = file.getName().toLowerCase();
                    if (fileName.endsWith(".mid") || fileName.endsWith(".midi")) {
                        song = MidiLoader.loadFromMidi(file);
                    } else if (fileName.endsWith(".nbs")) {
                        song = loadNbsSong(file);
                    }
                } catch (Exception exception) {
                    Main.LOGGER.error("Unable to read or parse song {}", file.getName(), exception);
                }
                if (song != null) {
                    song.fileName = file.getName().replaceAll("[\\n\\r]", "");
                    song.displayName = song.name.replaceAll("\\s", "").isEmpty() ? song.fileName : song.name + " (" + song.fileName + ")";
                    song.searchableFileName = song.fileName.toLowerCase().replaceAll("\\s", "");
                    song.searchableName = song.name.toLowerCase().replaceAll("\\s", "");
                    SONGS.add(song);
                }
            }
            // Sort songs alphabetically after loading
            SONGS.sort(Comparator.comparing(s -> s.displayName));
            // Rebuild suggestions after sorting
            SONG_SUGGESTIONS.clear();
            for (Song song : SONGS) SONG_SUGGESTIONS.add(song.displayName);

            Main.config.favorites.removeIf(favorite -> SongLoader.SONGS.stream().map(s -> s.fileName).noneMatch(favorite::equals));

            if (showToast && MinecraftClient.getInstance().textRenderer != null) {
                SystemToast.add(
                    MinecraftClient.getInstance().getToastManager(), 
                    SystemToast.Type.PACK_LOAD_FAILURE, 
                    Text.literal(Main.NAME), 
                    Text.translatable(Main.MOD_ID+".loading_done")
                );
            }
            showToast = true;
            loadingSongs = false;
        }).start();
    }

    public static Song loadNbsSong(File file) throws IOException {
        BinaryReader reader = new BinaryReader(Files.newInputStream(file.toPath()));
        Song song = new Song();

        song.length = reader.readShort();

        boolean newFormat = song.length == 0;
        if (newFormat) {
            song.formatVersion = reader.readByte();
            song.vanillaInstrumentCount = reader.readByte();
            song.length = reader.readShort();
        }

        song.height = reader.readShort();
        song.name = reader.readString().replaceAll("[\\n\\r]", "");
        song.author = reader.readString().replaceAll("[\\n\\r]", "");
        song.originalAuthor = reader.readString().replaceAll("[\\n\\r]", "");
        song.description = reader.readString().replaceAll("[\\n\\r]", "");
        song.tempo = reader.readShort();
        song.autoSaving = reader.readByte();
        song.autoSavingDuration = reader.readByte();
        song.timeSignature = reader.readByte();
        song.minutesSpent = reader.readInt();
        song.leftClicks = reader.readInt();
        song.rightClicks = reader.readInt();
        song.blocksAdded = reader.readInt();
        song.blocksRemoved = reader.readInt();
        song.importFileName = reader.readString().replaceAll("[\\n\\r]", "");

        if (newFormat) {
            song.loop = reader.readByte();
            song.maxLoopCount = reader.readByte();
            song.loopStartTick = reader.readShort();
        }

        short tick = -1;
        short jumps;
        while ((jumps = reader.readShort()) != 0) {
            tick += jumps;
            short layer = -1;
            while ((jumps = reader.readShort()) != 0) {
                layer += jumps;

                byte instrumentId = reader.readByte();
                byte noteId = (byte)(reader.readByte() - 33);

                if (newFormat) {
                    reader.readByte(); 
                    reader.readByte(); 
                    reader.readShort();
                }

                if (noteId < 0) {
                    noteId = 0;
                } else if (noteId > 24) {
                    noteId = 24;
                }

                Note note = new Note(Note.INSTRUMENTS[instrumentId], noteId);
                if (!song.uniqueNotes.contains(note)) song.uniqueNotes.add(note);

                song.notes = Arrays.copyOf(song.notes, song.notes.length + 1);
                song.notes[song.notes.length - 1] = tick | layer << Note.LAYER_SHIFT | (long)instrumentId << Note.INSTRUMENT_SHIFT | (long)noteId << Note.NOTE_SHIFT;
            }
        }

        return song;
    }

    public static void sort() {
        SONGS.sort(Comparator.comparing(song -> song.displayName));
    }
}
