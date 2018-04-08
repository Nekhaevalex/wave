import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainScreen {
    private JButton button1;
    private JPanel panel1;
    private JTextField textField1;
    private JPanel image;

    enum OS {Windows, macOS}

    private static OS os;

    private static JMenuItem openItem;

    private File file;

    private static BufferedImage img = null;

    private MainScreen() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redrawImage();
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
                    }
                }
            }
        });
    }

    private void redrawImage() {
        Graphics2D canvas = (Graphics2D) image.getGraphics();
        try {
            img = ImageIO.read(file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        canvas.drawImage(img, image.getWidth() / 2 - img.getWidth() / 2, image.getHeight() / 2 - img.getHeight() / 2, null);
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
