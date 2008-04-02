package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import name.kugelman.john.util.*;
import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class LevelPanel extends JPanel {
    public interface SelectionListener extends EventListener {
        void lineSelected  (Line line, Side side);
        void sectorSelected(Sector sector);
        void thingSelected (Thing thing);
    }

    private static int LEVEL_LEFT   = Short.MIN_VALUE;
    private static int LEVEL_RIGHT  = Short.MAX_VALUE;
    private static int LEVEL_BOTTOM = Short.MIN_VALUE;
    private static int LEVEL_TOP    = Short.MAX_VALUE;
    private static int LEVEL_WIDTH  = LEVEL_RIGHT - LEVEL_LEFT   + 1;
    private static int LEVEL_HEIGHT = LEVEL_TOP   - LEVEL_BOTTOM + 1;

    private static final int[] GRID_SPACINGS = new int[] { 0, 128, 64, 32 };

    private static final Color BACKGROUND_COLOR           = Color.WHITE;

    private static final Color SECTOR_COLOR               = Color.LIGHT_GRAY;
    private static final Color SELECTED_SECTOR_COLOR      = Color.LIGHT_GRAY;

    private static final Color GRID_COLOR                 = Color.GRAY;

    private static final Color LINE_COLOR                 = Color.BLACK;
    private static final Color TWO_SIDED_LINE_COLOR       = Color.DARK_GRAY;
    private static final Color SELECTED_LINE_COLOR        = Color.YELLOW;
    private static final Color SECRET_LINE_COLOR          = Color.GREEN.darker();
    private static final Color SELECTED_SECTOR_LINE_COLOR = Color.MAGENTA;

    private static final Color VERTEX_COLOR               = Color.BLUE;

    private static final Color THING_COLOR                = Color.BLACK;
    private static final Color SELECTED_THING_COLOR       = Color.YELLOW;
    private static final Color PLAYER_THING_COLOR         = Color.WHITE;
    private static final Color MONSTER_THING_COLOR        = new Color(0x8b4513);
    private static final Color WEAPON_THING_COLOR         = Color.RED;
    private static final Color AMMO_THING_COLOR           = Color.RED;
    private static final Color HEALTH_THING_COLOR         = Color.GREEN;
    private static final Color ARMOR_THING_COLOR          = Color.BLUE;
    private static final Color POWER_UP_THING_COLOR       = Color.MAGENTA;
    private static final Color KEY_THING_COLOR            = Color.MAGENTA;
    private static final Color OBSTACLE_THING_COLOR       = Color.GRAY;
    private static final Color DECORATION_THING_COLOR     = Color.LIGHT_GRAY;
    private static final Color SPECIAL_THING_COLOR        = Color.MAGENTA;
    private static final Color UNKNOWN_THING_COLOR        = Color.MAGENTA;


    private Level   level;
    private int     scale;
    private Palette palette;

    private List<SelectionListener> selectionListeners;
    private Line                    selectedLine;
    private Side                    selectedSide;
    private Sector                  selectedSector;
    private Thing                   selectedThing;

    private int     gridSpacingIndex;
    private boolean isFloorVisible, isCeilingVisible;

    public LevelPanel(Palette palette) {
        this(null, palette);
    }

    public LevelPanel(Level level, Palette palette) {
        this.palette            = palette;
        this.selectionListeners = new ArrayList<SelectionListener>();

        this.gridSpacingIndex   = 0;
        this.isFloorVisible     = false;
        this.isCeilingVisible   = false;

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                Level              level         = LevelPanel.this.level;
                Location           mouseLocation = mouseLocation();

                Collection<Line>   closestLines  = level.getLinesClosestTo   (mouseLocation, 32);
                Collection<Sector> activeSectors = level.getSectorsContaining(mouseLocation);
                Collection<Thing>  activeThings  = level.getThingsAt         (mouseLocation);

                Line               closestLine   = closestLines .isEmpty() ? null : closestLines .iterator().next();
                Side               closestSide   = closestLine == null     ? null : closestLine.sideFacing(mouseLocation);
                Sector             activeSector  = activeSectors.isEmpty() ? null : activeSectors.iterator().next();
                Thing              activeThing   = activeThings .isEmpty() ? null : activeThings .iterator().next();

                selectLine  (closestLine, closestSide);
                selectSector(activeSector);
                selectThing (activeThing);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_ADD:
                        if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
                            setScale(scale - 1);
                        }

                        break;

                    case KeyEvent.VK_SUBTRACT:
                        if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
                            setScale(scale + 1);
                        }

                        break;

                    case KeyEvent.VK_G:
                        changeGridSpacing(!event.isShiftDown());
                        break;

                    case KeyEvent.VK_F:
                        toggleFloor();
                        break;

                    case KeyEvent.VK_C:
                        toggleCeiling();
                        break;
                }
            }
        });

        setFocusable(true);
    }

    public void show(final Level level) {
        this.level = level;

        setScale((int) Math.ceil(Math.max((level.getMaxX() - level.getMinX()) / (double) getWidth (),
                                          (level.getMaxY() - level.getMinY()) / (double) getHeight())));

        zoomToMax();
    }

    public void setScale(int requestedScale) {
        this.scale = Math.max(1, Math.min(32, requestedScale));

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                setPreferredSize(new Dimension((int) Math.ceil((double) LEVEL_WIDTH  / scale),
                                               (int) Math.ceil((double) LEVEL_HEIGHT / scale)));

                revalidate();
                repaint   ();
            }
        });
    }

    public void zoomToMax() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                int       centerX     = screenX((level.getMinX() + level.getMaxX()) / 2);
                int       centerY     = screenY((level.getMinY() + level.getMaxY()) / 2);
                Rectangle visibleArea = getVisibleRect();

                scrollRectToVisible(new Rectangle(
                    centerX - visibleArea.width  / 2,
                    centerY - visibleArea.height / 2,
                    visibleArea.width, visibleArea.height
                ));
            }
        });
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


    public void changeGridSpacing(boolean increase) {
        gridSpacingIndex += increase ? 1 : GRID_SPACINGS.length - 1;
        gridSpacingIndex %= GRID_SPACINGS.length;

        repaint();
    }

    public void toggleFloor() {
        isFloorVisible   = !isFloorVisible;
        isCeilingVisible = false;

        repaint();
    }

    public void toggleCeiling() {
        isCeilingVisible = !isCeilingVisible;
        isFloorVisible   = false;

        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;

        clearCanvas(graphics);

        if (level == null) {
            return;
        }

        try {
            enableAntialiasing(graphics);
            drawSectors       (graphics);
            drawGrid          (graphics);
            drawLines         (graphics);
            drawVertices      (graphics);
            drawThings        (graphics);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void clearCanvas(Graphics2D graphics) {
        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, getSize().width, getSize().height);
    }

    private void enableAntialiasing(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private void drawSectors(Graphics2D graphics) throws IOException {
        for (Sector sector: level.sectors()) {
            Area area = new Area();

            for (List<Side> region: sector.getEnclosingRegions()) area.add     (createArea(region));
            for (List<Side> region: sector.getExcludingRegions()) area.subtract(createArea(region));

            if (isCeilingVisible) {
                graphics.setPaint(createFlatPaint(sector.getCeilingFlat()));
            }
            else if (isFloorVisible) {
                graphics.setPaint(createFlatPaint(sector.getFloorFlat()));
            }
            else if (sector == selectedSector) {
                graphics.setColor(SELECTED_SECTOR_COLOR);
            }
            else {
                graphics.setColor(SECTOR_COLOR);
            }

            graphics.fill(area);
        }
    }

    private Area createArea(List<Side> region) {
        Polygon polygon = new Polygon();

        for (Side side: region) {
            polygon.addPoint(screenX(side.getStart().getX()), screenY(side.getStart().getY()));
        }

        return new Area(polygon);
    }

    private Paint createFlatPaint(Flat flat) throws IOException {
        return new TexturePaint(
            flat.getImage(palette),
            new Rectangle2D.Double(screenX((short) 0), screenY((short) 0),
                                   (double) Flat.WIDTH / scale, (double) Flat.HEIGHT / scale)
        );
    }

    private void drawGrid(Graphics2D graphics) {
        int spacing = GRID_SPACINGS[gridSpacingIndex];

        if (spacing == 0) {
            return;
        }

        Rectangle clip   = graphics.getClipBounds();
        short     left   = mapX(clip.x);
        short     right  = mapX(clip.x + clip.width);
        short     bottom = mapY(clip.y + clip.height);
        short     top    = mapY(clip.y);

        left   += spacing - left   % spacing;
        bottom += spacing - bottom % spacing;
        right  -= right % spacing;
        top    -= top   % spacing;

        graphics.setColor(GRID_COLOR);

        for (int x = left; x <= right; x += spacing) {
            graphics.drawLine(screenX(x), 0, screenX(x), getHeight());
        }

        for (int y = bottom; y <= top; y += spacing) {
            graphics.drawLine(0, screenY(y), getWidth(), screenY(y));
        }
    }

    private void drawLines(Graphics2D graphics) {
        for (Line line: level.lines()) {
            graphics.setStroke(new BasicStroke());

            if (line == selectedLine) {
                graphics.setColor (SELECTED_LINE_COLOR);
                graphics.setStroke(new BasicStroke(1.5f));
            }
            else if (selectedSector != null && selectedSector.containsLine(line)) {
                graphics.setColor (SELECTED_SECTOR_LINE_COLOR);
                graphics.setStroke(new BasicStroke(1.5f));
            }
            else if (line.isSecret()) {
                graphics.setColor(SECRET_LINE_COLOR);
            }
            else if (line.isTwoSided()) {
                graphics.setColor(TWO_SIDED_LINE_COLOR);
            }
            else {
                graphics.setColor(LINE_COLOR);
            }

            graphics.drawLine(screenX(line.getStart().getX()), screenY(line.getStart().getY()),
                              screenX(line.getEnd  ().getX()), screenY(line.getEnd  ().getY()));
        }

        graphics.setStroke(new BasicStroke());
    }

    private void drawVertices(Graphics2D graphics) {
        graphics.setColor(VERTEX_COLOR);

        for (Vertex vertex: level.vertices()) {
            graphics.fillRect(screenX(vertex.getX()) - 1, screenY(vertex.getY()) - 1, 3, 3);
        }
    }

    private void drawThings(Graphics2D graphics) {
        for (Thing thing: level.things()) {
            switch (thing.getKind()) {
                case PLAYER:     graphics.setColor(PLAYER_THING_COLOR);     break;
                case MONSTER:    graphics.setColor(MONSTER_THING_COLOR);    break;
                case WEAPON:     graphics.setColor(WEAPON_THING_COLOR);     break;
                case AMMO:       graphics.setColor(AMMO_THING_COLOR);       break;
                case HEALTH:     graphics.setColor(HEALTH_THING_COLOR);     break;
                case ARMOR:      graphics.setColor(ARMOR_THING_COLOR);      break;
                case POWER_UP:   graphics.setColor(POWER_UP_THING_COLOR);   break;
                case KEY:        graphics.setColor(KEY_THING_COLOR);        break;
                case OBSTACLE:   graphics.setColor(OBSTACLE_THING_COLOR);   break;
                case DECORATION: graphics.setColor(DECORATION_THING_COLOR); break;
                case SPECIAL:    graphics.setColor(SPECIAL_THING_COLOR);    break;
                case UNKNOWN:    graphics.setColor(UNKNOWN_THING_COLOR) ;   break;
            }

            int   screenRadius = thing.getRadius() / scale;
            Shape circle       = new Ellipse2D.Double(screenX(thing.getLocation().getX()) - screenRadius,
                                                      screenY(thing.getLocation().getY()) - screenRadius,
                                                      screenRadius * 2, screenRadius * 2);

            graphics.fill(circle);

            if (thing == selectedThing) {
                graphics.setColor (SELECTED_THING_COLOR);
                graphics.setStroke(new BasicStroke(1.5f));
            }
            else {
                graphics.setColor(THING_COLOR);
            }

            graphics.draw(circle);

            if (thing.isDirectional()) {
                double startX = thing.getLocation().getX();
                double startY = thing.getLocation().getY();
                double endX   = startX + thing.getRadius() * Math.cos(thing.getAngle() * Math.PI / 180);
                double endY   = startY + thing.getRadius() * Math.sin(thing.getAngle() * Math.PI / 180);

                graphics.drawLine(screenX((short) startX), screenY((short) startY),
                                  screenX((short) endX),   screenY((short) endY));
            }

            if (thing == selectedThing) {
                graphics.setStroke(new BasicStroke());
            }
        }
    }

    private int   screenX(int x) { return (x - LEVEL_LEFT) / scale + 1; }
    private int   screenY(int y) { return (LEVEL_TOP - y)  / scale + 1; }
    private short mapX   (int x) { return (short) ((x - 1) * scale + LEVEL_LEFT); }
    private short mapY   (int y) { return (short) (LEVEL_TOP - (y - 1) * scale);  }


    private Location mouseLocation() {
        Point mousePosition = getMousePosition();

        if (mousePosition == null) {
            return null;
        }

        return new Location((short) ((mousePosition.x - 1) * scale + LEVEL_LEFT),
                            (short) (LEVEL_TOP - (mousePosition.y - 1) * scale));
    }
}
