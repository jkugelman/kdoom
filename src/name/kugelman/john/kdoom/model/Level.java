package name.kugelman.john.kdoom.model;

import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Level {
    private Wad  wad;
    private int  lumpIndex;
    private Lump nameLump, vertexesLump;

    private String       name;
    private List<Thing>  things;
    private List<Vertex> vertices;
    private List<Line>   lines;
    private short        minX, minY, maxX, maxY;

    public Level(Wad wad, String name) throws IOException {
        this.wad       = wad;
        this.lumpIndex = -1;

        for (int i = 0; i < wad.lumps().size(); ++i) {
            if (wad.lumps().get(i).getName().equals(name)) {
                lumpIndex = i;
                break;
            }
        }

        if (lumpIndex == -1) {
            throw new IOException("Level " + name + " not found.");
        }

        minX = minY = Short.MAX_VALUE;
        maxX = maxY = Short.MIN_VALUE;

        readName    (wad.lumps().get(lumpIndex + 0));
        readThings  (wad.lumps().get(lumpIndex + 1));
        readVertices(wad.lumps().get(lumpIndex + 4));
        readLines   (wad.lumps().get(lumpIndex + 2));
    }

    private void readName(Lump lump) throws IOException {
        this.name = lump.getName();
    }

    private void readThings(Lump lump) throws IOException {
        assert lump.getName().equals("THINGS");
        
        ShortBuffer buffer = lump.getData().asShortBuffer();

        things = new ArrayList<Thing>();

        for (int i = 0; i < lump.getSize() / 10; ++i) {
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
        assert lump.getName().equals("VERTEXES");
        
        ShortBuffer buffer = lump.getData().asShortBuffer();

        vertices = new ArrayList<Vertex>();

        for (int i = 0; i < lump.getSize() / 4; ++i) {
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
        assert lump.getName().equals("LINEDEFS");

        ShortBuffer buffer = lump.getData().asShortBuffer();

        lines = new ArrayList<Line>();

        for (int i = 0; i < lump.getSize() / 14; ++i) {
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
