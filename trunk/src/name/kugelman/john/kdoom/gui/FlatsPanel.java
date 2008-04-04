package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import info.clearthought.layout.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

import static info.clearthought.layout.TableLayoutConstraints.*;

public class FlatsPanel extends JPanel {
    public FlatsPanel() {
        setLayout(new GridLayout(0, 8, 8, 8));

        for (Flat flat: Resources.flats().values()) {
            JPanel panel = new JPanel();

            panel.add(new JLabel   (flat.getName()), BorderLayout.NORTH);
            panel.add(new FlatPanel(flat),           BorderLayout.CENTER);

            add(panel);
        }
    }


    public static void main(String[] arguments) {
        if (arguments.length < 1 || arguments.length > 2) {
            System.err.println("Usage: kdoom <doom.wad> [patch.wad]");
            System.exit(1);
        }

        try {
            Wad iwad = new Wad(new File(arguments[0]));
            Wad pwad = (arguments.length > 1) ? new Wad(new File(arguments[1])) : iwad;
            
            Resources.load(iwad, pwad);

            JFrame       frame      = new JFrame("KDOOM - " + arguments[0] + " - Flat List");
            FlatsPanel   panel      = new FlatsPanel();
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
