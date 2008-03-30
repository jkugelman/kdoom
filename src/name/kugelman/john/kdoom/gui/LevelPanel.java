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
    private Line                    selectedLine;
    private Side                    selectedSide;
    private Sector                  selectedSector;
    private Thing                   selectedThing;

    public LevelPanel() {
        this(null);
    }

    public LevelPanel(Level level) {
        this.scale              = 4;
        this.selectionListeners = new ArrayList<SelectionListener>();
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                Level              level         = LevelPanel.this.level;
                Location           mouseLocation = mouseLocation();

                Collection<Line>   closestLines  = level.getLinesClosestTo   (mouseLocation, 32);
                Collection<Sector> activeSectors = level.getSectorsContaining(mouseLocation);
                Collection<Thing>  closestThings = level.getThingsClosestTo  (mouseLocation, 32);
                
                Line               closestLine   = closestLines .isEmpty() ? null : closestLines .iterator().next();
                Side               closestSide   = closestLine == null     ? null : closestLine.sideFacing(mouseLocation);
                Sector             activeSector  = activeSectors.isEmpty() ? null : activeSectors.iterator().next();
                Thing              closestThing  = closestThings.isEmpty() ? null : closestThings.iterator().next();

                selectLine  (closestLine, closestSide);
                selectSector(activeSector);
                selectThing (closestThing);
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

    public void selectSector(Sector sector) {
        if (selectedSector == sector) {
            return;
        }

        selectedSector = sector;

        for (SelectionListener selectionListener: selectionListeners) {
            selectionListener.sectorSelected(sector);
        }

        repaint();
    }

    public void selectLine(Line line, Side side) {
        if (selectedLine == line && selectedSide == side) {
            return;
        }

        selectedLine = line;
        selectedSide = side;
    
        for (SelectionListener selectionListener: selectionListeners) {
            selectionListener.lineSelected(line, side);
        }
        
        repaint();
    }

    public void selectThing(Thing thing) {
        if (selectedThing == thing) {
            return;
        }

        selectedThing = thing;

        for (SelectionListener selectionListener: selectionListeners) {
            selectionListener.thingSelected(thing);
        }

        repaint();
    }

 


    @Override
    protected void paintComponent(Graphics graphics) {
        if (graphics instanceof Graphics2D) {
            ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, getSize().width, getSize().height);

        if (level == null) {
            return;
        }

        for (Line line: level.lines()) {
            if (line == selectedLine) {
                graphics.setColor(Color.YELLOW.darker());
            }
            else if (selectedSector != null && selectedSector.containsLine(line)) {
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
            if (thing == selectedThing) {
                graphics.setColor(Color.RED);
            }
            else {
                switch (thing.getKind()) {
                    case PLAYER:     graphics.setColor(Color.GREEN);         break;
                    case MONSTER:    graphics.setColor(new Color(0x8b4513)); break;
                    case WEAPON:     graphics.setColor(Color.RED);           break;
                    case AMMO:       graphics.setColor(Color.RED);           break;
                    case HEALTH:     graphics.setColor(Color.GREEN);         break;
                    case ARMOR:      graphics.setColor(Color.BLUE);          break;
                    case POWER_UP:   graphics.setColor(Color.MAGENTA);       break;
                    case KEY:        graphics.setColor(Color.MAGENTA);       break;
                    case OBSTACLE:   graphics.setColor(Color.GRAY);          break;
                    case DECORATION: graphics.setColor(Color.LIGHT_GRAY);    break;
                    case SPECIAL:    graphics.setColor(Color.MAGENTA);       break;
                    case UNKNOWN:    graphics.setColor(Color.MAGENTA);       break;
                }
            }

            int screenRadius = thing.getRadius() / scale;

            graphics.drawOval(screenX(thing.getLocation().getX()) - screenRadius,
                              screenY(thing.getLocation().getY()) - screenRadius,
                              screenRadius * 2, screenRadius * 2);

            if (thing.isDirectional()) {
                double startX = thing.getLocation().getX();
                double startY = thing.getLocation().getY();
                double endX   = startX + thing.getRadius() * Math.cos(thing.getAngle() * Math.PI / 180);
                double endY   = startY + thing.getRadius() * Math.sin(thing.getAngle() * Math.PI / 180);
                
                graphics.drawLine(screenX((short) startX), screenY((short) startY),
                                  screenX((short) endX),   screenY((short) endY));
            }
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
