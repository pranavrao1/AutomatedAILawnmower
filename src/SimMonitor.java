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
    private Constants constants;

    private Integer m_lawnHeight;
    private Integer m_lawnWidth;
    private Integer[][] m_lawnInfo;
    private Integer[] m_scanResult;
    private Integer mowerX, mowerY;
    private String mowerDirection;

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
        constants = new Constants();

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
                    m_lawnInfo[i][j] = constants.GRASS_CODE;
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
                m_lawnInfo[x][y] = constants.EMPTY_CODE;
                
                m_mowerState[k] = new MowerState(x,y,mowerDirection);
                m_mowers[k] = new Mower(x,y,mowerDirection,k);
                
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            m_numCrater = numCraters;
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                // place a crater at the given location
                m_lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = constants.CRATER_CODE;
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
                m_lawnInfo[x][y] = constants.PUPPY_GRASS_CODE;
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
    	
    	//Mower already off or Crashed , Return
    	if(mowerState.getState() == constants.MOWER_OFF || mowerState.getState() == constants.MOWER_CRASHED){
    		return;
    	}
    	
    	//Mower Stalled , either from running into mower earlier or current with a puppy
    	if(mowerState.getState() == constants.MOWER_STALLED){
    		
    		//Puppy on top, return
    		if(m_lawnInfo[mowerState.getX()][mowerState.getY()] == constants.PUPPY_MOWER_CODE) {
    			return;
    		}
    		
    		//Stalled from previous crash, decrement stall turn
    		int stallTurn = mowerState.getStallTurn();
    		if( stallTurn > 0) {
    			mowerState.setStallTurn(--stallTurn);
    		}
    		//Stall turn is zero , Mower becomes active again
    		if(mowerState.getStallTurn() == 0) {
    			mowerState.setState(constants.MOWER_ACTIVE);
    		}
    		mower.finishMove(new Move("unknown", 0), mowerState.getState(), mowerState.getStallTurn());
    		return;
    	}
   
        trackAction = mower.getNextAction();

        if (trackAction.equals("turn_off")) {
            trackAction = "turn_off";
            mowerState.setState(constants.MOWER_OFF);
            //TODO print to log
            return;
        }
        else if (trackAction.equals("scan") ){
            // select scanning as the action
            trackAction = "scan";
            scanSurrounding(mowerState);
            mower.provideScanResult(m_scanResult);
            // TODO print to log
            return;
        }
        else {
            // select moving forward and the turning as the action
            trackAction = "move";
            Move move = mower.getNextMove();
            trackMoveDistance = move.getStep();
            trackNewDirection = move.getDirection();
            
            //Move 0 step
            if(trackMoveDistance == 0) {
            	mowerState.setDirection(trackNewDirection);
            	mower.finishMove(new Move(mowerState.getDirection(), 0), mowerState.getState(), mowerState.getStallTurn());
            	return;
            }
            //Move 1 step
            else if(trackMoveDistance > 0) {
                int xOrientation = constants.xDIR_MAP.get(mowerState.getDirection());
                int yOrientation = constants.yDIR_MAP.get(mowerState.getDirection());


                //mowerDirection = trackNewDirection;

                //int newSquareX = mowerX + trackMoveDistance * xOrientation;
                //int newSquareY = mowerY + trackMoveDistance * yOrientation;
                int mowerX = mowerState.getX();     //original X
                int mowerY = mowerState.getY();     //original Y
                for (int i = 1; i <= trackMoveDistance; i++) {
                    int newSquareX = mowerX + i * xOrientation;
                    int newSquareY = mowerY + i * yOrientation;

                    //If mower doesn't crash into fence or crater
                    if ((newSquareX >= 0 & newSquareX < m_lawnWidth & newSquareY >= 0 & newSquareY < m_lawnHeight)
                            && (m_lawnInfo[newSquareX][newSquareY] != constants.CRATER_CODE)) {

                        //trackMoveCheck = "ok";

                        //Crash into another mower  or mower with puppy on top
                        if (m_lawnInfo[newSquareX][newSquareY] == constants.MOWER_CODE
                                || m_lawnInfo[newSquareX][newSquareY] == constants.PUPPY_MOWER_CODE) {
                            mowerState.setState(constants.MOWER_STALLED);
                            mowerState.setStallTurn(m_collisionDelay);
                            mower.finishMove(new Move(mowerState.getDirection(), i-1), mowerState.getState(), mowerState.getStallTurn());
                            return;
                        }
                        //Crash into a puppy on grass or on empty
                        else if (m_lawnInfo[newSquareX][newSquareY] == constants.PUPPY_GRASS_CODE
                                || m_lawnInfo[newSquareX][newSquareY] == constants.PUPPY_EMPTY_CODE) {

                            //if (m_lawnInfo[newSquareX][newSquareY] == constants.PUPPY_GRASS_CODE) {
                                //decrease grass count;;
                            //}
                            mowerState.setState(constants.MOWER_STALLED);
                            mowerState.setX(newSquareX);
                            mowerState.setY(newSquareY);
                            //mowerState.setDirection(trackNewDirection);
                            m_lawnInfo[mowerX][mowerY] = constants.EMPTY_CODE;
                            m_lawnInfo[newSquareX][newSquareY] = constants.PUPPY_MOWER_CODE;
                            mower.finishMove(new Move(mowerState.getDirection(), i), mowerState.getState(), mowerState.getStallTurn());
                            return;
                        }
                        //Regular grass or empty
                        else {
                            mowerState.setX(newSquareX);
                            mowerState.setY(newSquareY);
                            //mowerState.setDirection(trackNewDirection);
                            m_lawnInfo[mowerX][mowerY] = constants.EMPTY_CODE;
                            m_lawnInfo[newSquareX][newSquareY] = constants.MOWER_CODE;
                        }
                    }
                    //Crash into fence or crater
                    else {
                        trackMoveCheck = "crash";
                        mowerState.setState(constants.MOWER_CRASHED);
                        m_lawnInfo[mowerX][mowerY] = constants.EMPTY_CODE;
                        mower.finishMove(new Move(mowerState.getDirection(), 0), mowerState.getState(), 0);
                        return;
                    }
                }
                mowerState.setDirection(trackNewDirection);
                mower.finishMove(new Move(mowerState.getDirection(), trackMoveDistance), mowerState.getState(), mowerState.getStallTurn());
                mowerDirection = trackNewDirection;
            }
        }        
    }

    public void validateMowerAction() {
        int xOrientation, yOrientation;
//        for (Mower mower: m_mowers) {
//            validateSingleMowerAction(mower);
//        }
    }

    //TODO: Implement this method.
    public void getPuppyAction() {
        for (int i=0; i < m_puppies.length; ++i) {
            singlePuppy(m_puppies[i]);
        }
    }
    
    public void singlePuppy(Puppy puppy) {
    	
    	if(puppy.isStaying()) {
    		return;
    	}
    	
    	Boolean found = false;    	
    	while(!found){
    		
	    	Integer move = puppy.getMove();
	    	String dir = constants.DIRECTIONS[move];
	    	
	        int xOrientation = constants.xDIR_MAP.get(dir);
	        int yOrientation = constants.yDIR_MAP.get(dir);
	        int oldX = puppy.getX();     //original X
	        int oldY = puppy.getY();     //original Y
	        int newX = oldX + 1 * xOrientation;
	        int newY = oldY+ 1 * yOrientation;
	        
	        if ((newX >= 0 & newX < m_lawnWidth & newY >= 0 & newY < m_lawnHeight)
                    && (m_lawnInfo[newX][newY] != constants.CRATER_CODE)
                    && (m_lawnInfo[newX][newY] != constants.PUPPY_MOWER_CODE)
                    && (m_lawnInfo[newX][newY] != constants.PUPPY_GRASS_CODE)
                    && (m_lawnInfo[newX][newY] != constants.PUPPY_EMPTY_CODE)) {
	        	
	        	found = true;
	        	if(m_lawnInfo[newX][newY] == constants.EMPTY_CODE) {
	        		m_lawnInfo[newX][newY] = constants.PUPPY_EMPTY_CODE;
	        	}
	        	if(m_lawnInfo[newX][newY] == constants.GRASS_CODE) {
	        		m_lawnInfo[newX][newY] = constants.PUPPY_GRASS_CODE;
	        	}
	        	if(m_lawnInfo[newX][newY] == constants.MOWER_CODE) {
	        		m_lawnInfo[newX][newY] = constants.PUPPY_MOWER_CODE;
	        		for(int i = 0; i < m_mowers.length; i++) {
	        			if(m_mowerState[i].getX() == newX && m_mowerState[i].getY() == newY) {
	        				m_mowerState[i].setState(constants.MOWER_STALLED);
	        				break;
	        			}
	        		}
	        	}
	        	
	        	puppy.setX(newX);
	        	puppy.setY(newY);
	        	
	        	if(m_lawnInfo[oldX][oldY] == constants.PUPPY_EMPTY_CODE) {
	        		m_lawnInfo[oldX][oldY] = constants.EMPTY_CODE;
	        	}
	        	if(m_lawnInfo[oldX][oldY] == constants.PUPPY_GRASS_CODE) {
	        		m_lawnInfo[oldX][oldY] = constants.GRASS_CODE;
	        	}
	        	if(m_lawnInfo[oldX][oldY] == constants.PUPPY_MOWER_CODE) {
	        		m_lawnInfo[oldX][oldY] = constants.MOWER_CODE;
	        		for(int i = 0; i < m_mowers.length; i++) {
	        			if(m_mowerState[i].getX() == newX && m_mowerState[i].getY() == newY) {
	        				if(m_mowerState[i].getStallTurn() == 0) {
	        					m_mowerState[i].setState(constants.MOWER_ACTIVE);
	        					break;
	        				}
	        			}
	        		}
	        	}
	        }
    	}       
    }

    //TODO: Implement this method.
    public void printFinal(int number) {

    }

    public void scanSurrounding(MowerState mowerState){
        
    	int mowerX = mowerState.getX();
    	int mowerY = mowerState.getY();
        for(int i=0; i<8; ++i)
        {
            int x = constants.xDIR_MAP.get(constants.DIRECTIONS[i]);
            int y = constants.yDIR_MAP.get(constants.DIRECTIONS[i]);
            if ((mowerX + x) == m_lawnWidth || (mowerX + x) == 0)
                m_scanResult[i] = constants.FENCE_CODE;
            else
                m_scanResult[i] = m_lawnInfo[mowerX + x][mowerY+y];

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
                    if(m_lawnInfo[i][j] == constants.EMPTY_CODE)
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
