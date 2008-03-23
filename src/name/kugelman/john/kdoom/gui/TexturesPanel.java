package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

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

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;

        for (Texture texture: textureList) {
            constraints.insets.bottom = 4;
            add(new JLabel(String.format("%s (%dx%d)", texture.getName(), texture.getSize().width, texture.getSize().height)), constraints);

            ++constraints.gridy;
            constraints.insets.bottom = 4;
            String patches = "";
            for (int i = 0; i < texture.patches().size(); ++i) {
                Patch patch  = texture.patches().get(i);
                Point origin = texture.origins().get(i);

                if (i > 0) {
                    patches += "<br>";
                }

                if (patch.exists()) {
                    patches += String.format("%s %dx%d at (%d, %d)",
                        patch.getName(),
                        patch.getSize().width, patch.getSize().height,
                        origin.x, origin.y);
                }
                else {
                    patches += String.format("%s <b>***NOT FOUND***</b> at (%d, %d)", patch.getName(),
                        origin.x, origin.y);
                }

                if (patch.getOffset().x != patch.getSize().width / 2 - 1 || patch.getOffset().y != patch.getSize().height - 5) {
                    patches += String.format(" - offset at (%d, %d)", patch.getOffset().x, patch.getOffset().y);
                }
            }
            JLabel label   = new JLabel("<html>" + patches + "</html>");
            label.setFont(label.getFont().deriveFont(Font.ITALIC, 8));
            add(label, constraints);

            ++constraints.gridy;
            constraints.insets.bottom = 20;
            add(new TexturePanel(texture, palette), constraints);
            
            constraints.gridy -= 2;
            constraints.gridx += 1;

            if (constraints.gridx >= 4) {
                constraints.gridx  = 0;
                constraints.gridy += 3;
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
