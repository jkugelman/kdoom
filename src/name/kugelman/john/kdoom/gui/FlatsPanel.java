package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class FlatsPanel extends JPanel {
    private FlatList flatList;
    private Palette  palette;

    public FlatsPanel(FlatList flatList, Palette palette) {
        this.flatList = flatList;
        this.palette  = palette;

        setLayout(new GridBagLayout());

        int x = 0, y = 0;

        for (Flat flat: flatList.values()) {
            add(new JLabel   (flat.getName()), new Constraints(x, y)    .insets(4, 4, 4,  4));
            add(new FlatPanel(flat, palette),  new Constraints(x, y + 1).insets(4, 4, 20, 4));
            
            if (++x >= 8) {
                x  = 0;
                y += 2;
            }
        }
    }


    public static void main(String[] arguments) {
        if (arguments.length < 1 || arguments.length > 2) {
            System.err.println("Usage: kdoom <doom.wad> [patch.wad]");
            System.exit(1);
        }

        try {
            Wad         paletteWad = new Wad     (new File(arguments[0]));
            Palette     palette    = new Palette (paletteWad.getLump("PLAYPAL"));
            Wad         flatWad    = (arguments.length > 1) ? new Wad(new File(arguments[1])) : paletteWad;
            FlatList    flatList   = new FlatList(flatWad);

            JFrame      frame      = new JFrame("KDOOM - " + arguments[0] + " - Flat List");
            FlatsPanel  panel      = new FlatsPanel(flatList, palette);
            JScrollPane scrollPane = new JScrollPane(panel);

            scrollPane.setPreferredSize(new Dimension(800, 600));

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(scrollPane);
            frame.pack();
            frame.setVisible(true);
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
