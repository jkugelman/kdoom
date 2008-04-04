package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class TexturePanel extends JPanel {
    private Texture texture;
    private boolean isDynamic;

    public TexturePanel() {
        this(null, true);
    }

    public TexturePanel(Texture texture) {
        this(texture, false);
    }

    public TexturePanel(Texture texture, boolean isDynamic) {
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
    public void paintComponent(Graphics graphics) {
        if (texture == null) {
            return;
        }

        try {
            graphics.drawImage(texture.getImage(), 0, 0, this);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
