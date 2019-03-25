/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osmowsis;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.io.*;
/**
 *
 * @author oscarc
 */
class Move
{ 
    public String direction;  
    public int    step; 
 };

public class Controller {
    private static Random randGenerator;
    private Mower m_mower;

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 100;

    private Integer lawnHeight;
    private Integer lawnWidth;
    private Integer[][] lawnInfo;
    private Integer[] m_scanResult;
    private Integer mowerX, mowerY;
    private String mowerDirection;
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;
    private HashMap<Integer, String> SCAN_MAP;

    private String trackAction;
    private Integer trackMoveDistance;
    private String trackNewDirection;
    private String trackMoveCheck;
    private String trackScanResults;
    
    private Integer m_numCrater;
    private Integer m_turn;

    private final int EMPTY_CODE = 0;
    private final int GRASS_CODE = 1;
    private final int CRATER_CODE = 2;
    private final int FENCE_CODE = 3;

    public Controller() {
        randGenerator = new Random();
        

        lawnHeight = 0;
        lawnWidth = 0;
        lawnInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        m_scanResult = new Integer[8];
        mowerX = -1;
        mowerY = -1;
        mowerDirection = "North";
        
        m_numCrater = 0;
        m_turn = 0;

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
        
        SCAN_MAP = new HashMap<>();
        SCAN_MAP.put(EMPTY_CODE, "empty");
        SCAN_MAP.put(GRASS_CODE, "grass");
        SCAN_MAP.put(CRATER_CODE, "crater");
        SCAN_MAP.put(FENCE_CODE, "fence");
    }

