package PacMan;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Map extends JPanel {
    public static int cellSize;
    private ImageIcon wallHorizontal,wallVertical,square,
            leftDownCorner,leftUpCorner,rightDownCorner,
            rightUpCorner,point,endDownWall,endLeftWall,endRightWall, endUpWall;

    private JLabel[][] labels;
    char[][] mapArray;
    PacMan pacMan;
    private Ghosts[] ghosts;
    private GameFrame gamePanel;
    int score,lives = 3, rows, cols;
    List<Bonus> bonuses;
    boolean inGame;
    GameOver gameOverPanel;
    private  String filename;

    public Map(String filename, int cellSize, int[] pacmanStartPos, int[][] ghostsStartPos, GameFrame gamePanel) {
        this.cellSize = cellSize;
        this.gamePanel=gamePanel;
        this.filename=filename;
        this.bonuses = new ArrayList<>();
        loadIcons();
        mapArray = loadMapArray(filename);
        rows = mapArray.length;
        cols = mapArray[0].length;

        setLayout(new GridLayout(rows, cols));
        setBackground(Color.BLACK);
        labels = new JLabel[rows][cols];
        initMap();

        pacMan = new PacMan(this, pacmanStartPos[0], pacmanStartPos[1]);

        addKeyListener(pacMan);
        addKeyListener(gamePanel);

        initGhosts(ghostsStartPos);

        setFocusable(true);
        requestFocusInWindow();
        setVisible(true);

    }

    public void redrawMap() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                char c = mapArray[row][col];
                ImageIcon icon = getIcon(c);
                labels[row][col].setIcon(icon);
            }
        }
        for (Bonus bonus : bonuses) {
            labels[bonus.getY()][bonus.getX()].setIcon(Bonus.getBonusIcon(bonus.getType()));
        }
        for (Ghosts ghost : ghosts) {
            labels[ghost.getY()][ghost.getX()].setIcon(ghost.getGhostIcon());
        }
        labels[pacMan.getY()][pacMan.getX()].setIcon(pacMan.getCurrentImage());

    }
    private ImageIcon getIcon(char c) {
        return switch (c) {
            case '#' -> wallHorizontal;
            case '.' -> point;
            case '%' -> square;
            case '1' -> rightDownCorner;
            case '2' -> wallVertical;
            case '3' -> leftDownCorner;
            case '4' -> leftUpCorner;
            case '5' -> rightUpCorner;
            case '9' -> endRightWall;
            case '6' -> endLeftWall;
            case '@' -> endDownWall;
            case '$' -> endUpWall;
            case ' ' -> null;
            default -> null;
        };
    }


    private char[][] loadMapArray(String filename) {
        List<char[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines.add(line.toCharArray());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading map file: " + e.getMessage());
            return new char[0][0];
        }
        if (lines.isEmpty()) {
            return new char[0][0];
        }

        int rows = lines.size();
        int cols = lines.get(0).length;

        char[][] mapArray = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (lines.get(i).length != cols) {
                JOptionPane.showMessageDialog(this, "Inconsistent line length at line " + i + ", expected " + cols + " characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return new char[0][0];
            }
            mapArray[i] = lines.get(i);
        }

        return mapArray;
    }

    private void initMap() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setPreferredSize(new Dimension(cellSize, cellSize));
                initLabel(label, mapArray[row][col]);
                labels[row][col] = label;
                add(label);
            }
        }
    }

    private void initLabel(JLabel label, char c) {
        ImageIcon icon = getIcon(c);
        if (icon != null) {
            label.setIcon(icon);
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setBackground(Color.BLACK);
    }

    public void loadIcons() {
        try {
            wallHorizontal = loadImage("src/images/wallHorizontal.png");
            wallVertical = loadImage("src/images/wallVertical.png");
            leftDownCorner = loadImage("src/images/leftDownCorner.png");
            leftUpCorner = loadImage("src/images/leftUpCorner.png");
            rightDownCorner = loadImage("src/images/rightDownCorner.png");
            rightUpCorner = loadImage("src/images/rightUpCorner.png");
            point = loadImage("src/images/point.png");
            square = loadImage("src/images/square.png");
            endDownWall = loadImage("src/images/endDown.png");
            endLeftWall = loadImage("src/images/endLeft.png");
            endRightWall = loadImage("src/images/endRight.png");
            endUpWall = loadImage("src/images/endUp.png");
        }catch (Exception e4){
            System.err.println("Error loading icons: " + e4.getMessage());
            e4.printStackTrace();
        }
    }
    private ImageIcon loadImage(String path) {
        return resizeIcon(new ImageIcon(path), cellSize, cellSize);
    }

    public ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public void setPosition(int x, int y, ImageIcon icon) {
        if (x >= 0 && x < cols && y >= 0 && y < rows) {
            labels[y][x].setIcon(icon);
            labels[y][x].revalidate();
            labels[y][x].repaint();
        }
    }

    public boolean isValidPosition(int x, int y) {
        if (!(x >= 0 && x < cols && y >= 0 && y < cols)) {
            return false;
        }
        if (mapArray[y][x] != '.' && mapArray[y][x] != ' ') {
            return false;
        }
        for (Ghosts ghost : ghosts) {
            if (ghost.getX() == x && ghost.getY() == y)
                return false;
        }
        return true;
    }

    private void initGhosts(int[][] ghostsStartPos){
        ghosts = new Ghosts[4];
        ghosts[0] = new Ghosts(this, ghostsStartPos[0][0], ghostsStartPos[0][1], GhostType.BLINKY, 1);
        ghosts[1] = new Ghosts(this,ghostsStartPos[1][0] ,ghostsStartPos[1][1], GhostType.PINKY, 10);
        ghosts[2] = new Ghosts(this, ghostsStartPos[2][0],ghostsStartPos[2][1] , GhostType.INKY, 20);
        ghosts[3] = new Ghosts(this,ghostsStartPos[3][0] ,ghostsStartPos[3][1] , GhostType.CLYDE, 30);

    }
    public void decrementLives() {
        lives--;
        if (lives == 0) {
            gameOver();
        }
    }

    public void incrementScore() {
        score+=10;
    }

    public void checkForGhosts() {
        int pacX = pacMan.getX();
        int pacY = pacMan.getY();

        if (ghostAt(pacX, pacY - 1) ||
                ghostAt(pacX, pacY + 1) ||
                ghostAt(pacX - 1, pacY) ||
                ghostAt(pacX + 1, pacY)) {
            decrementLives();
        }
    }

    private boolean ghostAt(int x, int y) {
        for (Ghosts ghost : ghosts) {
            if (ghost.getX() == x && ghost.getY() == y) {
                SwingUtilities.invokeLater(() -> {
                });
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                ghost.resetPosition();
                redrawMap();
                revalidate();
                repaint();
                return true;
            }
        }
        return false;
    }


    public void bonusMayBeCreated() {
        if(bonuses.size()<20) {
            Bonus.createBonus(rows, cols, bonuses, this);
        }
    }
    public void bonusImpact(Bonus bonus){
        bonuses.remove(bonus);
        setPosition(bonus.getX(),bonus.getY(),null);
        switch (bonus.getType()){
            case SPEED_BOOST:
                pacMan.increaseSpeed();
                break;
            case EXTRA_POINTS :
                score+=100;
                break;
            case FREEZE :
                for(Ghosts ghost:ghosts) {
                    ghost.freeze();
                }
                break;
            case EXTRA_LIFE:
                if(lives<5) {
                    lives++;
                    SwingUtilities.invokeLater(GameFrame::updateLives);
                }
                break;
            case SLOW_GHOSTS:
                for (Ghosts ghost:ghosts){
                    ghost.speedDecrease();
                }
                break;
        }
    }
    public void checkForBonus(){
        Iterator<Bonus> iterator = bonuses.iterator();
        while (iterator.hasNext()) {
            Bonus bonus = iterator.next();
            if (bonus.getX() == pacMan.getX() && bonus.getY() == pacMan.getY()) {
                iterator.remove();
                bonusImpact(bonus);
            }
        }
    }
    void checkIfAllPointsCollected() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (mapArray[row][col] == '.') {
                    return;
                }
            }
        }
        restartGame();
    }

    private void restartGame() {
        mapArray = loadMapArray(filename);
        bonuses.clear();
        for(Ghosts ghost:ghosts){
            ghost.resetPosition();
        }
        redrawMap();
        revalidate();
        repaint();
        inGame=true;
    }
    void gameOver() {
        inGame=false;
        for (Ghosts ghost : ghosts) {
            ghost.movingThread.interrupt();
        }
        pacMan.disappearance();
        pacMan.stopSound();
        removeKeyListener(pacMan);
        revalidate();
        repaint();
    }

    public void showGameOver() {
        if (pacMan.animationFinished){
            gameOverPanel=new GameOver(score);
            gameOverPanel.setVisible(true);
            gamePanel.stopGame();
        }
    }
}
