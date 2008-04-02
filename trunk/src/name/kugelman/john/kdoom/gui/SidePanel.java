package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.model.*;

public class SidePanel extends JPanel {
    private Side side;

    private JLabel       xOffsetLabel, yOffsetLabel;
    private JLabel       upperTextureLabel, middleTextureLabel, lowerTextureLabel;
    private TexturePanel upperTexturePanel, middleTexturePanel, lowerTexturePanel;
    private TitledBorder titledBorder;

    public SidePanel(Palette palette) {
        this(null, palette);
    }

    public SidePanel(Side side, Palette palette) {
        titledBorder       = new TitledBorder("");
        xOffsetLabel       = new JLabel();
        yOffsetLabel       = new JLabel();
        upperTextureLabel  = new JLabel();
        upperTexturePanel  = new TexturePanel(palette);
        middleTextureLabel = new JLabel();
        middleTexturePanel = new TexturePanel(palette);
        lowerTextureLabel  = new JLabel();
        lowerTexturePanel  = new TexturePanel(palette);

        setBorder(titledBorder);

        setLayout(new GridBagLayout());
        add(new JLabel("X offset: "),       new Constraints(0, 0).anchorNortheast());
        add(xOffsetLabel,                   new Constraints(1, 0).anchorNorthwest());
        add(new JLabel("Y offset: "),       new Constraints(0, 1).anchorNortheast());
        add(yOffsetLabel,                   new Constraints(1, 1).anchorNorthwest());
        add(new JLabel("Upper texture: "),  new Constraints(0, 2).anchorNortheast().height(2));
        add(upperTextureLabel,              new Constraints(1, 2).anchorNorthwest());
        add(upperTexturePanel,              new Constraints(1, 3).anchorNorthwest());
        add(new JLabel("Middle texture: "), new Constraints(0, 4).anchorNortheast().height(2));
        add(middleTextureLabel,             new Constraints(1, 4).anchorNorthwest());
        add(middleTexturePanel,             new Constraints(1, 5).anchorNorthwest());
        add(new JLabel("Lower texture: "),  new Constraints(0, 6).anchorNortheast().height(2));
        add(lowerTextureLabel,              new Constraints(1, 6).anchorNorthwest());
        add(lowerTexturePanel,              new Constraints(1, 7).anchorNorthwest());
        add(Box.createGlue(),               new Constraints(2, 8).weight(1, 1));

        show(side);
    }

    public void show(Side side) {
        this.side = side;

        if (side == null) {
            titledBorder      .setTitle("Side");

            xOffsetLabel      .setText ("N/A");
            yOffsetLabel      .setText ("N/A");

            upperTextureLabel .setText ("N/A");
            upperTexturePanel .show    (null);
            middleTextureLabel.setText ("N/A");
            middleTexturePanel.show    (null);
            lowerTextureLabel .setText ("N/A");
            lowerTexturePanel .show    (null);
        }
        else {
            titledBorder      .setTitle("Side #" + side.getNumber());

            xOffsetLabel      .setText ("" + side.getXOffset());
            yOffsetLabel      .setText ("" + side.getYOffset());

            upperTextureLabel .setText (side.getUpperTexture () == null ? "-" : side.getUpperTexture ().getName());
            upperTexturePanel .show    (side.getUpperTexture ());
            middleTextureLabel.setText (side.getMiddleTexture() == null ? "-" : side.getMiddleTexture().getName());
            middleTexturePanel.show    (side.getMiddleTexture());
            lowerTextureLabel .setText (side.getLowerTexture () == null ? "-" : side.getLowerTexture ().getName());
            lowerTexturePanel .show    (side.getLowerTexture ());
        }

        repaint();
    }
}
