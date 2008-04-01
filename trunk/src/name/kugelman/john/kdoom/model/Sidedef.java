package name.kugelman.john.kdoom.model;

import java.util.*;

public class Sidedef {
    private short     number;
    private short     xOffset, yOffset;
    private Texture   upperTexture, lowerTexture, middleTexture;
    private Sector    sector;

    Sidedef(short number, short xOffset, short yOffset,
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
}
