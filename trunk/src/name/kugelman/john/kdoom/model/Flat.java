package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Flat {
    public static final int WIDTH  = 64;
    public static final int HEIGHT = 64;
    public static final int SIZE   = WIDTH * HEIGHT;

    private Lump lump;

    Flat(Lump lump) throws IOException {
        if (lump.getSize() != SIZE) {
            throw new IOException("Flat " + lump.getName() + " is incorrect size.");
        }

        this.lump = lump;
    }


    public String getName() {
        return lump.getName();
    }

    public static Dimension getSize() {
        return new Dimension(WIDTH, HEIGHT);
    }


    public BufferedImage getImage() throws IOException {
        IndexColorModel colorModel = Resources.getPalette().getColorModel();
        BufferedImage   image      = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_INDEXED, colorModel);

        // Read flat pixels.
        ByteBuffer buffer     = lump.getData();
        byte[]     pixels     = new byte[SIZE];

        buffer.get(pixels);

        // Copy to image.
        image.getRaster().setDataElements(0, 0, WIDTH, HEIGHT, pixels);

        return image;
    }
}
