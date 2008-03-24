package name.kugelman.john.kdoom.model;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.List;

import name.kugelman.john.kdoom.file.*;

public class Texture {
    String      name;
    Dimension   size;
    List<Patch> patches;
    List<Point> origins;

    public Texture(String name, Dimension size) {
        this.name    = name;
        this.size    = size;
        this.patches = new ArrayList<Patch>();
        this.origins = new ArrayList<Point>();
    }


    public String getName() {
        return name;
    }

    public Dimension getSize() {
        return size;
    }

    public void addPatch(Patch patch, Point origin) {
        patches.add(patch);
        origins.add(origin);
    }

    public List<Patch> patches() {
        return Collections.unmodifiableList(patches);
    }

    public List<Point> origins() {
        return Collections.unmodifiableList(origins);
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
                Toolkit toolkit = Toolkit.getDefaultToolkit();

                for (ImageConsumer consumer: consumers) {
                    consumer.setHints(ImageConsumer.SINGLEFRAME
                                    | ImageConsumer.RANDOMPIXELORDER);
                }
        
                ColorModel colorModel = palette.getColorModel();
        
                for (ImageConsumer consumer: consumers) {
                    consumer.setColorModel(colorModel);
                    consumer.setDimensions(size.width, size.height);
                }

                for (int i = 0; i < patches.size(); ++i) {
                    final Patch patch  = patches.get(i);
                    final Point origin = origins.get(i);

                    if (!patch.exists()) {
                        continue;
                    }

                    ImageConsumer consumer = new ImageConsumer() {
                        public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int offset, int scanSize) {
                            assert w == 1;
                            assert h == pixels.length;

                            x += origin.x;
                            y += origin.y;

                            // Don't draw columns outside of the texture.
                            if (x < 0 || x >= size.width) {
                                return;
                            }

                            // Don't draw rows outside of the texture.
                            if (y < 0) {
                                offset -= y;
                                h      += y;
                                y       = 0;
                            }

                            h = Math.min(h, size.height - y);

                            if (h < 0) {
                                return;
                            }
                        
                            try {
                                for (ImageConsumer consumer: consumers) {
                                    consumer.setPixels(x, y, w, h, model, pixels, offset, scanSize);
                                }
                            }
                            catch (IndexOutOfBoundsException exception) {
                                System.out.printf("Texture %s %dx%d, patch %s %dx%d at (%d, %d) -- setPixels(x=%d,y=%d,w=%d,h=%d,pixels.length=%d,offset=%d,scanSize=%d)%n", getName(), getSize().width, getSize().height, patch.getName(), patch.getSize().width, patch.getSize().height, origin.x, origin.y, x, y, w, h, pixels.length, offset, scanSize);
                            }
                        }
                            
                        public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int offset, int scanSize) {
                            throw new UnsupportedOperationException();
                        }

                        public void imageComplete(int status) {
                            // Ignore.
                        }

                        public void setColorModel(ColorModel model) {
                            // Ignore.
                        }

                        public void setDimensions(int width, int height) {
                            // Ignore.
                        }

                        public void setHints(int hints) {
                            // Ignore.
                        }
                            
                        public void setProperties(Hashtable<?, ?> properties) {
                            throw new UnsupportedOperationException();
                        }
                    };

                    ImageProducer producer = patch.getImageProducer(palette, i == 0);

                    producer.startProduction(consumer);
                    producer.removeConsumer (consumer);
                }

                // Finished.
                for (ImageConsumer consumer: consumers) {
                    consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
                }
            }

            public void requestTopDownLeftRightResend(ImageConsumer consumer) {
                // Ignore.
            }
        };
    }
}
