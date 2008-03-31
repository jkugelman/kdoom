package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class TexturePanel extends JPanel {
    private Texture   texture;
    private Palette   palette;

    private boolean   isDynamic;

    public TexturePanel(Palette palette) {
        this(null, palette, true);
    }

    public TexturePanel(Texture texture, Palette palette) {
        this(texture, palette, false);
    }

    public TexturePanel(Texture texture, Palette palette, boolean isDynamic) {
        this.palette   = palette;
        this.isDynamic = isDynamic;

        if (isDynamic) {
            setMinimumSize  (new Dimension(128, 128));
            setPreferredSize(new Dimension(128, 128));
        }
        else {
            setMinimumSize  (texture.getSize());
            setPreferredSize(texture.getSize());
        }

        show(texture);
    }

    public void show(Texture texture) {
        this.texture = texture;
        
        repaint();
    }

    @Override
    public void paint(Graphics graphics) {
        if (texture == null) {
            return;
        }
   
        try { 
            graphics.drawImage(texture.getImage(palette), 0, 0, this);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
