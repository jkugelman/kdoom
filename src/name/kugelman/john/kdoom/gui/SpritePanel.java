package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.model.*;

public class SpritePanel extends JPanel {
    private Sprite  sprite;
    private Palette palette;

    public SpritePanel(Palette palette) {
        this(null, palette);
    }

    public SpritePanel(Sprite sprite, Palette palette) {
        this.sprite  = sprite;
        this.palette = palette;

        setPreferredSize(new Dimension(128, 128));
    }

    public void show(Sprite sprite) {
        this.sprite = sprite;
        repaint();
    }


    @Override
    public void paint(Graphics graphics) {
        // graphics.drawImage(createImage(patch.getImageProducer(palette)), 0, 0, Color.CYAN, this);
    }
}
