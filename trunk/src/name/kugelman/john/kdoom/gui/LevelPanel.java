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
        void thingSelected (Thing thing);
    }

    private Level level;
    private int   scale;
    private List<SelectionListener> selectionListeners;

    public LevelPanel() {
        this(null);
    }

    public LevelPanel(Level level) {
        this.scale              = 4;
        this.selectionListeners = new ArrayList<SelectionListener>();
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ADD && event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
                    setScale(scale - 1);
                }
                else if (event.getKeyCode() == KeyEvent.VK_SUBTRACT && event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
                    setScale(scale + 1);
                }
            }
        });

        setFocusable(true);

        show(level);
    }

    public void show(Level level) {
        this.level = level;
        updateSize();   
    }

    public void setScale(int scale) {
        this.scale = Math.max(1, Math.min(32, scale));
        updateSize();
    }
    
    private void updateSize() {    
        setPreferredSize(new Dimension((level.getMaxX() - level.getMinX() + 1) / scale + 8,
                                       (level.getMaxY() - level.getMinY() + 1) / scale + 8));

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

        Collection<Line>   closestLines  = level.getLinesClosestTo   (mouseLocation(), 32);
        Collection<Sector> activeSectors = level.getSectorsContaining(mouseLocation());
        Collection<Thing>  closestThings = level.getThingsClosestTo  (mouseLocation(), 32);
        Line               closestLine   = closestLines .isEmpty() ? null : closestLines .iterator().next();
        Side               closestSide   = closestLine == null     ? null : closestLine.sideFacing(mouseLocation());
        Sector             activeSector  = activeSectors.isEmpty() ? null : activeSectors.iterator().next();
        Thing              closestThing  = closestThings.isEmpty() ? null : closestThings.iterator().next();

        for (SelectionListener selectionListener: selectionListeners) {
            selectionListener.lineSelected  (closestLine, closestSide);
            selectionListener.sectorSelected(activeSector);
            selectionListener.thingSelected (closestThing);
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

        for (Thing thing: level.things()) {
            graphics.setColor(closestThings.contains(thing) ? Color.GREEN : Color.RED);
            graphics.drawOval(screenX(thing.getLocation().getX()) - 2, screenY(thing.getLocation().getY()) - 2, 5, 5);
        }
    }

    private int screenX(short x) {
        return (x - level.getMinX()) / scale + 1;
    }

    private int screenY(short y) {
        return (level.getMaxY() - y) / scale + 1;
    }

    private Location mouseLocation() {
        Point mousePosition = getMousePosition();

        if (mousePosition == null) {
            return null;
        }

        return new Location((short) ((mousePosition.x - 1) * scale + level.getMinX()),
                            (short) (level.getMaxY() - (mousePosition.y - 1) * scale));
    }
}
