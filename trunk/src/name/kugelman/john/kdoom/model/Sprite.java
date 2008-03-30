package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class Sprite {
    private static final int FRAME_DELAY = 300;
    
    public class Frame {
        private String  number;
        private Patch   patch;
        private boolean isMirrored;

        Frame(String number, Patch patch, boolean isMirrored) throws IOException {
            this.number     = number;
            this.patch      = patch;
            this.isMirrored = isMirrored;
        }

        public String getNumber() {
            return number;
        }

        public Patch getPatch() {
            return patch;
        }

        public boolean isMirrored() {
            return isMirrored;
        }
    }

    String                   name;
    Palette                  palette;
    Dimension                size;
    SortedMap<String, Frame> frames;

    public Sprite(Wad wad, String name) throws IOException {
        this.name    = name;
        this.palette = new Palette(wad);
        this.frames  = new TreeMap<String, Frame>();

        this.size = new Dimension(0, 0);
        
        for (Lump lump: wad.getLumpsWithPrefix(name)) {
            if (!lump.getName().matches("....([A-Z][0-8])+")) {
                continue;
            }

            Patch patch = new Patch(lump);

            addFrame(lump.getName().substring(4, 6), patch, false);

            // Mirrored frame.
            if (lump.getName().length() > 6) {
                addFrame(lump.getName().substring(6, 8), patch, true);
            }
            
            size.width  = Math.max(size.width,  patch.getSize().width);
            size.height = Math.max(size.height, patch.getSize().height);   
        }

        if (frames.isEmpty()) {
            throw new IOException("No frames found for sprite " + name);
        }
    }

    private void addFrame(String name, Patch patch, boolean isMirrored) throws IOException {
        frames.put(name, new Frame(name, patch, isMirrored));
    }


    public String getName() {
        return name;
    }

    public Dimension getSize() {
        return size;
    }

    public SortedMap<String, Frame> frames() {
        return Collections.unmodifiableSortedMap(frames);
    }


    public Frame getFrame(String frameNumber) {
        if (!frameNumber.matches("[A-Z][0-8]")) {
            throw new IllegalArgumentException(frameNumber + " is not a valid sprite frame number.");
        }

        if (!frames.containsKey(frameNumber)) {
            throw new RuntimeException(name + " frame " + frameNumber + " not found.");
        }
        
        return frames.get(frameNumber);
    }

    public List<Frame> getFrames(String frameSequence) {
        if (!frameSequence.matches("([A-Z][0-8])+")) {
            throw new IllegalArgumentException(frameSequence + " is not a valid sprite frame sequence.");
        }
        
        List<Frame> frames = new ArrayList<Frame>(frameSequence.length() / 2);
    
        for (int i = 0; i < frameSequence.length(); i += 2) {
            frames.add(getFrame(frameSequence.substring(i, i + 2)));
        }

        return frames;
    }


    public ImageProducer getImageProducer(final String frameSequence) throws IOException {
        return new ImageProducer() {
            List<ImageConsumer> consumers   = Collections.synchronizedList(new ArrayList<ImageConsumer>());
            Thread              thread      = null;
            ColorModel          colorModel  = ColorModel.getRGBdefault();
            List<Frame>         frames      = getFrames(frameSequence);
            BufferedImage       buffer      = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            int[]               pixels      = new int[size.width * size.height];

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

                                for (int i = 0; !isInterrupted(); i = (i + 1) % frames.size()) {
                                    Frame frame = frames.get(i);
                                    
                                    // Reset buffer to all transparent.                       
                                    Arrays.fill(pixels, 0);
                                    buffer.setRGB(0, 0, size.width, size.height, pixels, 0, size.width);
                                   
                                    // Draw frame onto buffer.
                                    AffineTransform transform = new AffineTransform();

                                    if (frame.isMirrored) {
                                        transform.translate(frame.patch.getSize().width, 0);
                                        transform.scale    (-1, 1);
                                    }

                                    Graphics2D graphics = buffer.createGraphics();
                                    graphics.drawImage(frame.patch.getImage(palette), transform, null);
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
                            catch (IOException exception) {
                                exception.printStackTrace();
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
