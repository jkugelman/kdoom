package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class FlatPanel extends JPanel {
    private Flat flat;

    public FlatPanel() {
        this(null);
    }

    public FlatPanel(Flat flat) {
        this.flat = flat;

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

        try {
            graphics.drawImage(flat.getImage(), 0, 0, this);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
