package MetadataExtractionApp;

import javax.swing.*;
import java.awt.*;

public class MetadataGUI extends JFrame {

    public MetadataGUI() {
        init();
    }

    private void init() {
        setTitle("Metadata Extraction");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MetadataGUI().setVisible(true) );
    }
}
