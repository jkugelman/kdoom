package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import name.kugelman.john.util.*;
import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class LevelPanel extends JPanel {
    public interface SelectionListener extends EventListener {
        void lineSelected  (Line line);
        void sideSelected  (Side side);
        void sectorSelected(Sector sector);
        void thingSelected (Thing thing);
    }

    public abstract class SelectionAdapter implements SelectionListener {
        public void lineSelected  (Line line)     { }
        public void sideSelected  (Side side)     { }
        public void sectorSelected(Sector sector) { }
        public void thingSelected (Thing thing)   { }
    }


    private static int LEVEL_LEFT   = Short.MIN_VALUE;
    private static int LEVEL_RIGHT  = Short.MAX_VALUE;
    private static int LEVEL_BOTTOM = Short.MIN_VALUE;
    private static int LEVEL_TOP    = Short.MAX_VALUE;
    private static int LEVEL_WIDTH  = LEVEL_RIGHT - LEVEL_LEFT   + 1;
    private static int LEVEL_HEIGHT = LEVEL_TOP   - LEVEL_BOTTOM + 1;

    private static final int[] GRID_SPACINGS = new int[] { 0, 128, 64, 32 };

    private static final Color BACKGROUND_COLOR               = Color.WHITE;

    private static final float SECTOR_HUE                     = 0.0f;   // Gray.
    private static final float SECTOR_SATURATION              = 0.0f;
    private static final float SECTOR_BRIGHTNESS_MIN          = 0.1f;   // Based on light level.
    private static final float SECTOR_BRIGHTNESS_MAX          = 0.9f;
    private static final float SELECTED_SECTOR_HUE            = 0.833f; // Slight magenta tint.
    private static final float SELECTED_SECTOR_SATURATION     = 0.08f;
    private static final float SELECTED_SECTOR_BRIGHTNESS_MIN = 0.1f;
    private static final float SELECTED_SECTOR_BRIGHTNESS_MAX = 0.9f;

    private static final Color GRID_COLOR                     = Color.GRAY;

    private static final Color LINE_COLOR                     = Color.BLACK;
    private static final Color TWO_SIDED_LINE_COLOR           = Color.DARK_GRAY;
    private static final Color SELECTED_LINE_COLOR            = Color.YELLOW;
    private static final Color SECRET_LINE_COLOR              = Color.GREEN.darker();
    private static final Color SELECTED_SECTOR_LINE_COLOR     = Color.MAGENTA;

    private static final Color VERTEX_COLOR                   = Color.BLUE;

    private static final Color THING_COLOR                    = Color.BLACK;
    private static final Color SELECTED_THING_COLOR           = Color.YELLOW;
    private static final Color PLAYER_THING_COLOR             = Color.WHITE;
    private static final Color MONSTER_THING_COLOR            = new Color(0x8b4513);
    private static final Color WEAPON_THING_COLOR             = Color.RED;
    private static final Color AMMO_THING_COLOR               = Color.RED;
    private static final Color HEALTH_THING_COLOR             = Color.GREEN;
    private static final Color ARMOR_THING_COLOR              = Color.BLUE;
    private static final Color POWER_UP_THING_COLOR           = Color.MAGENTA;
    private static final Color KEY_THING_COLOR                = Color.MAGENTA;
    private static final Color OBSTACLE_THING_COLOR           = Color.GRAY;
    private static final Color DECORATION_THING_COLOR         = Color.LIGHT_GRAY;
    private static final Color SPECIAL_THING_COLOR            = Color.MAGENTA;
    private static final Color UNKNOWN_THING_COLOR            = Color.MAGENTA;


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

    private Map<Sector, Area>  sectorAreas;
    private Map<Sector, Paint> sectorPaints;


    public LevelPanel(Palette palette) {
        this(null, palette);
    }

    public LevelPanel(Level level, Palette palette) {
        this.palette            = palette;
        this.selectionListeners = new ArrayList<SelectionListener>();

        this.gridSpacingIndex   = 0;
        this.isFloorVisible     = false;
        this.isCeilingVisible   = false;

        this.sectorAreas        = new HashMap<Sector, Area> ();
        this.sectorPaints       = new HashMap<Sector, Paint>();

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

                selectLine  (closestLine);
                selectSide  (closestSide);
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

        addSelectionListener(new SelectionListener() {
            private Sector previousSector = null;
            
            public void lineSelected (Line  line)  { repaint(); }
            public void sideSelected (Side  side)  { repaint(); }
            public void thingSelected(Thing thing) { repaint(); }

            public void sectorSelected(Sector sector) {
                sectorPaints.remove(previousSector);
                sectorPaints.remove(sector);

                previousSector = sector;

                repaint();
            }
        });

        setFocusable(true);
    }

    public void show(final Level level) {
        this.level = level;

        zoomToMax();
    }


    // Scaling and zooming

    public void zoomToMax() {
        setScale((int) Math.ceil(Math.max((level.getMaxX() - level.getMinX()) / (double) getWidth (),
                                          (level.getMaxY() - level.getMinY()) / (double) getHeight())));

        centerViewAt(new Location((short) ((level.getMinX() + level.getMaxX()) / 2),
                                  (short) ((level.getMinY() + level.getMaxY()) / 2)));
    }

    public void setScale(final int requestedScale) {
        sectorAreas .clear();
        sectorPaints.clear();
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Rectangle visibleArea = getVisibleRect();
                short     centerX     = mapX(visibleArea.x + visibleArea.width  / 2);
                short     centerY     = mapY(visibleArea.y + visibleArea.height / 2);

                scale = Math.max(1, Math.min(32, requestedScale));

                setPreferredSize(new Dimension((int) Math.ceil((double) LEVEL_WIDTH  / scale),
                                               (int) Math.ceil((double) LEVEL_HEIGHT / scale)));

                scrollTo(new Location(centerX, centerY));

                revalidate();
                repaint   ();
            }
        });
    }

    public void centerViewAt(final Location location) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                scrollTo(location);
            }
        });
    }

    private void scrollTo(Location location) {
        Rectangle visibleArea = getVisibleRect();

        scrollRectToVisible(new Rectangle(
            screenX(location.getX()) - visibleArea.width  / 2,
            screenY(location.getY()) - visibleArea.height / 2,
            visibleArea.width, visibleArea.height
        ));
    }


    // Selecting objects

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
    }

    public void selectLine(Line line) {
        if (selectedLine == line) {
            return;
        }

        selectedLine = line;

        for (SelectionListener selectionListener: selectionListeners) {
            selectionListener.lineSelected(line);
        }
    }

    public void selectSide(Side side) {
        if (selectedSide == side) {
            return;
        }

        selectedSide = side;

        for (SelectionListener selectionListener: selectionListeners) {
            selectionListener.sideSelected(side);
        }
    }

    public void selectThing(Thing thing) {
        if (selectedThing == thing) {
            return;
        }

        selectedThing = thing;

        for (SelectionListener selectionListener: selectionListeners) {
            selectionListener.thingSelected(thing);
        }
    }


    // Grid toggling

    public void changeGridSpacing(boolean increase) {
        gridSpacingIndex += increase ? 1 : GRID_SPACINGS.length - 1;
        gridSpacingIndex %= GRID_SPACINGS.length;

        repaint();
    }

    
    // Floor and ceiling toggling

    public void toggleFloor() {
        isFloorVisible   = !isFloorVisible;
        isCeilingVisible = false;

        sectorPaints.clear();
        repaint();
    }

    public void toggleCeiling() {
        isCeilingVisible = !isCeilingVisible;
        isFloorVisible   = false;

        sectorPaints.clear();
        repaint();
    }


    // Repaint algorithm

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
            Area  area  = sectorAreas .get(sector);
            Paint paint = sectorPaints.get(sector);
            
            if (area == null) {
                area = createArea(sector);
                sectorAreas.put(sector, area);
            }

            if (paint == null) {
                paint = createPaint(sector);
                sectorPaints.put(sector, paint);
            }

            graphics.setPaint(paint);
            graphics.fill    (area);
        }
    }

    private Area createArea(Sector sector) {
        Area area = new Area();
        
        for (List<Side> region: sector.getEnclosingRegions()) area.add     (createArea(region));
        for (List<Side> region: sector.getExcludingRegions()) area.subtract(createArea(region));
        
        return area;
    }

    private Area createArea(List<Side> region) {
        Polygon polygon = new Polygon();

        for (Side side: region) {
            polygon.addPoint(screenX(side.getStart().getX()), screenY(side.getStart().getY()));
        }

        return new Area(polygon);
    }

    private Paint createPaint(Sector sector) throws IOException {
        boolean isSelected    = sector == selectedSector;
        float   brightnessMin = isSelected ? SELECTED_SECTOR_BRIGHTNESS_MIN : SECTOR_BRIGHTNESS_MIN;
        float   brightnessMax = isSelected ? SELECTED_SECTOR_BRIGHTNESS_MAX : SECTOR_BRIGHTNESS_MAX;
        float   brightness    = (float) (sector.getLightLevel() / 255.0 * (brightnessMax - brightnessMin) + brightnessMin);

        if (isCeilingVisible) {
            return createFlatPaint(sector.getCeilingFlat(), brightness);
        }
        else if (isFloorVisible) {
            return createFlatPaint(sector.getFloorFlat  (), brightness);
        }
        else {
            // Color sector based on light level.
            float hue        = isSelected ? SELECTED_SECTOR_HUE            : SECTOR_HUE;
            float saturation = isSelected ? SELECTED_SECTOR_SATURATION     : SECTOR_SATURATION;

            return Color.getHSBColor(hue, saturation, brightness);
        }
    }

    private Paint createFlatPaint(Flat flat, float brightness) throws IOException {
        RescaleOp     rescaleOp     = new RescaleOp(brightness, 0.0f, null);
        BufferedImage flatImage     = flat.getImage(palette);
        BufferedImage scalableImage = new BufferedImage(Flat.WIDTH, Flat.HEIGHT, BufferedImage.TYPE_INT_RGB);

        scalableImage.getGraphics().drawImage(flatImage, 0, 0, null);

        return new TexturePaint(
            rescaleOp.filter(scalableImage, null),
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
