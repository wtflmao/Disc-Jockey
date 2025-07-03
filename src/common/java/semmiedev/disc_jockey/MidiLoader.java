package semmiedev.disc_jockey;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MetaMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.enums.NoteBlockInstrument;

public class MidiLoader {
    // Maps General MIDI instrument program IDs to Minecraft Note Block instruments.
    private static final Map<Integer, NoteBlockInstrument> INSTRUMENT_MAP = new HashMap<>();

    static {
        // Populating the instrument map based on General MIDI to Minecraft Note Block sounds.
        // This mapping is subjective and can be adjusted for better sound representation.
        INSTRUMENT_MAP.put(0, NoteBlockInstrument.HARP); // Acoustic Grand Piano
        INSTRUMENT_MAP.put(1, NoteBlockInstrument.HARP); // Bright Acoustic Piano
        INSTRUMENT_MAP.put(2, NoteBlockInstrument.HARP); // Electric Grand Piano
        INSTRUMENT_MAP.put(3, NoteBlockInstrument.HARP); // Honky-tonk Piano
        INSTRUMENT_MAP.put(4, NoteBlockInstrument.HARP); // Electric Piano 1
        INSTRUMENT_MAP.put(5, NoteBlockInstrument.HARP); // Electric Piano 2
        INSTRUMENT_MAP.put(6, NoteBlockInstrument.HARP); // Harpsichord
        INSTRUMENT_MAP.put(7, NoteBlockInstrument.HARP); // Clavinet
        INSTRUMENT_MAP.put(8, NoteBlockInstrument.GLOWSTONE); // Celesta
        INSTRUMENT_MAP.put(9, NoteBlockInstrument.GLOWSTONE); // Glockenspiel
        INSTRUMENT_MAP.put(10, NoteBlockInstrument.BELL); // Music Box
        INSTRUMENT_MAP.put(11, NoteBlockInstrument.BELL); // Vibraphone
        INSTRUMENT_MAP.put(12, NoteBlockInstrument.BELL); // Marimba
        INSTRUMENT_MAP.put(13, NoteBlockInstrument.XYLOPHONE); // Xylophone
        INSTRUMENT_MAP.put(14, NoteBlockInstrument.BELL); // Tubular Bells
        INSTRUMENT_MAP.put(15, NoteBlockInstrument.BELL); // Dulcimer
        INSTRUMENT_MAP.put(16, NoteBlockInstrument.IRON_XYLOPHONE); // Drawbar Organ
        INSTRUMENT_MAP.put(17, NoteBlockInstrument.IRON_XYLOPHONE); // Percussive Organ
        INSTRUMENT_MAP.put(18, NoteBlockInstrument.IRON_XYLOPHONE); // Rock Organ
        INSTRUMENT_MAP.put(19, NoteBlockInstrument.IRON_XYLOPHONE); // Church Organ
        INSTRUMENT_MAP.put(20, NoteBlockInstrument.IRON_XYLOPHONE); // Reed Organ
        INSTRUMENT_MAP.put(21, NoteBlockInstrument.IRON_XYLOPHONE); // Accordion
        INSTRUMENT_MAP.put(22, NoteBlockInstrument.IRON_XYLOPHONE); // Harmonica
        INSTRUMENT_MAP.put(23, NoteBlockInstrument.IRON_XYLOPHONE); // Tango Accordion
        INSTRUMENT_MAP.put(24, NoteBlockInstrument.GUITAR); // Acoustic Guitar (nylon)
        INSTRUMENT_MAP.put(25, NoteBlockInstrument.GUITAR); // Acoustic Guitar (steel)
        INSTRUMENT_MAP.put(26, NoteBlockInstrument.GUITAR); // Electric Guitar (jazz)
        INSTRUMENT_MAP.put(27, NoteBlockInstrument.GUITAR); // Electric Guitar (clean)
        INSTRUMENT_MAP.put(28, NoteBlockInstrument.GUITAR); // Electric Guitar (muted)
        INSTRUMENT_MAP.put(29, NoteBlockInstrument.GUITAR); // Overdriven Guitar
        INSTRUMENT_MAP.put(30, NoteBlockInstrument.GUITAR); // Distortion Guitar
        INSTRUMENT_MAP.put(31, NoteBlockInstrument.GUITAR); // Guitar harmonics
        INSTRUMENT_MAP.put(32, NoteBlockInstrument.BASS); // Acoustic Bass
        INSTRUMENT_MAP.put(33, NoteBlockInstrument.BASS); // Electric Bass (finger)
        INSTRUMENT_MAP.put(34, NoteBlockInstrument.BASS); // Electric Bass (pick)
        INSTRUMENT_MAP.put(35, NoteBlockInstrument.BASS); // Fretless Bass
        INSTRUMENT_MAP.put(36, NoteBlockInstrument.BASS); // Slap Bass 1
        INSTRUMENT_MAP.put(37, NoteBlockInstrument.BASS); // Slap Bass 2
        INSTRUMENT_MAP.put(38, NoteBlockInstrument.BASS); // Synth Bass 1
        INSTRUMENT_MAP.put(39, NoteBlockInstrument.BASS); // Synth Bass 2
        INSTRUMENT_MAP.put(40, NoteBlockInstrument.FLUTE); // Violin
        INSTRUMENT_MAP.put(41, NoteBlockInstrument.FLUTE); // Viola
        INSTRUMENT_MAP.put(42, NoteBlockInstrument.FLUTE); // Cello
        INSTRUMENT_MAP.put(43, NoteBlockInstrument.FLUTE); // Contrabass
        INSTRUMENT_MAP.put(44, NoteBlockInstrument.FLUTE); // Tremolo Strings
        INSTRUMENT_MAP.put(45, NoteBlockInstrument.FLUTE); // Pizzicato Strings
        INSTRUMENT_MAP.put(46, NoteBlockInstrument.HARP); // Orchestral Harp
        INSTRUMENT_MAP.put(47, NoteBlockInstrument.HAT); // Timpani
        // This is a simplified mapping. A complete one would be very large.
        INSTRUMENT_MAP.put(56, NoteBlockInstrument.DIDGERIDOO); // Trumpet
        INSTRUMENT_MAP.put(57, NoteBlockInstrument.DIDGERIDOO); // Trombone
        INSTRUMENT_MAP.put(58, NoteBlockInstrument.DIDGERIDOO); // Tuba
        INSTRUMENT_MAP.put(60, NoteBlockInstrument.DIDGERIDOO); // French Horn
        INSTRUMENT_MAP.put(64, NoteBlockInstrument.FLUTE); // Soprano Sax
        INSTRUMENT_MAP.put(65, NoteBlockInstrument.FLUTE); // Alto Sax
        INSTRUMENT_MAP.put(66, NoteBlockInstrument.FLUTE); // Tenor Sax
        INSTRUMENT_MAP.put(67, NoteBlockInstrument.FLUTE); // Baritone Sax
        INSTRUMENT_MAP.put(71, NoteBlockInstrument.FLUTE); // Clarinet
        INSTRUMENT_MAP.put(73, NoteBlockInstrument.FLUTE); // Flute
        INSTRUMENT_MAP.put(114, NoteBlockInstrument.HAT); // Steel Drums
        INSTRUMENT_MAP.put(115, NoteBlockInstrument.COW_BELL); // Woodblock
        INSTRUMENT_MAP.put(118, NoteBlockInstrument.SNARE); // Synth Drum
    }

