package name.kugelman.john.kdoom.model;

import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Level {
    private String        name;

    private Wad           wad;
    private FlatList      flats;
    private TextureList   textures;

    private List<Thing>   things;
    private List<Vertex>  vertices;
    private List<Sidedef> sidedefs;
    private List<Line>    lines;
    private List<Sector>  sectors;

    private short         minX, minY, maxX, maxY;

    public Level(Wad wad, String name, FlatList flats, TextureList textures)
        throws IllegalArgumentException, IOException
    {
        if (!name.matches("E\\dM\\d|MAP\\d\\d")) {
            throw new IllegalArgumentException("Invalid map name " + name + ".");
        }

        this.wad      = wad;
        this.flats    = flats;
        this.textures = textures;    
        
        this.minX = this.minY = Short.MAX_VALUE;
        this.maxX = this.maxY = Short.MIN_VALUE;

        Lump nameLump = wad.getLump(name);

        readName    (wad.lumps().get(nameLump.getIndex() + 0));
        readThings  (wad.lumps().get(nameLump.getIndex() + 1));
        readVertices(wad.lumps().get(nameLump.getIndex() + 4));
        readSectors (wad.lumps().get(nameLump.getIndex() + 8));
        readSides   (wad.lumps().get(nameLump.getIndex() + 3));
        readLines   (wad.lumps().get(nameLump.getIndex() + 2));

        for (Sector sector: sectors) {
            sector.updateGeometry();
        }
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

            things.add(new Thing(wad, (short) things.size(), new Location(x, y), angle, type, flags));

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

            vertices.add(new Vertex((short) vertices.size(), x, y));

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }
    }

    private void readSectors(Lump lump) throws IOException {
        if (!lump.getName().equals("SECTORS")) {
            throw new IOException(name + " has no SECTORS.");
        }

        ByteBuffer buffer       = lump.getData();
        byte[]     floorBytes   = new byte[8];
        byte[]     ceilingBytes = new byte[8];

        sectors = new ArrayList<Sector>();

        while (buffer.hasRemaining()) {
            short  floorHeight    = buffer.getShort();
            short  ceilingHeight  = buffer.getShort(); 
                                    buffer.get(floorBytes);
                                    buffer.get(ceilingBytes);
            Flat   floorFlat      = flats.get(new String(floorBytes,   "ISO-8859-1").trim());
            Flat   ceilingFlat    = flats.get(new String(ceilingBytes, "ISO-8859-1").trim());
            short  lightLevel     = buffer.getShort();
            short  type           = buffer.getShort();
            short  tagNumber      = buffer.getShort();

            sectors.add(new Sector((short) sectors.size(), floorHeight, ceilingHeight, floorFlat, ceilingFlat, lightLevel, type, tagNumber));
        }
    }

    private void readLines(Lump lump) throws IOException {
        if (!lump.getName().equals("LINEDEFS")) {
            throw new IOException(name + " has no LINEDEFS.");
        }

        ShortBuffer buffer = lump.getData().asShortBuffer();

        lines = new ArrayList<Line>();

        while (buffer.hasRemaining()) {
            Vertex  start        = vertices.get(buffer.get());
            Vertex  end          = vertices.get(buffer.get());
            short   flags        = buffer.get();
            short   specialType  = buffer.get();
            short   sectorTag    = buffer.get();
            short   right        = buffer.get();
            short   left         = buffer.get();

            Sidedef leftSidedef  = left  < 0 ? null : sidedefs.get(left);
            Sidedef rightSidedef = right < 0 ? null : sidedefs.get(right);

            lines.add(new Line((short) lines.size(), start, end, leftSidedef, rightSidedef, flags));
        }
    }

    private void readSides(Lump lump) throws IOException {
        if (!lump.getName().equals("SIDEDEFS")) {
            throw new IOException(name + " has no SIDEDEFS.");
        }

        ByteBuffer buffer      = lump.getData();
        byte[]     upperBytes  = new byte[8];
        byte[]     lowerBytes  = new byte[8];
        byte[]     middleBytes = new byte[8];

        sidedefs = new ArrayList<Sidedef>();

        while (buffer.hasRemaining()) {
            short xOffset      = buffer.getShort();
            short yOffset      = buffer.getShort();
                                 buffer.get(upperBytes);
                                 buffer.get(lowerBytes);
                                 buffer.get(middleBytes);
            int sectorNumber   = buffer.getShort() & 0xFFFF;

            if (sectorNumber >= sectors.size()) {
                System.err.println("SIDEDEF " + sidedefs.size() + " has reference to non-existent SECTOR " + sectorNumber);
                sectorNumber = 0;
            }

            Texture upperTexture  = textures.get(new String(upperBytes,  "ISO-8859-1").trim());
            Texture lowerTexture  = textures.get(new String(lowerBytes,  "ISO-8859-1").trim());
            Texture middleTexture = textures.get(new String(middleBytes, "ISO-8859-1").trim());
            Sector sector         = sectors.get(sectorNumber);

            sidedefs.add(new Sidedef((short) sidedefs.size(), xOffset, yOffset, upperTexture, lowerTexture, middleTexture, sector));
        }
    }


    public String getName() {
        return name;
    }

    public Wad getWad() {
        return wad;
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

    public List<Sidedef> sidedefs() {
        return Collections.unmodifiableList(sidedefs);
    }

    public List<Sector> sectors() {
        return Collections.unmodifiableList(sectors);
    }


    public short getMinX() { return minX; }
    public short getMinY() { return minY; }
    public short getMaxX() { return maxX; }
    public short getMaxY() { return maxY; }


    public Collection<Line> getLinesClosestTo(Location location) {
        return getLinesClosestTo(location, Double.POSITIVE_INFINITY);
    }

    public Collection<Line> getLinesClosestTo(Location location, double maximumDistance) {
        List<Line> closestLines    = new ArrayList<Line>();
        double     closestDistance = maximumDistance;

        if (location != null) {
            for (Line line: lines) {
                double distance = line.distanceTo(location);

                if (distance > closestDistance) {
                    continue;
                }

                if (distance < closestDistance) {
                    closestLines.clear();
                    closestDistance = distance;
                }

                closestLines.add(line);
            }
        }

        return closestLines;
    }

    public Collection<Sector> getSectorsContaining(Location location) {
        Collection<Sector> sectors = new ArrayList<Sector>();
        
        for (Line line: getLinesClosestTo(location)) {
            Side facingSide = line.sideFacing(location);

            if (facingSide != null && facingSide.getSector() != null) {
                sectors.add(facingSide.getSector());
            }
        }

        return sectors; 
    }

    public Collection<Thing> getThingsAt(Location location) {
        List<Thing> things = new ArrayList<Thing>();

        if (location != null) {
            for (Thing thing: this.things) {
                if (location.distanceTo(thing.getLocation()) <= thing.getRadius()) {
                    things.add(thing);
                }
            }
        }

        return things;
    }
}
