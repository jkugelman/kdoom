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


    public double getLength() {
        return start.distanceTo(end);
    }

    public double distanceTo(Vertex vertex) {
        double xDiff        = end.getX() - start.getX();
        double yDiff        = end.getY() - start.getY();
        double rNumerator   = (vertex.getX() - start.getX()) * (end.getX() - start.getX())
                            + (vertex.getY() - start.getY()) * (end.getY() - start.getY());
        double rDenominator = xDiff * xDiff + yDiff * yDiff;
        double r            = rNumerator / rDenominator;

        if (r >= 0 && r <= 1) {
            return Math.abs((end.getX() - start.getX()) * (start.getY() - vertex.getY())
                          - (end.getY() - start.getY()) * (start.getX() - vertex.getX())) 
                 / Math.sqrt(rDenominator);
        }
        else {
            return Math.min(vertex.distanceTo(start), vertex.distanceTo(end));
        }
    }

    public double distanceTo(short x, short y) {
        return distanceTo(new Vertex(x, y));
    }

    
    public boolean isSecret() {
        return isSecret;
    }

    public boolean isTwoSided() {
        return isTwoSided;
    }
}
