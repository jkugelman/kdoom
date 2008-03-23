package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class FlatList extends AbstractList<Flat> {
    private List<Flat> flats;

    public FlatList(Wad wad) throws IOException {
        Lump startLump = wad.getLump("F_START");
        Lump endLump   = wad.getLump("F_END");

        this.flats = new ArrayList<Flat>(endLump.getIndex() - startLump.getIndex() - 1);

        for (int i = startLump.getIndex() + 1; i < endLump.getIndex(); ++i) {
            Lump lump = wad.lumps().get(i);

            if (lump.getName().matches("F[12]_(START|END)")) {
                continue;
            }

            flats.add(new Flat(lump));
        }
    }

    @Override
    public int size() {
        return flats.size();
    }

    @Override
    public Flat get(int index) throws IndexOutOfBoundsException {
        return flats.get(index);
    }
}
