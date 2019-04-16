import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.io.*;

public class SimDriver {
    private static Random randGenerator;

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 100;

    private Integer lawnHeight;
    private Integer lawnWidth;
    private Integer[][] lawnInfo;

    private final int EMPTY_CODE = 0;
    private final int GRASS_CODE = 1;
    private final int CRATER_CODE = 2;
    private final int FENCE_CODE = 3;
    private final int UNKNOWN_CODE = -1;
    private final String[] map = {"North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"};

    //mower info
    private Integer mowerX, mowerY;
    private String mowerDirection;
    //mower local info
    private Integer lmowerX, lmowerY;
    private Integer[][] knowledgeMap;

    private Integer knownHeight;
    private Integer knownWidth;
    private Integer knownBaseX;
    private Integer knownBaseY;
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;

    private String trackAction;
    private Integer trackMoveDistance;
    private String trackNewDirection;
    private String trackMoveCheck;
    private String trackScanResults;
    private Integer[] trackScanResultsInt;

    //result set
    private Integer orig_green;
    private Integer grass_cut;

    public SimDriver() {
        randGenerator = new Random();

        lawnHeight = 0;
        lawnWidth = 0;
        lawnInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        mowerX = -1;
        mowerY = -1;
        mowerDirection = "North";
        orig_green = 0;

        knowledgeMap = new Integer[2*DEFAULT_WIDTH][2*DEFAULT_HEIGHT];
        for (int i =0; i< 2*DEFAULT_WIDTH; i++){
            for (int j =0; j< 2*DEFAULT_HEIGHT; j++){
                knowledgeMap[i][j] = UNKNOWN_CODE;
            }
        }
        lmowerX = 100;
        lmowerY = 100;
        knownBaseX = 100;
        knownBaseY = 100;
        knowledgeMap[lmowerX][lmowerY] = EMPTY_CODE;
        knownHeight = 1;
        knownWidth = 1;
        grass_cut = 1;

        xDIR_MAP = new HashMap<>();
        xDIR_MAP.put("North", 0);
        xDIR_MAP.put("Northeast", 1);
        xDIR_MAP.put("East", 1);
        xDIR_MAP.put("Southeast", 1);
        xDIR_MAP.put("South", 0);
        xDIR_MAP.put("Southwest", -1);
        xDIR_MAP.put("West", -1);
        xDIR_MAP.put("Northwest", -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put("North", 1);
        yDIR_MAP.put("Northeast", 1);
        yDIR_MAP.put("East", 0);
        yDIR_MAP.put("Southeast", -1);
        yDIR_MAP.put("South", -1);
        yDIR_MAP.put("Southwest", -1);
        yDIR_MAP.put("West", 0);
        yDIR_MAP.put("Northwest", 1);
    }

    public boolean allGrassMowed(){
        return (trackAction == "turn_off" || trackMoveCheck == "crash");
    }
    public void uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            //System.out.println(testFileName);
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, j, k;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnHeight = Integer.parseInt(tokens[0]);

            // generate the lawn information
            lawnInfo = new Integer[lawnWidth][lawnHeight];
            for (i = 0; i < lawnWidth; i++) {
                for (j = 0; j < lawnHeight; j++) {
                    lawnInfo[i][j] = GRASS_CODE;
                }
            }
            orig_green = lawnWidth * lawnHeight;

            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerX = Integer.parseInt(tokens[0]);
                mowerY = Integer.parseInt(tokens[1]);
                mowerDirection = tokens[2];

                // mow the grass at the initial location
                lawnInfo[mowerX][mowerY] = EMPTY_CODE;
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = CRATER_CODE;
                orig_green--;
            }

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    public void pollMowerForAction() {
        int xOrientation = xDIR_MAP.get(mowerDirection);
        int yOrientation = yDIR_MAP.get(mowerDirection);
        // update knowledgemap
        if(trackScanResultsInt != null){
            //System.out.println("Scan Update");
            updateKMap(trackScanResultsInt);
            trackScanResultsInt = null;
        }
        // surrounded by unknown? scan
        if(surroundedByFence()){
            //System.out.println("Turn off");
            trackAction = "turn_off";
        }else if(nearbysquare(UNKNOWN_CODE)) {
            //System.out.println("Scan");
            trackAction = "scan";
        }
        else {
            //System.out.println("Move");
            trackAction = "move";
            // move in the direction if possible.
            // check if you can move 2 in current direction (Grass and empty are okay).
            int max_move = 0;

            for (max_move = 0; max_move < 3; max_move++){
                int value =knowledgeMap[lmowerX+max_move*xOrientation][lmowerY+max_move*yOrientation];
                if(value == CRATER_CODE || value == FENCE_CODE || value == UNKNOWN_CODE){
                    break;
                }
            }
            trackMoveDistance = max_move-1;
            //System.out.println(trackMoveDistance);


            for (max_move = 0; max_move <= trackMoveDistance; max_move++) {
                knowledgeMap[lmowerX+max_move*xOrientation][lmowerY+max_move*yOrientation] = EMPTY_CODE;
            }
            lmowerX += trackMoveDistance * xOrientation;
            lmowerY += trackMoveDistance * yOrientation;
            // direction:
            // check if around grass, if so, randomly pick grass
            if (nearbysquare(GRASS_CODE)) {
                trackNewDirection = find_rand_direction(GRASS_CODE);
            }
            // else randomly pick an empty space.
            else if(nearbysquare(EMPTY_CODE)){
                // find a way to get to green or Unknown square
                int posx, posy, posx1, posy1;
                int[] select = new int[8];
                int point = 0;
                for(int i =0; i< 8; i++){
                    posx = lmowerX + 2 * xDIR_MAP.get(map[i]);
                    posy = lmowerY + 2 * yDIR_MAP.get(map[i]);
                    posx1 = lmowerX + xDIR_MAP.get(map[i]);
                    posy1 = lmowerY + yDIR_MAP.get(map[i]);
                    if ((knowledgeMap[posx][posy] == UNKNOWN_CODE || knowledgeMap[posx][posy] == GRASS_CODE) & knowledgeMap[posx1][posy1] == EMPTY_CODE){
                        select[point] = i;
                        point++;
                    }
                }
                if (point ==0){
                    trackNewDirection = find_rand_direction(EMPTY_CODE);
                }else{
                    trackNewDirection = map[select[randGenerator.nextInt(point)]];
                }
            }
            //System.out.println(trackNewDirection);
        }
    }

