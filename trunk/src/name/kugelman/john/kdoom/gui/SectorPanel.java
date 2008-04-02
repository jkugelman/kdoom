package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.model.*;

public class SectorPanel extends JPanel {
    private Sector sector;

    private TitledBorder titledBorder;
    private JLabel       floorHeightLabel, ceilingHeightLabel;
    private JLabel       floorFlatLabel,   ceilingFlatLabel;
    private FlatPanel    floorFlatPanel,   ceilingFlatPanel;

    public SectorPanel(Palette palette) {
        this(null, palette);
    }

    public SectorPanel(Sector sector, Palette palette) {
        titledBorder       = new TitledBorder("");
        floorHeightLabel   = new JLabel();
        ceilingHeightLabel = new JLabel();
        floorFlatLabel     = new JLabel();
        floorFlatPanel     = new FlatPanel(palette);
        ceilingFlatLabel   = new JLabel();
        ceilingFlatPanel   = new FlatPanel(palette);

        setBorder(titledBorder);

        setLayout(new GridBagLayout());
        add(new JLabel("Floor height: "),   new Constraints(0, 0).anchorNortheast());
        add(floorHeightLabel,               new Constraints(1, 0).anchorNorthwest());
        add(new JLabel("Ceiling height: "), new Constraints(0, 1).anchorNortheast());
        add(ceilingHeightLabel,             new Constraints(1, 1).anchorNorthwest());
        add(new JLabel("Floor flat: "),     new Constraints(0, 2).anchorNortheast().height(2));
        add(floorFlatLabel,                 new Constraints(1, 2).anchorNorthwest());
        add(floorFlatPanel,                 new Constraints(1, 3).anchorNorthwest());
        add(new JLabel("Ceiling flat: "),   new Constraints(0, 4).anchorNortheast().height(2));
        add(ceilingFlatLabel,               new Constraints(1, 4).anchorNorthwest());
        add(ceilingFlatPanel,               new Constraints(1, 5).anchorNorthwest());
        add(Box.createGlue(),               new Constraints(2, 6).weight(1, 1));

        show(sector);
    }

    public void show(Sector sector) {
        this.sector = sector;

        if (sector == null) {
            titledBorder.setTitle("Sector");

            floorHeightLabel  .setText("N/A");
            ceilingHeightLabel.setText("N/A");
            floorFlatLabel    .setText("N/A");
            floorFlatPanel    .show   (null);
            ceilingFlatLabel  .setText("N/A");
            ceilingFlatPanel  .show   (null);
        }
        else {
            titledBorder.setTitle("Sector #" + sector.getNumber());

            floorHeightLabel  .setText("" + sector.getFloorHeight  ());
            ceilingHeightLabel.setText("" + sector.getCeilingHeight());

            floorFlatLabel    .setText(sector.getFloorFlat  () == null ? "-" : sector.getFloorFlat  ().getName());
            floorFlatPanel    .show   (sector.getFloorFlat  ());
            ceilingFlatLabel  .setText(sector.getCeilingFlat() == null ? "-" : sector.getCeilingFlat().getName());
            ceilingFlatPanel  .show   (sector.getCeilingFlat());
        }

        repaint();
    }
}
