package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class Texture {
    String      name;
    Dimension   size;

    List<Short> patchNumbers;
    List<Point> patchOrigins;

    Texture(String name, Dimension size) {
        this.name         = name;
        this.size         = size;

        this.patchNumbers = new ArrayList<Short>();
        this.patchOrigins = new ArrayList<Point>();
    }


    public String getName() {
        return name;
    }

    public Dimension getSize() {
        return size;
    }


    void addPatch(short number, Point origin) {
        patchNumbers.add(number);
        patchOrigins.add(origin);
    }

    public List<Short> patchNumbers() {
        return Collections.unmodifiableList(patchNumbers);
    }

    public List<Point> patchOrigins() {
        return Collections.unmodifiableList(patchOrigins);
    }


    public BufferedImage getImage() throws IOException {
        BufferedImage image    = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics      graphics = image.createGraphics();

        for (int i = 0; i < patchNumbers.size(); ++i) {
            final Patch patch  = Resources.patches().get(patchNumbers.get(i));
            final Point origin = patchOrigins.get(i);

            if (patch == null) {
                continue;
            }

            graphics.drawImage(patch.getImage(), origin.x, origin.y, null);
        }

        return image;
    }


    @Override
    public String toString() {
        return getName();
    }
}
