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
    private TexturePanel upperTexturePanel, middleTexturePanel, lowerTexturePanel;
    private TitledBorder titledBorder;

    public SidePanel(Palette palette) {
        this(null, palette);
    }

    public SidePanel(Side side, Palette palette) {
        titledBorder       = new TitledBorder("");
        xOffsetLabel       = new JLabel();
        yOffsetLabel       = new JLabel();
        upperTexturePanel  = new TexturePanel(palette);
        middleTexturePanel = new TexturePanel(palette);
        lowerTexturePanel  = new TexturePanel(palette);

        setBorder(titledBorder);

        setLayout(new GridBagLayout());
        add(new JLabel("X offset: "),       new Constraints(0, 0).anchorNortheast());
        add(xOffsetLabel,                   new Constraints(1, 0).anchorNorthwest());
        add(new JLabel("Y offset: "),       new Constraints(0, 1).anchorNortheast());
        add(yOffsetLabel,                   new Constraints(1, 1).anchorNorthwest());
        add(new JLabel("Upper texture: "),  new Constraints(0, 2).anchorNortheast());
        add(upperTexturePanel,              new Constraints(1, 2).anchorNorthwest());
        add(new JLabel("Middle texture: "), new Constraints(0, 3).anchorNortheast());
        add(middleTexturePanel,             new Constraints(1, 3).anchorNorthwest());
        add(new JLabel("Lower texture: "),  new Constraints(0, 4).anchorNortheast());
        add(lowerTexturePanel,              new Constraints(1, 4).anchorNorthwest());
        add(Box.createGlue(),               new Constraints(2, 5).weight(1, 1));

        show(side);
    }

    public void show(Side side) {
        this.side = side;

        if (side == null) {
            titledBorder.setTitle("Side");

            xOffsetLabel.setText("N/A");
            yOffsetLabel.setText("N/A");
                    
            upperTexturePanel .show(null);
            middleTexturePanel.show(null);
            lowerTexturePanel .show(null);
        }
        else {
            titledBorder.setTitle("Side #" + side.getNumber());

            xOffsetLabel.setText("" + side.getXOffset());
            yOffsetLabel.setText("" + side.getYOffset());
            
            upperTexturePanel .show(side.getUpperTexture ());
            middleTexturePanel.show(side.getMiddleTexture());
            lowerTexturePanel .show(side.getLowerTexture ());
        }

        repaint();
    }
}
