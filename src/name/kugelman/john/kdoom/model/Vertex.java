package name.kugelman.john.kdoom.model;

import java.util.*;

public class Vertex extends Location {
    private short            number;
            Collection<Side> startingSides;
            Collection<Side> endingSides;

    Vertex(short number, short x, short y) {
        super(x, y);

        this.number        = number;
        this.startingSides = new ArrayList<Side>();
        this.endingSides   = new ArrayList<Side>();
    }

    public short getNumber() {
        return number;
    }

    public Collection<Side> getStartingSides() {
        return Collections.unmodifiableCollection(startingSides);
    }

    public Collection<Side> getEndingSides() {
        return Collections.unmodifiableCollection(endingSides);
    }
}
