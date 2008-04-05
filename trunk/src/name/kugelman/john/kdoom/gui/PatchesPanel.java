package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import info.clearthought.layout.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

import static info.clearthought.layout.TableLayoutConstraints.*;

public class PatchesPanel extends JPanel {
    public PatchesPanel() {
        setLayout(new GridLayout(0, 4, 8, 8));
        
        for (Map.Entry<String, Patch> entry: Resources.patches().entrySet()) {
            JPanel panel = new JPanel();

            Component patchComponent = entry.getValue() != null
                ? new PatchPanel(entry.getValue())
                : new JLabel("NOT FOUND");
            
            panel.add(new JLabel(entry.getKey()), BorderLayout.NORTH);
            panel.add(patchComponent,             BorderLayout.CENTER);
        
            add(panel);
        }
    }


    public static void main(String[] arguments) {
        if (arguments.length < 1 || arguments.length > 2) {
            System.err.println("Usage: kdoom <doom.wad> [patch.wad]");
            System.exit(1);
        }

        try {
            WadFileSet wad = new WadFileSet(new WadFile(new File(arguments[0])));

            if (arguments.length > 1) {
                wad.addPatch(new WadFile(new File(arguments[1])));
            }

            Resources.load(wad);

            JFrame       frame      = new JFrame("KDOOM - " + wad + " - Patch List");
            PatchesPanel panel      = new PatchesPanel();
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
