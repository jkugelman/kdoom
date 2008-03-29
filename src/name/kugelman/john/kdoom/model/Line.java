package name.kugelman.john.kdoom.model;

public class Line {
    private short   number;
    private Vertex  start, end;
    private Side    leftSide, rightSide;
    private short   flags;

    private double  xDiff, yDiff;
    private double  slope;
    private int     leftSign;
    
    public Line(short number, Vertex start, Vertex end, Side leftSide, Side rightSide, short flags) {
        this.number     = number;
        this.start      = start;
        this.end        = end;
        this.leftSide   = leftSide;
        this.rightSide  = rightSide;
        this.flags      = flags;

        this.xDiff      = end.getX() - start.getX();
        this.yDiff      = end.getY() - start.getY();
        this.slope      = yDiff / xDiff;
        this.leftSign   = end.getX() < start.getX() ? -1 : +1;
    }


    public short getNumber() {
        return number;
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


    public short getFlags() {
        return flags;
    }

    public boolean isTwoSided() {
        return (flags & 0x0004) == 0x0004;
    }

    public boolean isSecret() {
        return (flags & 0x0020) == 0x0020;
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
        // If the line is one-sided then just return that side.
        if (leftSide  == null) return rightSide;
        if (rightSide == null) return leftSide;

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

    
    @Override
    public String toString() {
        return String.format("%s-%s", start, end);
    }
}
