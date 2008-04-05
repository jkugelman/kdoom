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
        palette  = new Palette    (wad);
        flats    = new FlatList   (wad);
        patches  = new PatchList  (wad);
        textures = new TextureList(wad);
        sounds   = new SoundList  (wad);
        levels   = new LevelList  (wad);
    }


    public static Palette     getPalette() { return palette;  }
    public static FlatList    flats     () { return flats;    }
    public static PatchList   patches   () { return patches;  }
    public static TextureList textures  () { return textures; }
    public static SoundList   sounds    () { return sounds;   }
    public static LevelList   levels    () { return levels;   }
}
