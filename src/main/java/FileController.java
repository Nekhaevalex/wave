import javax.swing.*;
import java.io.File;

public class FileController {
    public static File file = null;

    public static File getPlaylist() {
        JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showDialog(null, "Open...");
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            if (file.toString().substring(file.toString().lastIndexOf('.') + 1).equals("vtu")) {
                System.out.println("VTU converter is not ready yet");
                return file;
            } else if (file.toString().substring(file.toString().lastIndexOf('.') + 1).equals("playlist")) {
                System.out.println("Success");
                return file;
            }
        }
        return file;
    }
}
