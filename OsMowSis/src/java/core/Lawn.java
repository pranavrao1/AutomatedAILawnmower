package core;

public class Lawn {
    private int height;
    private int width;
    private int [][] grid;
    private Constants c = new Constants();
    private boolean debug_print = false;
    
    public Lawn(int w, int h, int square_type, boolean debug) {
        width = w;
        height = h;
        grid = new int[width][height];

        for (int i =0; i< width; i++){
            for (int j =0; j< height; j++){
                grid[i][j] = square_type;
            }
        }
        debug_print=false;
    }

    public int [][] getGrid() {
        return grid;
    }
    public int getHeight() { return height; }
    public int getWidth() { return width; }

    public int [] getSurroundingSquares(int x, int y) {
        int [] scan_result = new int[8];
        for (int i = 0; i < 8; i++) {
            int x_axis = x + c.xDIR_MAP.get(c.DIRECTIONS[i]);
            int y_axis = y + c.yDIR_MAP.get(c.DIRECTIONS[i]);
            if (x_axis < 0 || x_axis >= width || y_axis < 0 || y_axis >= height)
                scan_result[i] = c.FENCE_CODE;
            else
                scan_result[i] = grid[x_axis][y_axis];
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
        if (debug_print)
            System.out.print("old_type: "+ c.SQUARES[old_type] +", new_type: " + c.SQUARES[square_type]);
        grid[x][y] = square_type;
        return old_type;
    }

    public int getSquareType(int x, int y) {
//        System.out.println("getSquareType: x: "+x+", y: "+y);
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
    
    public String renderLawnForUI() {
        
        int i, j;
        int charWidth = 2 * width + 2;
        
        StringBuilder sb = new StringBuilder();
    	
        for (j = height - 1; j >= 0; j--) {  
            // display the contents of each square on this row
            for (i = 0; i < width; i++) {
                sb.append("|");
                
                if(grid[i][j] == c.EMPTY_CODE) {
                    sb.append("  ");
                }
                else if(grid[i][j] == c.GRASS_CODE) {
                    sb.append(" g");
                }
                else if(grid[i][j] == c.CRATER_CODE) {
                    sb.append(" c");
                }
                else if(grid[i][j] == c.MOWER_CODE) {
                    sb.append(" m");
                }
                else if(grid[i][j] == c.PUPPY_EMPTY_CODE) {
                    sb.append("p ");
                }
                else if(grid[i][j] == c.PUPPY_MOWER_CODE) {
                    sb.append("pm");
                }
                else if(grid[i][j] == c.PUPPY_GRASS_CODE) {
                    sb.append("pg");
                }
                else if(grid[i][j] == c.FENCE_CODE) {
                    sb.append(" f");
                }
                else if(grid[i][j] == c.UNKNOWN_CODE) {
                    sb.append("??");
                }
            }
            sb.append("|");
            sb.append("\n");
        }
        return sb.toString();
    }
}