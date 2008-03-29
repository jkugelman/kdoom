package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class TextureList extends AbstractMap<String, Texture> implements SortedMap<String, Texture> {
    SortedMap<String, Texture> textures;

    public TextureList(Wad wad) throws IOException {
        PatchList patchList = new PatchList(wad);

        this.textures = new TreeMap<String, Texture>();
        
        readTextures(wad.getLump   ("TEXTURE1"), patchList);
        readTextures(wad.lookupLump("TEXTURE2"), patchList);
    }

    private void readTextures(Lump lump, PatchList patchList) throws IOException {
        if (lump == null) {
            return;
        }
        
        ByteBuffer buffer  = lump.getData();
        int[]      offsets = new int[buffer.getInt()];

        for (int i = 0; i < offsets.length; ++i) {
            offsets[i] = buffer.getInt();
        }

        for (int offset: offsets) {
            buffer.position(offset);

            byte[] nameBytes = new byte[8];

            buffer.get(nameBytes);

            String  name       = new String(nameBytes, "ISO-8859-1").trim();
                                 buffer.getInt  ();
            short   width      = buffer.getShort();
            short   height     = buffer.getShort();
                                 buffer.getInt  ();
            short   patchCount = buffer.getShort();

            Texture texture    = new Texture(name, new Dimension(width, height));
       
            for (int i = 0; i < patchCount; ++i) {
                short x     = buffer.getShort();
                short y     = buffer.getShort();
                Patch patch = patchList.get(buffer.getShort());
                              buffer.getShort();
                              buffer.getShort();

                texture.addPatch(patch, new Point(x, y));
            }

            textures.put(name, texture);
        }
    }


    // Implementation of AbstractMap

    @Override
    public Set<Map.Entry<String, Texture>> entrySet() {
        return textures.entrySet();
    }


    // Implementation of SortedMap
    
    public Comparator<? super String> comparator() {
        return textures.comparator();
    }

    public String firstKey() {
        return textures.firstKey();
    }

    public String lastKey() {
        return textures.lastKey();
    }

    public SortedMap<String, Texture> headMap(String toKey) {
        return textures.headMap(toKey);
    }
                   
    public SortedMap<String, Texture> subMap(String fromKey, String toKey) {
        return textures.subMap(fromKey, toKey);
    }
                           
    public SortedMap<String, Texture> tailMap(String fromKey) {
        return textures.tailMap(fromKey);
    }
}
