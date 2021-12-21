package pl.fyko.sudoku;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Filip Kołodziejczyk
 */
class Grid {
    private Integer[][] result = {
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0}
    };
    private Integer[][] grid = {
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0}
    };

    public static void main(String[] args) {

        Grid grid = new Grid();
        grid.generate();
        grid.printGrid();

    }

    void generateFirstBlock() {
        List<Integer> block = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9));
        Collections.shuffle(block);
        for (int i = 0; i < block.size(); i++) {
            grid[i/3][i%3] = block.get(i);
        }
    }

    void generateFirstRow() {
        rows: for (int i = 0; i < 3; i++) {
            for (int j = 3; j < 9; j++) {
                List<Integer> numbers = new ArrayList<>(List.of(validNumbersForPoint(i, j)));
                if (numbers.size() == 0) {
                    // clear new blocks and restart loops
                    clearBlock(0, 1);
                    clearBlock(0, 2);
                    i = -1;
                    continue rows;
                }
                Collections.shuffle(numbers);
                grid[i][j] = numbers.get(0);
            }
        }
    }

    void generateColumns() {
        rows: for (int i = 3; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                List<Integer> numbers = new ArrayList<>(List.of(validNumbersForPoint(i, j)));
                if (numbers.size() == 0) {
                    // clear new blocks and restart loops
                    clearBlock(1, 0);
                    clearBlock(1, 1);
                    clearBlock(1, 2);
                    clearBlock(2, 0);
                    clearBlock(2, 1);
                    clearBlock(2, 2);
                    i = 2;
                    continue rows;
                }
                Collections.shuffle(numbers);
                grid[i][j] = numbers.get(0);
            }
        }
    }

    void clearBlock(int x, int y) {
        int startX = x * 3;
        int startY = y * 3;

        for (int i = startX; i < startX + 3; i++) {
            for (int j = startY; j < startY + 3; j++) {
                grid[i][j] = 0;
            }
        }
    }

    void generate() {
        long start = System.currentTimeMillis();
        // generate a complete solution
        generateFirstBlock();
        generateFirstRow();
        generateColumns();
        // store generated grid as result
        result = grid;
        // remove random numbers from grid
        removeDigits(10, 64);
        // verify that sudoku has exactly one solution
        System.out.println("Generation time: " + (System.currentTimeMillis() - start) + "ms");
    }

    void removeDigits(int from, int to) {
        // different ranges may result in different difficulties
        Random random = new Random();
        int toDeleteCount = random.nextInt(to - from) + (from + 1);
        System.out.println("Removing " + toDeleteCount + " digits");
        for (int i = 0; i < toDeleteCount; i++) {
            int toDelete = random.nextInt(80) + 1;
            int x = toDelete / 9;
            int y = 8 - (toDelete % 9);
            if (grid[x][y] == 0) {
                i--;
            } else {
                grid[x][y] = 0;
            }
        }
    }

    void printGrid() {
        System.out.println(".---.---.---.---.---.---.---.---.---.");
        for (int i = 0; i < grid.length; i++) {
            printRow(grid[i]);
            printVLine(i);
        }
        System.out.println("'---'---'---'---'---'---'---'---'---'");

    }

    private void printRow(Integer[] row) {
        System.out.println(String.format("| %d : %d : %d | %d : %d : %d | %d : %d : %d |", row)
                .replace("0", " "));
    }

    private void printVLine(int row) {
        row += 1;
        if (row >= grid.length) return;
        if (row % 3 == 0) {
            System.out.println("|---+---+---|---+---+---|---+---+---|");
        } else {
            System.out.println("|---:---:---|---:---:---|---:---:---|");
        }
    }

    Integer[] getBlock(Point p) {
        return getBlock(p.x, p.y);
    }

    Integer[] getBlock(int x, int y) {
        Integer[] block = new Integer[grid.length];
        int l = 0;

        int startX = x * 3;
        int startY = y * 3;

        for (int i = startX; i < startX + 3; i++) {
            for (int j = startY; j < startY + 3; j++) {
                block[l] = grid[i][j];
                l++;
            }
        }

        return block;
    }

    Point getBlockCoords(int x, int y) {
        return new Point(x / 3, y / 3);
    }

    Integer[] getRow(int row) {
        return grid[row];
    }

    Integer[] getColumn(int column) {

        Integer[] block = new Integer[grid.length];
        for (int i = 0; i < grid.length; i++) {
            block[i] = grid[i][column];
        }
        return block;

    }

    boolean arrayContains(Integer[] array, int value) {
        return Arrays.stream(array).anyMatch(v -> v == value && v != 0);
    }

    Integer[] validNumbersForPoint(int x, int y) {
        Integer[] result = {1,2,3,4,5,6,7,8,9};
        Set<Integer> set = new HashSet<>(Arrays.asList(result));
        List.of(getBlock(getBlockCoords(x, y))).forEach(set::remove);
        List.of(getRow(x)).forEach(set::remove);
        List.of(getColumn(y)).forEach(set::remove);
        return set.toArray(new Integer[0]);
    }

    boolean isValid(int x, int y, int value) {
        int temp = grid[x][y];
        grid[x][y] = 0;
        boolean result = isValidOnPoint(x, y, value);
        grid[x][y] = temp;
        return result;
    }

    boolean isGridValid() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!isValid(i, j, grid[i][j])) return false;
            }
        }
        return true;
    }

    private boolean isValidOnPoint(int x, int y, int value) {
        return !arrayContains(getBlock(getBlockCoords(x, y)), value)
                && !arrayContains(getRow(x), value)
                && !arrayContains(getColumn(y), value);
    }
}
