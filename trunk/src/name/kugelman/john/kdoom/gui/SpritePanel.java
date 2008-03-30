package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.model.*;

public class SpritePanel extends JPanel {
    private Sprite  sprite;
    private String  frameSequence;
    private Palette palette;

    private Image   image;
    
    public SpritePanel(Palette palette) {
        this(null, null, palette);
    }

    public SpritePanel(Sprite sprite, String frameSequence, Palette palette) {
        this.sprite        = sprite;
        this.frameSequence = frameSequence;
        this.palette       = palette;

        setPreferredSize(new Dimension(128, 128));
    }

    public void show(Sprite sprite, String frameSequence) {
        this.sprite        = sprite;
        this.frameSequence = frameSequence;
    
        try {
            this.image     = sprite == null ? null : createImage(sprite.getImageProducer(frameSequence));
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        repaint();
    }


    @Override
    public void paint(Graphics graphics) {
        if (image == null) {
            return;
        }
        
        graphics.clearRect(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        graphics.drawImage(image, 0, 0, new Color(0, 0, 0, 0), this);
    }
}