    public void updateKMap(Integer [] scan_result){
        String[] map = {"North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"};
        int posx, posy;
        for(int i =0; i< 8; i++){
            posx = lmowerX + xDIR_MAP.get(map[i]);
            posy = lmowerY + yDIR_MAP.get(map[i]);
            knowledgeMap[posx][posy] = scan_result[i];
            if (posx < knownBaseX){
                knownWidth += 1;//(knownBaseX - posx);
                knownBaseX = posx;
            }else if (posx > (knownWidth + knownBaseX -1)) {
                knownWidth += 1;//posx - (knownWidth + knownBaseX - 1);
            }

            if (posy < knownBaseY){
                knownHeight += 1;//(knownBaseY - posy);
                knownBaseY = posy;
            }else if (posy > (knownHeight + knownBaseY -1)) {
                knownHeight += 1;//posy - (knownHeight + knownBaseY - 1);
            }
        }
    }

    public boolean fencesFound(){
        //check columns
        for(int j =0; j< knownHeight; j++){
            if (knowledgeMap[knownBaseX][knownBaseY+j] != FENCE_CODE || knowledgeMap[knownBaseX+knownWidth-1][knownBaseY+j] != FENCE_CODE){
                return false;
            }
        }

        //check rows
        for(int j =0; j< knownWidth; j++){
            if (knowledgeMap[knownBaseX+j][knownBaseY] != FENCE_CODE || knowledgeMap[knownBaseX+j][knownBaseY+knownHeight-1] != FENCE_CODE){
                return false;
            }
        }
        return true;
    }

    public boolean surroundedByFence(){
        // check if all the blocks inside are empty
        for(int j =1; j< knownWidth-1; j++){
            for(int i =1; i< knownHeight-1; i++){
                if (!(knowledgeMap[knownBaseX+j][knownBaseY+i] == EMPTY_CODE || knowledgeMap[knownBaseX+j][knownBaseY+i] == CRATER_CODE)){
                    return false;
                }
            }
        }

        //check if fence exist and it makes an enclosed area
        return fencesFound();
    }

    public boolean nearbysquare(int code){
        String[] map = {"North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"};
        int posx, posy;
        for(int i =0; i< 8; i++){
            posx = lmowerX + xDIR_MAP.get(map[i]);
            posy = lmowerY + yDIR_MAP.get(map[i]);
            if (knowledgeMap[posx][posy] == code){
                return true;
            }
        }
        return false;
    }

    public String find_rand_direction(int code){

        int posx, posy;
        int[] select = new int[8];
        int point = 0;
        for(int i =0; i< 8; i++){
            posx = lmowerX + xDIR_MAP.get(map[i]);
            posy = lmowerY + yDIR_MAP.get(map[i]);
            if (knowledgeMap[posx][posy] == code){
                select[point] = i;
                point++;
            }
        }
        return map[select[randGenerator.nextInt(point)]];
    }

