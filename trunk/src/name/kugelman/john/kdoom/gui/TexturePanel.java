package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class TexturePanel extends JPanel {
    private Texture texture;
    private Palette palette;

    public TexturePanel(Palette palette) {
        this(null, palette);
    }

    public TexturePanel(Texture texture, Palette palette) {
        this.palette = palette;

        setPreferredSize(new Dimension(128, 128));
        show(texture);
    }

    public void show(Texture texture) {
        this.texture = texture;
        
        if (texture == null) {
            setSize(0, 0);
        }
        else {
            setSize(texture.getSize().width, texture.getSize().height);
        }

        revalidate();
        repaint();
    }

    @Override
    public void paint(Graphics graphics) {
        if (texture == null) {
            return;
        }
    
        graphics.drawImage(createImage(texture.getImageProducer(palette)), 0, 0, Color.CYAN, this);
    }
}
