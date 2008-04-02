package name.kugelman.john.kdoom.model;

public class Vertex extends Location {
    private short number;

    Vertex(short number, short x, short y) {
        super(x, y);

        this.number = number;
    }

    public short getNumber() {
        return number;
    }
}
