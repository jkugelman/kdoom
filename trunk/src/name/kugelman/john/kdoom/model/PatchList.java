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

    public PatchList(Wad wad) throws IOException {
        ByteBuffer buffer = wad.getLump("PNAMES").getData();

        this.patches = new ArrayList<Patch>();

        for (int i = buffer.getInt(); i > 0; --i) {
            byte[] nameBytes = new byte[8];

            buffer.get(nameBytes);

            String name = new String(nameBytes, "ISO-8859-1").trim().toUpperCase();
            Lump   lump = wad.lookupLump(name);

            if (lump == null) {
                System.err.printf("No such patch %s.%n", name);
                continue;
            }

            patches.add(new Patch(lump));
        }
    }


    public Patch get(int index) throws IndexOutOfBoundsException {
        return patches.get(index);
    }

    public int size() {
        return patches.size();
    }
}
