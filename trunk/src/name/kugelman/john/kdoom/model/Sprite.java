package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class Sprite {
    String                   name;
    SortedMap<String, Patch> frames;

    public Sprite(Wad wad, String name) throws IOException {
        this.name   = name;
        this.frames = new TreeMap<String, Patch>();

        for (Lump lump: wad.getLumpsWithPrefix(name)) {
            if (!lump.getName().matches("....([A-Z][0-8])+")) {
                continue;
            }

            Patch patch = new Patch(lump);
        
            for (int i = 4; i <= lump.getName().length() - 2; i += 2) {
                frames.put(lump.getName().substring(i, i + 2), patch);
            }
        }
    }


    public String getName() {
        return name;
    }

    public SortedMap<String, Patch> frames() {
        return Collections.unmodifiableSortedMap(frames);
    }

    public Patch getFrame(char sequence) {
        for (int i = 0; i <= 8; ++i) {
            Patch patch = getFrame(sequence, 0);
    
            if (patch != null) {
                return patch;
            }
        }

        return null;
    }

    public Patch getFrame(char sequence, int angle) {
        return getFrame("" + sequence + angle);
    }

    public Patch getFrame(String frameNumber) {
        if (!frameNumber.matches("[A-Z][0-8]")) {
            throw new IllegalArgumentException(frameNumber + " is not a valid sprite frame number.");
        }
        
        return frames.get(frameNumber);
    }
}
