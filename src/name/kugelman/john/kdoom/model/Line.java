package name.kugelman.john.kdoom.model;

import java.util.*;

import static java.lang.Math.*;

public class Line {
    public static final short PLAYERS_BLOCKED  = 0x0001;
    public static final short MONSTERS_BLOCKED = 0x0002;
    public static final short TWO_SIDED        = 0x0004;
    public static final short UPPER_UNPEGGED   = 0x0008;
    public static final short LOWER_UNPEGGED   = 0x0010;
    public static final short SECRET           = 0x0020;
    public static final short SOUND_BLOCKED    = 0x0040;
    public static final short INVISIBLE        = 0x0080;
    public static final short ALWAYS_VISIBLE   = 0x0100;


    private short   number;
    private Vertex  start, end;
    private short   flags, specialType, tagNumber;
    private Side    rightSide, leftSide;

    private double  xDiff, yDiff;
    private double  slope;
    private int     leftSign;

    Line(short number, Vertex start, Vertex end, short flags, short specialType, short tagNumber,
         Sidedef rightSidedef, Sidedef leftSidedef)
    {
        this.number        = number;
        this.start         = start;
        this.end           = end;
        this.flags         = flags;
        this.specialType   = specialType;
        this.tagNumber     = tagNumber;
        this.rightSide     = rightSidedef == null ? null : new Side(this, rightSidedef, true);
        this.leftSide      = leftSidedef  == null ? null : new Side(this, leftSidedef, false);

        this.xDiff         = end.getX() - start.getX();
        this.yDiff         = end.getY() - start.getY();
        this.slope         = yDiff / xDiff;
        this.leftSign      = end.getX() < start.getX() ? -1 : +1;
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

    public short getFlags() {
        return flags;
    }

    public short getSpecialType() {
        return specialType;
    }

    public short getTagNumber() {
        return tagNumber;
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

    public boolean isPoint() {
        return start.getX() == end.getX() && start.getY() == end.getY();
    }

    public double distanceTo(Location location) {
        // See http://www.codeguru.com/forum/printthread.php?t=194400
        double rNumerator   = (location.getX() - start.getX()) * xDiff
                            + (location.getY() - start.getY()) * yDiff;
        double rDenominator = xDiff * xDiff + yDiff * yDiff;
        double r            = rNumerator / rDenominator;

        if (r >= 0 && r <= 1) {
            return abs(xDiff * (start.getY() - location.getY())
                     - yDiff * (start.getX() - location.getX()))
                 / sqrt(rDenominator);
        }
        else {
            return min(location.distanceTo(start), location.distanceTo(end));
        }
    }

    public Side sideClosestTo(Location location) {
        if (leftSide  == null) return rightSide;
        if (rightSide == null) return leftSide;

        return sideFacing(location);
    }

    public Side sideFacing(Location location) {
        // See http://mathforum.org/library/drmath/view/54823.html
        int sign = (int) signum((location.getY() - start.getY())
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
