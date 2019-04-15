import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Mower extends LawnmowerShared {
  private static Random randGenerator;
  private String direction;
  private String mowerAction;
  public String mowerState;
  private int mowerX, mowerY;
  private Integer trackMoveDistance;
  private String trackNewDirection;
  private Constants c = new Constants();
  public int turns_stalled;
  public int mower_id;
  private Move nextMove;

  public Mower(int locX, int locY, String dir, int id) {
    mower_id = id;
    mowerX = locX;
    mowerY = locY;
    direction = dir;
    mowerState = "on";
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
    if (mowerState == "stalled") {
      turns_stalled--;
      if (turns_stalled == 0)
        mowerState = "on";
      mowerAction = "move";
      return "move";
    }
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
      if (point ==0){
        trackNewDirection = find_rand_direction(c.EMPTY_CODE);
      }else{
        trackNewDirection = c.DIRECTIONS[select[randGenerator.nextInt(point)]];
      }
    }
    nextMove = new Move(trackNewDirection, trackMoveDistance);
    return mowerAction;
  }

  //TODO: Implement this method.
  public Move getMove() {
    return nextMove;
  }

  //TODO: Implement this method.
  public void finishMove(int x, int y, String dir,int mowerStatus) {

  }

  //TODO: Implement this method.
  public void provideScanResult(Integer[] values) {

  }
}
