package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class MapPanel extends JPanel {
    private static final int SCALE = 4;

    private Level level;

    public MapPanel(Level level) {
        this.level = level;

        setPreferredSize(new Dimension(
            (level.getMaxX() - level.getMinX() + 1) / SCALE + 8,
            (level.getMaxY() - level.getMinY() + 1) / SCALE + 8));
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

            g.drawLine(screenX(line.getStart().getX()), screenY(line.getStart().getY()),
                       screenX(line.getEnd  ().getX()), screenY(line.getEnd  ().getY()));
        }

        g.setColor(Color.BLUE);

        for (Vertex vertex: level.vertices()) {
            g.fillRect(screenX(vertex.getX()) - 1, screenY(vertex.getY()) - 1, 3, 3);
        }

        g.setColor(Color.RED);

        for (Thing thing: level.things()) {
            g.drawOval(screenX(thing.getX()) - 2, screenY(thing.getY()) - 2, 5, 5);
        }
    }

    private int screenX(short x) {
        return (x - level.getMinX()) / SCALE + 1;
    }

    private int screenY(short y) {
        return (level.getMaxY() - y) / SCALE + 1;
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
        catch (IllegalArgumentException exception) {
            System.err.println(exception.getLocalizedMessage());
            System.exit(1);
        }
        catch (IOException exception) {
            exception.printStackTrace();
            System.exit(-1);
        }
    }
}
