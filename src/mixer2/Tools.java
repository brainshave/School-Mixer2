/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mixer2;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author szymon
 */
public class Tools {

    public static void genPixelArrays(BufferedImage im1, int[] pix1Buffer,
            BufferedImage im2, int[] pix2Buffer, int width, int height) {
        try {
            PixelGrabber pix1 = new PixelGrabber(im1, 0, 0, width, height, pix1Buffer, 0, width);
            PixelGrabber pix2 = new PixelGrabber(im2, 0, 0, width, height, pix2Buffer, 0, width);
            pix1.grabPixels();
            pix2.grabPixels();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public abstract class Tool extends Thread {
        private int[] buff1, buff2, buffOut;
        private int size, howMuchEffect, width, height;
        private Component objToRepaint;
        private WritableRaster raster;

        public Tool() {
            start();
        }

        public void mixInBg(int[] pix1Buffer, int[] pix2Buffer, int[] outBuffer,
                int size, int howMuchEffect, Component objToRepaint,
                WritableRaster raster, int width, int height) {
            this.buff1 = pix1Buffer;
            this.buff2 = pix2Buffer;
            this.buffOut = outBuffer;
            this.size = size;
            this.howMuchEffect = howMuchEffect;
            this.objToRepaint = objToRepaint;
            this.width = width;
            this.height = height;
            this.raster = raster;

            this.interrupt();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Tool.sleep(1000);
                } catch (InterruptedException ex) {
                    do {
                        int pixels = mix(buff1, buff2, buffOut, size, howMuchEffect);
                        int lines = pixels / width;
                        lines = lines < height ? lines : height;
                        raster.setDataElements(0, 0, width, lines, buffOut);
                        objToRepaint.repaint(0, 0, width, lines);
                    } while (Tool.interrupted());
                }
            }
        }

        private int mix(int[] pix1Buffer, int[] pix2Buffer, int[] outBuffer, int size, int howMuchEffect) {
            long startT = new Date().getTime();
            int outpixel = 0;
            int inpixel1 = 0;
            int inpixel2 = 0;
            int tmp = 0;
            int offset = 0;
            int k = 0, i = 0, end;
            int step = 100000;
            int incolor1;
            while (k < size) {
                if (this.isInterrupted()) {
                    return i;
                }
                i = k;
                k += step;
                end = k < size ? k : size;
                for (; i < end; ++i) {
                    inpixel1 = pix1Buffer[i];
                    inpixel2 = pix2Buffer[i];
                    outpixel = 0;
                    for (offset = 0; offset < 25; offset += 8) {
                        incolor1 = (inpixel1 >> offset) & 0xff;
                        tmp = (mixedVal(incolor1, (inpixel2 >> offset) & 0xff) * howMuchEffect + incolor1 * (255 - howMuchEffect)) >> 8;
                        if (tmp > 255) {
                            tmp = 255;
                        } else if (tmp < 0) {
                            tmp = 0;
                        }
                        outpixel |= tmp << offset;
                    }
                    outBuffer[i] = outpixel;
                }
            }
            long endT = new Date().getTime();
            System.out.println("Czas: " + (endT - startT) + "ms dla " + size + " pikseli, srednio " + (((double) endT - startT) / size));
            return size;
        }

        protected abstract int mixedVal(int a, int b);
    }
    private SortedMap<String, Tool> tools;

    public Tools() {
        tools = new TreeMap<String, Tool>();

        tools.put("Multiply", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return (a * b) >> 8;
            }
        });
        tools.put("Screen", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return 255 - (((255 - a) * (255 - b)) >> 8);
            }
        });
        tools.put("Darken", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a < b ? a : b;
            }
        });
        tools.put("Lighten", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a > b ? a : b;
            }
        });
        tools.put("Difference", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return Math.abs(a - b);
            }
        });
        tools.put("Additive", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a + b;
            }
        });
        tools.put("Substractive", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a + b - 255;
            }
        });
        tools.put("Negation", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return 255 - Math.abs(255 - a - b);
            }
        });
        tools.put("Exclusion", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a + b - ((a * b) >> 7);
            }
        });
        tools.put("Overlay", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a < 128
                        ? (a * b) >> 7 //2 * a * b
                        : 255 - (((255 - a) * (255 - b)) >> 7);// 255 - 2 * (255 - a) * (255 - b);
            }
        });
        tools.put("Hard Light", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return b < 128
                        ? (a * b) >> 7 //2 * a * b
                        : 255 - (((255 - a) * (255 - b)) >> 7);// 255 - 2 * (255 - a) * (255 - b);
            }
        });
        tools.put("Soft Light", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return b < 128
                        ? ((a * b) >> 7) + ((a * a * (255 - 2 * b)) >> 16)//2 * a * b + a * a * (255 - 2 * b)
                        : (((int) (Math.sqrt(a) * (2 * b - 255))) >> 4) + ((a * (255 - b)) >> 7);//2 * a * (255 - b);
            }
        });
        tools.put("Color Dodge", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return (a << 8) / (256 - b);
            }
        });
        tools.put("Color Burn", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return 255 - ((255 - a) << 8) / (b + 1);
            }
        });
        tools.put("Reflect", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a * a / (256 - b);
            }
        });

    }

    public void addTool(String name, Tool tool) {
        tools.put(name, tool);
    }

    public Tool getTool(String name) {
        return tools.get(name);
    }

    public Set<String> getLabels() {
        return tools.keySet();
    }

    public Set<Entry<String, Tool>> getAll() {
        return tools.entrySet();
    }
}
