package name.kugelman.john.kdoom.model;

import java.io.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class SoundList extends AbstractMap<String, Sound> implements SortedMap<String, Sound> {
    private SortedMap<String, Sound> sounds;

    SoundList(Wad wad) throws IOException {
        this.sounds = new TreeMap<String, Sound>();

        for (Lump lump: wad.lumpsStartingWith("DS")) {
            sounds.put(lump.getName(), new Sound(lump));
        }
    }


    // Implementation of AbstractMap

    @Override
    public Set<Map.Entry<String, Sound>> entrySet() {
        return sounds.entrySet();
    }


    // Implementation of SortedMap

    public Comparator<? super String> comparator() {
        return sounds.comparator();
    }

    public String firstKey() {
        return sounds.firstKey();
    }

    public String lastKey() {
        return sounds.lastKey();
    }

    public SortedMap<String, Sound> headMap(String toKey) {
        return sounds.headMap(toKey);
    }

    public SortedMap<String, Sound> subMap(String fromKey, String toKey) {
        return sounds.subMap(fromKey, toKey);
    }

    public SortedMap<String, Sound> tailMap(String fromKey) {
        return sounds.tailMap(fromKey);
    }
}
