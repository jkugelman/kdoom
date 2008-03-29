package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class LevelPanel extends JPanel {
    public interface SelectionListener extends EventListener {
        void lineSelected  (Line line, Side side);
        void sectorSelected(Sector sector);
    }

    private static final int SCALE = 4;

    private Level level;
    private List<SelectionListener> selectionListeners;

    public LevelPanel() {
        this(null);
    }

    public LevelPanel(Level level) {
        this.selectionListeners = new ArrayList<SelectionListener>();
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                repaint();
            }
        });

        show(level);
    }

    public void show(Level level) {
        this.level = level;
        
        setPreferredSize(new Dimension((level.getMaxX() - level.getMinX() + 1) / SCALE + 8,
                                       (level.getMaxY() - level.getMinY() + 1) / SCALE + 8));

        revalidate();
        repaint   ();
    }

    public void addSelectionListener(SelectionListener selectionListener) {
        selectionListeners.add(selectionListener);
    }

    public void removeSelectionListener(SelectionListener selectionListener) {
        selectionListeners.remove(selectionListener);
    }


    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, getSize().width, getSize().height);

        if (level == null) {
            return;
        }

        Collection<Line>   closestLines  = getClosestLines();
        Collection<Sector> activeSectors = getActiveSectors();
        Sector             activeSector  = activeSectors.isEmpty() ? null : activeSectors.iterator().next();
        Line               closestLine   = closestLines .isEmpty() ? null : closestLines .iterator().next();
        Side               closestSide   = closestLine == null     ? null : closestLine.sideFacing(mouseX(), mouseY());

        for (SelectionListener selectionListener: selectionListeners) {
            selectionListener.lineSelected  (closestLine, closestSide);
            selectionListener.sectorSelected(activeSector);
        }

        for (Line line: level.lines()) {
            boolean isInActiveSector = false;

            for (Sector sector: activeSectors) {
                if (sector.containsLine(line)) {
                    isInActiveSector = true;
                    break;
                }
            }

            if (closestLines.contains(line)) {
                graphics.setColor(Color.YELLOW.darker());
            }
            else if (isInActiveSector) {
                graphics.setColor(Color.MAGENTA);
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

    private short mouseX() {
        return mapX(getMousePosition().x);
    }

    private short mouseY() {
        return mapY(getMousePosition().y);
    }

    private Collection<Line> getClosestLines() {
        Point mousePosition = getMousePosition();

        if (mousePosition == null) {
            return Collections.<Line>emptyList();
        }

        return level.getLinesClosestTo(mapX(mousePosition.x), mapY(mousePosition.y), 64);
    }

    private Collection<Sector> getActiveSectors() {
        Point mousePosition = getMousePosition();

        if (mousePosition == null) {
            return Collections.<Sector>emptyList();
        }

        return level.getSectorsContaining(mapX(mousePosition.x), mapY(mousePosition.y));
    }
}
