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

        setPreferredSize(new Dimension(
            (level.getMaxX() - level.getMinX() + 1) / 4 + 8,
            (level.getMaxY() - level.getMinY() + 1) / 4 + 8));
    }

    @Override
    public void paint(Graphics g) {
        for (Line line: level.lines()) {
            if (line.isSecret()) {
                g.setColor(Color.GREEN);
            }
            else if (line.isTwoSided()) {
                g.setColor(Color.GRAY);
            }
            else {
                g.setColor(Color.BLACK);
            }

            g.drawLine((line.getStart().getX() - level.getMinX()) / 4 + 1,
                       (line.getStart().getY() - level.getMinY()) / 4 + 1,
                       (line.getEnd  ().getX() - level.getMinX()) / 4 + 1,
                       (line.getEnd  ().getY() - level.getMinY()) / 4 + 1);
        }

        g.setColor(Color.BLUE);

        for (Vertex vertex: level.vertices()) {
            g.fillRect((vertex.getX() - level.getMinX()) / 4 + 1 - 1,
                       (vertex.getY() - level.getMinY()) / 4 + 1 - 1,
                       3, 3);
        }

        g.setColor(Color.RED);

        for (Thing thing: level.things()) {
            g.drawOval((thing.getX() - level.getMinX()) / 4 + 1 - 2,
                       (thing.getY() - level.getMinY()) / 4 + 1 - 2,
                       5, 5);
        }
    }


    public static void main(String[] arguments) {
        if (arguments.length != 2) {
            System.err.println("Usage: kdoom <file.wad> <level>");
            System.exit(1);
        }

        try {
            Wad      wad      = new Wad     (new File(arguments[0]));
            Level    level    = new Level   (wad, arguments[1]);
            MapPanel mapPanel = new MapPanel(level);

            JFrame   frame    = new JFrame("KDOOM - " + arguments[0] + " - " + level.getName());

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(mapPanel);
            frame.pack();
            frame.setVisible(true);
        }
        catch (IOException exception) {
            exception.printStackTrace();
            System.exit(-1);
        }
    }
}
