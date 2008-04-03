package name.kugelman.john.kdoom.model;

import java.util.*;

public class Side {
    private Line    line;
    private Sidedef sidedef;
    private boolean isRightSidedef;

    Side(Line line, Sidedef sidedef, boolean isRightSidedef) {
        this.line           = line;
        this.sidedef        = sidedef;
        this.isRightSidedef = isRightSidedef;

        sidedef.getSector().sides.add(this);

        getStart().startingSides.add(this);
        getEnd  ().endingSides  .add(this);
    }


    public Sidedef getSidedef() {
        return sidedef;
    }

    public Line getLine() {
        return line;
    }

    public boolean isRightSidedef() {
        return isRightSidedef;
    }


    public short getNumber() {
        return sidedef.getNumber();
    }

    public short getXOffset() {
        return sidedef.getXOffset();
    }

    public short getYOffset() {
        return sidedef.getYOffset();
    }

    public Texture getUpperTexture() {
        return sidedef.getUpperTexture();
    }

    public Texture getLowerTexture() {
        return sidedef.getLowerTexture();
    }

    public Texture getMiddleTexture() {
        return sidedef.getMiddleTexture();
    }

    public Sector getSector() {
        return sidedef.getSector();
    }


    public Vertex getStart() {
        return isRightSidedef ? line.getStart() : line.getEnd();
    }

    public Vertex getEnd() {
        return isRightSidedef ? line.getEnd() : line.getStart();
    }

    
    public Collection<Side> getConnectingSides() {
        Collection<Side> sides = new ArrayList <Side>();
        Queue     <Side> queue = new LinkedList<Side>(getEnd().getStartingSides());

        while (!queue.isEmpty()) {
            Side side = queue.remove();

            if (side.getLine().isPoint()) {
                queue.addAll(side.getEnd().getStartingSides());
            }
            else {
                sides.add(side);
            }
        }

        return sides;
    }


    public static double angleBetween(Side side1, Side side2) {
        if (side1.getLine() == side2.getLine()) {
            return -Math.PI;
        }

        double angle1 = Math.atan2(side1.getEnd().getY() - side1.getStart().getY(),
                                   side1.getEnd().getX() - side1.getStart().getX());
        double angle2 = Math.atan2(side2.getEnd().getY() - side2.getStart().getY(),
                                   side2.getEnd().getX() - side2.getStart().getX());

        double angle  = angle1 - angle2;

        // Force angle to be between -pi and +pi.
        angle += Math.PI * 2;
        angle %= Math.PI * 2;

        if (angle >  Math.PI) {
            angle -= Math.PI * 2;
        }

        // System.out.printf("%s-%s to %s-%s, angle = %s%n", side1.getStart(), side1.getEnd(), side2.getStart(), side2.getEnd(), (int) (angle * 180 / Math.PI));

        return angle;
    }
}
