package PacMan;

import java.util.HashMap;
import java.util.Map;

public class StartPositions {
    public static final Map<String, int[]> pacManStartPositions = new HashMap<>();
    public static final Map<String, int[][]> ghostsStartPositions = new HashMap<>();

    static {
        pacManStartPositions.put("src/maps/map1.txt", new int[]{9, 8});
        pacManStartPositions.put("src/maps/map2.txt", new int[]{10, 8});
        pacManStartPositions.put("src/maps/map3.txt", new int[]{13, 8});
        pacManStartPositions.put("src/maps/map4.txt", new int[]{16, 8});
        pacManStartPositions.put("src/maps/map5.txt", new int[]{21, 8});

        ghostsStartPositions.put("src/maps/map1.txt", new int[][]{{9, 5}, {9, 6}, {8, 6}, {10, 6}});
        ghostsStartPositions.put("src/maps/map2.txt", new int[][]{{10, 5}, {10, 6}, {11, 6}, {9, 6}});
        ghostsStartPositions.put("src/maps/map3.txt", new int[][]{{13, 5}, {14, 6}, {13,6}, {12, 6}});
        ghostsStartPositions.put("src/maps/map4.txt", new int[][]{{16, 5}, {15, 6}, {16, 6}, {17, 6}});
        ghostsStartPositions.put("src/maps/map5.txt", new int[][]{{21, 5}, {20, 6}, {21, 6}, {22, 6}});
    }
}
