package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class TexturePanel extends JPanel {
    private Patch     patch;
    private Image     image;
    private ImageIcon icon;

    public TexturePanel(Patch patch) {
        this.patch = patch;
        this.image = createImage(patch.getSource());
        this.icon  = new ImageIcon(image);

        setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }

    @Override
    public void paint(Graphics g) {
        icon.paintIcon(this, g, 0, 0);
    }


    public static void main(String[] arguments) {
        if (arguments.length != 2) {
            System.err.println("Usage: kdoom <file.wad> <patch>");
            System.exit(1);
        }

        try {
            Wad          wad     = new Wad    (new File(arguments[0]));
            Palette      palette = new Palette(wad.find("PLAYPAL"));
            Patch        patch   = new Patch  (wad.find(arguments[1]), palette);

            JFrame       frame   = new JFrame("KDOOM - " + arguments[0] + " - " + patch.getName());
            TexturePanel panel   = new TexturePanel(patch);

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
