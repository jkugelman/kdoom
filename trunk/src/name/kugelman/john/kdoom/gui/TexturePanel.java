package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class TexturePanel extends JPanel {
    private Texture texture;
    private Palette palette;

    public TexturePanel(Texture texture, Palette palette) {
        this.texture = texture;
        this.palette = palette;

        setPreferredSize(texture.getSize());
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.drawImage(createImage(texture.getImageProducer(palette)), 0, 0, Color.CYAN, this);
    }
}
