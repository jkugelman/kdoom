package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Patch {
    String    name;
    Lump      lump;
    Dimension size;
    Point     offset;

    public Patch(Wad wad, String name) throws IOException {
        this.name = name;
        this.lump = wad.lookupLump(name);

        if (lump != null) {
            ShortBuffer buffer = lump.getData().asShortBuffer();

            this.size   = new Dimension(buffer.get(), buffer.get());
            this.offset = new Point    (buffer.get(), buffer.get());
        }
    }


    public String getName() {
        return name;
    }

    public boolean exists() {
        return lump != null;
    }

    public Dimension getSize() {
        return size;
    }

    public Point getOffset() {
        return offset;
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


            private void produceImage() {
                for (ImageConsumer consumer: consumers) {
                    consumer.setHints(ImageConsumer.SINGLEFRAME
                                    | ImageConsumer.SINGLEPASS
                                    | ImageConsumer.RANDOMPIXELORDER);
                }
             
                try {
                    ByteBuffer buffer = lump.getData();
        
                    // Read image dimensions.
                    int   width  = buffer.getShort() & 0xFFFF;
                    int   height = buffer.getShort() & 0xFFFF;
                    short top    = buffer.getShort();
                    short left   = buffer.getShort();
        
                    ColorModel colorModel = palette.getColorModel();
        
                    for (ImageConsumer consumer: consumers) {
                        consumer.setColorModel(colorModel);
                        consumer.setDimensions(width, height);
                    }

                    // Read column offsets.
                    int[] columnOffsets = new int[width];
            
                    for (int i = 0; i < width; ++i) {
                        columnOffsets[i] = buffer.getInt();
                    }

                    // Read pixel data for each column.
                    for (int column = 0; column < columnOffsets.length; ++column) {
                        buffer.position(columnOffsets[column]);

                        for (;;) {
                            int rowStart = buffer.get() & 0xFF;

                            if (rowStart == 255) {
                                break;
                            }

                            byte[] pixels = new byte[buffer.get() & 0xFF];
                    
                            buffer.get();
                            buffer.get(pixels);
                            buffer.get();

                            for (ImageConsumer consumer: consumers) {
                                consumer.setPixels(column, rowStart, 1, pixels.length, colorModel, pixels, 0, 1);
                            }
                        }
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

            public void requestTopDownLeftRightResend(ImageConsumer consumer) {
                // Ignore.
            }
        };
    }
}
