package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class TexturesPanel extends JPanel {
    private TextureList textureList;
    private Palette     palette;

    public TexturesPanel(TextureList textureList, Palette palette) {
        this.textureList = textureList;
        this.palette     = palette;

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        int x = 0, y = 0;

        for (Texture texture: textureList.values()) {
            String        name    = String.format("%s (%dx%d)", texture.getName(), texture.getSize().width, texture.getSize().height);
            StringBuilder patches = new StringBuilder();

            for (int i = 0; i < texture.patches().size(); ++i) {
                Patch patch  = texture.patches().get(i);
                Point origin = texture.origins().get(i);

                if (patches.length() > 0) {
                    patches.append("<br>");
                }

                if (patch.exists()) {
                    patches.append(String.format("%s %dx%d at (%d, %d)",
                        patch.getName(),
                        patch.getSize().width, patch.getSize().height,
                        origin.x, origin.y
                    ));
                }
                else {
                    patches.append(String.format("%s <b>***NOT FOUND***</b> at (%d, %d)",
                        patch.getName(), origin.x, origin.y
                    ));
                }
            }

            JLabel       nameLabel    = new JLabel(name);
            TexturePanel texturePanel = new TexturePanel(texture, palette);
            JLabel       patchesLabel = new JLabel("<html>" + patches + "</html>");
            
            patchesLabel.setFont(patchesLabel.getFont().deriveFont(Font.ITALIC, 8));
            
            add(nameLabel,    new Constraints(x, y)    .insets(4, 0, 4,  4));
            add(texturePanel, new Constraints(x, y + 1).insets(4, 0, 4,  4).anchorNorth());
            add(patchesLabel, new Constraints(x, y + 2).insets(4, 0, 20, 4).anchorNorth());

            if (++x >= 4) {
                x  = 0;
                y += 3;
            }
        }
    }


    public static void main(String[] arguments) {
        if (arguments.length < 1 || arguments.length > 2) {
            System.err.println("Usage: kdoom <doom.wad> [patch.wad]");
            System.exit(1);
        }

        try {
            Wad           paletteWad  = new Wad      (new File(arguments[0]));
            Palette       palette     = new Palette  (paletteWad.getLump("PLAYPAL"));
            Wad           textureWad  = (arguments.length > 1) ? new Wad(new File(arguments[1])) : paletteWad;
            TextureList   textureList = new TextureList(textureWad);

            JFrame        frame       = new JFrame("KDOOM - " + arguments[0] + " - Texture List");
            TexturesPanel panel       = new TexturesPanel(textureList, palette);
            JScrollPane   scrollPane  = new JScrollPane(panel);

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
