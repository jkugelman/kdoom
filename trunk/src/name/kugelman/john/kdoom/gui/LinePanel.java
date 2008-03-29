package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.model.*;

public class LinePanel extends JPanel {
    private Line line;

    private JLabel       startLabel, endLabel;
    private TexturePanel upperTexturePanel, middleTexturePanel, lowerTexturePanel;
    private TitledBorder titledBorder;

    public LinePanel() {
        this(null);
    }

    public LinePanel(Line line) {
        titledBorder = new TitledBorder("");
        startLabel   = new JLabel();
        endLabel     = new JLabel();

        setBorder(titledBorder);

        setLayout(new GridBagLayout());
        add(new JLabel("Start vertex: "), new Constraints(0, 0).anchorNortheast());
        add(startLabel,                   new Constraints(1, 0).anchorNorthwest());
        add(new JLabel("End vertex: "),   new Constraints(0, 1).anchorNortheast());
        add(endLabel,                     new Constraints(1, 1).anchorNorthwest());
        add(Box.createGlue(),             new Constraints(2, 2).weight(1, 1));

        show(line);
    }

    public void show(Line line) {
        this.line = line;

        if (line == null) {
            titledBorder.setTitle("Line");
            startLabel  .setText ("N/A");
            endLabel    .setText ("N/A");
        }
        else {
            titledBorder.setTitle("Line #" + line.getNumber());
            startLabel  .setText (line.getStart().toString());
            endLabel    .setText (line.getEnd  ().toString());
        }

        repaint();
    }
}
