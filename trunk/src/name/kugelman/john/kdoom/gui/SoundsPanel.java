package name.kugelman.john.kdoom.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

import name.kugelman.john.gui.*;
import name.kugelman.john.kdoom.file.*;
import name.kugelman.john.kdoom.model.*;

public class SoundsPanel extends JPanel {
    private SoundList soundList;

    public SoundsPanel(SoundList soundList) throws IOException {
        this.soundList = soundList;

        setLayout(new GridBagLayout());

        int row = 0;

        for (final Sound sound: soundList) {
            final JButton button = new JButton("\u25B6");
            final JLabel  label  = new JLabel(String.format("%s (%.1fs)", sound.getName(), sound.getAudioInputStream().getFrameLength() / sound.getAudioFormat().getFrameRate()));

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    button.setEnabled(false);

                    (new Thread("Playing " + sound.getName()) {
                        @Override
                        public void run() {
                            try {
                                Clip clip = AudioSystem.getClip();
             
                                clip.open (sound.getAudioInputStream());
                                clip.start();
                                clip.drain();
                                clip.close();
                            }
                            catch (IOException exception) {
                                exception.printStackTrace();
                            }
                            catch (LineUnavailableException exception) {
                                exception.printStackTrace();
                            }

                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    button.setEnabled(true);
                                }
                            });
                        }
                    }).start();
                }
            });

            add(button, new Constraints(0, row));
            add(label,  new Constraints(1, row).anchorWest());

            ++row;
        }
    }


    public static void main(String[] arguments) {
        if (arguments.length < 1 || arguments.length > 2) {
            System.err.println("Usage: kdoom <doom.wad> [patch.wad]");
            System.exit(1);
        }

        try {
            Wad         wad        = new Wad      (new File(arguments[0]));
            SoundList   soundList  = new SoundList(wad);

            JFrame      frame      = new JFrame("KDOOM - " + arguments[0] + " - Sound List");
            SoundsPanel panel      = new SoundsPanel(soundList);
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
