package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import info.clearthought.layout.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

import static info.clearthought.layout.TableLayoutConstraints.*;

public class PatchesPanel extends JPanel {
    private PatchList patchList;
    private Palette   palette;

    public PatchesPanel(PatchList patchList, Palette palette) {
        this.patchList = patchList;
        this.palette   = palette;

        setLayout(new GridLayout(0, 4, 8, 8));
        
        for (Patch patch: patchList) {
            JPanel panel = new JPanel();

            Component patchComponent = patch.exists()
                ? new PatchPanel(patch, palette)
                : new JLabel("NOT FOUND");
            
            panel.add(new JLabel(patch.getName()), BorderLayout.NORTH);
            panel.add(patchComponent,              BorderLayout.CENTER);
        
            add(panel);
        }
    }


    public static void main(String[] arguments) {
        if (arguments.length < 1 || arguments.length > 2) {
            System.err.println("Usage: kdoom <doom.wad> [patch.wad]");
            System.exit(1);
        }

        try {
            Wad          paletteWad = new Wad      (new File(arguments[0]));
            Palette      palette    = new Palette  (paletteWad.getLump("PLAYPAL"));
            Wad          patchWad   = (arguments.length > 1) ? new Wad(new File(arguments[1])) : paletteWad;
            PatchList    patchList  = new PatchList(patchWad);

            JFrame       frame      = new JFrame("KDOOM - " + arguments[0] + " - Patch List");
            PatchesPanel panel      = new PatchesPanel(patchList, palette);
            JScrollPane  scrollPane = new JScrollPane(panel);

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
