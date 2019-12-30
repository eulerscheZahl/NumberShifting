package NumberShifting;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Text;

import java.util.ArrayList;
import java.util.Random;

public class NumberShifting {
    private int width;
    private int height;
    private int level;
    private int[][] grid;
    private static long seed = 0; // seed is different in online version
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
    }

    int[] dx = {0, 1, 0, -1};
    int[] dy = {1, 0, -1, 0};
    String[] dirs = {"D", "R", "U", "L"};
    public NumberShifting(String pass) {
        if (pass.equals("")) level++;
        else {
            while (!levelPasswords[level].equals(pass)) level++;
        }
        this.width = 5 + (int) Math.sqrt(level);
        this.height = 5 + (int) Math.sqrt(level);
        this.grid = new int[width][height];

        Random random = new Random(seed);
        for (int i = 0; i < level; i++) random.nextInt();

        int spawns = (int) Math.pow(width, 1.5);
        if (level < 10) spawns = width;
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
                    if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height && grid[x1][y1] != 0 && grid[x2][y2] == 0) {
                        grid[x2][y2] = length;
                        if (add) grid[x1][y1] -= length;
                        else grid[x1][y1] += length;
                        grid[x1][y1] = Math.abs(grid[x1][y1]);
                        solution.add(x2 + " " + y2 + " " + dirs[dir] + " " + (add ? "+" : "-"));
                        break;
                    }
                }
            }
        }
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

        gridText[x][y].setX(gridText[x2][y2].getX()).setY(gridText[x2][y2].getY()).setAlpha(0);
        gridText[x2][y2].setText(String.valueOf(grid[x2][y2]));
        if (grid[x2][y2] == 0) gridText[x2][y2].setAlpha(0);
    }

    public int getLevel() {
        return level + 1;
    }

    public String nextLevel(){
        return levelPasswords[level+1];
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

    private static Text[][] gridText;
    public void drawBoard(GraphicEntityModule graphics) {
        gridText = new Text[width][height];
        graphics.createRectangle().setFillColor(0xffffff).setWidth(1920).setHeight(1080);
        int cellSize = (int) Math.min(1920.0 / width, 1080.0 / height);
        for (int x = 0; x <= width; x++) {
            graphics.createLine().setX(x * cellSize).setX2(x * cellSize).setY(0).setY2(height * cellSize).setFillColor(0).setLineWidth(1);
        }
        for (int y = 0; y <= height; y++) {
            graphics.createLine().setY(y * cellSize).setY2(y * cellSize).setX(0).setX2(width * cellSize).setFillColor(0).setLineWidth(1);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] == 0) continue;
                gridText[x][y] = graphics.createText(String.valueOf(grid[x][y])).setX(x * cellSize + cellSize / 2).setY(y * cellSize + cellSize / 2).setAnchor(0.5).setFontSize(cellSize);
            }
        }
    }
}
