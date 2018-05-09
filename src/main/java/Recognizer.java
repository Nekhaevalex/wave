import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class Recognizer {
    //TODO: распознаем волну
    //фильтруем по высотам через ступеньку
    //Пилим картинку
    private BufferedImage wave;
    private ArrayList heights;
    private BufferedImage waveFiltered;
    private JPanel adapted;

    public Graphics2D canvas;

    private static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    private int critical(int min, int max) {
        return min + (max - min) / 10;
    }

    void filter() {
        ChannelAnalyzer heighter = new ChannelAnalyzer();
        int[][] heightmap = heighter.getHeightMap(wave);
        int max = 0;
        int min = 10;
        for (int x = 0; x < heightmap.length; x++) {
            for (int y = 0; y < heightmap[1].length; y++) {
                if (heightmap[x][y] > max) {
                    max = heightmap[x][y];
                }
                if (heightmap[x][y] < min) {
                    min = heightmap[x][y];
                }
            }
        }
        int average = critical(min, max);
        waveFiltered = copyImage(wave);
        for (int x = 0; x < waveFiltered.getWidth(); x++) {
            for (int y = 0; y < waveFiltered.getHeight(); y++) {
                if (heightmap[x][y] > average) {
                    waveFiltered.setRGB(x, y, new Color(255, 255, 255).getRGB());
                } else {
                    waveFiltered.setRGB(x, y, new Color(0, 0, 0).getRGB());
                }
            }
        }
    }

    void setWave(BufferedImage wave) {
        this.wave = wave;
    }

    BufferedImage getWaveFiltered() {
        return waveFiltered;
    }

    int[][] makeBreaks(BufferedImage img, int parts) {
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

    void drawObject(int[][] source) {
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
        drawObject(makeBreaks(waveFiltered, 5));
    }

    Recognizer(JPanel adapted) {
        this.adapted = adapted;
    }
}
