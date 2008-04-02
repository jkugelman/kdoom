package name.kugelman.john.kdoom.model;

import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Palette {
    public static final int COLORS = 256;
    public static final int SIZE   = COLORS * 3;

    byte[][]          paletteData;
    IndexColorModel[] colorModels;
    int               activePalette;

    public Palette(Wad wad) throws IOException {
        this(wad.getLump("PLAYPAL"));
    }

    public Palette(Lump lump) throws IOException {
        if (!lump.getName().equals("PLAYPAL")) {
            throw new IllegalArgumentException(lump.getName() + " is not a palette.");
        }

        if (lump.getSize() % SIZE != 0) {
            throw new IOException("PLAYPAL not multiple of " + SIZE + " bytes.");
        }

        this.paletteData   = new byte[lump.getSize() / SIZE][];
        this.colorModels   = new IndexColorModel[paletteData.length];
        this.activePalette = 0;

        ByteBuffer buffer = lump.getData();

        for (int i = 0; i < paletteData.length; ++i) {
            paletteData[i] = new byte[SIZE];
            buffer.get(paletteData[i]);

            colorModels[i] = new IndexColorModel(8, COLORS, paletteData[i], 0, false);
        }
    }

    public int getCount() {
        return paletteData.length;
    }

    public int getActivePalette() {
        return activePalette;
    }

    public void setActivePalette(int activePalette) {
        this.activePalette = activePalette;
    }


    public IndexColorModel getColorModel() {
        return colorModels[activePalette];
    }
}
