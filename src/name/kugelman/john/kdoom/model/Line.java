package name.kugelman.john.kdoom.model;

public class Line {
    private short   number;
    private Vertex  start, end;
    private Side    leftSide, rightSide;
    private short   flags;

    private double  xDiff, yDiff;
    private double  slope;
    private int     leftSign;
    
    Line(short number, Vertex start, Vertex end, Side leftSide, Side rightSide, short flags) {
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

        if (leftSide  != null) leftSide .lines.add(this);
        if (rightSide != null) rightSide.lines.add(this);
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

    public boolean arePlayersBlocked     () { return (flags & 0x0001) == 0x0001; }
    public boolean areMonstersBlocked    () { return (flags & 0x0002) == 0x0002; }
    public boolean isTwoSided            () { return (flags & 0x0004) == 0x0004; }
    public boolean isUpperTextureUnpegged() { return (flags & 0x0008) == 0x0008; }
    public boolean isLowerTextureUnpegged() { return (flags & 0x0010) == 0x0010; }
    public boolean isSecret              () { return (flags & 0x0020) == 0x0020; }
    public boolean isSoundBlocked        () { return (flags & 0x0040) == 0x0040; }
    public boolean isInvisible           () { return (flags & 0x0080) == 0x0080; }
    public boolean isAlwaysVisible       () { return (flags & 0x0100) == 0x0100; }


    public double getLength() {
        return start.distanceTo(end);
    }

    public double distanceTo(Location location) {
        // See http://www.codeguru.com/forum/printthread.php?t=194400
        double rNumerator   = (location.getX() - start.getX()) * xDiff
                            + (location.getY() - start.getY()) * yDiff;
        double rDenominator = xDiff * xDiff + yDiff * yDiff;
        double r            = rNumerator / rDenominator;

        if (r >= 0 && r <= 1) {
            return Math.abs(xDiff * (start.getY() - location.getY())
                          - yDiff * (start.getX() - location.getX())) 
                 / Math.sqrt(rDenominator);
        }
        else {
            return Math.min(location.distanceTo(start), location.distanceTo(end));
        }
    }

    public Side sideClosestTo(Location location) {
        if (leftSide  == null) return rightSide;
        if (rightSide == null) return leftSide;

        return sideFacing(location);
    }

    public Side sideFacing(Location location) {
        // See http://mathforum.org/library/drmath/view/54823.html
        int sign = (int) Math.signum((location.getY() - start.getY())
                           - slope * (location.getX() - start.getX()));

        // Vertex is on line, neither side is facing.
        if (sign == 0) {
            return null;
        }

        return sign == leftSign ? leftSide : rightSide;
    }

    
    @Override
    public String toString() {
        return String.format("%s-%s", start, end);
    }
}
