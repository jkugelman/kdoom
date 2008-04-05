package name.kugelman.john.kdoom.file;

import java.io.*;
import java.nio.*;
import java.util.*;

public class WadFileSet extends Wad {
    List<WadFile>           wadFiles;
    SortedMap<String, Lump> lumpsByName;

    public WadFileSet(WadFile iwad) throws IOException {
        if (iwad.isPatch()) {
            throw new IOException(iwad + " is not an IWAD.");
        }

        this.wadFiles    = new ArrayList<WadFile>();
        this.lumpsByName = new TreeMap<String, Lump>();

        addFile(iwad);
    }


    public void addPatch(WadFile pwad) throws IOException {
        if (!pwad.isPatch()) {
            throw new IOException(pwad + " is not a PWAD.");
        }

        addFile(pwad);
    }

    private void addFile(WadFile wadFile) {
        wadFiles   .add   (wadFile);
        lumpsByName.putAll(wadFile.lumpsByName());
    }


    @Override
    public SortedMap<String, Lump> lumpsByName() {
        return Collections.unmodifiableSortedMap(lumpsByName);
    }


    @Override
    public String toString() {
        // Just an IWAD? Return its name.
        if (wadFiles.size() == 1) {
            return wadFiles.get(0).toString();
        }
        // One or more PWADs.
        else {
            StringBuilder name = new StringBuilder();

            for (int i = 1; i < wadFiles.size(); ++i) {
                if (name.length() > 0) {
                    name.append("/");
                }

                name.append(wadFiles.get(i));
            }

            return name.toString();
        }
    }
}
