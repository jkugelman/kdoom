package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
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

    private Level   level;
    private int     scale;
    private Palette palette;

    private List<SelectionListener> selectionListeners;
    private Line                    selectedLine;
    private Side                    selectedSide;
    private Sector                  selectedSector;
    private Thing                   selectedThing;

    public LevelPanel(Palette palette) {
        this(null, palette);
    }

    public LevelPanel(Level level, Palette palette) {
        this.scale              = 4;
        this.palette            = palette;
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
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;

        // Enable anti-aliasing.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clear canvas.
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, getSize().width, getSize().height);

        if (level == null) {
            return;
        }

        // Draw sectors.
        graphics.setColor(Color.LIGHT_GRAY);
        
        nextSector: for (Sector sector: level.sectors()) {
            Set<Line>     lines               = new LinkedHashSet<Line>();
            List<Polygon> additivePolygons    = new ArrayList<Polygon>();
            List<Polygon> subtractivePolygons = new ArrayList<Polygon>();

            for (Side side: sector.sides()) {
                lines.addAll(side.lines());
            }

            nextPolygon: while (!lines.isEmpty()) {
//                System.out.printf("Sector #%d, polygon #%d%n", sector.getNumber(), additivePolygons.size() + subtractivePolygons.size() + 1);

                List<Vertex> vertices        = new ArrayList<Vertex>();
                Line         firstLine       = lines.iterator().next();
                double       angleSum        = 0;
                boolean      isSectorOnRight = sector.isOnRightSideOf(firstLine);

                vertices.add(firstLine.getStart());
                vertices.add(firstLine.getEnd  ());

                lines.remove(firstLine);

                nextLine: for (;;) {
                    Line   nextLine   = null;
                    Vertex nextVertex = null;
                    Vertex lastVertex = vertices.get(vertices.size() - 1);

                    // Find the next line which starts where this line ends.
                    for (Line line: lines) {
                        if (line.getStart() == lastVertex) {
                            nextLine   = line;
                            nextVertex = line.getEnd();
                            
                            break;
                        }
                        else if (line.getEnd() == lastVertex) {
                            nextLine   = line;
                            nextVertex = line.getStart();
                            
                            break;
                        }
                    }

                    // Didn't find a connecting line.
                    if (nextLine == null) {
                        System.err.println("Sector #" + sector.getNumber() + " is unclosed.");
                        continue nextSector;
                    }

                    lines.remove(nextLine);

                    // Add vertex to list and compute angle change.
                    if (lastVertex.getX() != nextVertex.getX() ||
                        lastVertex.getY() != nextVertex.getY())
                    {
                        angleSum += angleChange(vertices.get(vertices.size() - 2), lastVertex, nextVertex, isSectorOnRight);
                        
                        if (nextVertex != vertices.get(0)) {
                            vertices.add(nextVertex);
                        }
                    }
 
                    // Polygon closed. 
                    if (nextVertex == vertices.get(0)) {
                        angleSum += angleChange(lastVertex, nextVertex, vertices.get(1), isSectorOnRight);
                        
                        // Create Polygon object.
                        Polygon polygon = new Polygon();

                        for (Vertex vertex: vertices) {
                            polygon.addPoint(screenX(vertex.getX()), screenY(vertex.getY()));
                        }
                        
//                        System.out.printf("[%s] %-10s %d vertices sum to %s%n", isSectorOnRight ? "R" : "L", angleSum > 0 ? "ADDITIVE" : "SUBTRACTIVE", vertices.size(), angleSum);

                        // Determine if polygon is additive or subtractive.
                        if (angleSum > 0) additivePolygons   .add(polygon);
                        else              subtractivePolygons.add(polygon);

                        continue nextPolygon;
                    } 
                } 
            }

            // Generate an area composed of the polygons we found.
            Area area = new Area();

            for (Polygon polygon: additivePolygons)    area.add     (new Area(polygon));
            for (Polygon polygon: subtractivePolygons) area.subtract(new Area(polygon));
            
            try {
                graphics.setPaint(new TexturePaint(sector.getFloorFlat().getImage(palette),
                                               new Rectangle2D.Double(screenX((short) 0), screenY((short) 0), (double) Flat.WIDTH / scale, (double) Flat.HEIGHT / scale)));
                graphics.fill    (area);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        // Draw lines.
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

        // Draw vertices.
        graphics.setColor(Color.BLUE);

        for (Vertex vertex: level.vertices()) {
            graphics.fillRect(screenX(vertex.getX()) - 1, screenY(vertex.getY()) - 1, 3, 3);
        }

        // Draw things.
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

            graphics.fillOval(screenX(thing.getLocation().getX()) - screenRadius,
                              screenY(thing.getLocation().getY()) - screenRadius,
                              screenRadius * 2, screenRadius * 2);
            
            graphics.setColor(Color.BLACK);

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

    private double angleChange(Vertex vertex1, Vertex vertex2, Vertex vertex3, boolean isSectorOnRight) {
        double angle1  = Math.atan2(vertex2.getY() - vertex1.getY(),
                                    vertex2.getX() - vertex1.getX());
        double angle2  = Math.atan2(vertex3.getY() - vertex2.getY(),
                                    vertex3.getX() - vertex2.getX());

        double angle   = (angle2 - angle1 + Math.PI * 2) % (Math.PI * 2);

        if (angle >  Math.PI) {
            angle -= Math.PI * 2;
        }

        if (isSectorOnRight) {
            angle *= -1;
        }

//        System.out.printf("[%s] %s-%s to %s-%s, angle = %s%n", isSectorOnRight ? "R" : "L", vertex1, vertex2, vertex2, vertex3, (int) (angle * 180 / Math.PI));

        return angle;
    }
}
