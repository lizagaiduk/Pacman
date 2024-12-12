package PacMan;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;

public class PacMan implements KeyListener {
    private int x, y;
    private Map map;
    private int mouthState = 0;
    private int currentDirection = -1;
    public final Thread movingThread;
    private ImageIcon[] disappearing;
    private Clip deathClip;
    private int defaultDelay=200;
    public boolean lose=false;
    boolean animationFinished = false;
    Clip gameSound;


    private ImageIcon currentImage, pacUp, pacDown, pacLeft, pacRight, pacClosed,
            pacLessUp, pacLessDown, pacLessLeft, pacLessRight;

    public PacMan(Map map, int startX, int startY) {
        this.map=map;
        this.x = startX;
        this.y = startY;


        loadPacImages();
        currentImage = pacClosed;

        map.setPosition(startX, startY, currentImage);

        movingThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if(!lose){
                        if (currentDirection != -1) {
                            moveInCurrentDirection();
                            map.redrawMap();
                        }}
                    Thread.sleep(defaultDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        movingThread.start();
    }

    public ImageIcon getCurrentImage() {

        return currentImage;
    }
    public int getX() {

        return x;
    }

    public int getY() {
        return y;
    }

    private void loadPacImages() {
        try {
            pacUp = loadImage("src/images/openMouthUp.png");
            pacDown = loadImage("src/images/openMouthDown.png");
            pacLeft = loadImage("src/images/openMouthLeft.png");
            pacRight = loadImage("src/images/openMouthRight.png");
            pacClosed = loadImage("src/images/closedMouth.png");
            pacLessDown = loadImage("src/images/lessOpenMouthDown.png");
            pacLessUp = loadImage("src/images/lessOpenMouthUp.png");
            pacLessLeft = loadImage("src/images/lessOpenMouthLeft.png");
            pacLessRight = loadImage("src/images/lessOpenMouthRight.png");

            disappearing = new ImageIcon[]{
                    loadImage("src/images/closedMouth.png"),
                    loadImage("src/images/lessOpenMouth1.png"),
                    loadImage("src/images/openMouth1.png"),
                    loadImage("src/images/disappear1.png"),
                    loadImage("src/images/disappear2.png"),
                    loadImage("src/images/disappear3.png"),
                    loadImage("src/images/disappear4.png"),
                    loadImage("src/images/disappear5.png"),
                    loadImage("src/images/disappear6.png"),
                    loadImage("src/images/disappear7.png"),
                    loadImage("src/images/disappear8.png"),
            };
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading images: " + e.getMessage(), "Image Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private ImageIcon loadImage(String path) {
        return map.resizeIcon(new ImageIcon(path), Map.cellSize - 6, Map.cellSize - 6);
    }

    private void moveInCurrentDirection() {
        int newX = x, newY = y;
        ImageIcon nextImage = pacClosed;
        switch (mouthState) {
            case 0 -> mouthState = 1;
            case 1 -> mouthState = 2;
            case 2 -> mouthState = 3;
            case 3 -> mouthState = 0;
        }

        switch (currentDirection) {
            case KeyEvent.VK_UP:
                newY--;
                nextImage = mouthState == 1 ? pacLessUp : (mouthState == 2 ? pacUp : (mouthState == 3 ? pacLessUp : pacClosed));
                break;
            case KeyEvent.VK_DOWN:
                newY++;
                nextImage = mouthState == 1 ? pacLessDown : (mouthState == 2 ? pacDown : (mouthState == 3 ? pacLessDown : pacClosed));
                break;
            case KeyEvent.VK_LEFT:
                newX--;
                nextImage = mouthState == 1 ? pacLessLeft : (mouthState == 2 ? pacLeft : (mouthState == 3 ? pacLessLeft : pacClosed));
                break;
            case KeyEvent.VK_RIGHT:
                newX++;
                nextImage = mouthState == 1 ? pacLessRight : (mouthState == 2 ? pacRight : (mouthState == 3 ? pacLessRight : pacClosed));

                break;

        }

        if (map.isValidPosition(newX, newY)) {
            map.checkIfAllPointsCollected();
            if (map.mapArray[newY][newX] == '.') {
                map.incrementScore();
                playGameSound();
                map.mapArray[newY][newX] = ' ';
            }
            map.setPosition(newX, newY, nextImage);
            x = newX;
            y = newY;
            map.checkForBonus();
            currentImage = nextImage;
        }
    }

    public void disappearance() {
        lose = true;
        movingThread.interrupt();

        new Thread(() -> {
            playSound();
            try {
                for (ImageIcon icon : disappearing) {
                    SwingUtilities.invokeLater(() -> map.setPosition(x, y, icon));
                    Thread.sleep(150);
                }
                SwingUtilities.invokeLater(() -> {
                    map.setPosition(x, y, null);
                    animationFinished=true;
                    map.showGameOver();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }



    private void playSound() {
        try {
            File musicPath = new File("src/sounds/pacmanDeath.wav");
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                deathClip = AudioSystem.getClip();
                deathClip.open(audioInput);
                deathClip.start();
            } else {
                System.out.println("Cannot find the music file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playGameSound() {
        try {
            File soundFile = new File("src/sounds/gameSound.wav");
            if (soundFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile);
                gameSound = AudioSystem.getClip();
                gameSound.open(audioInput);
                gameSound.start();
            } else {
                System.out.println("Cannot find the game sound file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stopSound() {
        if (deathClip != null && deathClip.isRunning()) {
            deathClip.stop();
            deathClip.close();
        }
    }

    public void increaseSpeed() {
        int origDelay=defaultDelay;
        defaultDelay=100;
        new Thread(()->{
            try{
                Thread.sleep(5000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            defaultDelay=origDelay;
        }).start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||
                e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentDirection = e.getKeyCode();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }


}
