package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class MapPanel extends JPanel {
    private static final int SCALE = 4;

    private Level level;

    public MapPanel(Level level) {
        this.level = level;

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                repaint();
            }
        });

        setPreferredSize(new Dimension(
            (level.getMaxX() - level.getMinX() + 1) / SCALE + 8,
            (level.getMaxY() - level.getMinY() + 1) / SCALE + 8));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, getSize().width, getSize().height);

        Line closestLine = getClosestLine();
        
        for (Line line: level.lines()) {
            if (line == closestLine) {
                graphics.setColor(Color.YELLOW.darker());
            }
            else if (line.isSecret()) {
                graphics.setColor(Color.GREEN);
            }
            else if (line.isTwoSided()) {
                graphics.setColor(Color.GRAY);
            }
            else {
                graphics.setColor(Color.BLACK);
            }

            graphics.drawLine(screenX(line.getStart().getX()), screenY(line.getStart().getY()),
                              screenX(line.getEnd  ().getX()), screenY(line.getEnd  ().getY()));
        }

        graphics.setColor(Color.BLUE);

        for (Vertex vertex: level.vertices()) {
            graphics.fillRect(screenX(vertex.getX()) - 1, screenY(vertex.getY()) - 1, 3, 3);
        }

        graphics.setColor(Color.RED);

        for (Thing thing: level.things()) {
            graphics.drawOval(screenX(thing.getX()) - 2, screenY(thing.getY()) - 2, 5, 5);
        }
    }

    private int screenX(short x) {
        return (x - level.getMinX()) / SCALE + 1;
    }

    private int screenY(short y) {
        return (level.getMaxY() - y) / SCALE + 1;
    }

    private short mapX(int x) {
        return (short) ((x - 1) * SCALE + level.getMinX());
    }

    private short mapY(int y) {
        return (short) (level.getMaxY() - (y - 1) * SCALE);
    }

    private Line getClosestLine() {
        Point mousePosition = getMousePosition();

        if (mousePosition == null) {
            return null;
        }

        return level.getLineClosestTo(mapX(mousePosition.x), mapY(mousePosition.y), 64);
    }


    public static void main(String[] arguments) {
        if (arguments.length != 2) {
            System.err.println("Usage: kdoom <file.wad> <level>");
            System.exit(1);
        }

        try {
            Wad         wad        = new Wad     (new File(arguments[0]));
            Level       level      = new Level   (wad, arguments[1]);

            JFrame      frame      = new JFrame("KDOOM - " + arguments[0] + " - " + level.getName());
            MapPanel    mapPanel   = new MapPanel(level);
            JScrollPane scrollPane = new JScrollPane(mapPanel);

            scrollPane.setPreferredSize(new Dimension(800, 600));

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(scrollPane);
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
