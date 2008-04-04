package name.kugelman.john.kdoom.model;

import java.io.*;

import name.kugelman.john.kdoom.file.*;

public abstract class Resources {
    private static Palette     palette;
    private static FlatList    flats;
    private static PatchList   patches;
    private static TextureList textures;
    private static SoundList   sounds;
    private static LevelList   levels;
    

    public static void load(Wad wad) throws IOException {
        load(wad, wad);
    }

    public static void load(Wad iwad, Wad pwad) throws IOException {
        palette  = new Palette    (iwad);
        flats    = new FlatList   (iwad);
        patches  = new PatchList  (iwad);
        textures = new TextureList(iwad);
        sounds   = new SoundList  (iwad);
        levels   = new LevelList  (pwad);
    }


    public static Palette     getPalette() { return palette;  }
    public static FlatList    flats     () { return flats;    }
    public static PatchList   patches   () { return patches;  }
    public static TextureList textures  () { return textures; }
    public static SoundList   sounds    () { return sounds;   }
    public static LevelList   levels    () { return levels;   }
}
