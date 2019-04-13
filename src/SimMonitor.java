/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.io.*;
/**
 *
 * @author oscar
 */

public class SimMonitor {
    private static Random randGenerator;
    private Mower[] m_mowers;
    private MowerState[] m_mowerState;
    private Puppy[] m_puppies;

    private static final int DEFAULT_WIDTH = 15;
    private static final int DEFAULT_HEIGHT = 10;
    private Constants code;

    private Integer m_lawnHeight;
    private Integer m_lawnWidth;
    private Integer[][] m_lawnInfo;
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
    private Integer m_maxTurn;
    private Integer m_collisionDelay;
    private Integer m_stayPercent;




    public SimMonitor() {
        randGenerator = new Random();
        code = new Constants();

        m_lawnHeight = 0;
        m_lawnWidth = 0;
        m_lawnInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        
        m_scanResult = new Integer[8];
        mowerX = -1;
        mowerY = -1;
        mowerDirection = "North";
        
        m_numCrater = 0;
        m_turn = 0;
        m_maxTurn = 0;
        m_collisionDelay = 0;
        m_stayPercent = 0;

        
        SCAN_MAP = new HashMap<>();
        SCAN_MAP.put(code.EMPTY_CODE, "empty");
        SCAN_MAP.put(code.GRASS_CODE, "grass");
        SCAN_MAP.put(code.CRATER_CODE, "crater");
        SCAN_MAP.put(code.FENCE_CODE, "fence");
    }

    public void uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, j, k , x, y;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            m_lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            m_lawnHeight = Integer.parseInt(tokens[0]);
            
            m_lawnInfo = new Integer[m_lawnWidth][m_lawnHeight];

            for (i = 0; i < m_lawnWidth; i++) {
                for (j = 0; j < m_lawnHeight; j++) {
                    m_lawnInfo[i][j] = code.GRASS_CODE;
                }
            }

            // read in the lawn mower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            m_mowerState = new MowerState [numMowers];
            m_mowers = new Mower [numMowers];
            
            tokens = takeCommand.nextLine().split(DELIMITER);
            m_collisionDelay = Integer.parseInt(tokens[0]);
            
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerDirection = tokens[2];
                // mow the grass at the initial location
                x = Integer.parseInt(tokens[0]);
                y = Integer.parseInt(tokens[1]);
                m_lawnInfo[x][y] = code.EMPTY_CODE;
                
