package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class LevelViewer extends JFrame {
    private Level level;

    private LevelPanel  levelPanel;
    private SectorPanel sectorPanel;
    private LinePanel   linePanel;
    private SidePanel   sidePanel;
    private ThingPanel  thingPanel;

    public LevelViewer(Level level, Palette palette) {
        super("KDOOM - " + level.getWad().getName() + " - " + level.getName());

        this.level = level;

        this.levelPanel  = new LevelPanel (level, palette);
        this.sectorPanel = new SectorPanel(palette);
        this.linePanel   = new LinePanel  ();
        this.sidePanel   = new SidePanel  (palette);
        this.thingPanel  = new ThingPanel (palette);

        levelPanel.addSelectionListener(new LevelPanel.SelectionListener() {
            public void lineSelected(Line line, Side side) {
                linePanel.show(line);
                sidePanel.show(side);
            }

            public void sectorSelected(Sector sector) {
                sectorPanel.show(sector);
            }

            public void thingSelected(Thing thing) {
                thingPanel.show(thing);
            }
        });

        JScrollPane levelScrollPane = new JScrollPane(levelPanel);
        levelScrollPane.setPreferredSize(new Dimension(800, 800));

        setLayout(new GridBagLayout());
        add(levelScrollPane,  new Constraints(0, 0).height(4).weight(1, 1).fillBoth());
        add(sectorPanel,      new Constraints(1, 0).fillHorizontal());
        add(linePanel,        new Constraints(1, 1).fillHorizontal());
        add(sidePanel,        new Constraints(1, 2).fillHorizontal());
        add(thingPanel,       new Constraints(2, 0).height(3).anchorNorthwest());

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pack();
        setVisible(true);
    }


    public static void main(String[] arguments) {
        if (arguments.length != 2) {
            System.err.println("Usage: kdoom <file.wad> <level>");
            System.exit(1);
        }

        try {
            Wad         wad         = new Wad        (new File(arguments[0]));
            FlatList    flats       = new FlatList   (wad);
            TextureList textures    = new TextureList(wad);
            Palette     palette     = new Palette    (wad);
            Level       level       = new Level      (wad, arguments[1], flats, textures);

            LevelViewer levelViewer = new LevelViewer(level, palette);
        }
        catch (IllegalArgumentException exception) {
            System.err.println(exception.getLocalizedMessage());
            System.exit(1);
        }
        catch (IOException exception) {
            exception.printStackTrace();
            System.exit(-1);
        }
    }
}
