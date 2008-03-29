package name.kugelman.john.kdoom.model;

public class Line {
    private Vertex  start, end;
    private Side    leftSide, rightSide;
    private boolean isSecret, isTwoSided;

    private double  xDiff, yDiff;
    private double  slope;
    private int     leftSign;
    
    public Line(Vertex start, Vertex end, Side leftSide, Side rightSide, boolean isSecret, boolean isTwoSided) {
        this.start      = start;
        this.end        = end;
        this.leftSide   = leftSide;
        this.rightSide  = rightSide;

        this.isSecret   = isSecret;
        this.isTwoSided = isTwoSided;

        this.xDiff      = end.getX() - start.getX();
        this.yDiff      = end.getY() - start.getY();
        this.slope      = yDiff / xDiff;
        this.leftSign   = end.getX() < start.getX() ? -1 : +1;
    }


    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public Side getLeftSide() {
        return leftSide;
    }

    public Side getRightSide() {
        return rightSide;
    }


    public double getLength() {
        return start.distanceTo(end);
    }

    public double distanceTo(Vertex vertex) {
        // See http://www.codeguru.com/forum/printthread.php?t=194400
        double rNumerator   = (vertex.getX() - start.getX()) * xDiff
                            + (vertex.getY() - start.getY()) * yDiff;
        double rDenominator = xDiff * xDiff + yDiff * yDiff;
        double r            = rNumerator / rDenominator;

        if (r >= 0 && r <= 1) {
            return Math.abs(xDiff * (start.getY() - vertex.getY())
                          - yDiff * (start.getX() - vertex.getX())) 
                 / Math.sqrt(rDenominator);
        }
        else {
            return Math.min(vertex.distanceTo(start), vertex.distanceTo(end));
        }
    }

    public double distanceTo(short x, short y) {
        return distanceTo(new Vertex(x, y));
    }

    public Side sideFacing(Vertex vertex) {
        // See http://mathforum.org/library/drmath/view/54823.html
        int sign = (int) Math.signum((vertex.getY() - start.getY())
                           - slope * (vertex.getX() - start.getX()));

        // Vertex is on line, neither side is facing.
        if (sign == 0) {
            return null;
        }

        return sign == leftSign ? leftSide : rightSide;
    }

    public Side sideFacing(short x, short y) {
        return sideFacing(new Vertex(x, y));
    }

    
    public boolean isSecret() {
        return isSecret;
    }

    public boolean isTwoSided() {
        return isTwoSided;
    }


    @Override
    public String toString() {
        return String.format("%s-%s", start, end);
    }
}