                m_mowerState[k] = new MowerState(x,y,mowerDirection);
                m_mowers[k] = new Mower(x,y,mowerDirection,k,m_collisionDelay);
                
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            m_numCrater = numCraters;
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                // place a crater at the given location
                m_lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = code.CRATER_CODE;
            }
            
            // read in the lawn mower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numPuppy = Integer.parseInt(tokens[0]);
            m_puppies = new Puppy [numPuppy];
            
            tokens = takeCommand.nextLine().split(DELIMITER);
            m_stayPercent = Integer.parseInt(tokens[0]);
            for (k = 0; k < numPuppy; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                
                x = Integer.parseInt(tokens[0]);
                y = Integer.parseInt(tokens[1]);
                m_puppies[k] = new Puppy(m_stayPercent,x,y);
                m_lawnInfo[x][y] = code.PUPPY_GRASS_CODE;
            }
            
            tokens = takeCommand.nextLine().split(DELIMITER);
            m_maxTurn = Integer.parseInt(tokens[0]);

            takeCommand.close();
            
            //m_mower = new Mower(mowerDirection);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    public void pollMowerForAction() {
        ++m_turn;
        for (int i=0; i < m_mowers.length; ++i) {
            singleMower(m_mowers[i], m_mowerState[i]);
        }
    }

    private void singleMower(Mower mower, MowerState mowerState) {
    	
    	if(mowerState.getState() == code.MOWER_OFF || mowerState.getState() == code.MOWER_CRASHED){
    		return;
    	}
    	if(mowerState.getState() == code.MOWER_STALLED){
    		int stallTurn = mowerState.getStallTurn();
    		if( stallTurn > 0) {
    			mowerState.setStallTurn(--stallTurn);
    		}
    		if(mowerState.getStallTurn() == 0 && m_lawnInfo[mowerState.getX()][mowerState.getY()] != code.PUPPY_MOWER_CODE) {
    			mowerState.setState(code.MOWER_ACTIVE);
    		}
    		return;
    	}
   
        trackAction = mower.getNextAction();
        //moveRandomChoice = randGenerator.nextInt(100);
        if (trackAction.equals("turn_off")) {
            trackAction = "turn_off";
            mowerState.setState(code.MOWER_OFF);
        }
        else if (trackAction.equals("scan") ){
            // select scanning as the action
            trackAction = "scan";
            scanSurrounding();
            mower.provideScanResult(m_scanResult);
        }
        else {
            // select moving forward and the turning as the action
            trackAction = "move";
            Move move = mower.getMove();
            trackMoveDistance = move.getStep();
            trackNewDirection = move.getDirection();
            
            if(trackMoveDistance == 0) {
            	mowerState.setDirection(trackNewDirection);
            	mower.finishMove(mowerState.getX(), mowerState.getY(), mowerState.getDirection(), mowerState.getState());
            }
            else if(trackMoveDistance == 1) {


                int xOrientation = xDIR_MAP.get(mowerState.getDirection());
                int yOrientation = yDIR_MAP.get(mowerState.getDirection());


                mowerDirection = trackNewDirection;

                int newSquareX = mowerX + trackMoveDistance * xOrientation;
                int newSquareY = mowerY + trackMoveDistance * yOrientation;

                if ((newSquareX >= 0 & newSquareX < m_lawnWidth & newSquareY >= 0 & newSquareY < m_lawnHeight)
                        && (m_lawnInfo[newSquareX][newSquareY] != code.CRATER_CODE)) {
                    mowerX = newSquareX;
                    mowerY = newSquareY;
                    trackMoveCheck = "ok";

                    // update lawn status
                    m_lawnInfo[mowerX][mowerY] = code.EMPTY_CODE;
                } else {
                    trackMoveCheck = "crash";
                }
            }


        }
    }

    public void validateMowerAction() {
        int xOrientation, yOrientation;
        for (Mower mower: m_mowers) {
            validateSingleMowerAction(mower);
        }
    }

    //TODO: Implement this method.
    public void getPuppyAction() {

    }

    //TODO: Implement this method.
    public void printFinal(int number) {

    }

    private void validateSingleMowerAction(Mower mower) {
        int xOrientation;
        int yOrientation;
        if (trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a north bound orientation
            mower.provideScanResult(m_scanResult);
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

            if ((newSquareX >= 0 & newSquareX < m_lawnWidth & newSquareY >= 0 & newSquareY < m_lawnHeight)
                    && (m_lawnInfo[newSquareX][newSquareY] != code.CRATER_CODE)) {
                mowerX = newSquareX;
                mowerY = newSquareY;
                trackMoveCheck = "ok";

                // update lawn status
                m_lawnInfo[mowerX][mowerY] = code.EMPTY_CODE;
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
                    if(mowerY+1 == m_lawnHeight)
                    {
                        m_scanResult[0] = code.FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[0] = m_lawnInfo[mowerX][mowerY+1];
                    }
                    break;
                case 1:
                    if(mowerY+1 == m_lawnHeight || mowerX+1 == m_lawnWidth)
                    {
                        m_scanResult[1] = code.FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[1] = m_lawnInfo[mowerX+1][mowerY+1];
                    }
                    break;
                case 2:
                    if(mowerX+1 == m_lawnWidth)
                    {
                        m_scanResult[2] = code.FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[2] = m_lawnInfo[mowerX+1][mowerY];
                    }
                    break;
                case 3:
                    if(mowerX+1 == m_lawnWidth || mowerY == 0)
                    {
                        m_scanResult[3] = code.FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[3] = m_lawnInfo[mowerX+1][mowerY-1];
                    }
                    break;
                case 4:
                    if(mowerY == 0)
                    {
                        m_scanResult[4] = code.FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[4] = m_lawnInfo[mowerX][mowerY-1];
                    }
                    break;
                case 5:
                    if(mowerX == 0 || mowerY == 0)
                    {
                        m_scanResult[5] = code.FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[5] = m_lawnInfo[mowerX-1][mowerY-1];
                    }
                    break;
                case 6:
                    if(mowerX == 0)
                    {
                        m_scanResult[6] = code.FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[6] = m_lawnInfo[mowerX-1][mowerY];
                    }
                    break;
                case 7:
                    if(mowerX == 0 || mowerY+1 == m_lawnHeight)
                    {
                        m_scanResult[7] = code.FENCE_CODE;
                    }
                    else
                    {
                        m_scanResult[7] = m_lawnInfo[mowerX-1][mowerY+1];
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
            for (int i = 0; i < m_lawnWidth; i++) {
                for (int j = 0; j < m_lawnHeight; j++) {
                    if(m_lawnInfo[i][j] == code.EMPTY_CODE)
                    {
                        total++;
                    }
                }
            }
        int lawnSize = m_lawnWidth * m_lawnHeight;
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
        int charWidth = 2 * m_lawnWidth + 2;

        // display the rows of the lawn from top to bottom
        for (j = m_lawnHeight - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < m_lawnWidth; i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (i == mowerX & j == mowerY) {
                    System.out.print("M");
                } 
                else {
//                    switch (m_lawnInfo[i][j]) {
//                        case code.EMPTY_CODE:
//                            System.out.print(" ");
//                            break;
//                        case code.GRASS_CODE:
//                            System.out.print("g");
//                            break;
//                        case code.CRATER_CODE:
//                            System.out.print("c");
//                            break;
//                        default:
//                            break;
//                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < m_lawnWidth; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's direction
        System.out.println("dir: " + mowerDirection);
        System.out.println("");
    }


    //TODO: Implement Stop run
    public boolean stopRun() {
        return false;
    }

}
