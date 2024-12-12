package PacMan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

public class GameFrame extends JFrame implements KeyListener {
    private final String mapFilePath;
    private final int cellSize;
    private final int[] pacmanStartPos;
    private final int[][] ghostsStartPos;
    private Thread timer,bonusThread;
    private double elapsedTime = 0;
    public JLabel timerLabel;
    public static JLabel scoreLabel;
    private static JLabel escapeLabel;
    public static JLabel[] hearts;
    static Map gameMap;
    private static Font font;
    private static Font escFont;

    static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("src/mainFont.ttf")).deriveFont(23f);
            escFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/mainFont.ttf")).deriveFont(15f);
        } catch (FontFormatException | IOException e) {
            System.out.println("Error loading font: " + e.getMessage());
            font = new Font("GameFont", Font.BOLD, 20);
            escFont = new Font("GameFont", Font.BOLD, 10); // default font if custom font fails

        }
    }

    public GameFrame(String mapFilePath, int cellSize, int[]pacmanStartPos, int[][]ghostsStartPos) {
        this.mapFilePath = mapFilePath;
        this.cellSize = cellSize;
        this.pacmanStartPos = pacmanStartPos;
        this.ghostsStartPos = ghostsStartPos;

        setTitle("Pac-Man");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));
        topPanel.setBackground(Color.BLACK);
        topPanel.setPreferredSize(new Dimension(1000,100));

        escapeLabel = new JLabel("Esc->menu");
        escapeLabel.setFont(escFont);
        escapeLabel.setForeground(Color.BLUE);
        topPanel.add(escapeLabel);

        scoreLabel = new JLabel("Score:");
        scoreLabel.setFont(font);
        scoreLabel.setForeground(Color.YELLOW);
        topPanel.add(scoreLabel);

        timerLabel = new JLabel("Time:0s");
        timerLabel.setForeground(Color.YELLOW);
        timerLabel.setFont(font);
        topPanel.add(timerLabel);

        gameMap = new Map(mapFilePath, cellSize,pacmanStartPos,ghostsStartPos,this);
        gameMap.setPreferredSize(new Dimension(700, 520));
        JPanel mapPanel = new JPanel(new GridBagLayout());
        mapPanel.setBackground(Color.BLACK);
        mapPanel.add(gameMap);
        mainPanel.add(mapPanel, BorderLayout.CENTER);


        JPanel heartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 15));
        heartPanel.setBackground(Color.BLACK);
        heartPanel.setPreferredSize(new Dimension(110,40));
        hearts=new JLabel[5];
        for (int i = 0; i < hearts.length; i++) {
            hearts[i] = new JLabel(new ImageIcon("src/images/heart.png"));
            hearts[i].setPreferredSize(new Dimension(20,20));
            hearts[i].setVisible(false);
            heartPanel.add(hearts[i]);
        }
        topPanel.add(heartPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        add(mainPanel);


        initThreads();
        bonusThread.start();
        timer.start();
    }

    private void initThreads() {
        timer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
                    gameMap.checkForGhosts();
                    updateScoreDisplay(gameMap.score);
                    updateLives();
                    updateTimer();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        bonusThread=new Thread(()->{
            while (!Thread.currentThread().isInterrupted()){
                try{
                    Thread.sleep(5000);
                    gameMap.bonusMayBeCreated();
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void updateTimer() {
        elapsedTime++;
        timerLabel.setText("Time:" + elapsedTime / 10 + "s");
    }

    public void updateScoreDisplay(int newScore) {
        SwingUtilities.invokeLater(() -> scoreLabel.setText("Score:" + newScore));
    }

    public static void updateLives() {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisible(i < gameMap.lives);
        }
    }
    public void stopGame() {
        if (timer != null) {
            timer.interrupt();
        }
        if (bonusThread != null) {
            bonusThread.interrupt();
        }
        dispose();
    }
    public void backToMenu(){
        stopGame();
        SwingUtilities.invokeLater(()->{
            Menu menu=new Menu();
            menu.setVisible(true);
        });


    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
           backToMenu();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}