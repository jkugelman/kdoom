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
    private List<Vertex> vertices;
    private short        minX, minY, maxX, maxY;

    public Level(Wad wad, int lumpIndex) throws IOException {
        this.wad       = wad;
        this.lumpIndex = lumpIndex;

        readName    (wad.lumps().get(lumpIndex + 0));
        readVertices(wad.lumps().get(lumpIndex + 4));
    }

    private void readName(Lump lump) throws IOException {
        this.name = lump.getName();
    }

    private void readVertices(Lump lump) throws IOException {
        this.vertices = new ArrayList<Vertex>();

        ShortBuffer vertexData = lump.getData().asShortBuffer();

        minX = minY = Short.MAX_VALUE;
        maxX = maxY = Short.MIN_VALUE;

        for (int i = 0; i < lump.getSize() / 4; ++i) {
            short x = vertexData.get();
            short y = vertexData.get();

            vertices.add(new Vertex(x, y));

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }
    }


    public String getName() {
        return name;
    }

    public List<Vertex> vertices() {
        return Collections.unmodifiableList(vertices);
    }

    public short getMinX() { return minX; }
    public short getMinY() { return minY; }
    public short getMaxX() { return maxX; }
    public short getMaxY() { return maxY; }


    public static void main(String[] arguments) {
        try {
            Wad   wad   = new Wad(new File(arguments[0]));
            Level level = new Level(wad, 0);

            System.out.printf("Level:    %s%n", level.getName());
            System.out.printf("Vertices: %d%n", level.vertices().size());
            System.out.println();

            for (int i = 0; i < level.vertices().size(); ++i) {
                Vertex vertex = level.vertices().get(i);

                System.out.printf("#%d -\t(%d, %d)%n", i + 1, vertex.getX(), vertex.getY());
            }

            System.out.println();
            System.out.printf("Map extents: (%d, %d) to (%d, %d)%n",
                level.getMinX(), level.getMinY(), level.getMaxX(), level.getMaxY());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
