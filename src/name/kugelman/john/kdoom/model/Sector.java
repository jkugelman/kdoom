package name.kugelman.john.kdoom.model;

import java.util.*;

import static java.lang.Math.*;

public class Sector {
    public enum SpecialType {
        NORMAL                       (0,  "Normal", "Normal"),
        SECRET                       (9,  "Secret", "Player entering this sector gets credit for finding a secret"),
        LIGHT_BLINK_FAST             (2,  "Light",  "Blink 0.5 second"),
        LIGHT_BLINK_SLOW             (3,  "Light",  "Blink 1.0 second"),
        LIGHT_BLINK_FAST_SYNCHRONIZED(12, "Light",  "Blink 0.5 second, synchronized"),
        LIGHT_BLINK_SLOW_SYNCHRONIZED(13, "Light",  "Blink 1.0 second, synchronized"),
        LIGHT_BLINK_RANDOM           (1,  "Light",  "Blink random"),
        LIGHT_FLICKER_RANDOM         (17, "Light",  "Flickers randomly"),
        LIGHT_OSCILLATE              (8,  "Light",  "Oscillates"),
        DAMAGE_5                     (7,  "Damage", "5% damage per second"),
        DAMAGE_10                    (5,  "Damage", "10% damage per second"),
        DAMAGE_20                    (16, "Damage", "20% damage per second"),
        DAMAGE_20_LEVEL_END          (11, "Damage", "20% damage per second. When player dies, level ends"),
        DAMAGE_20_BLINK_FAST         (4,  "Both",   "20% damage per second plus light blink 0.5 second"),
        DOOR_CLOSE_AFTER_30          (10, "Door",   "30 seconds after level start, ceiling closes like a door"),
        DOOR_OPEN_AFTER_300          (14, "Door",   "300 seconds after level start, ceiling opens like a door");


        private short  number;
        private String category;
        private String description;

        private SpecialType(int number, String category, String description) {
            this.number      = (short) number;
            this.category    = category;
            this.description = description;
        }

        public short getNumber() {
            return number;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }


        public static SpecialType forNumber(short number) {
            for (SpecialType type: values()) {
                if (type.number == number) {
                    return type;
                }
            }

            return null;
        }
    }


    private short       number;
    private short       floorHeight,   ceilingHeight;
    private String      floorFlatName, ceilingFlatName;
    private short       lightLevel;
    private SpecialType specialType;
    private short       tagNumber;

    Collection<Side>    sides;

    private Collection<List<Side>> additiveRegions;
    private Collection<List<Side>> subtractiveRegions;
    private Collection<List<Side>> unclosedRegions;


    Sector(short number,
           short  floorHeight,   short  ceilingHeight,
           String floorFlatName, String ceilingFlatName,
           short lightLevel, short specialType, short tagNumber)
    {
        this.number          = number;
        this.floorHeight     = floorHeight;
        this.ceilingHeight   = ceilingHeight;
        this.floorFlatName   = floorFlatName;
        this.ceilingFlatName = ceilingFlatName;
        this.lightLevel      = lightLevel;
        this.specialType     = SpecialType.forNumber(specialType);
        this.tagNumber       = tagNumber;

        this.sides           = new ArrayList<Side>();
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

    public String getFloorFlatName() {
        return floorFlatName;
    }

    public String getCeilingFlatName() {
        return ceilingFlatName;
    }

    public Flat getFloorFlat() {
        return Resources.flats().get(floorFlatName);
    }

    public Flat getCeilingFlat() {
        return Resources.flats().get(ceilingFlatName);
    }

    public int getLightLevel() {
        return lightLevel & 0xFFFF;
    }

    public SpecialType getSpecialType() {
        return specialType;
    }

    public short getTagNumber() {
        return tagNumber;
    }


    public Collection<Side> sides() {
        return Collections.unmodifiableCollection(sides);
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
