import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Mower extends LawnmowerShared {
  private static Random randGenerator;
  public String direction;
  private String mowerAction;
  public int mowerState;
  public int mowerX, mowerY;
  private Integer trackMoveDistance;
  private String trackNewDirection;
  private Constants c = new Constants();
  public int turns_stalled;
  public int mower_id;
  private Move nextMove;

  public Mower(int locX, int locY, String dir, int id) {
    randGenerator = new Random();
    mower_id = id;
    mowerX = locX;
    mowerY = locY;
    direction = dir;
    mowerState = c.MOWER_ACTIVE;
    turns_stalled = 0;
    mowerAction = "move";
    trackMoveDistance = 0;
    trackNewDirection = dir;
    if(grid_observed == null){
      grid_observed = new Lawn(c.DEFAULT_WIDTH,c.DEFAULT_HEIGHT,c.UNKNOWN_CODE);
    }
  }

  private boolean nearbysquare(int code){
    int [] local_map_scan = grid_observed.getSurroundingSquares(mowerX, mowerY);
    for (int i : local_map_scan)
    {
      if (i == code){
        return true;
      }
    }
    return false;
  }

  private String find_rand_direction(int code){
    int [] local_map_scan = grid_observed.getSurroundingSquares(mowerX, mowerY);
    int [] all_values = new int[8];
    int point = 0;
    for(int i =0; i< 8; i++){
      if (local_map_scan[i] == code){
        all_values[point] = i;
        point++;
      }
    }
    return c.DIRECTIONS[all_values[randGenerator.nextInt(point)]];
  }

  //TODO: Implement this method.
  public String getNextAction() {
    // should I turn off?
    // check if surrounding square is unknown
    if (nearbysquare(c.UNKNOWN_CODE)) {
      mowerAction = "scan";
      return mowerAction;
    }

    // move
    mowerAction = "move";
    // move in the direction if possible.
    // check if you can move 2 in current direction (Grass and empty are okay).
    int max_move = 0;
    int [][] knowledgeMap = grid_observed.getGrid();
    int xOrientation = c.xDIR_MAP.get(direction);
    int yOrientation = c.yDIR_MAP.get(direction);
    int lmowerX = mowerX;
    int lmowerY = mowerY;
    for (max_move = 0; max_move < 2; max_move++){
      int value =knowledgeMap[mowerX+max_move*xOrientation][mowerY+max_move*yOrientation];
      if(value == c.CRATER_CODE || value == c.FENCE_CODE || value == c.UNKNOWN_CODE) {
        break;
      }
    }
    trackMoveDistance = max_move-1;
    //System.out.println(trackMoveDistance);


    /*
    for (max_move = 0; max_move <= trackMoveDistance; max_move++) {
      knowledgeMap[mowerX+max_move*xOrientation][mowerY+max_move*yOrientation] = c.EMPTY_CODE;
    }*/
    lmowerX += trackMoveDistance * xOrientation;
    lmowerY += trackMoveDistance * yOrientation;

    // direction:
    // check if around grass, if so, randomly pick grass
    if (nearbysquare(c.GRASS_CODE)) {
      trackNewDirection = find_rand_direction(c.GRASS_CODE);
    }
    // else randomly pick an empty space.
    else if(nearbysquare(c.EMPTY_CODE)){
      // find a way to get to green or Unknown square
      int posx, posy, posx1, posy1;
      int[] select = new int[8];
      int point = 0;
      for(int i =0; i< 8; i++){
        posx = lmowerX + 2 * c.xDIR_MAP.get(c.DIRECTIONS[i]);
        posy = lmowerY + 2 * c.yDIR_MAP.get(c.DIRECTIONS[i]);
        posx1 = lmowerX + c.xDIR_MAP.get(c.DIRECTIONS[i]);
        posy1 = lmowerY + c.yDIR_MAP.get(c.DIRECTIONS[i]);
        if ((knowledgeMap[posx][posy] == c.UNKNOWN_CODE || knowledgeMap[posx][posy] == c.GRASS_CODE) & knowledgeMap[posx1][posy1] == c.EMPTY_CODE){
          select[point] = i;
          point++;
        }
      }
      if (point == 0){
        trackNewDirection = find_rand_direction(c.EMPTY_CODE);
      }else{
        trackNewDirection = c.DIRECTIONS[select[randGenerator.nextInt(point)]];
      }
    }
    nextMove = new Move(trackNewDirection, trackMoveDistance);
    return mowerAction;
  }

  /**
   * This methods returns the value for the next move as determined by the mower logic.
   * @return
   */
  public Move getNextMove() {
    return nextMove;
  }

  /**
   * This method is run to update the mower when a move is completed.
   * @param x mower X grid value.
   * @param y mower y grid value.
   * @param dir String direction value.
   * @param mowerStatus integer for mower state.
   */
  public void finishMove(Move m, int mowerStatus, int stalled_turns) { //int x, int y, String dir,
    grid_observed.updateGrid(mowerX, mowerY, c.EMPTY_CODE);
    this.mowerState = mowerStatus;
    this.turns_stalled = stalled_turns;

    if (mowerStatus != c.MOWER_CRASHED){
      int xOrientation = c.xDIR_MAP.get(direction);
      int yOrientation = c.yDIR_MAP.get(direction);
      for (int step = 0; step <= m.getStep(); step++) {
        grid_observed.updateGrid(mowerX+step*xOrientation, mowerY+step*yOrientation, c.EMPTY_CODE);
      }
      int step = m.getStep();
      int x = mowerX+step*xOrientation;
      int y = mowerY+step*yOrientation;
      grid_observed.updateGrid(x, y, c.MOWER_CODE);
      this.mowerX = x;
      this.mowerY = y;
      if (m.getDirection() != "unknown")
        this.direction = m.getDirection();
    }
  }

  /**
   * This method will update the scan results for the map.
   * It will assign the values for the map starting with North Direction and moving clockwise.
   * @param values
   */
  public void provideScanResult(Integer[] values) {
    if (values == null || values.length != 8) {
      throw new IllegalArgumentException( "The method 'provideScanResult' needs 8 values and incorrect number were provided " + values);
    }
    grid_observed.updateGrid(mowerX + c.xDIR_MAP.get("north"), mowerY + c.yDIR_MAP.get("north"), values[0]);
    grid_observed.updateGrid(mowerX + c.xDIR_MAP.get("northeast"), mowerY + c.yDIR_MAP.get("northeast"), values[1]);
    grid_observed.updateGrid(mowerX + c.xDIR_MAP.get("east"), mowerY + c.yDIR_MAP.get("east"), values[2]);
    grid_observed.updateGrid(mowerX + c.xDIR_MAP.get("southeast"), mowerY + c.yDIR_MAP.get("southeast"), values[3]);
    grid_observed.updateGrid(mowerX + c.xDIR_MAP.get("south"), mowerY + c.yDIR_MAP.get("south"), values[4]);
    grid_observed.updateGrid(mowerX + c.xDIR_MAP.get("southwest"), mowerY + c.yDIR_MAP.get("southwest"), values[5]);
    grid_observed.updateGrid(mowerX + c.xDIR_MAP.get("west"), mowerY + c.yDIR_MAP.get("west"), values[6]);
    grid_observed.updateGrid(mowerX + c.xDIR_MAP.get("northwest"), mowerY + c.yDIR_MAP.get("northwest"), values[7]);
  }
}
