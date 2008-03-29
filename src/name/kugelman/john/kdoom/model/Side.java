package name.kugelman.john.kdoom.model;

public class Side {
    private short   xOffset, yOffset;
    private Texture upperTexture, lowerTexture, middleTexture;
    private Sector  sector;

    public Side(short xOffset, short yOffset,
                Texture upperTexture, Texture lowerTexture, Texture middleTexture,
                Sector sector)
    {
        this.xOffset       = xOffset;
        this.yOffset       = yOffset;
        this.upperTexture  = upperTexture;
        this.lowerTexture  = lowerTexture;
        this.middleTexture = middleTexture;
        this.sector        = sector;

        sector.sides.add(this);
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
