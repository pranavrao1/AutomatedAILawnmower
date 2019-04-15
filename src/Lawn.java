import java.util.Arrays;
import java.util.List;

public class Lawn {
    private int height;
    private int width;
    private int [][] grid;
    private Constants c = new Constants();

    public Lawn(int w, int h, int square_type) {
        width = w;
        height = h;
        grid = new int[width][height];

        for (int i =0; i< width; i++){
            for (int j =0; j< height; j++){
                grid[i][j] = square_type;
            }
        }
    }

    public int [][] getGrid() {
        return grid;
    }

    public int [] getSurroundingSquares(int x, int y) {
        int [] scan_result = new int[8];
        for (int i = 0; i < 8; i++) {
            scan_result[i] = grid[x + c.xDIR_MAP.get(c.DIRECTIONS[i])][y + c.yDIR_MAP.get(c.DIRECTIONS[i])];
        }
        return scan_result;
    }

    public String [] getSurroundingSquaresString(int x, int y) {
        int [] scan_result = getSurroundingSquares(x, y);
        String [] str_scan_result = new String[8];
        for (int i = 0; i < 8; i++) {
            str_scan_result[i] = c.SQUARES[scan_result[i]];
        }
        return str_scan_result;
    }

    public int updateGrid(int x, int y, int square_type) {
        int old_type = grid[x][y];
        grid[x][y] = square_type;
        return old_type;
    }

    public int getSquare_type(int x, int y) {
        return grid[x][y];
    }

    public int getMowedSquares(){
        int m = 0;
        int [] a2check = new int[]{c.MOWER_CODE, c.EMPTY_CODE, c.PUPPY_EMPTY_CODE, c.PUPPY_MOWER_CODE};
        for (int i =0; i< width; i++){
            for (int j =0; j< height; j++){
                if (grid[i][j] == c.MOWER_CODE || grid[i][j] == c.EMPTY_CODE|| grid[i][j] == c.PUPPY_EMPTY_CODE || grid[i][j] == c.PUPPY_MOWER_CODE){
                    m++;
                }
            }
        }
        return m;
    }

}
