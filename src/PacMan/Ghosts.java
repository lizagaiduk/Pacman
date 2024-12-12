package PacMan;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Ghosts {
    private ImageIcon ghostIcon;
    private Map map;
    private int x, y,startX,startY;
    public Thread movingThread;
    private GhostType type;
    private int defaultDelay=300;
    private boolean isFrozen = false;
    private int lastDirection = -1;
    public Ghosts(Map map, int startX, int startY, GhostType type, int delay) {
        this.map = map;
        this.x = startX;
        this.y = startY;
        this.startX=startX;
        this.startY=startY;
        this.type = type;
        loadGhostImage();
        map.setPosition(x, y, ghostIcon);
        movingThread = new Thread(() -> {
            try {
                Thread.sleep(delay * 1000);
                while (!Thread.currentThread().isInterrupted()) {
                    if(!isFrozen) {
                        moveRandomly();
                        map.redrawMap();
                    }
                    Thread.sleep(defaultDelay);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        movingThread.start();
    }



    private void loadGhostImage() {
            switch (type) {
                case BLINKY:
                    ghostIcon = map.resizeIcon(new ImageIcon("src/images/blinky_right.png"), Map.cellSize - 5, Map.cellSize - 5);
                    break;
                case PINKY:
                    ghostIcon = map.resizeIcon(new ImageIcon("src/images/pinky_right.png"), Map.cellSize - 5, Map.cellSize - 5);
                    break;
                case INKY:
                    ghostIcon = map.resizeIcon(new ImageIcon("src/images/inky_right.png"), Map.cellSize - 5, Map.cellSize - 5);
                    break;
                case CLYDE:
                    ghostIcon = map.resizeIcon(new ImageIcon("src/images/clyde_right.png"), Map.cellSize - 5, Map.cellSize - 5);
                    break;
            }
        }


    private void moveRandomly() {
        List<Integer> possibleDirections = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        int oppositeDirection = getOppositeDirection(lastDirection);
        possibleDirections.remove(Integer.valueOf(oppositeDirection));
        Collections.shuffle(possibleDirections);

        boolean moved = false;
        for (int direction : possibleDirections) {
            if (tryToMove(direction)) {
                moved = true;
                break;
            }
        }
        if (!moved) {
            tryToMove(oppositeDirection);
        }

    }

    private boolean tryToMove(int direction) {
        int newX = x, newY = y;
        switch (direction) {
            case 0 -> newY--;
            case 1 -> newY++;
            case 2 -> newX--;
            case 3 -> newX++;
        }

        if (map.isValidPosition(newX, newY)) {
            map.setPosition(newX, newY, ghostIcon);
            x = newX;
            y = newY;
            lastDirection = direction;

            return true;
        }
        return false;
    }

    private int getOppositeDirection(int lastDirection) {
        return switch (lastDirection) {
            case 0 -> 1;
            case 1 -> 0;
            case 2 -> 3;
            case 3 -> 2;
            default -> -1;
        };
    }

    public int getX() {
        return x;
    }

    public int getY() {

        return y;
    }

    public void freeze() {
        isFrozen=true;
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                isFrozen=false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void speedDecrease() {
        int origDelay = defaultDelay;
        defaultDelay = 500;

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                defaultDelay = origDelay;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    public Icon getGhostIcon() {
        return ghostIcon;
    }

    public void resetPosition() {
        this.x=startX;
        this.y=startY;
        map.setPosition(x,y,ghostIcon);
    }
}