    public void uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
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
            m_numCrater = numCraters;
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = CRATER_CODE;
            }

            takeCommand.close();
            
            m_mower = new Mower(mowerDirection);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    public void pollMowerForAction() {
        //int moveRandomChoice;
        ++m_turn;
        trackAction = m_mower.getNextAction();
        //moveRandomChoice = randGenerator.nextInt(100);
        if (trackAction.equals("turn_off")) {
            // select turning off the mower as the action
            trackAction = "turn_off";
        } 
        //else if (moveRandomChoice < 10) {
        else if (trackAction.equals("scan") ){
            // select scanning as the action
            trackAction = "scan";
            scanSurrounding();
        } 
        else {
            // select moving forward and the turning as the action
            trackAction = "move";
            Move move = m_mower.getMove();
            trackMoveDistance = move.step;
            trackNewDirection = move.direction;
            m_mower.finishMove();
            // determine a distance
    /*        moveRandomChoice = randGenerator.nextInt(100);
            if (moveRandomChoice < 20) {
                trackMoveDistance = 0;
            } else if (moveRandomChoice < 70) {
                trackMoveDistance = 1;
            } else {
                trackMoveDistance = 2;
            }

            // determine a new direction
            moveRandomChoice = randGenerator.nextInt(100);
            if (moveRandomChoice < 50) {
                switch (mowerDirection) {
                    case "South":
                        trackNewDirection = "Southwest";
                        break;
                    case "Southwest":
                        trackNewDirection = "West";
                        break;
                    case "West":
                        trackNewDirection = "Northwest";
                        break;
                    case "Northwest":
                        trackNewDirection = "North";
                        break;
                    case "Southeast":
                        trackNewDirection = "South";
                        break;
                    case "North":
                        trackNewDirection = "Northeast";
                        break;
                    case "Northeast":
                        trackNewDirection = "East";
                        break;
                    case "East":
                        trackNewDirection = "Southeast";
                        break;
                    default:
                        trackNewDirection = mowerDirection;
                        break;
                }
            } else {
                trackNewDirection = mowerDirection;
            }  */
        }
    }

    public void validateMowerAction() {
        int xOrientation, yOrientation;

        if (trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation
            m_mower.provideScanResult(m_scanResult);
            //trackScanResults = "empty,grass,crater,fence,empty,grass,crater,fence";
            trackScanResults = SCAN_MAP.get(m_scanResult[0]) + "," +
                    SCAN_MAP.get(m_scanResult[1]) + "," +
                    SCAN_MAP.get(m_scanResult[2]) + "," +
                    SCAN_MAP.get(m_scanResult[3]) + "," +
                    SCAN_MAP.get(m_scanResult[4]) + "," +
                    SCAN_MAP.get(m_scanResult[5]) + "," +
                    SCAN_MAP.get(m_scanResult[6]) + "," +
                    SCAN_MAP.get(m_scanResult[7]);

        } else if (trackAction.equals("move")) {
            // in the case of a move, ensure that the move doesn't cross craters or fences
            
             
            xOrientation = xDIR_MAP.get(mowerDirection);
            yOrientation = yDIR_MAP.get(mowerDirection);

            // just for this demonstration, allow the mower to change direction
            // even if the move forward causes a crash
            mowerDirection = trackNewDirection;

            int newSquareX = mowerX + trackMoveDistance * xOrientation;
            int newSquareY = mowerY + trackMoveDistance * yOrientation;

            if ((newSquareX >= 0 & newSquareX < lawnWidth & newSquareY >= 0 & newSquareY < lawnHeight)
                    && (lawnInfo[newSquareX][newSquareY] != CRATER_CODE)) {
                mowerX = newSquareX;
                mowerY = newSquareY;
                trackMoveCheck = "ok";

                // update lawn status
                lawnInfo[mowerX][mowerY] = EMPTY_CODE;
            } else {
                trackMoveCheck = "crash";
            }

        } else if (trackAction.equals("turn_off")) {
            trackMoveCheck = "ok";
        }
    }
    
    public void scanSurrounding(){
        
        for(int i=0; i<8; ++i)
        {
            switch(i)
            {
                case 0:
                    if(mowerY+1 == lawnHeight)
                    {
                        m_scanResult[0] = FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[0] = lawnInfo[mowerX][mowerY+1];
                    }
                    break;
                case 1:
                    if(mowerY+1 == lawnHeight || mowerX+1 == lawnWidth)
                    {
                        m_scanResult[1] = FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[1] = lawnInfo[mowerX+1][mowerY+1];
                    }
                    break;
                case 2:
                    if(mowerX+1 == lawnWidth)
                    {
                        m_scanResult[2] = FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[2] = lawnInfo[mowerX+1][mowerY];
                    }
                    break;
                case 3:
                    if(mowerX+1 == lawnWidth || mowerY == 0)
                    {
                        m_scanResult[3] = FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[3] = lawnInfo[mowerX+1][mowerY-1];
                    }
                    break;
                case 4:
                    if(mowerY == 0)
                    {
                        m_scanResult[4] = FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[4] = lawnInfo[mowerX][mowerY-1];
                    }
                    break;
                case 5:
                    if(mowerX == 0 || mowerY == 0)
                    {
                        m_scanResult[5] = FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[5] = lawnInfo[mowerX-1][mowerY-1];
                    }
                    break;
                case 6:
                    if(mowerX == 0)
                    {
                        m_scanResult[6] = FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[6] = lawnInfo[mowerX-1][mowerY];
                    }
                    break;
                case 7:
                    if(mowerX == 0 || mowerY+1 == lawnHeight)
                    {
                        m_scanResult[7] = FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[7] = lawnInfo[mowerX-1][mowerY+1];
                    }
                    break;
                default:
                    break;
            }
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
    
    public void printFinalReport(){
        
        
        int total = 0;
            for (int i = 0; i < lawnWidth; i++) {
                for (int j = 0; j < lawnHeight; j++) {
                    if(lawnInfo[i][j] == EMPTY_CODE)
                    {
                        total++;
                    }
                }
            }
        int lawnSize = lawnWidth*lawnHeight;
        int numGrass = lawnSize - m_numCrater;
        System.out.println(lawnSize + "," + numGrass + "," + total + "," + m_turn);
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

}
