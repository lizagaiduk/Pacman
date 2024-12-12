package PacMan;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import static java.awt.FlowLayout.*;

public class Menu extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private JLabel logo;
    private JButton buttonNG, buttonExit, buttonRecords;
    private Clip backgroundMusic;
    private static Font font;

    static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(
                    "src/mainFont.ttf")).deriveFont(25f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Menu() {
        setTitle("Pac-Man Menu");
        setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(CENTER, 400, 60));
        mainPanel.setBackground(Color.BLACK);

        ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/images/Logo.png").getImage().getScaledInstance(600, 150, Image.SCALE_SMOOTH));
        logo = new JLabel(logoIcon);
        mainPanel.add(logo);

        buttonNG = createButton("New Game");
        buttonExit = createButton("Exit");
        buttonRecords = createButton("High Scores");

        mainPanel.add(buttonNG);
        mainPanel.add(buttonRecords);
        mainPanel.add(buttonExit);
        add(mainPanel);

        playBackgroundMusic("src/sounds/menuMusic.wav");
        setVisible(true);

    }
    private JButton createButton(String name) {
        JButton button = new JButton(name);
        button.setPreferredSize(new Dimension(320, 50));
        button.setFont(font);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.YELLOW);
        button.setFocusPainted(false); //control appearance of focus indicator
        button.addActionListener(this);
        return button;
    }

    private void playBackgroundMusic(String filePath) {
        try {
            File musicFile = new File(filePath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInput);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)/*pathway through which audio data flows within the system*/ {
            JOptionPane.showMessageDialog(this, "Failed to load music:: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonNG) {
         openMapSelection();
         mainPanel.setVisible(false);
         dispose();
        } else if (e.getSource() == buttonRecords) {
            displayRecords();
        } else if (e.getSource() == buttonExit) {
            System.exit(0);
        }
    }


    private void displayRecords() {
        JFrame hsFrame = new JFrame("High Scores");
        hsFrame.setSize(500, 600);
        hsFrame.setResizable(false);
        hsFrame.setLocationRelativeTo(null);
        hsFrame.setBackground(Color.BLACK);
        hsFrame.setLayout(new BorderLayout());

        List<Record> highScores = Record.loadRecords();
        JTextArea scoresArea = new JTextArea();
        scoresArea.setEditable(false);
        scoresArea.setBackground(Color.BLACK);
        scoresArea.setForeground(Color.MAGENTA);
        scoresArea.setFont(font);

        for (Record rec : highScores) {
            scoresArea.append(rec.getName() + "....... " + rec.getScore() + "\n");
        }

        JScrollPane scrollPane = new JScrollPane(scoresArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        hsFrame.add(scrollPane,BorderLayout.CENTER);
        hsFrame.setVisible(true);
    }



    private void openMapSelection() {
        JFrame mapSelectionFrame = new JFrame("Select Map");
        mapSelectionFrame.setSize(800, 800);
        mapSelectionFrame.setResizable(false);
        mapSelectionFrame.setLocationRelativeTo(null);
        mapSelectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] mapNames = {"Map 1", "Map 2", "Map 3", "Map 4", "Map 5"};
        String[] mapFiles = {"src/maps/1.txt", "src/maps/map2.txt", "src/maps/map3.txt", "src/maps/map4.txt", "src/maps/map5.txt"};
        int[] cellSizes = {43, 35, 29, 23, 18};

        HashMap<String,String> buttonMap = new HashMap<>();
        HashMap<String,Integer> cellSizeMap=new HashMap<>();
        for (int i = 0; i < mapNames.length; i++) {
            buttonMap.put(mapNames[i], mapFiles[i]);
            cellSizeMap.put(mapNames[i], cellSizes[i]);
        }

        JPanel panel = new JPanel(new FlowLayout(CENTER, 400, 20));
        panel.setBackground(Color.BLACK);

        JLabel text=new JLabel("Choose map:");
        text.setFont(font);
        text.setBackground(Color.BLACK);
        text.setForeground(Color.magenta);
        panel.add(text);
        for (String mapName : mapNames) {
            JButton selectButton = new JButton(mapName);
            selectButton.setFont(font);
            selectButton.setBackground(Color.BLACK);
            selectButton.setForeground(Color.YELLOW);
            selectButton.setFocusPainted(false);
            selectButton.setPreferredSize(new Dimension(320,80) );

            selectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedMap = buttonMap.get(((JButton) e.getSource()).getText());
                    int cellSize = cellSizeMap.get(((JButton) e.getSource()).getText());
                    mapSelectionFrame.dispose();
                    stopBackgroundMusic();
                   int[] pacmanStartPos=StartPositions.pacManStartPositions.get(selectedMap);
                   int[][]ghostsStartPos=StartPositions.ghostsStartPositions.get(selectedMap);
                    GameFrame gameWindow = new GameFrame(selectedMap,cellSize,pacmanStartPos,ghostsStartPos);
                   gameWindow.setVisible(true);
                }
            });
            panel.add(selectButton);
        }

        mapSelectionFrame.add(panel);
        mapSelectionFrame.setVisible(true);
    }
}
