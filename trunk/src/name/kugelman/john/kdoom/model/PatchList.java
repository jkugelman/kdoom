package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class PatchList extends AbstractMap<String, Patch> {
    List<String>       names;
    Map<String, Patch> patches;

    public PatchList(Wad wad) throws IOException {
        ByteBuffer buffer     = wad.getLump("PNAMES").getData();
        int        patchCount = buffer.getInt();

        this.names   = new ArrayList    <String>       (patchCount);
        this.patches = new LinkedHashMap<String, Patch>(patchCount);

        for (int i = 0; i < patchCount; ++i) {
            byte[] nameBytes = new byte[8];

            buffer.get(nameBytes);

            String name  = new String(nameBytes, "ISO-8859-1").trim().toUpperCase();
            Lump   lump  = wad.lookupLump(name);
            Patch  patch = (lump == null) ? null : new Patch(lump);

            names  .add(name);
            patches.put(name, patch);
        }
    }

    @Override
    public Set<Entry<String, Patch>> entrySet() {
        return patches.entrySet();
    }

    public Patch get(int index) throws IndexOutOfBoundsException {
        return patches.get(names.get(index));
    }

    public String getName(int index) throws IndexOutOfBoundsException {
        return names.get(index);
    }
}
