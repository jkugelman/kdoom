package name.kugelman.john.kdoom.file;

import java.io.*;
import java.nio.*;
import java.util.*;

public class Wad {
    File             path;
    RandomAccessFile file;

    private boolean                 isComplete;
    private int                     directoryOffset;
    private List<Lump>              lumps;
    private SortedMap<String, Lump> lumpsByName;

    public Wad(File path) throws IOException {
        this.path = path;
        this.file = new RandomAccessFile(path, "r");

        readHeader   ();
        readDirectory();
    }

    private void readHeader() throws IOException {
        byte[]     headerBytes = new byte[12];
        ByteBuffer buffer      = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

        synchronized (file) {
            file.seek     (0);
            file.readFully(headerBytes);
        }

        // Read identification.
        switch (buffer.getInt()) {
            case ('I' << 0) + ('W' << 8) + ('A' << 16) + ('D' << 24):
                isComplete = true;
                break;

            case ('P' << 0) + ('W' << 8) + ('A' << 16) + ('D' << 24):
                isComplete = false;
                break;

            default:
                throw new IOException("File neither IWAD nor PWAD.");
        }

        // Read number of lumps.
        lumps       = new ArrayList<Lump>(Arrays.asList(new Lump[buffer.getInt()]));
        lumpsByName = new TreeMap<String, Lump>();

        // Read directory offset.
        directoryOffset = buffer.getInt();
    }

    private void readDirectory() throws IOException {
        synchronized (file) {
            file.seek(directoryOffset);

            byte[] lumpBytes = new byte[16];
            byte[] nameBytes = new byte[8];

            for (int i = 0; i < lumps.size(); ++i) {
                ByteBuffer buffer = ByteBuffer.wrap(lumpBytes).order(ByteOrder.LITTLE_ENDIAN);

                file.readFully(lumpBytes);

                int    lumpOffset = buffer.getInt();
                int    lumpSize   = buffer.getInt();
                                    buffer.get(nameBytes);
                String lumpName   = new String(nameBytes, "ISO-8859-1").trim();

//                System.out.printf("Lump %-4d - %-8s (%d bytes)%n", i, lumpName, lumpSize);

                Lump lump = new Lump(this, i, lumpOffset, lumpSize, lumpName);

                lumps      .set(i, lump);
                lumpsByName.put(lumpName, lump);
            }
        }
    }


    public File getFile() {
        return path;
    }

    public String getName() {
        return path.toString();
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isPatch() {
        return !isComplete();
    }

    public List<Lump> lumps() {
        return Collections.unmodifiableList(lumps);
    }

    public SortedMap<String, Lump> lumpsByName() {
        return Collections.unmodifiableSortedMap(lumpsByName);
    }

    public Lump lump(String name) throws IOException {
        Lump lump = lumpsByName.get(name);

        if (lump == null) {
            throw new IOException(name + " not found.");
        }

        return lump;
    }

    public Lump lookup(String name) {
        return lumpsByName.get(name);
    }

    public List<Lump> lumpsBetween(Lump start, Lump end) {
        return Collections.unmodifiableList(lumps.subList(start.getIndex() + 1, end.getIndex()));
    }

    public Collection<Lump> lumpsStartingWith(String prefix) {
        SortedMap<String, Lump> map = lumpsByName().tailMap(prefix);

        for (String lumpName: map.keySet()) {
            if (!lumpName.startsWith(prefix)) {
                map = map.headMap(lumpName);
                break;
            }
        }

        return map.values();
    }

    public Collection<Lump> lumpsMatching(String regex) {
        List<Lump> lumps = new ArrayList<Lump>();

        for (String lumpName: lumpsByName.keySet()) {
            if (lumpName.matches(regex)) {
                lumps.add(lumpsByName.get(lumpName));
            }
        }

        return lumps;
    }
}
