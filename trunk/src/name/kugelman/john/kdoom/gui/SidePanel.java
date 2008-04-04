package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import info.clearthought.layout.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.model.*;

import static info.clearthought.layout.TableLayoutConstants.*;

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

        double[][] size = {
            { PREFERRED, PREFERRED },
            { PREFERRED, PREFERRED,
              PREFERRED, PREFERRED,
              PREFERRED, PREFERRED, PREFERRED,
              PREFERRED, PREFERRED, PREFERRED,
              PREFERRED, PREFERRED, PREFERRED }
        };

        setLayout(new TableLayout(size));
        add(new JLabel("X offset: "),       "0, 0,       TRAILING, TOP");
        add(xOffsetLabel,                   "1, 0,       LEADING,  TOP");
        add(new JLabel("Y offset: "),       "0, 1,       TRAILING, TOP");
        add(yOffsetLabel,                   "1, 1,       LEADING,  TOP");
        add(new JLabel("Upper texture: "),  "0, 2, 0, 3, TRAILING, TOP");
        add(upperTextureLabel,              "1, 2,       LEADING,  TOP");
        add(upperTexturePanel,              "1, 3,       LEADING,  TOP");
        add(new JLabel("Middle texture: "), "0, 4, 0, 5, TRAILING, TOP");
        add(middleTextureLabel,             "1, 4,       LEADING,  TOP");
        add(middleTexturePanel,             "1, 5,       LEADING,  TOP");
        add(new JLabel("Lower texture: "),  "0, 6, 0, 7, TRAILING, TOP");
        add(lowerTextureLabel,              "1, 6,       LEADING,  TOP");
        add(lowerTexturePanel,              "1, 7,       LEADING,  TOP");

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
