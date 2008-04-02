package name.kugelman.john.kdoom.model;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class SoundList extends AbstractList<Sound> {
    private List<Sound> sounds;

    public SoundList(Wad wad) throws IOException {
        this.sounds = new ArrayList<Sound>();

        for (Lump lump: wad.lumps()) {
            if (lump.getName().matches("DS.*")) {
                sounds.add(new Sound(lump));
            }
        }
    }

    @Override
    public int size() {
        return sounds.size();
    }

    @Override
    public Sound get(int index) throws IndexOutOfBoundsException {
        return sounds.get(index);
    }
}
