package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.model.*;

public class SidePanel extends JPanel {
    private Line line;
    private Side side;

    private JLabel       startLabel, endLabel;
    private TexturePanel upperTexturePanel, middleTexturePanel, lowerTexturePanel;

    public SidePanel(Palette palette) {
        this(null, null, palette);
    }

    public SidePanel(Line line, Side side, Palette palette) {
        startLabel         = new JLabel();
        endLabel           = new JLabel();
        upperTexturePanel  = new TexturePanel(palette);
        middleTexturePanel = new TexturePanel(palette);
        lowerTexturePanel  = new TexturePanel(palette);

        setLayout(new GridBagLayout());
        add(new JLabel("Start vertex: "),   new Constraints(0, 0).anchorNortheast());
        add(startLabel,                     new Constraints(1, 0).anchorNorthwest());
        add(new JLabel("End vertex: "),     new Constraints(0, 1).anchorNortheast());
        add(endLabel,                       new Constraints(1, 1).anchorNorthwest());
        add(new JLabel("Upper texture: "),  new Constraints(0, 2).anchorNortheast());
        add(upperTexturePanel,              new Constraints(1, 2).anchorNorthwest());
        add(new JLabel("Middle texture: "), new Constraints(0, 3).anchorNortheast());
        add(middleTexturePanel,             new Constraints(1, 3).anchorNorthwest());
        add(new JLabel("Lower texture: "),  new Constraints(0, 4).anchorNortheast());
        add(lowerTexturePanel,              new Constraints(1, 4).anchorNorthwest());

        show(line, side);
    }

    public void show(Line line, Side side) {
        this.line = line;
        this.side = side;

        if (line == null) {
            startLabel.setText("N/A");
            endLabel  .setText("N/A");
        }
        else {
            startLabel.setText(line.getStart().toString());
            endLabel  .setText(line.getEnd  ().toString());
        }

        if (side == null) {
            upperTexturePanel .show(null);
            middleTexturePanel.show(null);
            lowerTexturePanel .show(null);
        }
        else {
            upperTexturePanel .show(side.getUpperTexture ());
            middleTexturePanel.show(side.getMiddleTexture());
            lowerTexturePanel .show(side.getLowerTexture ());
        }
    }
}
