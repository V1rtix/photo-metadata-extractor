package MetadataExtractionApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MetadataGUI extends JFrame {

    private final Color bgMain = new Color(45, 45, 45);
    private final Color bgPanel = new Color(55, 55, 55);
    private final Color bgSelected = new Color(70, 70, 70);
    private final Color accentGreen = new Color(92, 184, 92);
    private final Color textLight = new Color(220, 220, 220);
    private final Color btnDark = new Color(60, 60, 60);

    private CardLayout cardLayout;
    private JPanel cardPanel, fileListContainer, resultsContainer;
    private List<File> uploadedFiles = new ArrayList<>();
    private List<ResultRow> resultRows = new ArrayList<>();

    private JToggleButton tglName, tglFocal, tglFNumber, tglIso, tglExposure;

    public MetadataGUI() {
        init();
    }

    private void init() {
        setTitle("Metadata Extraction");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 850); // Mírně širší pro lepší rozestupy
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(bgMain);

        cardPanel.add(createUploadView(), "UPLOAD_VIEW");
        cardPanel.add(createResultsView(), "RESULTS_VIEW");

        add(cardPanel);
    }

    private void applyModernScrollbar(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(28);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = new Color(80, 80, 80);
                this.trackColor = bgMain;
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() { return new JButton() {{ setPreferredSize(new Dimension(0,0)); }}; }
        });
    }

    private JPanel createUploadView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(bgMain);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel dropArea = new JPanel(new GridBagLayout());
        dropArea.setBackground(bgPanel);
        dropArea.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 2, 5, 2, true));
        dropArea.setPreferredSize(new Dimension(0, 200));

        JLabel dropLabel = new JLabel("<html><center><h1 style='color:white; font-size:24px;'>Drop files here</h1><p style='color:gray; font-size:16px;'>or click to upload</p></center></html>");
        dropArea.add(dropLabel);
        dropArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dropArea.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { openFileChooser(); }
        });

        panel.add(dropArea, BorderLayout.NORTH);

        fileListContainer = new JPanel();
        fileListContainer.setLayout(new BoxLayout(fileListContainer, BoxLayout.Y_AXIS));
        fileListContainer.setBackground(bgMain);

        JScrollPane sp = new JScrollPane(fileListContainer);
        applyModernScrollbar(sp);
        panel.add(sp, BorderLayout.CENTER);

        JButton btnRun = createStyledButton("Run", accentGreen, Color.WHITE, 22);
        btnRun.setPreferredSize(new Dimension(0, 65));
        btnRun.addActionListener(e -> startExtraction());
        panel.add(btnRun, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResultsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(bgMain);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        // 2. Results - Vycentrováno
        JLabel title = new JLabel("Results", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        topSection.add(title);

        // 2. Include - Nalevo
        JLabel incLbl = new JLabel("Include:");
        incLbl.setForeground(Color.WHITE);
        incLbl.setFont(new Font("Arial", Font.BOLD, 20));
        incLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        incLbl.setBorder(new EmptyBorder(0, 45, 10, 0));
        topSection.add(incLbl);

        // 2. Tlačítka - Výška 55px, Zelená, Přesné lícování
        JPanel toggleRow = new JPanel(new GridLayout(1, 5, 25, 0));
        toggleRow.setOpaque(false);
        toggleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        toggleRow.setBorder(new EmptyBorder(0, 45, 0, 10)); // Stejný border jako u řádků níže

        tglName = createHeaderToggle("Filename");
        tglFocal = createHeaderToggle("Focal");
        tglFNumber = createHeaderToggle("F-Num");
        tglIso = createHeaderToggle("ISO");
        tglExposure = createHeaderToggle("Exp.");

        toggleRow.add(tglName); toggleRow.add(tglFocal); toggleRow.add(tglFNumber);
        toggleRow.add(tglIso); toggleRow.add(tglExposure);
        toggleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(toggleRow);

        panel.add(topSection, BorderLayout.NORTH);

        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(bgMain);

        JScrollPane sp = new JScrollPane(resultsContainer);
        applyModernScrollbar(sp);
        panel.add(sp, BorderLayout.CENTER);

        // Spodní tlačítka - Větší text
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JButton btnBack = createStyledButton("Back", btnDark, textLight, 18);
        btnBack.setPreferredSize(new Dimension(130, 50));
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, "UPLOAD_VIEW"));

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightSide.setOpaque(false);
        JButton btnSelectAll = createStyledButton("Select All Rows", btnDark, textLight, 18);
        btnSelectAll.addActionListener(e -> { for(ResultRow r : resultRows) r.setSelected(true); });
        JButton btnCopy = createStyledButton("Copy Selected", accentGreen, Color.WHITE, 18);
        btnCopy.setPreferredSize(new Dimension(240, 50));
        btnCopy.addActionListener(e -> copyResults());

        rightSide.add(btnSelectAll); rightSide.add(btnCopy);
        footer.add(btnBack, BorderLayout.WEST);
        footer.add(rightSide, BorderLayout.EAST);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JToggleButton createHeaderToggle(String text) {
        JToggleButton tgl = new JToggleButton(text, true);
        tgl.setFocusPainted(false);
        tgl.setFont(new Font("Arial", Font.BOLD, 16));
        tgl.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Vynucení vzhledu bez systémových vlivů
        tgl.setOpaque(true);
        tgl.setBorder(new LineBorder(accentGreen, 2));

        updateToggleStyle(tgl);
        tgl.addActionListener(e -> updateToggleStyle(tgl));
        return tgl;
    }

    private void updateToggleStyle(JToggleButton b) {
        if (b.isSelected()) {
            b.setBackground(accentGreen);
            b.setForeground(Color.WHITE);
        } else {
            b.setBackground(btnDark);
            b.setForeground(textLight);
        }
    }

    private void openFileChooser() {
        // Tady je to okno co minule (image_fe93f7.png / nativní Windows)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFileChooser jfc = new JFileChooser();
            jfc.setMultiSelectionEnabled(true);
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                for (File f : jfc.getSelectedFiles()) addFileToList(f);
            }
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void addFileToList(File f) {
        if (uploadedFiles.contains(f)) return;
        uploadedFiles.add(f);
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(bgPanel);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        row.setBorder(new EmptyBorder(5, 20, 5, 10));

        JLabel name = new JLabel(f.getName());
        name.setFont(new Font("Arial", Font.PLAIN, 18));
        name.setForeground(textLight);
        row.add(name, BorderLayout.CENTER);

        JButton btnX = createStyledButton("X", new Color(180, 70, 70), Color.WHITE, 14);
        btnX.setPreferredSize(new Dimension(50, 40));
        btnX.addActionListener(e -> {
            uploadedFiles.remove(f);
            fileListContainer.remove(row.getParent());
            fileListContainer.revalidate();
            fileListContainer.repaint();
        });
        row.add(btnX, BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 0, 8, 0));
        wrap.add(row);
        fileListContainer.add(wrap);
        fileListContainer.revalidate();
    }

    private void startExtraction() {
        if (uploadedFiles.isEmpty()) return;
        resultsContainer.removeAll();
        resultRows.clear();
        cardLayout.show(cardPanel, "RESULTS_VIEW");
        for (File f : uploadedFiles) {
            ResultRow rr = new ResultRow(f.getName(), new String[]{"70mm", "f/5.6", "ISO-200", "1/125s"});
            resultRows.add(rr);
            resultsContainer.add(rr.getUI());
        }
        resultsContainer.revalidate();
    }

    private void copyResults() {
        StringBuilder sb = new StringBuilder();
        for (ResultRow rr : resultRows) {
            if (rr.isSelected) {
                List<String> line = new ArrayList<>();
                if (tglName.isSelected()) line.add(rr.name);
                if (tglFocal.isSelected()) line.add(rr.data[0]);
                if (tglFNumber.isSelected()) line.add(rr.data[1]);
                if (tglIso.isSelected()) line.add(rr.data[2]);
                if (tglExposure.isSelected()) line.add(rr.data[3]);

                sb.append(String.join(" | ", line)).append("\n");
            }
        }
        if (sb.length() > 0) {
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
            JOptionPane.showMessageDialog(this, "Copied!");
        }
    }

    private JButton createStyledButton(String t, Color bg, Color fg, int fontSize) {
        JButton btn = new JButton(t);
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, fontSize));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private class ResultRow {
        JPanel ui, content;
        String name;
        String[] data;
        boolean isSelected = true;

        public ResultRow(String name, String[] data) {
            this.name = name; this.data = data;
            ui = new JPanel(new BorderLayout());
            ui.setOpaque(false);
            ui.setBorder(new EmptyBorder(0, 0, 8, 0));

            // GridLayout(1, 5) - Identické mezery jako u tlačítek
            content = new JPanel(new GridLayout(1, 5, 25, 0));
            content.setBackground(bgSelected);
            content.setBorder(new EmptyBorder(15, 45, 15, 10)); // Identický BorderX jako nahoře
            content.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

            content.add(createVal(name));
            for(String s : data) content.add(createVal(s));

            content.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { setSelected(!isSelected); }
            });
            ui.add(content);
        }

        private JLabel createVal(String t) {
            JLabel l = new JLabel(t);
            l.setFont(new Font("Arial", Font.PLAIN, 17));
            l.setForeground(textLight);
            return l;
        }

        public void setSelected(boolean s) {
            isSelected = s;
            content.setBackground(isSelected ? bgSelected : bgMain);
            content.repaint();
        }
        public JPanel getUI() { return ui; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MetadataGUI().setVisible(true));
    }
}