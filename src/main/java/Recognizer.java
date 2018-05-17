import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class Recognizer {
    //TODO: распознаем волну
    //фильтруем по высотам через ступеньку
    //Пилим картинку
    private BufferedImage wave;
    private BufferedImage waveFiltered;
    private JPanel adapted;

    private Graphics2D canvas;
    private int totalMax;

    private static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    private int critical(int min, int max) {
        return min + (max - min) / 2;
    }

    void filter() {
        ChannelAnalyzer heighter = new ChannelAnalyzer();
        int[][] heightmap = heighter.getHeightMap(wave);
        int max = 0;
        int min = 10;
        for (int[] aHeightmap : heightmap) {
            for (int y = 0; y < heightmap[1].length; y++) {
                if (aHeightmap[y] > max) {
                    max = aHeightmap[y];
                }
                if (aHeightmap[y] < min) {
                    min = aHeightmap[y];
                }
            }
        }
        int average = critical(min, max);
        if (max > totalMax) {
            totalMax = max;
        }
        waveFiltered = copyImage(wave);
        for (int x = 0; x < waveFiltered.getWidth(); x++) {
            for (int y = 0; y < waveFiltered.getHeight(); y++) {
                if (heightmap[x][y] > average) {
                    waveFiltered.setRGB(x, y, new Color(255, 255, 255).getRGB());
                } else {
                    int color = 255 - 255 * average / totalMax;
                    waveFiltered.setRGB(x, y, new Color(color, color, color).getRGB());
                }
            }
        }
    }

    void setWave(BufferedImage wave) {
        this.wave = wave;
    }

    private int[][] makeBreaks(BufferedImage img, int parts) {
        int[][] result = new int[parts + 1][2];
        double[] formula = MathProcessor.approximate(img, 1);

        System.out.print("Formula: ");
        for (int i = 0; i < formula.length; i++) {
            System.out.print(formula[i] + "*x^" + i + "+");
        }
        System.out.println("0");

        int i = 0;
        for (int x = 0; x <= img.getWidth(); x += Math.round(img.getWidth() / parts)) {
            result[i][0] = x;
            for (int k = 0; k < formula.length; k++) {
                result[i][1] += (int) (formula[k] * Math.pow(x, k));
            }
            System.out.println(result[i][0] + ":" + result[i][1]);
            i++;
        }
        return result;
    }

    private void drawObject(int[][] source) {
        canvas = (Graphics2D) adapted.getGraphics();
        canvas.setColor(new Color(255, 0, 174));
        int x0 = adapted.getWidth() / 2 - waveFiltered.getWidth() / 4;
        int y0 = adapted.getHeight() / 2 - waveFiltered.getHeight() / 4;
        for (int i = 0; i < source.length - 1; i++) {
            int x = x0 + (source[i][0]) / 2;
            int y = y0 + (source[i][1]) / 2;

            int xe = x0 + (source[i + 1][0]) / 2;
            int ye = y0 + (source[i + 1][1]) / 2;

            canvas.drawRect(x - 2, y - 2, 4, 4);
            canvas.drawLine(x, y, xe, ye);
        }
        canvas.drawRect(x0 + (source[source.length - 1][0]) / 2 - 2, y0 + (source[source.length - 1][1]) / 2 - 2, 4, 4);
    }

    void redraw() {
        canvas = (Graphics2D) adapted.getGraphics();
        canvas.drawImage(waveFiltered, (adapted.getWidth() / 2 - waveFiltered.getWidth() / 4), (adapted.getHeight() / 2 - waveFiltered.getHeight() / 4), waveFiltered.getWidth() / 2, waveFiltered.getHeight() / 2, null);
        drawObject(makeBreaks(waveFiltered, 10));
    }

    Recognizer(JPanel adapted) {
        this.adapted = adapted;
    }
}
