import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class AnimationPlayer {
    JPanel histogramPanel;
    private Graphics2D canvas;
    private JPanel screen;
    private ArrayList<BufferedImage> tape;
    private int currentFrame = 0;
    private JSlider slider;
    private ChannelAnalyzer analyzer;
    private Recognizer recognizer;
    double scale = 0.5;

    void loadPlaylist(File playlist) throws FileNotFoundException {
        Scanner sc = new Scanner(playlist);
        System.out.println("Loading");
        ArrayList<String> list = new ArrayList<>();
        while (sc.hasNextLine()) {
            list.add(sc.nextLine());
        }
        tape = new ArrayList<>();
        for (String aList : list) {
            try {
                tape.add(ImageIO.read(new File(aList)));
            } catch (IOException ex) {
                System.out.println("Error while reading");
                ex.printStackTrace();
            }
        }
    }

    int getFramesAmount() {
        return tape.size();
    }

    void redrawImage(int frameNumber) {
        BufferedImage frame = tape.get(frameNumber);
        canvas.drawImage(frame, (screen.getWidth() - (int) Math.round(frame.getWidth() * scale)), (screen.getHeight() - (int) Math.round(frame.getHeight() * scale)), null);
    }

    BufferedImage sendFrame() {
        return tape.get(currentFrame);
    }

    void setFrame(int frameNumber) {
        currentFrame = frameNumber;
    }

    void playAnimation() throws InterruptedException {
        canvas = (Graphics2D) screen.getGraphics();
        canvas.scale(scale, scale);
        for (; currentFrame < tape.size(); currentFrame++) {
            redrawImage(currentFrame);
            if (analyzer != null) {
                analyzer.makeHistogram(sendFrame());
            }
            if (recognizer != null) {
                recognizer.setWave(sendFrame());
                recognizer.filter();
                recognizer.redraw();
            }
            int framerate = 5;
            Thread.sleep(1000 / framerate);
            slider.setValue(currentFrame);
        }
        currentFrame = 0;
    }

    AnimationPlayer(JPanel screen, JSlider slider, ChannelAnalyzer analyzer, Recognizer recognizer) {
        this.screen = screen;
        this.slider = slider;
        this.analyzer = analyzer;
        this.recognizer = recognizer;
    }
}
