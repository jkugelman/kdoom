package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.model.*;

public class LinePanel extends JPanel {
    private Line line;

    private TitledBorder titledBorder;
    private JLabel       startLabel, endLabel;
    private JLabel       flagsLabel;
    private JLabel       leftSideLabel, rightSideLabel;

    public LinePanel() {
        this(null);
    }

    public LinePanel(Line line) {
        titledBorder   = new TitledBorder("");
        startLabel     = new JLabel();
        endLabel       = new JLabel();
        flagsLabel     = new JLabel();
        leftSideLabel  = new JLabel();
        rightSideLabel = new JLabel();

        setBorder(titledBorder);

        setLayout(new GridBagLayout());
        add(new JLabel("Start vertex: "), new Constraints(0, 0).anchorNortheast());
        add(startLabel,                   new Constraints(1, 0).anchorNorthwest());
        add(new JLabel("End vertex: "),   new Constraints(0, 1).anchorNortheast());
        add(endLabel,                     new Constraints(1, 1).anchorNorthwest());
        add(new JLabel("Flags: "),        new Constraints(0, 2).anchorNortheast());
        add(flagsLabel,                   new Constraints(1, 2).anchorNorthwest());
        add(new JLabel("Left side: "),    new Constraints(0, 3).anchorNortheast());
        add(leftSideLabel,                new Constraints(1, 3).anchorNorthwest());
        add(new JLabel("Right side: "),   new Constraints(0, 4).anchorNortheast());
        add(rightSideLabel,               new Constraints(1, 4).anchorNorthwest());
        add(Box.createGlue(),             new Constraints(2, 5).weight(1, 1));

        show(line);
    }

    public void show(Line line) {
        this.line = line;

        if (line == null) {
            titledBorder  .setTitle("Line");
            startLabel    .setText ("N/A");
            endLabel      .setText ("N/A");
            flagsLabel    .setText ("N/A");
            leftSideLabel .setText ("N/A");
            rightSideLabel.setText ("N/A");
        }
        else {
            titledBorder  .setTitle("Line #" + line.getNumber());
            startLabel    .setText (line.getStart().toString());
            endLabel      .setText (line.getEnd  ().toString());
            flagsLabel    .setText (String.format("0x%04X", line.getFlags()));
            leftSideLabel .setText (line.getLeftSide () == null ? "-" : "#" + line.getLeftSide ().getNumber());
            rightSideLabel.setText (line.getRightSide() == null ? "-" : "#" + line.getRightSide().getNumber());
        }

        repaint();
    }
}
