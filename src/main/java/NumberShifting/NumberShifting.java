package NumberShifting;

import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Text;

import java.util.ArrayList;
import java.util.Random;

public class NumberShifting {
    private int width;
    private int height;
    private int level;
    private int[][] grid;
    private static long seed = 1; // seed is different in online version
    private static String[] levelPasswords = new String[1000];

    static {
        Random random = new Random(seed);
        for (int i = 0; i < 1000; i++) {
            String pass = "";
            for (int x = 0; x < 32; x++) {
                pass += (char) ('a' + random.nextInt(26));
            }
            levelPasswords[i] = pass;
        }
        levelPasswords[0] = "first_level";
    }

    int[] dx = {0, 1, 0, -1};
    int[] dy = {1, 0, -1, 0};
    String[] dirs = {"D", "R", "U", "L"};

    public NumberShifting(String pass) {
        while (!levelPasswords[level].equals(pass)) level++;
        createMap();
    }

    public NumberShifting(int level) {
        this.level = level;
        createMap();
    }

    private void createMap() {
        this.height = 5 + (int) (0.2 * Math.sqrt(level));
        this.width = this.height * 16 / 9;
        this.grid = new int[width][height];

        Random random = new Random(seed ^ level);

        int spawns = (int) Math.pow(width, 0.5 + (1 - Math.exp(-0.05 * level)));
        if (spawns < 3) spawns = 3;
        ArrayList<String> solution = new ArrayList<>();
        for (int i = 0; i < spawns; i++) {
            if (i == 0 || random.nextInt(5) == 0) {
                // find pair of empty cells
                while (true) {
                    int x1 = random.nextInt(width);
                    int y1 = random.nextInt(height);
                    int dir = random.nextInt(4);
                    int length = 1 + random.nextInt(width);
                    int x2 = x1 - length * dx[dir];
                    int y2 = y1 - length * dy[dir];
                    if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height && grid[x1][y1] == 0 && grid[x2][y2] == 0) {
                        grid[x1][y1] = length;
                        grid[x2][y2] = length;
                        solution.add(x2 + " " + y2 + " " + dirs[dir] + " -");
                        break;
                    }
                }
            } else {
                // split number
                while (true) {
                    int x1 = random.nextInt(width);
                    int y1 = random.nextInt(height);
                    int dir = random.nextInt(4);
                    int length = 1 + random.nextInt(width);
                    int x2 = x1 - length * dx[dir];
                    int y2 = y1 - length * dy[dir];
                    boolean add = random.nextBoolean();
                    if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height && grid[x1][y1] != 0 && grid[x1][y1] != length && grid[x2][y2] == 0) {
                        grid[x2][y2] = length;
                        if (add) grid[x1][y1] -= length;
                        else grid[x1][y1] += length;
                        if (grid[x1][y1] < 0) {
                            grid[x1][y1] = -grid[x1][y1];
                            add = !add;
                        }
                        if (add) solution.add(x2 + " " + y2 + " " + dirs[dir] + " +");
                        else solution.add(x2 + " " + y2 + " " + dirs[dir] + " -");
                        break;
                    }
                }
            }
        }
        System.err.println("level: " + level);
        System.err.println(levelPasswords[level]);
        for (String s : exportMap())
            System.err.println(s);
        for (int i = solution.size() - 1; i >= 0; i--) {
            System.err.println(solution.get(i));
        }
    }

    public void apply(String action) {
        String[] parts = action.split(" ");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int dir = "DRUL".indexOf(parts[2]);
        boolean add = parts[3].equals("+");
        int x2 = x + dx[dir] * grid[x][y];
        int y2 = y + dy[dir] * grid[x][y];
        if (grid[x][y] == 0 || grid[x2][y2] == 0) throw new IllegalArgumentException("source or target cell empty");
        if (add) grid[x2][y2] += grid[x][y];
        else grid[x2][y2] -= grid[x][y];
        grid[x][y] = 0;
        grid[x2][y2] = Math.abs(grid[x2][y2]);

        gridNumbers[x][y].setZIndex(2);
        gridNumbers[x2][y2].setZIndex(1);
        int w = (int) (cellSize / 12);
        Group group = graphics.createGroup().setZIndex(2).setX((int) cellSize - 3 * w).setY(-w);
        group.add(graphics.createRectangle().setWidth(4 * w).setHeight(4 * w).setFillColor(0xbbddbb).setLineColor(0).setLineWidth(2));
        group.add(graphics.createText(parts[3]).setAnchor(0.5).setX(2 * w).setY(2 * w).setFontSize(8 * w / 3));
        gridNumbers[x2][y2].add(group);
        graphics.commitWorldState(0);

        gridNumbers[x][y].setX(gridNumbers[x2][y2].getX(), Curve.EASE_IN_AND_OUT).setY(gridNumbers[x2][y2].getY(), Curve.EASE_IN_AND_OUT).setAlpha(0);
        gridTexts[x2][y2].setText(String.valueOf(grid[x2][y2]));
        gridNumbers[x2][y2].setZIndex(0);
        if (grid[x2][y2] == 0) gridNumbers[x2][y2].setAlpha(0);
        graphics.commitEntityState(0.8, group);
        group.setAlpha(0);
    }

    public int getLevel() {
        return level;
    }

    public String nextLevel() {
        return levelPasswords[level + 1];
    }

    public boolean solved() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] != 0) return false;
            }
        }
        return true;
    }

    public ArrayList<String> exportMap() {
        ArrayList<String> result = new ArrayList<>();
        result.add(width + " " + height);
        for (int y = 0; y < height; y++) {
            String line = "";
            for (int x = 0; x < width; x++) {
                line += grid[x][y] + " ";
            }
            result.add(line.trim());
        }
        return result;
    }

    private static GraphicEntityModule graphics;
    private static Group[][] gridNumbers;
    private static Text[][] gridTexts;
    private static double cellSize;

    public void drawBoard(GraphicEntityModule graphics) {
        this.graphics = graphics;
        gridNumbers = new Group[width][height];
        gridTexts = new Text[width][height];
        cellSize = 1040.0 / height;
        Group group = graphics.createGroup().setX((1920 - (int) (cellSize * width)) / 2).setY(20);
        group.add(graphics.createRectangle().setFillColor(0xbbbbdd).setWidth((int) (cellSize * width)).setHeight(1040));
        for (int x = 0; x <= width; x++) {
            group.add(graphics.createLine().setX((int) (x * cellSize)).setX2((int) (x * cellSize)).setY(0).setY2((int) (height * cellSize)).setFillColor(0xaaaac0).setLineWidth(1));
        }
        for (int y = 0; y <= height; y++) {
            group.add(graphics.createLine().setY((int) (y * cellSize)).setY2((int) (y * cellSize)).setX(0).setX2((int) (width * cellSize)).setFillColor(0xaaaac0).setLineWidth(1));
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] == 0) continue;
                gridNumbers[x][y] = graphics.createGroup().setX((int) (x * cellSize)).setY((int) (y * cellSize));
                group.add(gridNumbers[x][y]);
                gridNumbers[x][y].add(graphics.createRectangle().setWidth((int) cellSize).setHeight((int) cellSize).setFillColor(0xddddff).setLineWidth(5).setLineColor(0x1d1d2d));
                gridTexts[x][y] = graphics.createText(String.valueOf(grid[x][y])).setX((int) (cellSize / 2)).setY((int) (cellSize / 2)).setAnchor(0.5).setFontSize((int) (cellSize * 2 / 3));
                gridNumbers[x][y].add(gridTexts[x][y]);
            }
        }
    }
}
