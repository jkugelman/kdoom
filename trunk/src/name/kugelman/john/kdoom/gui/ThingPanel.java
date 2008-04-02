package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.model.*;

public class ThingPanel extends JPanel {
    private Thing   thing;
    private Palette palette;

    private TitledBorder titledBorder;
    private JLabel       locationLabel;
    private JLabel       angleLabel;
    private JLabel       typeLabel;
    private JLabel       flagsLabel;
    private SpritePanel  spritePanel;

    public ThingPanel(Palette palette) {
        this(null, palette);
    }

    public ThingPanel(Thing thing, Palette palette) {
        titledBorder  = new TitledBorder("");
        locationLabel = new JLabel();
        angleLabel    = new JLabel();
        typeLabel     = new JLabel();
        flagsLabel    = new JLabel();
        spritePanel   = new SpritePanel(palette);

        setBorder(titledBorder);

        setLayout(new GridBagLayout());
        add(new JLabel("Location: "), new Constraints(0, 0).anchorNortheast());
        add(locationLabel,            new Constraints(1, 0).anchorNorthwest());
        add(new JLabel("Angle: "),    new Constraints(0, 1).anchorNortheast());
        add(angleLabel,               new Constraints(1, 1).anchorNorthwest());
        add(new JLabel("Type: "),     new Constraints(0, 2).anchorNortheast());
        add(typeLabel,                new Constraints(1, 2).anchorNorthwest());
        add(new JLabel("Flags: "),    new Constraints(0, 3).anchorNortheast());
        add(flagsLabel,               new Constraints(1, 3).anchorNorthwest());
        add(spritePanel,              new Constraints(1, 4).anchorNorthwest().insets(8, 0, 0, 0));
        add(Box.createGlue(),         new Constraints(2, 5).weight(1, 1));

        show(thing);

        setPreferredSize(new Dimension(300, getPreferredSize().height));
    }

    public void show(Thing thing) {
        this.thing = thing;

        if (thing == null) {
            titledBorder  .setTitle("Thing");
            locationLabel .setText ("N/A");
            angleLabel    .setText ("N/A");
            typeLabel     .setText ("N/A");
            flagsLabel    .setText ("N/A");
            spritePanel   .show    (null, null);
        }
        else {
            titledBorder  .setTitle("Thing #" + thing.getNumber());
            locationLabel .setText (thing.getLocation().toString());
            angleLabel    .setText ("" + thing.getAngle() + "\u00B0");
            typeLabel     .setText ("" + thing.getTypeName());
            flagsLabel    .setText (String.format("0x%04X", thing.getFlags()));
            spritePanel   .show    (thing.getSprite(), thing.getFrameSequence());
        }

        repaint();
    }
}
