package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class MapPanel extends JPanel {
    private Level level;

    public MapPanel(Level level) {
        this.level = level;
    }

    @Override
    public void paint(Graphics g) {
        for (Vertex vertex: level.vertices()) {
            g.drawRect((vertex.getX() - level.getMinX()) / 4,
                       (vertex.getY() - level.getMinY()) / 4,
                       3, 3);
        }
    }


    public static void main(String[] arguments) {
        try {
            Wad      wad      = new Wad     (new File(arguments[0]));
            Level    level    = new Level   (wad, 0);
            MapPanel mapPanel = new MapPanel(level);

            JFrame   frame    = new JFrame("KDOOM");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(mapPanel);
            frame.pack();
            frame.setVisible(true);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
