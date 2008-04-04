package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.model.*;

public class PatchPanel extends JPanel {
    private Patch patch;

    public PatchPanel(Patch patch) {
        this.patch = patch;

        setPreferredSize(patch.getSize());
    }

    @Override
    public void paintComponent(Graphics graphics) {
        try {
            graphics.clearRect(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
            graphics.drawImage(patch.getImage(), 0, 0, this);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
