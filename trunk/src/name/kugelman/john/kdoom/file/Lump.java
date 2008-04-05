package name.kugelman.john.kdoom.file;

import java.io.*;
import java.nio.*;

public class Lump {
    private WadFile wadFile;
    private int     index;
    private int     offset;
    private int     size;
    private String  name;

    public Lump(WadFile wadFile, int index, int offset, int size, String name) {
        this.wadFile = wadFile;

        this.index   = index;
        this.offset  = offset;
        this.size    = size;
        this.name    = name;
    }


    public WadFile getWadFile() {
        return wadFile;
    }

    public int getIndex() {
        return index;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }


    public ByteBuffer getData() throws IOException {
        byte[]     data   = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        synchronized (wadFile.file) {
            wadFile.file.seek     (offset);
            wadFile.file.readFully(data);
        }

        return buffer;
    }


    @Override
    public String toString() {
        return getName();
    }
}
