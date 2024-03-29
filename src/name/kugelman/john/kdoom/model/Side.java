package name.kugelman.john.kdoom.model;

import java.util.*;

import static java.lang.Math.*;

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
        // Usually only one connecting side.
        Collection<Side> connectingSides = new ArrayList<Side>(1);

        addConnectingSides(connectingSides);

        return connectingSides;
    }

    private void addConnectingSides(Collection<Side> connectingSides) {
        for (Side connectingSide: getEnd().getStartingSides()) {
            // Ignore zero-length lines.
            if (connectingSide.getLine().isPoint()) {
                connectingSide.addConnectingSides(connectingSides);
            }
            else {
                connectingSides.add(connectingSide);
            }
        }
    }


    public static double angleBetween(Side side1, Side side2) {
        if (side1.getLine() == side2.getLine()) {
            return -PI;
        }

        double angle1 = atan2(side1.getEnd().getY() - side1.getStart().getY(),
                              side1.getEnd().getX() - side1.getStart().getX());
        double angle2 = atan2(side2.getEnd().getY() - side2.getStart().getY(),
                              side2.getEnd().getX() - side2.getStart().getX());

        double angle  = angle1 - angle2;

        // Force angle to be between -pi and +pi.
        angle += PI * 2;
        angle %= PI * 2;

        if (angle >  PI) {
            angle -= PI * 2;
        }

        // System.out.printf("%s-%s to %s-%s, angle = %s%n", side1.getStart(), side1.getEnd(), side2.getStart(), side2.getEnd(), (int) (angle * 180 / PI));

        return angle;
    }
}
