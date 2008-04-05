package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class PatchList extends AbstractMap<String, Patch> {
    List<String>       patchNames;
    List<Patch>        patches;
    Map<String, Patch> patchesByName;

    PatchList() throws IOException {
        Wad        wad        = Resources.getWad();
        ByteBuffer buffer     = wad.lump("PNAMES").getData();
        int        patchCount = buffer.getInt();
        byte[]     nameBytes  = new byte[8];

        this.patchNames    = new ArrayList    <String>       (patchCount);
        this.patches       = new ArrayList    <Patch>        (patchCount);
        this.patchesByName = new LinkedHashMap<String, Patch>(patchCount);

        for (int i = 0; i < patchCount; ++i) {
            buffer.get(nameBytes);

            String name  = new String(nameBytes, "ISO-8859-1").trim().toUpperCase();
            Lump   lump  = wad.lookup(name);
            Patch  patch = lump == null ? null : new Patch(lump);

            patches      .add(patch);
            patchesByName.put(name, patch);
        }
    }


    @Override
    public Set<Entry<String, Patch>> entrySet() {
        return patchesByName.entrySet();
    }


    public String getName(short number) throws IndexOutOfBoundsException {
        return patchNames.get(number);
    }

    public Patch get(short number) throws IndexOutOfBoundsException {
        return patches.get(number);
    }
}
