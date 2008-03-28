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


    public double distanceTo(Vertex vertex) {
        int xDiff = x - vertex.x;
        int yDiff = y - vertex.y;
        
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
}
