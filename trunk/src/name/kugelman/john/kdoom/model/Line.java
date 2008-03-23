package name.kugelman.john.kdoom.model;

public class Line {
    private Vertex  start, end;
    private boolean isSecret, isTwoSided;

    public Line(Vertex start, Vertex end, boolean isSecret, boolean isTwoSided) {
        this.start      = start;
        this.end        = end;

        this.isSecret   = isSecret;
        this.isTwoSided = isTwoSided;
    }


    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    
    public boolean isSecret() {
        return isSecret;
    }

    public boolean isTwoSided() {
        return isTwoSided;
    }
}
