package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class FlatPanel extends JPanel {
    private Flat    flat;
    private Palette palette;

    public FlatPanel(Palette palette) {
        this(null, palette);
    }

    public FlatPanel(Flat flat, Palette palette) {
        this.flat    = flat;
        this.palette = palette;

        setMinimumSize  (Flat.getSize());
        setPreferredSize(Flat.getSize());
    }

    public void show(Flat flat) {
        this.flat = flat;
        repaint();
    }

    @Override
    public void paint(Graphics graphics) {
        if (flat == null) {
            return;
        }

        graphics.drawImage(createImage(flat.getImageProducer(palette)), 0, 0, Color.CYAN, this);
    }
}
