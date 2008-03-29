package name.kugelman.john.kdoom.model;

public class Location {
    private short x, y;

    public Location(short x, short y) {
        this.x = x;
        this.y = y;
    }


    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }


    public double distanceTo(Location that) {
        int xDiff = that.x - this.x;
        int yDiff = that.y - this.y;
        
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }


    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }
}