    public void validateMowerAction() {
        int xOrientation, yOrientation;

        if (trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation
            trackScanResultsInt = scan();
            trackScanResults = "";
            for (int k = 0; k < 8; k++) {
                if (k==0){
                    trackScanResults = int_to_sstate(trackScanResultsInt[k]);
                }else{
                    trackScanResults += ","+int_to_sstate(trackScanResultsInt[k]);
                }
            }
            //trackScanResults = "empty,grass,crater,fence,empty,grass,crater,fence";

        } else if (trackAction.equals("move")) {
            // in the case of a move, ensure that the move doesn't cross craters or fences
            xOrientation = xDIR_MAP.get(mowerDirection);
            yOrientation = yDIR_MAP.get(mowerDirection);

            // just for this demonstration, allow the mower to change direction
            // even if the move forward causes a crash
            mowerDirection = trackNewDirection;

            int newSquareX = mowerX;// + trackMoveDistance * xOrientation;
            int newSquareY = mowerY;// + trackMoveDistance * yOrientation;

            for (int i = 0; i <= trackMoveDistance; i++) {
                if (newSquareX >= 0 & newSquareX < lawnWidth & newSquareY >= 0 & newSquareY < lawnHeight & lawnInfo[newSquareX][newSquareY] != CRATER_CODE) {
                    mowerX = newSquareX;
                    mowerY = newSquareY;
                    trackMoveCheck = "ok";

                    // update lawn status
                    if(lawnInfo[mowerX][mowerY] == GRASS_CODE){
                        grass_cut++;
                    }
                    lawnInfo[mowerX][mowerY] = EMPTY_CODE;
                    newSquareX += xOrientation;
                    newSquareY += yOrientation;
                } else {
                    trackMoveCheck = "crash";
                    break;
                }
            }

        } else if (trackAction.equals("turn_off")) {
            trackMoveCheck = "ok";
        }
    }

    public Integer[] scan(){
        Integer[] ans = new Integer[8];
        ans[0] = scan_square(mowerX+xDIR_MAP.get("North"), mowerY + yDIR_MAP.get("North"));
        ans[1] = scan_square(mowerX+xDIR_MAP.get("Northeast"), mowerY + yDIR_MAP.get("Northeast"));
        ans[2] = scan_square(mowerX+xDIR_MAP.get("East"), mowerY + yDIR_MAP.get("East"));
        ans[3] = scan_square(mowerX+xDIR_MAP.get("Southeast"), mowerY + yDIR_MAP.get("Southeast"));
        ans[4] = scan_square(mowerX+xDIR_MAP.get("South"), mowerY + yDIR_MAP.get("South"));
        ans[5] = scan_square(mowerX+xDIR_MAP.get("Southwest"), mowerY + yDIR_MAP.get("Southwest"));
        ans[6] = scan_square(mowerX+xDIR_MAP.get("West"), mowerY + yDIR_MAP.get("West"));
        ans[7] = scan_square(mowerX+xDIR_MAP.get("Northwest"), mowerY + yDIR_MAP.get("Northwest"));
        return ans;
    }

    public String int_to_sstate(int i){
        switch (i){
            case 0:
                return "empty";
            case 1:
                return "grass";
            case 2:
                return "crater";
            case 3:
                return "fence";
        }
        return "unknown";
    }

    public Integer scan_square(int indX, int indY){
        if (lawnHeight <= indY || 0 > indY || 0 > indX || lawnWidth <= indX){
            return FENCE_CODE;
        } else {
            return lawnInfo[indX][indY];
        }
    }

    public void displayActionAndResponses() {
        // display the mower's actions
        System.out.print(trackAction);
        if (trackAction.equals("move")) {
            System.out.println("," + trackMoveDistance + "," + trackNewDirection);
        } else {
            System.out.println();
        }

        // display the simulation checks and/or responses
        if (trackAction.equals("move") | trackAction.equals("turn_off")) {
            System.out.println(trackMoveCheck);
        } else if (trackAction.equals("scan")) {
            System.out.println(trackScanResults);
        } else {
            System.out.println("action not recognized");
        }
    }

    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public void renderLawn() {
        int i, j;
        int charWidth = 2 * lawnWidth + 2;

        // display the rows of the lawn from top to bottom
        for (j = lawnHeight - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < lawnWidth; i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (i == mowerX & j == mowerY) {
                    System.out.print("M");
                } else {
                    switch (lawnInfo[i][j]) {
                        case EMPTY_CODE:
                            System.out.print(" ");
                            break;
                        case GRASS_CODE:
                            System.out.print("g");
                            break;
                        case CRATER_CODE:
                            System.out.print("c");
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < lawnWidth; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's direction
        System.out.println("dir: " + mowerDirection);
        System.out.println("");
    }

    public void renderKnownLawn() {
        int i, j;
        int charWidth = 2 * knownWidth + 2;

        // display the rows of the lawn from top to bottom
        for (j = (knownHeight) - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < (knownWidth); i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (i + knownBaseX == lmowerX & j + knownBaseY == lmowerY) {
                    System.out.print("M");
                } else {
                    switch (knowledgeMap[i+knownBaseX][j+knownBaseY]) {
                        case EMPTY_CODE:
                            System.out.print(" ");
                            break;
                        case GRASS_CODE:
                            System.out.print("g");
                            break;
                        case CRATER_CODE:
                            System.out.print("c");
                            break;
                        case UNKNOWN_CODE:
                            System.out.print("U");
                            break;
                        case FENCE_CODE:
                            System.out.print("F");
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < (knownWidth); i++) {
            System.out.print(" " + i);
        }
        System.out.println("");
    }

    public void printFinal(int turns){
        int total_squares = lawnHeight * lawnWidth;
        System.out.println(String.valueOf(total_squares)+","+String.valueOf(orig_green)+","+String.valueOf(grass_cut)+","+String.valueOf(turns));
    }

}