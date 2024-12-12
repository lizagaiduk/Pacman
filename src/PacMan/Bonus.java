package PacMan;

import javax.swing.*;
import java.util.List;
import java.util.Random;

public class Bonus {
    Map map;
    private static ImageIcon strawberry,cherry,apple,orange,melon;

    public enum BonusType{
        FREEZE,SPEED_BOOST,EXTRA_POINTS,SLOW_GHOSTS,EXTRA_LIFE
    }
    private BonusType type;
    private int x,y;
    public Bonus(BonusType type,int x,int y,Map map){
        this.map=map;
        this.type=type;
        this.x=x;
        this.y=y;
        loadBonusIcons();
    }
    public static Bonus createBonus(int rows, int cols, List<Bonus> bonuses, Map map) {
       Random rand=new Random();
        if(rand.nextInt(100)<25){
            int x,y;
            do{
                x= rand.nextInt(cols);
                y= rand.nextInt(rows);
            }while (!isValidPosition(x,y,map));
            BonusType type=BonusType.values()[rand.nextInt(BonusType.values().length)];
            Bonus bonus =new Bonus(type,x,y,map);
            bonuses.add(bonus);
            map.mapArray[y][x]=' ';
            map.setPosition(x,y,getBonusIcon(type));
            return bonus;
        }
        return null;
    }

    private static boolean isValidPosition(int x, int y, Map map) {
        return map.mapArray[y][x]==' '||map.mapArray[y][x]=='.';
    }

    public void loadBonusIcons(){
        try {
            strawberry = map.resizeIcon(new ImageIcon("src/images/strawberry.png"), Map.cellSize - 5, Map.cellSize - 5);
            cherry = map.resizeIcon(new ImageIcon("src/images/cherry.png"), Map.cellSize - 5, Map.cellSize - 5);
            apple = map.resizeIcon(new ImageIcon("src/images/apple.png"), Map.cellSize - 5, Map.cellSize - 5);
            orange = map.resizeIcon(new ImageIcon("src/images/orange.png"), Map.cellSize - 5, Map.cellSize - 5);
            melon = map.resizeIcon(new ImageIcon("src/images/melon.png"), Map.cellSize - 5, Map.cellSize - 5);
        }catch (Exception e2){
            JOptionPane.showMessageDialog(null, "Failed to load bonus icons: " + e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    static ImageIcon getBonusIcon(BonusType type) {
        switch (type){
            case SPEED_BOOST :
                return strawberry;
            case SLOW_GHOSTS:
                return cherry;
            case EXTRA_POINTS:
                return apple;
            case FREEZE:
                return orange;
            case EXTRA_LIFE:
                return melon;
            default:
                return null;
        }
    }
    public BonusType getType(){
        return type;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
