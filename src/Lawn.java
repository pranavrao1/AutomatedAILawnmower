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
                grid_observed[i][j] = square_type;
            }
        }
    }

    public int [][] getGrid() {
        return grid;
    }

    public int [] getSurroundingSquares(int x, int y) {
        int [] scan_result = new int[8];
        for (int i = 0; i < 8; i++) {
            scan_result[i] = grid[x + xDIR_MAP.get(c.DIRECTIONS[i])][y + yDIR_MAP.get(c.DIRECTIONS[i])];
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
        int a2check = new Array[]{c.MOWER_CODE, c.EMPTY_CODE, c.PUPPY_EMPTY_CODE, c.PUPPY_MOWER_CODE};
        List<int> list = Arrays.asList(a2check);
        for (int i =0; i< width; i++){
            for (int j =0; j< height; j++){
                if (list.contains(grid_observed[i][j])){
                    m++;
                }
            }
        }
        return m;
    }

}
