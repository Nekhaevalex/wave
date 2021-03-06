import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

class ChannelAnalyzer {
    private JPanel histogram;
    int scale = 0;
    int mode = 0;
    int filter = 0;

    private double rgbToHeight(int r, int g, int b) {
//        double result = (asin(1-y/125)+PI*(5/6))/5;
//        System.out.println(r+" -> "+y+" : "+result);
        double result = 0;
        if ((double) b > 255 / 4) {
            result = 1 / 6 - asin(1 - (2 * (double) b) / 255) / PI;
        }
        if (((double) b - (double) r > 63.75)) {
            result = asin(1 - (2 * (double) b) / 255) / PI + 7 / 6;
        }
//        System.out.println(r+" -> "+r+" : "+result);
        return result;
    }

    void makeHistogram(BufferedImage img) {
        Graphics2D hist = (Graphics2D) histogram.getGraphics();
        hist.fillRect(0, 0, histogram.getWidth(), histogram.getHeight());
        int[][] rgb = new int[3][256];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 256; j++) {
                rgb[i][j] = 0;
            }
        }
        for (int x = 1; x < img.getWidth(); x++) {
            for (int y = 1; y < img.getHeight(); y++) {
                int clr = img.getRGB(x, y);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                rgb[0][red]++;
                rgb[1][green]++;
                rgb[2][blue]++;
            }
        }
        int max = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 256; j++) {
                if (rgb[i][j] > max) {
                    max = rgb[i][j];
                }
            }
        }
        if (mode == 0) {
            Color[] dim = new Color[3];
            dim[0] = Color.RED;
            dim[1] = Color.GREEN;
            dim[2] = Color.BLUE;
            int filterBegin = 0;
            int filterEnd = 3;
            if (filter == 1) {
                filterEnd = 1;
            } else if (filter == 2) {
                filterBegin = 1;
                filterEnd = 2;
            } else if (filter == 3) {
                filterBegin = 2;
                filterEnd = 3;
            }
            for (int i = filterBegin; i < filterEnd; i++) {
                for (int x = 0; x < 256; x++) {
                    hist.setColor(dim[i]);
                    if (scale == 0) {
                        hist.drawLine(x * histogram.getWidth() / 256, histogram.getHeight(), x * histogram.getWidth() / 256, histogram.getHeight() - (rgb[i][x] * histogram.getHeight() / max));
                    } else if (scale == 1) {
                        Double dHeight = (double) histogram.getHeight();
                        Double dMax = (double) max;
                        Double dColor = (double) rgb[i][x];
                        Double value = (log(dColor) * dHeight) / log(dMax);
                        int trueHeight = value.intValue();
                        if (trueHeight == -2147483648) {
                            trueHeight = 0;
                        }
                        hist.drawLine(x * histogram.getWidth() / 256, histogram.getHeight(), x * histogram.getWidth() / 256, histogram.getHeight() - trueHeight);
                    }
                }
            }
        } else {
            BasicStroke heightBrush = new BasicStroke(1);
            double[] heights = new double[img.getHeight()];
            for (int i = 0; i < img.getHeight(); i++) {
                int clr = img.getRGB(img.getWidth() / 2, i);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;

                heights[i] = rgbToHeight(red, green, blue);


            }
            hist.setColor(Color.white);
            hist.setStroke(heightBrush);
            for (int i = 1; i < img.getHeight(); i++) {
                int x1 = ((i - 1) * histogram.getWidth()) / heights.length;
                int y1 = (int) (heights[i - 1] * histogram.getHeight());
                int x2 = (i * histogram.getWidth()) / heights.length;
                int y2 = (int) (heights[i] * histogram.getHeight());
                hist.drawLine(x1, y1, x2, y2);
            }
        }
    }

    int[][] getHeightMap(BufferedImage img) {
        int[][] heighMap = new int[img.getWidth()][img.getHeight()];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int clr = img.getRGB(x, y);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;

                heighMap[x][y] = (int) (rgbToHeight(red, green, blue) * 10);
            }
        }
        return heighMap;
    }


    ChannelAnalyzer(JPanel histogramScreen) {
        this.histogram = histogramScreen;
    }

    ChannelAnalyzer() {

    }
}
