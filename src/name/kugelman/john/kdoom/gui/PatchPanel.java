package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class PatchPanel extends JPanel {
    private Patch   patch;
    private Palette palette;

    public PatchPanel(Patch patch, Palette palette) {
        this.patch   = patch;
        this.palette = palette;

        setPreferredSize(patch.getSize());
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(createImage(patch.getImageProducer(palette)), 0, 0, Color.CYAN, this);
    }


    public static void main(String[] arguments) {
        if (arguments.length != 2) {
            System.err.println("Usage: kdoom <file.wad> <patch>");
            System.exit(1);
        }

        try {
            Wad        wad     = new Wad    (new File(arguments[0]));
            Palette    palette = new Palette(wad.getLump("PLAYPAL"));
            Patch      patch   = new Patch  (wad.getLump(arguments[1]));

            JFrame     frame   = new JFrame("KDOOM - " + arguments[0] + " - " + patch.getName());
            PatchPanel panel   = new PatchPanel(patch, palette);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(panel);
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
