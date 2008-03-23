package name.kugelman.john.kdoom.file;

import java.io.*;
import java.nio.*;

public class Lump {
    Wad wad;

    private int    offset;
    private int    size;
    private String name;

    public Lump(Wad wad, int offset, int size, String name) {
        this.wad    = wad;

        this.offset = offset;
        this.size   = size;
        this.name   = name;
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
        // Jump to lump.
        wad.file.seek(offset);

        byte[]     data   = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN); 

        wad.file.readFully(data);

        return buffer;
    }
}
