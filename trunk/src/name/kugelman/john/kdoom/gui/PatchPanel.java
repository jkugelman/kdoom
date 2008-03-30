package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

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
    public void paint(Graphics graphics) {
        try {
            graphics.drawImage(patch.getImage(palette), 0, 0, Color.CYAN, this);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
