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

    public Sector(short number,
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
}