    // Maps MIDI percussion keys (pitch on channel 9) to Minecraft drum sounds.
    private static NoteBlockInstrument getPercussionInstrument(int midiPitch) {
        return switch (midiPitch) {
            case 35, 36, 41, 43, 45, 47, 48, 50 -> NoteBlockInstrument.BASEDRUM; // Acoustic Bass Drum, Bass Drum 1, etc.
            case 38, 40 -> NoteBlockInstrument.SNARE; // Acoustic Snare, Electric Snare
            case 42, 44, 46, 49, 51, 52, 53, 57, 59 -> NoteBlockInstrument.HAT; // Closed Hi-Hat, Pedal Hi-Hat, etc.
            case 56 -> NoteBlockInstrument.COW_BELL; // Cowbell
            case 70 -> NoteBlockInstrument.BELL; // Maracas
            case 60 -> NoteBlockInstrument.GUITAR; // Hi Bongo -> using a somewhat similar sound
            case 63 -> NoteBlockInstrument.BASS; // Open Hi Conga -> using a somewhat similar sound
            default -> NoteBlockInstrument.HAT; // Default to hi-hat for unmapped percussion
        };
    }

    /**
     * Loads a MIDI file and converts it into a Song object.
     *
     * @param midiFile The MIDI file to load.
     * @return A Song object representing the MIDI file.
     * @throws Exception If an error occurs during loading or parsing.
     */
    public static Song loadFromMidi(File midiFile) throws Exception {
        Sequence sequence = MidiSystem.getSequence(midiFile);

        // --- Tempo (BPM) Calculation ---
        long mspqn = 500000; // Default to 120 BPM (microseconds per quarter note)
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof MetaMessage metaMessage) {
                    if (metaMessage.getType() == 0x51) { // Set Tempo Meta-event
                        byte[] data = metaMessage.getData();
                        mspqn = ((data[0] & 0xFF) << 16) | ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);
                        break; // Use the first tempo event found
                    }
                }
            }
        }
        double bpm = 60000000.0 / mspqn;
        short tempo = (short) Math.round((bpm / 60.0) * 20.0 * 100.0);

        // --- Note Processing ---
        List<Note> notes = new ArrayList<>();
        int ppq = sequence.getResolution(); // Pulses (ticks) per quarter note

        int[] channelInstruments = new int[16];

        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage sm) {
                    int channel = sm.getChannel();
                    int command = sm.getCommand();
                    int data1 = sm.getData1(); // Note pitch
                    int data2 = sm.getData2(); // Velocity

                    if (command == ShortMessage.PROGRAM_CHANGE) {
                        channelInstruments[channel] = data1;
                    } else if (command == ShortMessage.NOTE_ON && data2 > 0) { // Note On with velocity > 0
                        long midiTick = event.getTick();

                        // Convert MIDI tick to song tick (20 ticks per second)
                        double timeInMs = ((double)midiTick / ppq) * ((double)mspqn / 1000.0);
                        int songTick = (int)Math.round(timeInMs / 50.0);

                        int originalMidiPitch = data1;
                        int noteId = originalMidiPitch % 25; // Fold into Minecraft's 2-octave range (0-24)

                        NoteBlockInstrument instrument;
                        if (channel == 9) { // MIDI channel 10 (0-indexed 9) is for percussion
                            instrument = getPercussionInstrument(originalMidiPitch);
                        } else {
                            int instrumentId = channelInstruments[channel];
                            instrument = INSTRUMENT_MAP.getOrDefault(instrumentId, NoteBlockInstrument.HARP);
                        }
                        int instrumentId = instrument.ordinal();

                        // Layer is unused for MIDI files, defaulting to 0
                        short layer = 0;

                        // Encode original MIDI pitch into the note's long value for advanced playback
                        // Format: tick | layer << 16 | instrumentId << 24 | noteId << 32 | originalMidiPitch << 40
                        long noteLong = (long)songTick | (long)layer << 16 | (long)instrumentId << 24 | (long)noteId << 32 | (long)originalMidiPitch << 40;
                        notes.add(new Note(noteLong));
                    }
                }
            }
        }
        
        // Sort notes by tick as they might be out of order after processing tracks
        notes.sort((n1, n2) -> Integer.compare(n1.tick(), n2.tick()));

        String name = midiFile.getName().substring(0, midiFile.getName().lastIndexOf('.'));
        short length = notes.isEmpty() ? 0 : (short) notes.get(notes.size() - 1).tick();

        return new Song(name, "", "", length, tempo, notes.stream().mapToLong(Note::note).toArray());
    }
} 