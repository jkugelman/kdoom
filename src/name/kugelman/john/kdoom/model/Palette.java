package name.kugelman.john.kdoom.model;

import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Palette {
    public static final int COLORS = 256;
    public static final int SIZE   = COLORS * 3;

    Lump     lump;
    byte[][] paletteData;
    int      activePalette;

    public Palette(Lump lump) throws IOException {
        if (!lump.getName().equals("PLAYPAL")) {
            throw new IllegalArgumentException(lump.getName() + " is not palette.");
        }

        if (lump.getSize() % SIZE != 0) {
            throw new IOException("PLAYPAL not multiple of " + SIZE + " bytes.");
        }
         
        this.lump          = lump;
        this.paletteData   = new byte[lump.getSize() / SIZE][];
        this.activePalette = 0;

        ByteBuffer buffer = lump.getData();

        for (int i = 0; i < paletteData.length; ++i) {
            paletteData[i] = new byte[SIZE];
            buffer.get(paletteData[i]);
        }
    }

    public int getCount() {
        return lump.getSize() / SIZE;
    }

    public int getActivePalette() {
        return activePalette;
    }

    public void setActivePalette(int activePalette) {
        this.activePalette = activePalette;
    }


    public ColorModel getColorModel() throws IOException {
        return new IndexColorModel(8, COLORS, paletteData[activePalette], 0, false);
    }
}
