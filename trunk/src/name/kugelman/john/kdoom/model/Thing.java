package name.kugelman.john.kdoom.model;

public class Thing {
    private short x, y;
    private short angle;

    public Thing(short x, short y, short angle) {
        this.x = x;
        this.y = y;
    }


    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public short getAngle() {
        return angle;
    }
}
