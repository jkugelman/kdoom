package name.kugelman.john.kdoom.file;

import java.io.*;
import java.nio.*;
import java.util.*;

public class WadFile extends Wad {
    File             path;
    RandomAccessFile file;

    private boolean                 isPatch;
    private int                     directoryOffset;
    private List<Lump>              lumps;
    private SortedMap<String, Lump> lumpsByName;

    public WadFile(File path) throws IOException {
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
                isPatch = false;
                break;

            case ('P' << 0) + ('W' << 8) + ('A' << 16) + ('D' << 24):
                isPatch = true;
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

    public boolean isComplete() {
        return !isPatch;
    }

    public boolean isPatch() {
        return isPatch;
    }

    public List<Lump> lumps() {
        return Collections.unmodifiableList(lumps);
    }

    @Override
    public SortedMap<String, Lump> lumpsByName() {
        return Collections.unmodifiableSortedMap(lumpsByName);
    }


    @Override
    public String toString() {
        return path.getName();
    }
}
