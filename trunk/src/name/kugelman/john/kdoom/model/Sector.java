package name.kugelman.john.kdoom.model;

import java.util.*;

import static java.lang.Math.*;

public class Sector {
    private short      number;
    private short      floorHeight, ceilingHeight;
    private Flat       floorFlat,   ceilingFlat;
    private short      lightLevel;
    private short      type;
    private short      tagNumber;
            List<Side> sides;

    private Collection<List<Side>> additiveRegions;
    private Collection<List<Side>> subtractiveRegions;
    private Collection<List<Side>> unclosedRegions;


    Sector(short number,
           short floorHeight, short ceilingHeight,
           Flat  floorFlat,   Flat  ceilingFlat,
           short lightLevel, short type, short tagNumber)
    {
        this.number        = number;
        this.floorHeight   = floorHeight;
        this.ceilingHeight = ceilingHeight;
        this.floorFlat     = floorFlat;
        this.ceilingFlat   = ceilingFlat;
        this.lightLevel    = lightLevel;
        this.type          = type;
        this.tagNumber     = tagNumber;
        this.sides         = new ArrayList<Side>();
    }


    public short getNumber() {
        return number;
    }

    public short getFloorHeight() {
        return floorHeight;
    }

    public short getCeilingHeight() {
        return ceilingHeight;
    }

    public Flat getFloorFlat() {
        return floorFlat;
    }

    public Flat getCeilingFlat() {
        return ceilingFlat;
    }

    public int getLightLevel() {
        return lightLevel & 0xFFFF;
    }

    public short getType() {
        return type;
    }

    public short getTagNumber() {
        return tagNumber;
    }

    public List<Side> sides() {
        return Collections.unmodifiableList(sides);
    }


    public boolean containsSide(Side side) {
        if (side == null) {
            return false;
        }

        return sides.contains(side);
    }

    public boolean containsLine(Line line) {
        return containsSide(line.getLeftSide()) || containsSide(line.getRightSide());
    }

    public boolean isOnLeftSideOf(Line line) {
        return line.getLeftSide() != null && line.getLeftSide().getSector() == this;
    }

    public boolean isOnRightSideOf(Line line) {
        return line.getRightSide() != null && line.getRightSide().getSector() == this;
    }


    public Collection<List<Side>> getAdditiveRegions() {
        return Collections.unmodifiableCollection(additiveRegions);
    }

    public Collection<List<Side>> getSubtractiveRegions() {
        return Collections.unmodifiableCollection(subtractiveRegions);
    }

    public Collection<List<Side>> getUnclosedRegions() {
        return Collections.unmodifiableCollection(unclosedRegions);
    }

    void updateGeometry() {
        additiveRegions    = new ArrayList<List<Side>>();
        subtractiveRegions = new ArrayList<List<Side>>();
        unclosedRegions    = new ArrayList<List<Side>>();

        Set<Side> sides    = new LinkedHashSet<Side>(this.sides);

        nextRegion: while (!sides.isEmpty()) {
            // System.out.printf("Sector #%d, region #%d%n", number, additiveRegions.size() + subtractiveRegions.size() + unclosedRegions.size() + 1);

            List<Side> region    = new ArrayList<Side>();
            Side       firstSide = sides.iterator().next();
            double     angleSum  = 0;

            sides .remove(firstSide);
            region.add   (firstSide);

            // Each iteration finds the next side in the region.
            for (;;) {
                Side   lastSide  = region.get(region.size() - 1);
                Side   nextSide  = null;
                double nextAngle = Double.NEGATIVE_INFINITY;

                // Find the best connecting side, the one with the most acute angle.
                for (Side side: lastSide.getConnectingSides()) {
                    double angle = Side.angleBetween(lastSide, side);

                    if (angle > nextAngle) {
                        nextSide  = side;
                        nextAngle = angle;
                    }
                }

                // Didn't find a connecting side.
                if (nextSide == null) {
                    unclosedRegions.add(region);
                    continue nextRegion;
                }

                sides.remove(nextSide);

                // Add side to region and compute angle change.
                if (!nextSide.getLine().isPoint()) {
                    region.add(nextSide);
                    angleSum += Side.angleBetween(lastSide, nextSide);
                }

                // Polygon closed.
                if (nextSide.getEnd() == firstSide.getStart()) {
                    angleSum += Side.angleBetween(nextSide, firstSide);

                    // Angles should add up to either 360 or -360.
                    assert abs((int) round(angleSum * 180 / PI)) == 360;

                    // System.out.printf("%-11s %d angles sum to %s%n", angleSum > 0 ? "ADDITIVE" : "SUBTRACTIVE", region.size() - 1, (int) round(angleSum * 180 / PI));

                    // Determine if polygon is additive or subtractive.
                    if (angleSum > 0) additiveRegions.add(region);
                    else              subtractiveRegions.add(region);

                    continue nextRegion;
                }
            }
        }
    }


    @Override
    public String toString() {
        return "Sector #" + number;
    }
}
