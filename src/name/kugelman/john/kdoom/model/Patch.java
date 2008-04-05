package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Patch {
    private String    name;
    private Lump      lump;
    private Dimension size;
    private Point     offset;

    Patch(Wad wad, String name) throws IOException {
        this.name = name;
        this.lump = wad.lookup(name);

        if (lump != null) {
            ShortBuffer buffer = lump.getData().asShortBuffer();

            this.size   = new Dimension(buffer.get(), buffer.get());
            this.offset = new Point    (buffer.get(), buffer.get());
        }
    }

    Patch(Lump lump) throws IOException {
        this(lump.getWadFile(), lump.getName());
    }


    public String getName() {
        return name;
    }

    public boolean exists() {
        return lump != null;
    }

    public Dimension getSize() {
        return size;
    }

    public Point getOffset() {
        return offset;
    }


    public BufferedImage getImage() throws IOException {
        ByteBuffer buffer = lump.getData();

        // Read image dimensions.
        int   width  = buffer.getShort() & 0xffff;
        int   height = buffer.getShort() & 0xffff;
        short top    = buffer.getShort();
        short left   = buffer.getShort();

        BufferedImage  image      = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ColorModel     colorModel = Resources.getPalette().getColorModel();

        // Read column offsets.
        int[] columnOffsets = new int[width];

        for (int i = 0; i < width; ++i) {
            columnOffsets[i] = buffer.getInt();
        }

        // Read pixel data for each column.
        for (int column = 0; column < columnOffsets.length; ++column) {
            buffer.position(columnOffsets[column]);

            int y = 0;

            // Draw posts.
            for (;;) {
                int rowStart = buffer.get() & 0xff;

                if (rowStart == 255) {
                    break;
                }

                int pixelCount = buffer.get() & 0xff;

                buffer.get();

                // Draw transparent pixels between posts.
                while (y < rowStart) {
                    image.setRGB(column, y++, 0);
                }

                // Draw post.
                for (; pixelCount > 0; --pixelCount) {
                    image.setRGB(column, y++, colorModel.getRGB(buffer.get() & 0xff));
                }

                buffer.get();
            }

            // Draw transparent pixels at bottom of column.
            while (y < height) {
                image.setRGB(column, y++, 0);
            }
        }

        return image;
    }
}
