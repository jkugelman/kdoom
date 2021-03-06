package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.model.*;

public class SpritePanel extends JPanel {
    private Sprite  sprite;
    private String  frameSequence;

    private Image   image;

    public SpritePanel() {
        this(null, null);
    }

    public SpritePanel(Sprite sprite, String frameSequence) {
        this.sprite        = sprite;
        this.frameSequence = frameSequence;

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

    @Override
    public boolean imageUpdate(Image image, int flags, int x, int y, int width, int height) {
        // If we're not showing this image any more, return false so
        // the sprite's ImageProducer can be notified.
        if (image != this.image) {
            return false;
        }

        return super.imageUpdate(image, flags, x, y, width, height);
    }
}
