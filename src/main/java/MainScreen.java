import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainScreen {
    private JButton button1;
    private JPanel panel1;
    private JPanel image;
    private JPanel histogram;
    private JPanel filtered;
    private JPanel simplified;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JSlider slider1;
    private JButton playButton;
    private JTextField scaleTextField;

    private static JMenuItem openItem;

    private File file;

    private int filter = 0;
    private static int mode = 0;

    private AnimationPlayer player;
    private ChannelAnalyzer analyzer;
    private Recognizer recognizer;

    private MainScreen() {
        analyzer = new ChannelAnalyzer(histogram);
        recognizer = new Recognizer(filtered);
        player = new AnimationPlayer(image, slider1, analyzer, recognizer);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.redrawImage(0);
                player.histogramPanel = histogram;
                try {
                    analyzer.makeHistogram(player.sendFrame());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    file = FileController.getPlaylist();
                    player.loadPlaylist(file);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                slider1.setMaximum(player.getFramesAmount());
            }
        });
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filterName = (String) comboBox1.getSelectedItem();
                assert filterName != null;
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
                analyzer.filter = filter;

            }
        });
        comboBox3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzer.scale = comboBox3.getSelectedIndex();
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
                analyzer.mode = mode;
            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    player.playAnimation();
                } catch (InterruptedException | IOException e1) {
                    e1.printStackTrace();
                }
                slider1.setValue(0);
            }
        });
        slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                player.setFrame(slider1.getValue());
            }
        });
        scaleTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.scale = Double.parseDouble(scaleTextField.getText());
            }
        });
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
        } else if (vers.contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Wave Predictor");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));
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
