package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class PatchList extends AbstractList<Patch> {
    List<Patch> patches;

    PatchList(Wad wad) throws IOException {
        ByteBuffer buffer     = wad.lump("PNAMES").getData();
        int        patchCount = buffer.getInt();
        byte[]     nameBytes  = new byte[8];

        this.patches = new ArrayList<Patch>(patchCount);

        for (int i = 0; i < patchCount; ++i) {
            buffer.get(nameBytes);

            String name  = new String(nameBytes, "ISO-8859-1").trim().toUpperCase();
            Patch  patch = new Patch(wad, name);

            patches.add(patch);
        }
    }

    @Override
    public int size() {
        return patches.size();
    }

    @Override
    public Patch get(int index) throws IndexOutOfBoundsException {
        return patches.get(index);
    }
}
