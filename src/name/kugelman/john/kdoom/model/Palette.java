package name.kugelman.john.kdoom.model;

import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Palette {
    public static final int COLORS = 256;
    public static final int SIZE   = COLORS * 3;

    Lump lump;

    public Palette(Lump lump) throws IOException {
        if (!lump.getName().equals("PLAYPAL")) {
            throw new IllegalArgumentException(lump.getName() + " is not palette.");
        }

        if (lump.getSize() % SIZE != 0) {
            throw new IOException("PLAYPAL not multiple of " + SIZE + " bytes.");
        }
         
        this.lump = lump;
    }

    public int getCount() {
        return lump.getSize() / SIZE;
    }

    public ColorModel getColorModel(int paletteNumber) throws IOException {
        byte[]     data   = new byte[SIZE];
        ByteBuffer buffer = lump.getData();

        buffer.position(paletteNumber * SIZE);
        buffer.get     (data);

        return new IndexColorModel(8, COLORS, data, 0, false);
    }
}
