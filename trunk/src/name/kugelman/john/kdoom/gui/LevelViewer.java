package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import info.clearthought.layout.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

import static info.clearthought.layout.TableLayoutConstants.*;

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

        this.levelPanel  = new LevelPanel (palette);
        this.sectorPanel = new SectorPanel(palette);
        this.linePanel   = new LinePanel  ();
        this.sidePanel   = new SidePanel  (palette);
        this.thingPanel  = new ThingPanel (palette);

        levelPanel.addSelectionListener(new LevelPanel.SelectionListener() {
            public void lineSelected  (Line   line)   { linePanel  .show(line);   }
            public void sideSelected  (Side   side)   { sidePanel  .show(side);   }
            public void sectorSelected(Sector sector) { sectorPanel.show(sector); }
            public void thingSelected (Thing  thing)  { thingPanel .show(thing);  }
        });

        JScrollPane levelScrollPane = new JScrollPane(levelPanel);
        levelScrollPane.setPreferredSize(new Dimension(800, 800));

        double[][] size = {
            { FILL, PREFERRED, PREFERRED },
            { PREFERRED, PREFERRED, PREFERRED, FILL }
        };

        setLayout(new TableLayout(size));
        add(levelScrollPane,  "0, 0, 0, 3, FULL, FULL");
        add(sectorPanel,      "1, 0,       FULL, TOP");
        add(linePanel,        "1, 1,       FULL, TOP");
        add(sidePanel,        "1, 2,       FULL, TOP");
        add(thingPanel,       "2, 0, 2, 3, FULL, TOP");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pack();
        setVisible(true);

        levelPanel.show(level);
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
