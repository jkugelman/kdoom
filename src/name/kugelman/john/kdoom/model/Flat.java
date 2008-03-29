package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Flat {
    public static final int WIDTH  = 64;
    public static final int HEIGHT = 64;
    public static final int SIZE   = WIDTH * HEIGHT;

    private Lump lump;

    public Flat(Lump lump) throws IOException {
        if (lump.getSize() != SIZE) {
            throw new IOException("Flat " + lump.getName() + " is incorrect size.");
        }

        this.lump = lump;
    }


    public String getName() {
        return lump.getName();
    }

    public static Dimension getSize() {
        return new Dimension(WIDTH, HEIGHT);
    }


    public ImageProducer getImageProducer(final Palette palette) {
        return new ImageProducer() {
            Set<ImageConsumer> consumers = new HashSet<ImageConsumer>();

            public void addConsumer(ImageConsumer consumer) {
                consumers.add(consumer);
            }

            public boolean isConsumer(ImageConsumer consumer) {
                return consumers.contains(consumer);
            }

            public void removeConsumer(ImageConsumer consumer) {
                consumers.remove(consumer);
            }

            public void startProduction(ImageConsumer consumer) {
                addConsumer(consumer);
                produceImage();
            }

            public void requestTopDownLeftRightResend(ImageConsumer consumer) {
                startProduction(consumer);
            }


            private void produceImage() {
                for (ImageConsumer consumer: consumers) {
                    consumer.setHints(ImageConsumer.SINGLEFRAME
                                    | ImageConsumer.SINGLEPASS
                                    | ImageConsumer.TOPDOWNLEFTRIGHT);
                }

                try {
                    // Read flat pixels.
                    ColorModel colorModel = palette.getColorModel();
                    ByteBuffer buffer     = lump.getData();
                    byte[]     pixels     = new byte[SIZE];

                    buffer.get(pixels);

                    for (ImageConsumer consumer: consumers) {
                        consumer.setColorModel(colorModel);
                        consumer.setDimensions(WIDTH, HEIGHT);

                        consumer.setPixels(0, 0, WIDTH, HEIGHT, colorModel, pixels, 0, WIDTH);
                    }

                    // Finished.
                    for (ImageConsumer consumer: consumers) {
                        consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
                    }
                }
                catch (IOException exception) {
                    exception.printStackTrace();

                    // Error.
                    for (ImageConsumer consumer: consumers) {
                        consumer.imageComplete(ImageConsumer.IMAGEERROR);
                    }
                }
            }
        };
    }
}
