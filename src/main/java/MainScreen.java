import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.abs;
import static java.lang.Math.log;

public class MainScreen {
    private JButton button1;
    private JPanel panel1;
    private JLabel textField1;
    private JPanel image;
    private JPanel histogram;
    private JPanel adapted;
    private JPanel simplified;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JSlider slider1;
    private JButton playButton;

    enum OS {Windows, macOS}

    private static OS os;

    private static JMenuItem openItem;

    private File file;

    private static BufferedImage img = null;
    private int filter = 0;
    private static int scale = 0;
    private static int mode = 0;

    private MainScreen() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redrawImage();
                try {
                    makeHistogram();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int ret = fileChooser.showDialog(null, "Open...");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    textField1.setText(file.getPath());
                    if (file.toString().substring(file.toString().lastIndexOf('.') + 1).equals("vtu")) {
                        System.out.println("VTU converter is not ready yet");
                        if (os == OS.macOS) {
                            try {
                                Runtime.getRuntime().exec(new String[]{"osascript", "-e", "display notification \"VTU converter is not ready yet\" with title \"Wave Predictor\" sound name \"Funk\""});
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } else if (file.toString().substring(file.toString().lastIndexOf('.') + 1).equals("png")) {
                        redrawImage();
                        try {
                            makeHistogram();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filterName = (String) comboBox1.getSelectedItem();
                switch (filterName) {
                    case "All":
                        filter = 0;
                        break;
                    case "Red":
                        filter = 1;
                        break;
                    case "Green":
                        filter = 2;
                        break;
                    case "Blue":
                        filter = 3;
                        break;
                    default:
                        break;
                }
                try {
                    makeHistogram();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        comboBox3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scale = comboBox3.getSelectedIndex();
                try {
                    makeHistogram();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        comboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = comboBox2.getSelectedIndex();
                if (comboBox2.getSelectedIndex() == 1) {
                    comboBox1.setEnabled(false);
                } else {
                    comboBox1.setEnabled(true);
                }
                try {
                    makeHistogram();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void redrawImage() {
        Graphics2D canvas = (Graphics2D) image.getGraphics();
        canvas.fillRect(0, 0, histogram.getWidth(), histogram.getHeight());
        try {
            img = ImageIO.read(file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        canvas.drawImage(img, image.getWidth() / 2 - img.getWidth() / 2, image.getHeight() / 2 - img.getHeight() / 2, null);
    }

    private void makeHistogram() throws IOException {
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
            if (filter == 0) {
                filterBegin = 0;
                filterEnd = 3;
            } else if (filter == 1) {
                filterBegin = 0;
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
            BufferedImage heightsOrigin = ImageIO.read(new File("/Users/alex/IdeaProjects/wave/src/main/resources/Snap.png"));
            BasicStroke heightBrush = new BasicStroke(1);
            int[] heights = new int[img.getHeight()];
            for (int i = 0; i < img.getHeight(); i++) {
                int clr = img.getRGB(img.getWidth() / 2, i);
                int red = (clr & 0x00ff0000) >> 16;
                int blue = clr & 0x000000ff;

                for (int j = 0; j < heightsOrigin.getHeight(); j++) {
                    int clrH = heightsOrigin.getRGB(1, j);
                    int redH = (clrH & 0x00ff0000) >> 16;
                    int blueH = clrH & 0x000000ff;
                    if ((abs(blue - blueH) + abs(red - redH)) / 2 <= 20) {
                        heights[i] = j;
                        break;
                    }
                }
            }
            hist.setColor(Color.white);
            hist.setStroke(heightBrush);
            for (int i = 1; i < img.getHeight(); i++) {
                int x1 = (i - 1) * histogram.getWidth() / img.getHeight();
                int y1 = histogram.getHeight() - heights[i - 1] * histogram.getHeight() / img.getHeight();
                int x2 = i * histogram.getWidth() / img.getHeight();
                int y2 = histogram.getHeight() - heights[i] * histogram.getHeight() / img.getHeight();
                hist.drawLine(x1, y1, x2, y2);
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open...");
        fileMenu.add(openItem);
        menuBar.add(fileMenu);

        String vers = System.getProperty("os.name").toLowerCase();
        if (vers.contains("windows")) {
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
            os = OS.Windows;
        } else if (vers.contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Wave Predictor");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));
            os = OS.macOS;
        }

        JFrame frame = new JFrame("Wave Predictor");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setJMenuBar(menuBar);
        frame.setContentPane(new MainScreen().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
