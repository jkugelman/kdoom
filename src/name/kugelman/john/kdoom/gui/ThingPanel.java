package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import info.clearthought.layout.*;

import name.kugelman.john.kdoom.model.*;

import static info.clearthought.layout.TableLayoutConstraints.*;

public class ThingPanel extends JPanel {
    private Thing thing;

    private TitledBorder titledBorder;
    private JLabel       locationLabel;
    private JLabel       angleLabel;
    private JLabel       typeLabel;
    private JLabel       flagsLabel;
    private SpritePanel  spritePanel;

    public ThingPanel() {
        this(null);
    }

    public ThingPanel(Thing thing) {
        titledBorder  = new TitledBorder("");
        locationLabel = new JLabel();
        angleLabel    = new JLabel();
        typeLabel     = new JLabel();
        flagsLabel    = new JLabel();
        spritePanel   = new SpritePanel();

        setBorder(titledBorder);

        double[][] size = {
            { PREFERRED, 200 },
            { PREFERRED, PREFERRED, PREFERRED, PREFERRED, 8, PREFERRED }
        };

        setLayout(new TableLayout(size));
        add(new JLabel("Location: "), "0, 0, TRAILING, TOP");
        add(locationLabel,            "1, 0, LEADING,  TOP");
        add(new JLabel("Angle: "),    "0, 1, TRAILING, TOP");
        add(angleLabel,               "1, 1, LEADING,  TOP");
        add(new JLabel("Type: "),     "0, 2, TRAILING, TOP");
        add(typeLabel,                "1, 2, LEADING,  TOP");
        add(new JLabel("Flags: "),    "0, 3, TRAILING, TOP");
        add(flagsLabel,               "1, 3, LEADING,  TOP");
        add(spritePanel,              "1, 5, TRAILING, TOP");

        show(thing);
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
