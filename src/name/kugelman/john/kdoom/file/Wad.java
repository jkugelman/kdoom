package name.kugelman.john.kdoom.file;

import java.io.*;
import java.nio.*;
import java.util.*;

public abstract class Wad {
    public abstract SortedMap<String, Lump> lumpsByName();


    public Lump lump(String name) throws IOException {
        Lump lump = lumpsByName().get(name);

        if (lump == null) {
            throw new IOException(name + " not found.");
        }

        return lump;
    }

    public Lump lookup(String name) {
        return lumpsByName().get(name);
    }

    public List<Lump> lumpGroup(Lump start, int count) {
        return start.getWadFile().lumps().subList(start.getIndex(), start.getIndex() + count);
    }

    public List<Lump> lumpsBetween(Lump start, Lump end) throws IOException {
        if (start.getWadFile() != end.getWadFile()) {
            throw new IOException(start + " and " + end + " are in different files.");
        }

        return start.getWadFile().lumps().subList(start.getIndex() + 1, end.getIndex());
    }

    public Collection<Lump> lumpsStartingWith(String prefix) {
        SortedMap<String, Lump> map = lumpsByName().tailMap(prefix);

        for (String lumpName: map.keySet()) {
            if (!lumpName.startsWith(prefix)) {
                map = map.headMap(lumpName);
                break;
            }
        }

        return map.values();
    }

    public Collection<Lump> lumpsMatching(String regex) {
        List<Lump> lumps = new ArrayList<Lump>();

        for (Map.Entry<String, Lump> entry: lumpsByName().entrySet()) {
            if (entry.getKey().matches(regex)) {
                lumps.add(entry.getValue());
            }
        }

        return Collections.unmodifiableCollection(lumps);
    }
}
