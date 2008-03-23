package name.kugelman.john.kdoom.model;

import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Level {
    private String       name;
    private List<Thing>  things;
    private List<Vertex> vertices;
    private List<Line>   lines;
    private short        minX, minY, maxX, maxY;

    public Level(Wad wad, String name) throws IllegalArgumentException, IOException {
        if (!name.matches("E\\dM\\d|MAP\\d\\d")) {
            throw new IllegalArgumentException("Invalid map name " + name + ".");
        }

        Lump nameLump = wad.getLump(name);

        minX = minY = Short.MAX_VALUE;
        maxX = maxY = Short.MIN_VALUE;

        readName    (wad.lumps().get(nameLump.getIndex() + 0));
        readThings  (wad.lumps().get(nameLump.getIndex() + 1));
        readVertices(wad.lumps().get(nameLump.getIndex() + 4));
        readLines   (wad.lumps().get(nameLump.getIndex() + 2));
    }

    private void readName(Lump lump) throws IOException {
        this.name = lump.getName();
    }

    private void readThings(Lump lump) throws IOException {
        if (!lump.getName().equals("THINGS")) {
            throw new IOException(name + " has no THINGS.");
        }

        ShortBuffer buffer = lump.getData().asShortBuffer();

        things = new ArrayList<Thing>();

        while (buffer.hasRemaining()) {
            short x     = buffer.get();
            short y     = buffer.get();
            short angle = buffer.get();
            short type  = buffer.get();
            short flags = buffer.get();

            things.add(new Thing(x, y, angle));

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }       
    }

    private void readVertices(Lump lump) throws IOException {
        if (!lump.getName().equals("VERTEXES")) {
            throw new IOException(name + " has no VERTEXES.");
        }

        ShortBuffer buffer = lump.getData().asShortBuffer();

        vertices = new ArrayList<Vertex>();

        while (buffer.hasRemaining()) {
            short x = buffer.get();
            short y = buffer.get();

            vertices.add(new Vertex(x, y));

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }
    }

    private void readLines(Lump lump) throws IOException {
        if (!lump.getName().equals("LINEDEFS")) {
            throw new IOException(name + " has no LINEDEFS.");
        }

        ShortBuffer buffer = lump.getData().asShortBuffer();

        lines = new ArrayList<Line>();

        while (buffer.hasRemaining()) {
            Vertex start       = vertices.get(buffer.get());
            Vertex end         = vertices.get(buffer.get());
            short  flags       = buffer.get();
            short  specialType = buffer.get();
            short  sectorTag   = buffer.get();
            short  rightSide   = buffer.get();
            short  leftSide    = buffer.get();

            lines.add(new Line(start, end, (flags & 0x0020) != 0, (flags & 0x0004) != 0));
        }
    }


    public String getName() {
        return name;
    }

    public List<Thing> things() {
        return Collections.unmodifiableList(things);
    }

    public List<Vertex> vertices() {
        return Collections.unmodifiableList(vertices);
    }

    public List<Line> lines() {
        return Collections.unmodifiableList(lines);
    }


    public short getMinX() { return minX; }
    public short getMinY() { return minY; }
    public short getMaxX() { return maxX; }
    public short getMaxY() { return maxY; }
}
