package semmiedev.disc_jockey;

import net.minecraft.block.enums.NoteBlockInstrument;

/**
 * 适配器类，用于兼容旧版本的Note用法
 */
public class NoteAdapter {
    private final long noteData;
    
    public NoteAdapter(long noteData) {
        this.noteData = noteData;
    }
    
    public int tick() {
        return (int)(noteData & 0xFFFF); // Extract the first 16 bits
    }
    
    public byte note() {
        return (byte)(noteData >> Note.NOTE_SHIFT);
    }
    
    public NoteBlockInstrument instrumentEnum() {
        int instrumentId = (byte)(noteData >> Note.INSTRUMENT_SHIFT);
        return instrumentId < Note.INSTRUMENTS.length ? Note.INSTRUMENTS[instrumentId] : Note.INSTRUMENTS[0];
    }
    
    public int originalMidiPitch() {
        return (int)((noteData >> 40) & 0xFF);
    }
    
    public Note toNote() {
        return new Note(instrumentEnum(), note());
    }
    
    public static NoteAdapter fromNote(Note note, int tick, int originalMidiPitch) {
        // Format: tick | layer << 16 | instrumentId << 24 | noteId << 32 | originalMidiPitch << 40
        int instrumentId = 0;
        for (int i = 0; i < Note.INSTRUMENTS.length; i++) {
            if (Note.INSTRUMENTS[i] == note.instrument()) {
                instrumentId = i;
                break;
            }
        }
        
        return new NoteAdapter(
            (long)tick | 
            0L << Note.LAYER_SHIFT | 
            (long)instrumentId << Note.INSTRUMENT_SHIFT | 
            (long)note.note() << Note.NOTE_SHIFT | 
            (long)originalMidiPitch << 40
        );
    }
} 