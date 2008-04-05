package name.kugelman.john.kdoom.model;

import java.io.*;
import java.nio.*;
import java.util.*;
import javax.sound.sampled.*;

import name.kugelman.john.kdoom.file.*;

public class Sound {
    private Lump        lump;
    private AudioFormat audioFormat;

    Sound(Lump lump) throws IOException {
        ByteBuffer  buffer      = lump.getData();
                                  buffer.getShort();
        float       sampleRate  = buffer.getShort() & 0xFFFF;
        int         sampleCount = buffer.getShort() & 0xFFFF;
                                  buffer.getShort();

        this.lump        = lump;
        this.audioFormat = new AudioFormat(sampleRate, 8, 1, false, false);
    }

    public String getName() {
        return lump.getName();
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public int getSampleCount() {
        return lump.getSize() - 8;
    }

    public AudioInputStream getAudioInputStream() throws IOException {
        byte[]      samples     = new byte[getSampleCount()];
        InputStream inputStream = new ByteArrayInputStream(samples);

        lump.getData().get(samples);

        return new AudioInputStream(inputStream, audioFormat, samples.length);
    }


    public static void main(String[] arguments) {
        if (arguments.length != 2) {
            System.err.println("Usage: kdoom <file.wad> <DSNAME>");
            System.exit(1);
        }

        try {
            Resources.load(new WadFile(new File(arguments[0])));

            Sound sound = Resources.sounds().get(arguments[1].toUpperCase());
            Clip  clip  = AudioSystem.getClip();

            clip.open (sound.getAudioInputStream());
            clip.start();
            clip.drain();
            clip.close();
        }
        catch (IllegalArgumentException exception) {
            System.err.println(exception.getLocalizedMessage());
            System.exit(1);
        }
        catch (IOException exception) {
            exception.printStackTrace();
            System.exit(-1);
        }
        catch (LineUnavailableException exception) {
            exception.printStackTrace();
            System.exit(2);
        }
    }


    @Override
    public String toString() {
        return getName();
    }
}
