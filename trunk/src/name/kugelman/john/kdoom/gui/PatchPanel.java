package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class PatchPanel extends JPanel {
    private Patch   patch;
    private Palette palette;

    public PatchPanel(Patch patch, Palette palette) {
        this.patch   = patch;
        this.palette = palette;

        setPreferredSize(patch.getSize());
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(createImage(patch.getImageProducer(palette)), 0, 0, Color.CYAN, this);
    }
}