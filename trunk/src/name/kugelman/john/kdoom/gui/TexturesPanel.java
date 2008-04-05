package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class TexturesPanel extends JPanel {
    public TexturesPanel() {
        setLayout(new GridLayout(0, 4, 8, 8));

        for (Texture texture: Resources.textures().values()) {
            String        name    = String.format("%s (%dx%d)", texture.getName(), texture.getSize().width, texture.getSize().height);
            StringBuilder patches = new StringBuilder();

            for (int i = 0; i < texture.patchNumbers().size(); ++i) {
                short  patchNumber = texture.patchNumbers().get(i);
                Point  patchOrigin = texture.patchOrigins().get(i);
                String patchName   = Resources.patches().getName(patchNumber);
                Patch  patch       = Resources.patches().get    (patchNumber);

                if (patches.length() > 0) {
                    patches.append("<br>");
                }

                if (patch != null) {
                    patches.append(String.format("%s %dx%d at (%d, %d)",
                        patchName,
                        patch.getSize().width, patch.getSize().height,
                        patchOrigin.x, patchOrigin.y
                    ));
                }
                else {
                    patches.append(String.format("%s <b>***NOT FOUND***</b> at (%d, %d)",
                        patchName, patchOrigin.x, patchOrigin.y
                    ));
                }
            }

            JPanel       panel        = new JPanel();
            JLabel       nameLabel    = new JLabel(name);
            TexturePanel texturePanel = new TexturePanel(texture);
            JLabel       patchesLabel = new JLabel("<html>" + patches + "</html>");

            patchesLabel.setFont(patchesLabel.getFont().deriveFont(Font.ITALIC, 8));

            panel.add(nameLabel,    BorderLayout.NORTH);
            panel.add(texturePanel, BorderLayout.CENTER);
            panel.add(patchesLabel, BorderLayout.SOUTH);
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
            
            JFrame        frame       = new JFrame("KDOOM - " + arguments[0] + " - Texture List");
            TexturesPanel panel       = new TexturesPanel();
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
