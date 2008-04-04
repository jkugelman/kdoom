package name.kugelman.john.kdoom.model;

import java.io.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class LevelList extends AbstractMap<String, Level> implements SortedMap<String, Level> {
    private SortedMap<String, Level> levels;
    
    LevelList(Wad wad) throws IOException {
        this.levels = new TreeMap<String, Level>();

        for (Lump lump: wad.lumpsMatching("E\\dM\\d|MAP\\d\\d")) {
            levels.put(lump.getName(), new Level(wad, lump.getName()));
        }
    }

    
    // Implementation of AbstractMap

    @Override
    public Set<Map.Entry<String, Level>> entrySet() {
        return levels.entrySet();
    }


    // Implementation of SortedMap

    public Comparator<? super String> comparator() {
        return levels.comparator();
    }

    public String firstKey() {
        return levels.firstKey();
    }

    public String lastKey() {
        return levels.lastKey();
    }

    public SortedMap<String, Level> headMap(String toKey) {
        return levels.headMap(toKey);
    }

    public SortedMap<String, Level> subMap(String fromKey, String toKey) {
        return levels.subMap(fromKey, toKey);
    }

    public SortedMap<String, Level> tailMap(String fromKey) {
        return levels.tailMap(fromKey);
    }
}
