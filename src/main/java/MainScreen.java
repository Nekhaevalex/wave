import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class MainScreen {
    private JButton button1;
    private JPanel panel1;
    private JTextArea textArea;

    private static JMenuBar menuBar;
    private static JMenu fileMenu;

    private static JMenuItem openItem;

    private File file;

    public MainScreen() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open button pressed");
                JFileChooser fileChooser = new JFileChooser();
                int ret = fileChooser.showDialog(null, "Open...");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    textArea.setText(file.toString());
                }
            }
        });
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open...");
        fileMenu.add(openItem);
        menuBar.add(fileMenu);

        String vers = System.getProperty("os.name").toLowerCase();
        if (vers.contains("windows")) {
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
        } else if (vers.contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Wave Predictor");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.META_MASK));
        }

        JFrame frame = new JFrame("Wave Predictor");
        frame.setJMenuBar(menuBar);
        frame.setContentPane(new MainScreen().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
