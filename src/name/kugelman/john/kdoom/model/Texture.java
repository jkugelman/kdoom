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
    List<Patch> patches;
    List<Point> origins;

    Texture(String name, Dimension size) {
        this.name    = name;
        this.size    = size;
        this.patches = new ArrayList<Patch>();
        this.origins = new ArrayList<Point>();
    }


    public String getName() {
        return name;
    }

    public Dimension getSize() {
        return size;
    }

    public void addPatch(Patch patch, Point origin) {
        patches.add(patch);
        origins.add(origin);
    }

    public List<Patch> patches() {
        return Collections.unmodifiableList(patches);
    }

    public List<Point> origins() {
        return Collections.unmodifiableList(origins);
    }


    public BufferedImage getImage() throws IOException {
        BufferedImage image    = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics      graphics = image.createGraphics();

        for (int i = 0; i < patches.size(); ++i) {
            final Patch patch  = patches.get(i);
            final Point origin = origins.get(i);

            if (!patch.exists()) {
                continue;
            }

            graphics.drawImage(patch.getImage(), origin.x, origin.y, null);
        }

        return image;
    }
}
