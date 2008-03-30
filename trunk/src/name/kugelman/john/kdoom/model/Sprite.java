package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class Sprite {
    private static final int FRAME_DELAY = 300;
    
    String                   name;
    Palette                  palette;
    Dimension                size;
    SortedMap<String, Patch> frames;

    public Sprite(Wad wad, String name) throws IOException {
        this.name    = name;
        this.palette = new Palette(wad);
        this.frames  = new TreeMap<String, Patch>();

        this.size = new Dimension(128, 128);
        
        for (Lump lump: wad.getLumpsWithPrefix(name)) {
            if (!lump.getName().matches("....([A-Z][0-8])+")) {
                continue;
            }

            Patch patch = new Patch(lump);
       
            if (size == null) { 
                size = patch.getSize();
            }

            for (int i = 4; i < lump.getName().length(); i += 2) {
                frames.put(lump.getName().substring(i, i + 2), patch);
            }
        }

        if (frames.isEmpty()) {
            throw new IOException("No frames found for sprite " + name);
        }
    }


    public String getName() {
        return name;
    }

    public Dimension getSize() {
        return size;
    }

    public SortedMap<String, Patch> frames() {
        return Collections.unmodifiableSortedMap(frames);
    }

    public Patch getFrame(char sequence) {
        for (int i = 0; i <= 8; ++i) {
            Patch patch = getFrame(sequence, 0);
    
            if (patch != null) {
                return patch;
            }
        }

        return null;
    }

    public Patch getFrame(char sequenceLetter, int angle) {
        return getFrame("" + sequenceLetter + angle);
    }

    public Patch getFrame(String frameNumber) {
        if (!frameNumber.matches("[A-Z][0-8]")) {
            throw new IllegalArgumentException(frameNumber + " is not a valid sprite frame number.");
        }

        if (!frames.containsKey(frameNumber)) {
            throw new RuntimeException(name + ": frame " + frameNumber + " not found.");
        }
        
        return frames.get(frameNumber);
    }


    public ImageProducer getImageProducer(final String frameSequence) throws IOException {
        if (!frameSequence.matches("([A-Z][0-8])+")) {
            throw new IllegalArgumentException(frameSequence + " is not a valid sprite frame sequence.");
        }
        
        if (frameSequence.length() == 2) {
            return getFrame(frameSequence).getImage(palette).getSource();    
        }

        return new ImageProducer() {
            List<ImageConsumer> consumers   = Collections.synchronizedList(new ArrayList<ImageConsumer>());
            Thread              thread      = null;
            ColorModel          colorModel  = ColorModel.getRGBdefault();
            List<Patch>         frames      = new ArrayList<Patch>        (frameSequence.length() / 2);
            List<BufferedImage> frameImages = new ArrayList<BufferedImage>(frameSequence.length() / 2);
            BufferedImage       buffer      = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            int[]               pixels      = new int[size.width * size.height];

            {
                for (int i = 0; i < frameSequence.length(); i += 2) {
                    String frameNumber = frameSequence.substring(i, i + 2);
                    Patch  frame       = getFrame(frameNumber);

                    frames     .add(frame);
                    frameImages.add(frame.getImage(palette));
                }
            }

            public void addConsumer(ImageConsumer consumer) {
                consumers.add(consumer);
            }

            public boolean isConsumer(ImageConsumer consumer) {
                return consumers.contains(consumer);
            }

            public void startProduction(ImageConsumer consumer) {
                addConsumer(consumer);

                if (thread == null) {
                    thread = new Thread("Animating " + name + " sprite") {
                        @Override
                        public void run() {            
                            try {
                                synchronized (consumers) {
                                    for (int c = consumers.size() - 1; c >= 0; --c) {
                                        ImageConsumer consumer = consumers.get(c);
                                        
                                        consumer.setDimensions(size.width, size.height);
                                        consumer.setColorModel(colorModel);
                                    }
                                }

                                for (int i = 0; !isInterrupted(); i = (i + 1) % frameImages.size()) {
//                                    System.out.printf("[%08x] Showing %s frame %s to %d consumers%n", hashCode(), name, frameSequence.substring(i * 2, i * 2 + 2), consumers.size());

                                    // Reset buffer to all transparent.                       
                                    Arrays.fill(pixels, 0);
                                    buffer.setRGB(0, 0, size.width, size.height, pixels, 0, size.width);
                                   
                                    // Draw frame onto buffer. 
                                    Graphics graphics = buffer.getGraphics();
                                    graphics.setColor(new Color(0, 0, 0, 0));
                                    graphics.drawImage(frameImages.get(i), frames.get(i).getOffset().x, frames.get(i).getOffset().y, null);
                                    graphics.dispose();

                                    // Get pixels from buffer.
                                    buffer.getRGB(0, 0, size.width, size.height, pixels, 0, size.width);
                                    
                                    // Send pixels to consumers.
                                    synchronized (consumers) {
                                        // Iterate manually over list in reverse order to prevent
                                        // ConcurrentModificationException if consumer is removed
                                        // during iteration.
                                        for (int c = consumers.size() - 1; c >= 0; --c) {
                                            ImageConsumer consumer = consumers.get(c);
                                        
                                            consumer.setPixels(0, 0, size.width, size.height, colorModel, pixels, 0, size.width);
                                            consumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
                                        }
                                    }

                                    Thread.sleep(FRAME_DELAY); 
                                }
                            }
                            catch (InterruptedException exception) {
                                // Stop thread.
                            }
                        }
                    };

                    thread.start();
                }
            }

            public void removeConsumer(ImageConsumer consumer) {
                synchronized (consumers) {
                    consumers.remove(consumer);
                
                    if (consumers.isEmpty()) {
                        thread.interrupt();
                        thread = null;
                    }
                }
            }

            public void requestTopDownLeftRightResend(ImageConsumer consumer) {
                // Ignore.
            }
        };
    }
}
