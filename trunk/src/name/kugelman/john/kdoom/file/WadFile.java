package name.kugelman.john.kdoom.file;

import java.io.*;
import java.nio.*;

public class WadFile {
    public enum Identification {
        IWAD,
        PWAD
    }

    private RandomAccessFile file;
    private Identification   identification;
    private String[]         lumpNames;

    public WadFile(RandomAccessFile file) throws IOException {
        this.file = file;

        readHeader   ();
        readDirectory();
    }

    private void readHeader() throws IOException {
        byte[]     headerBytes = new byte[12];
        ByteBuffer buffer      = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

        file.readFully(headerBytes);

        // Read identification.
        byte[] idBytes = new byte[4];
        String idString;

        buffer.get(idBytes);
        idString = new String(idBytes, "US-ASCII");

        if (idString.equals("IWAD")) {
            identification = Identification.IWAD;
        }
        else if (idString.equals("PWAD")) {
            identification = Identification.PWAD;
        }
        else {
            throw new IOException("Bad identification `" + idString + "'");
        }

        // Read number of lumps.
        lumpNames = new String[buffer.getInt()];

        // Jump to directory.
        file.seek(buffer.getInt());
    }

    private void readDirectory() throws IOException {
        byte[] lumpBytes = new byte[16];
        byte[] nameBytes = new byte[8];

        for (int i = 0; i < lumpNames.length; ++i) {
            ByteBuffer buffer = ByteBuffer.wrap(lumpBytes).order(ByteOrder.LITTLE_ENDIAN);

            file.readFully(lumpBytes);

            buffer.getInt();
            buffer.getInt();
            buffer.get(nameBytes);

            lumpNames[i] = new String(nameBytes, "US-ASCII");
        }
    }

    public Identification getIdentification() {
        return identification;
    }

    public String[] getLumpNames() {
        return lumpNames;
    }

    public static void main(String[] arguments) {
        try {
            WadFile wadFile = new WadFile(new RandomAccessFile(arguments[0], "r"));

            System.out.printf("identification = %s%n", wadFile.getIdentification());

            for (String lumpName: wadFile.getLumpNames()) {
                System.out.printf("lump = %s%n", lumpName);
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
