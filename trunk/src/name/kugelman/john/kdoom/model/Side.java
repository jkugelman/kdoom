package name.kugelman.john.kdoom.model;

import java.util.*;

public class Side {
    private short     number;
    private short     xOffset, yOffset;
    private Texture   upperTexture, lowerTexture, middleTexture;
    private Sector    sector;
            Set<Line> lines;

    Side(short number, short xOffset, short yOffset,
         Texture upperTexture, Texture lowerTexture, Texture middleTexture,
         Sector sector)
    {
        this.number        = number;
        this.xOffset       = xOffset;
        this.yOffset       = yOffset;
        this.upperTexture  = upperTexture;
        this.lowerTexture  = lowerTexture;
        this.middleTexture = middleTexture;
        this.sector        = sector;
        this.lines         = new LinkedHashSet<Line>();

        sector.sides.add(this);
    }


    public short getNumber() {
        return number;
    }

    public short getXOffset() {
        return xOffset;
    }

    public short getYOffset() {
        return yOffset;
    }

    public Texture getUpperTexture() {
        return upperTexture;
    }

    public Texture getLowerTexture() {
        return lowerTexture;
    }

    public Texture getMiddleTexture() {
        return middleTexture;
    }

    public Sector getSector() {
        return sector;
    }

    public Set<Line> lines() {
        return Collections.unmodifiableSet(lines);
    }
}
