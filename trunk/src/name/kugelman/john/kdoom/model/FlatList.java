package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class FlatList extends AbstractMap<String, Flat> implements SortedMap<String, Flat> {
    private SortedMap<String, Flat> flats;

    public FlatList(Wad wad) throws IOException {
        Lump startLump = wad.getLump("F_START");
        Lump endLump   = wad.getLump("F_END");

        this.flats = new TreeMap<String, Flat>();

        for (int i = startLump.getIndex() + 1; i < endLump.getIndex(); ++i) {
            Lump lump = wad.lumps().get(i);

            if (lump.getName().matches("F[12]_(START|END)")) {
                continue;
            }

            Flat flat = new Flat(lump);

            flats.put(flat.getName(), flat);
        }
    }


    // Implementation of AbstractMap

    @Override
    public Set<Map.Entry<String, Flat>> entrySet() {
        return flats.entrySet();
    }


    // Implementation of SortedMap

    public Comparator<? super String> comparator() {
        return flats.comparator();
    }

    public String firstKey() {
        return flats.firstKey();
    }

    public String lastKey() {
        return flats.lastKey();
    }

    public SortedMap<String, Flat> headMap(String toKey) {
        return flats.headMap(toKey);
    }

    public SortedMap<String, Flat> subMap(String fromKey, String toKey) {
        return flats.subMap(fromKey, toKey);
    }

    public SortedMap<String, Flat> tailMap(String fromKey) {
        return flats.tailMap(fromKey);
    }
}
