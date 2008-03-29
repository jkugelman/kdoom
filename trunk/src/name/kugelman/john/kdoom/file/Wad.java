package name.kugelman.john.kdoom.file;

import java.io.*;
import java.nio.*;
import java.util.*;

public class Wad {
    File             path;
    RandomAccessFile file;

    private boolean    isComplete;
    private int        directoryOffset;
    private List<Lump> lumps;

    public Wad(File path) throws IOException {
        this.path = path;
        this.file = new RandomAccessFile(path, "r");

        readHeader   ();
        readDirectory();
    }

    private void readHeader() throws IOException {
        // Jump to header.
        file.seek(0);

        byte[]     headerBytes = new byte[12];
        ByteBuffer buffer      = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

        file.readFully(headerBytes);

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
        lumps = new ArrayList<Lump>(Arrays.asList(new Lump[buffer.getInt()]));

        // Read directory offset.
        directoryOffset = buffer.getInt();
    }

    private void readDirectory() throws IOException {
        // Jump to directory.
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

            lumps.set(i, new Lump(this, i, lumpOffset, lumpSize, lumpName));
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

    public Lump lookupLump(String name) throws IOException {
        for (Lump lump: lumps) {
            if (lump.getName().equals(name)) {
                return lump;
            }
        }

        return null;
    }
    
    public Lump getLump(String name) throws IOException {
        Lump lump = lookupLump(name);

        if (lump == null) {
            throw new IOException(name + " not found.");
        }

        return lump;
    }


}
