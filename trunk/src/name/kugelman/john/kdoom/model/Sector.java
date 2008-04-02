package name.kugelman.john.kdoom.model;

import java.util.*;

public class Sector {
    private short      number;
    private short      floorHeight, ceilingHeight;
    private Flat       floorFlat,   ceilingFlat;
    private short      lightLevel;
    private short      type;
    private short      tagNumber;
            List<Side> sides;

    private Collection<List<Side>> enclosingRegions;
    private Collection<List<Side>> excludingRegions;
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

    public short getLightLevel() {
        return lightLevel;
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


    public Collection<List<Side>> getEnclosingRegions() {
        return Collections.unmodifiableCollection(enclosingRegions);
    }

    public Collection<List<Side>> getExcludingRegions() {
        return Collections.unmodifiableCollection(excludingRegions);
    }

    public Collection<List<Side>> getUnclosedRegions() {
        return Collections.unmodifiableCollection(unclosedRegions);
    }

    void updateGeometry() {
        enclosingRegions = new ArrayList<List<Side>>();
        excludingRegions = new ArrayList<List<Side>>();
        unclosedRegions  = new ArrayList<List<Side>>();

        Set<Side> sides  = new LinkedHashSet<Side>(this.sides);

        nextRegion: while (!sides.isEmpty()) {
//            System.out.printf("Sector #%d, region #%d%n", number, enclosingRegions.size() + excludingRegions.size() + unclosedRegions.size() + 1);

            List<Side> region    = new ArrayList<Side>();
            Side       firstSide = sides.iterator().next();
            double     angleSum  = 0;

            sides .remove(firstSide);
            region.add   (firstSide);

            // Each iteration finds the next side in the region.
            for (;;) {
                Side lastSide = region.get(region.size() - 1);
                Side nextSide = null;

                // Find the side which connects to the last side.
                for (Side side: sides) {
                    if (side.getStart() == lastSide.getEnd()) {
                        nextSide = side;
                        break;
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
                    assert Math.abs((int) Math.round(angleSum * 180 / Math.PI)) == 360;

//                    System.out.printf("%-11s %d angles sum to %s%n", angleSum > 0 ? "ADDITIVE" : "SUBTRACTIVE", region.size() - 1, (int) Math.round(angleSum * 180 / Math.PI));

                    // Determine if polygon is additive or subtractive.
                    if (angleSum > 0) enclosingRegions.add(region);
                    else              excludingRegions.add(region);

                    continue nextRegion;
                }
            }
        }
    }
}
