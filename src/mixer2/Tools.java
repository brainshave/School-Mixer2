/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mixer2;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author szymon
 */
public class Tools {

    public abstract class Tool {

        public void mix(BufferedImage im1, BufferedImage im2, BufferedImage imOut) {
            long startT = 0, alocT = 0, grabber1T = 0, grabber2T = 0, grab1T = 0, grab2T = 0, loopT = 0, setDataT = 0;
            try {
                //System.out.println("Typy: im1: " + im1.getType() + " im2: " + im2.getType() + " imOut: " + imOut.getType());
                startT = new Date().getTime();
                WritableRaster outRaster = imOut.getRaster();
                int width = Math.min(im1.getWidth(), im2.getWidth());
                int height = Math.min(im1.getHeight(), im2.getHeight());
                int size = width * height;
                int[] pix1Buffer = new int[size];
                int[] pix2Buffer = new int[size];
                //int[] outBuffer = new int[size];
                alocT = new Date().getTime();
                PixelGrabber pix1 = new PixelGrabber(im1, 0, 0, width, height, pix1Buffer, 0, width);
                grabber1T = new Date().getTime();
                PixelGrabber pix2 = new PixelGrabber(im2, 0, 0, width, height, pix2Buffer, 0, width);
                grabber2T = new Date().getTime();
                pix1.grabPixels();
                grab1T = new Date().getTime();
                pix2.grabPixels();
                grab2T = new Date().getTime();
                int r1 = 0;
                int g1 = 0;
                int b1 = 0;
                int r2 = 0;
                int g2 = 0;
                int b2 = 0;
                int outpixel = 0;
                int inpixel1 = 0;
                int inpixel2 = 0;
                int tmp = 0;
                int offset = 0;
                for (int i = 0; i < size; ++i) {
                    inpixel1 = pix1Buffer[i];
                    inpixel2 = pix2Buffer[i];
                    outpixel = 0;
                    for (offset = 0; offset < 25; offset += 8) {
                        tmp = mixedVal((inpixel1 >> offset) & 0xff, (inpixel2 >> offset) & 0xff);
                        if (tmp > 255) {
                            tmp = 255;
                        } else if (tmp < 0) {
                            tmp = 0;
                        }
                        outpixel |= tmp << offset;
                    }
                    pix1Buffer[i] = outpixel;
                }
                loopT = new Date().getTime();
                outRaster.setDataElements(0, 0, width, height, pix1Buffer);
                setDataT = new Date().getTime();

                System.out.println("Czasy [ms]:" +
                        "\nAlokacja:                    " + (alocT - startT) +
                        "\nStworzenie PixeGrabbera 1:   " + (grabber1T - alocT) +
                        "\nStworzenie PixeGrabbera 2:   " + (grabber2T - grabber1T) +
                        "\nGrabber 1:                   " + (grab1T - grabber2T) +
                        "\nGrabber 2:                   " + (grab2T - grab1T) +
                        "\nCzas petli:                  " + (loopT - grab2T) +
                        "\nSredni czas obrotu:          " + (((double) (loopT - grab2T)) / size) +
                        "\nUstawianie danych w bitmapie:" + (setDataT - loopT));
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
        }

        protected abstract int mixedVal(int a, int b);
    }
    private Map<String, Tool> tools;

    public Tools() {
        tools = new HashMap<String, Tool>();

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
        tools.put("Difference 1-2", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a - b;
            }
        });
        tools.put("Difference 2-1", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return b - a;
            }
        });
        tools.put("Additive", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a + b - 255;
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
                return a + b - ((2 * a * b) >> 8);
            }
        });
        tools.put("Overlay", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                return a < 128
                        ? (a * b) >> 8 //2 * a * b
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
                        ? ((a * b) >> 8) + ((a * a * (255 - 2 * b)) >> 16)//2 * a * b + a * a * (255 - 2 * b)
                        : ((((int) Math.sqrt(a)) * (2 * b - 255)) >> 4) + ((a * (255 - b)) >> 8);//2 * a * (255 - b);
            }
        });
        tools.put("Color Dodge", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                if (b == 255) {
                    return 255;
                }
                return (a << 8) / (255 - b);
            }
        });
        tools.put("Color Burn", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                if (b == 0) {
                    return 0;
                }
                return 255 - ((255 - a) << 8) / b;
            }
        });
        tools.put("Reflect", new Tool() {

            @Override
            public int mixedVal(int a, int b) {
                if (b == 255) {
                    return 255;
                }
                return a * a / (255 - b);
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
