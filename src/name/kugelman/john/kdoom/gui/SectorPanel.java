package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.model.*;

public class SectorPanel extends JPanel {
    private Sector sector;

    private JLabel    floorHeightLabel, ceilingHeightLabel;
    private FlatPanel floorFlatPanel,   ceilingFlatPanel;

    public SectorPanel(Palette palette) {
        this(null, palette);
    }

    public SectorPanel(Sector sector, Palette palette) {
        floorHeightLabel   = new JLabel();
        ceilingHeightLabel = new JLabel();
        floorFlatPanel     = new FlatPanel(palette);
        ceilingFlatPanel   = new FlatPanel(palette);

        setLayout(new GridBagLayout());
        add(new JLabel("Floor height: "),   new Constraints(0, 0).anchorNortheast());
        add(floorHeightLabel,               new Constraints(1, 0).anchorNorthwest());
        add(new JLabel("Ceiling height: "), new Constraints(0, 1).anchorNortheast());
        add(ceilingHeightLabel,             new Constraints(1, 1).anchorNorthwest());
        add(new JLabel("Floor flat: "),     new Constraints(0, 2).anchorNortheast());
        add(floorFlatPanel,                 new Constraints(1, 2).anchorNorthwest());
        add(new JLabel("Ceiling flat: "),   new Constraints(0, 3).anchorNortheast());
        add(ceilingFlatPanel,               new Constraints(1, 3).anchorNorthwest());

        show(sector);
    }

    public void show(Sector sector) {
        this.sector = sector;

        if (sector == null) {
            floorHeightLabel  .setText("N/A");
            ceilingHeightLabel.setText("N/A");
        
            floorFlatPanel  .show(null);
            ceilingFlatPanel.show(null);
        }
        else {
            floorHeightLabel  .setText("" + sector.getFloorHeight  ());
            ceilingHeightLabel.setText("" + sector.getCeilingHeight());
        
            floorFlatPanel  .show(sector.getFloorFlat  ());
            ceilingFlatPanel.show(sector.getCeilingFlat());
        }
    }
}
