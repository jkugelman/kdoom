package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import info.clearthought.layout.*;

import name.kugelman.john.kdoom.model.*;

import static info.clearthought.layout.TableLayoutConstants.*;

public class SectorPanel extends JPanel {
    private Sector sector;

    private TitledBorder titledBorder;
    private JLabel       floorHeightLabel, ceilingHeightLabel;
    private JLabel       lightLevelLabel;
    private JLabel       specialTypeLabel;
    private JLabel       tagNumberLabel;
    private JLabel       floorFlatLabel,   ceilingFlatLabel;
    private FlatPanel    floorFlatPanel,   ceilingFlatPanel;

    public SectorPanel() {
        this(null);
    }

    public SectorPanel(Sector sector) {
        titledBorder       = new TitledBorder("");
        floorHeightLabel   = new JLabel();
        ceilingHeightLabel = new JLabel();
        lightLevelLabel    = new JLabel();
        specialTypeLabel   = new JLabel();
        tagNumberLabel     = new JLabel();
        floorFlatLabel     = new JLabel();
        floorFlatPanel     = new FlatPanel();
        ceilingFlatLabel   = new JLabel();
        ceilingFlatPanel   = new FlatPanel();

        setBorder(titledBorder);

        double[][] size = {
            { PREFERRED, PREFERRED },
            { PREFERRED, PREFERRED, PREFERRED, PREFERRED, PREFERRED, PREFERRED, PREFERRED, PREFERRED, PREFERRED }
        };

        setLayout(new TableLayout(size));
        add(new JLabel("Floor height: "),   "0, 0,       TRAILING, TOP");
        add(floorHeightLabel,               "1, 0,       LEADING,  TOP");
        add(new JLabel("Ceiling height: "), "0, 1,       TRAILING, TOP");
        add(ceilingHeightLabel,             "1, 1,       LEADING,  TOP");
        add(new JLabel("Light level: "),    "0, 2,       TRAILING, TOP");
        add(lightLevelLabel,                "1, 2,       LEADING,  TOP");
        add(new JLabel("Special type: "),   "0, 3,       TRAILING, TOP");
        add(specialTypeLabel,               "1, 3,       LEADING,  TOP");
        add(new JLabel("Tag number: "),     "0, 4,       TRAILING, TOP");
        add(tagNumberLabel,                 "1, 4,       LEADING,  TOP");
        add(new JLabel("Floor flat: "),     "0, 5, 0, 6, TRAILING, TOP");
        add(floorFlatLabel,                 "1, 5,       LEADING,  TOP");
        add(floorFlatPanel,                 "1, 6,       LEADING,  TOP");
        add(new JLabel("Ceiling flat: "),   "0, 7, 0, 8, TRAILING, TOP");
        add(ceilingFlatLabel,               "1, 7,       LEADING,  TOP");
        add(ceilingFlatPanel,               "1, 8,       LEADING,  TOP");

        show(sector);
    }

    public void show(Sector sector) {
        this.sector = sector;

        if (sector == null) {
            titledBorder      .setTitle("Sector");

            floorHeightLabel  .setText("N/A");
            ceilingHeightLabel.setText("N/A");
            lightLevelLabel   .setText("N/A");
            specialTypeLabel  .setText("N/A");
            tagNumberLabel    .setText("N/A");

            floorFlatLabel    .setText("N/A");
            floorFlatPanel    .show   (null);
            ceilingFlatLabel  .setText("N/A");
            ceilingFlatPanel  .show   (null);
        }
        else {
            titledBorder      .setTitle("Sector #" + sector.getNumber());

            floorHeightLabel  .setText("" + sector.getFloorHeight  ());
            ceilingHeightLabel.setText("" + sector.getCeilingHeight());
            lightLevelLabel   .setText("" + sector.getLightLevel   ());
            specialTypeLabel  .setText(sector.getSpecialType().getDescription());
            tagNumberLabel    .setText("" + sector.getTagNumber    ());

            floorFlatLabel    .setText(sector.getFloorFlatName  ());
            floorFlatPanel    .show   (sector.getFloorFlat      ());
            ceilingFlatLabel  .setText(sector.getCeilingFlatName());
            ceilingFlatPanel  .show   (sector.getCeilingFlat    ());
        }

        repaint();
    }
}
