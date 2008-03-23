package name.kugelman.john.kdoom.model;

public class Vertex {
    private short x, y;

    public Vertex(short x, short y) {
        this.x = x;
        this.y = y;
    }


    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public void setX(short x) {
        this.x = x;
    }

    public void setY(short y) {
        this.y = y;
    }
}
