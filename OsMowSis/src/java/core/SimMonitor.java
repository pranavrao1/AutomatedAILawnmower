package core;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.text.DecimalFormat;

/**
 *
 * @author oscar
 */

public class SimMonitor {
    private static SimMonitor instance;

    private static Random randGenerator;
    private Mower[] m_mowers;
    private MowerState[] m_mowerState;
    private Puppy[] m_puppies;

    private static final int DEFAULT_WIDTH = 15;
    private static final int DEFAULT_HEIGHT = 10;
    private HashMap<Integer, String> SCAN_MAP;
    private Constants constants;

    private Lawn lawnInfo;
    private int[] m_scanResult;

    private String  trackLawnObject;
    private Integer trackLawnObjectId;
    private String  trackAction;
    private Integer trackMoveDistance;
    private String  trackNewDirection;
    private Integer trackActualMovedStep;
    private String  trackMoveResult;
    private String  trackScanResults;
    private Integer trackNewX;
    private Integer trackNewY;
    
    private Integer m_numCrater;
    private Integer m_turn;
    private Integer m_maxTurn;
    private Integer m_collisionDelay;
    private Integer m_stayPercent;
    private boolean m_stopRun;
    private int m_lawnWidth;
    private int m_lawnHeight;
    
    public int indexForNextBtn = 0;
    private StringBuilder log;


    public static synchronized SimMonitor getInstance(){
        if(instance == null){
            instance = new SimMonitor();
        }
        return instance;
    }

    public SimMonitor() {
        randGenerator = new Random();
        constants = new Constants();
        lawnInfo = new Lawn(DEFAULT_WIDTH, DEFAULT_HEIGHT, constants.GRASS_CODE, true);
        m_scanResult = new int[8];
   
        m_numCrater = 0;
        m_turn = 0;
        m_maxTurn = 0;
        m_collisionDelay = 0;
        m_stayPercent = 0;
        m_stopRun = false;
        
        SCAN_MAP = new HashMap<>();
        SCAN_MAP.put(constants.EMPTY_CODE, "empty");
        SCAN_MAP.put(constants.GRASS_CODE, "grass");
        SCAN_MAP.put(constants.MOWER_CODE, "mower");
        SCAN_MAP.put(constants.CRATER_CODE, "crater");
        SCAN_MAP.put(constants.FENCE_CODE, "fence");
        SCAN_MAP.put(constants.PUPPY_EMPTY_CODE, "puppy_empty");
        SCAN_MAP.put(constants.PUPPY_GRASS_CODE, "puppy_grass");
        SCAN_MAP.put(constants.PUPPY_MOWER_CODE, "puppy_mower");
        LawnmowerShared.grid_observed = null;
        indexForNextBtn = 0;
        log = new StringBuilder();
        instance = this;
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

            lawnInfo = new Lawn(m_lawnWidth, m_lawnHeight, constants.GRASS_CODE, true);

            // read in the lawn mower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            m_mowerState = new MowerState [numMowers];
            m_mowers = new Mower [numMowers];
            
            tokens = takeCommand.nextLine().split(DELIMITER);
            m_collisionDelay = Integer.parseInt(tokens[0]);
            
            String mowerDirection;
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerDirection = tokens[2].toLowerCase();
                // mow the grass at the initial location
                x = Integer.parseInt(tokens[0]);
                y = Integer.parseInt(tokens[1]);
                lawnInfo.updateGrid(x, y, constants.MOWER_CODE);
                
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
                lawnInfo.updateGrid(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), constants.CRATER_CODE);
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
                lawnInfo.updateGrid(x, y, constants.PUPPY_GRASS_CODE);
            }
            
            tokens = takeCommand.nextLine().split(DELIMITER);
            m_maxTurn = Integer.parseInt(tokens[0]);

            takeCommand.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error"+e.toString());
        }
    }

    public String uploadStartingFileUI(InputStream is) {
        final String DELIMITER = ",";

        try {
            BufferedReader takeCommand = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            
            //remove information lines
            takeCommand.readLine();
            takeCommand.readLine();
            takeCommand.readLine();
            takeCommand.readLine();
            
            String[] tokens;
            int i, j, k , x, y;

            // read in the lawn information
            tokens = takeCommand.readLine().split(DELIMITER);
            m_lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.readLine().split(DELIMITER);
            m_lawnHeight = Integer.parseInt(tokens[0]);

            lawnInfo = new Lawn(m_lawnWidth, m_lawnHeight, constants.GRASS_CODE, true);

            // read in the lawn mower starting information
            tokens = takeCommand.readLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            m_mowerState = new MowerState [numMowers];
            m_mowers = new Mower [numMowers];
            
            tokens = takeCommand.readLine().split(DELIMITER);
            m_collisionDelay = Integer.parseInt(tokens[0]);
            
            String mowerDirection;
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.readLine().split(DELIMITER);
                mowerDirection = tokens[2].toLowerCase();
                // mow the grass at the initial location
                x = Integer.parseInt(tokens[0]);
                y = Integer.parseInt(tokens[1]);
                lawnInfo.updateGrid(x, y, constants.MOWER_CODE);
                
                m_mowerState[k] = new MowerState(x,y,mowerDirection);
                m_mowers[k] = new Mower(x,y,mowerDirection,k);
                
            }

            // read in the crater information
            tokens = takeCommand.readLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            m_numCrater = numCraters;
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.readLine().split(DELIMITER);
                // place a crater at the given location
                lawnInfo.updateGrid(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), constants.CRATER_CODE);
            }
            
            // read in the lawn mower starting information
            tokens = takeCommand.readLine().split(DELIMITER);
            int numPuppy = Integer.parseInt(tokens[0]);
            m_puppies = new Puppy [numPuppy];
            
            tokens = takeCommand.readLine().split(DELIMITER);
            m_stayPercent = Integer.parseInt(tokens[0]);
            for (k = 0; k < numPuppy; k++) {
                tokens = takeCommand.readLine().split(DELIMITER);
                
                x = Integer.parseInt(tokens[0]);
                y = Integer.parseInt(tokens[1]);
                m_puppies[k] = new Puppy(m_stayPercent,x,y);
                lawnInfo.updateGrid(x, y, constants.PUPPY_GRASS_CODE);
            }
            
            tokens = takeCommand.readLine().split(DELIMITER);
            m_maxTurn = Integer.parseInt(tokens[0]);

            takeCommand.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error");
        }
        return renderLawnForUI(-1,-1);

    }

    public void beforePollMowersForAction(){
        ++m_turn;
        Boolean continueSimulation = false;
        for (int i=0; i < m_mowerState.length; ++i) {
            if(m_mowerState[i].getState() != constants.MOWER_OFF && m_mowerState[i].getState() != constants.MOWER_CRASHED){
                continueSimulation = true;
                break;
            }
        }
        if(m_turn == m_maxTurn)
        {
            continueSimulation = false;
        }
        if(!continueSimulation) {
            m_stopRun = true;
        }
    }

    public synchronized void singleMower(Mower mower, int id) {
        
        MowerState mowerState = m_mowerState[id];
        
    	//Mower already off or Crashed , Return 
    	if(mowerState.getState() == constants.MOWER_OFF || mowerState.getState() == constants.MOWER_CRASHED){
            return;
    	}
    	//Mower Stalled , either from running into mower earlier or current with a puppy
    	if(mowerState.getState() == constants.MOWER_STALLED){
    		//Puppy on top, return
                int stallX = mowerState.getX();
                int stallY = mowerState.getY();
                int stalled = lawnInfo.getSquareType(mowerState.getX(), mowerState.getY());
    		if(lawnInfo.getSquareType(mowerState.getX(), mowerState.getY()) == constants.PUPPY_MOWER_CODE) {
    		    return;
                }
    		
//    		Stalled from previous crash, decrement stall turn
    		int stallTurn = mowerState.getStallTurn();
    		if( stallTurn > 0) {
                    mowerState.setStallTurn(--stallTurn);
                    mower.finishMove(new Move("unknown", 0), mowerState.getState(), mowerState.getStallTurn());
                    return;
    		}
    		//Stall turn is zero , Mower becomes active again
    		if(mowerState.getStallTurn() == 0) {
                    mowerState.setState(constants.MOWER_ACTIVE);
    		}
    		
    	}else{
            
            trackAction = mower.getNextAction();
            trackLawnObject = "mower";
            trackLawnObjectId = id + 1;  //display ID starts at 1

            if (trackAction.equals("turn_off")) {
                trackAction = "turn_off";
                mowerState.setState(constants.MOWER_OFF);
                trackMoveResult = "ok";
                mower.finishMove(new Move(mowerState.getDirection(), 0), mowerState.getState(), 0);
                return;
            }
            else if (trackAction.equals("scan") ){
                // select scanning as the action
                trackAction = "scan";
                //scanSurrounding(mowerState);
                m_scanResult = lawnInfo.getSurroundingSquares(mowerState.getX(), mowerState.getY());
                trackScanResults = SCAN_MAP.get(m_scanResult[0]) + "," +
                        SCAN_MAP.get(m_scanResult[1]) + "," +
                        SCAN_MAP.get(m_scanResult[2]) + "," +
                        SCAN_MAP.get(m_scanResult[3]) + "," +
                        SCAN_MAP.get(m_scanResult[4]) + "," +
                        SCAN_MAP.get(m_scanResult[5]) + "," +
                        SCAN_MAP.get(m_scanResult[6]) + "," +
                        SCAN_MAP.get(m_scanResult[7]);

                mower.provideScanResult(m_scanResult);
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
                    trackMoveResult = "ok";
                    return;
                }
                //Move 1 step
                else if(trackMoveDistance > 0 && trackMoveDistance <= 2) {
                    int xOrientation = constants.xDIR_MAP.get(mowerState.getDirection());
                    int yOrientation = constants.yDIR_MAP.get(mowerState.getDirection());

                    int mowerX = mowerState.getX();     //original X
                    int mowerY = mowerState.getY();     //original Y
                    int newSquareX = mowerX;
                    int newSquareY = mowerY;
                    for (int i = 1; i <= trackMoveDistance; i++) {
                        int oldX = newSquareX;
                        int oldY = newSquareY;
                        newSquareX = mowerX + i * xOrientation;
                        newSquareY = mowerY + i * yOrientation;
                        int curSquareType = lawnInfo.getSquareType(newSquareX, newSquareY);

                        //If mower doesn't crash into fence or crater
                        if ((newSquareX >= 0 & newSquareX < lawnInfo.getWidth() & newSquareY >= 0 & newSquareY < lawnInfo.getHeight())
                                && (curSquareType != constants.CRATER_CODE)) {

                            trackMoveResult = "ok";

                            //Crash into another mower  or mower with puppy on top
                            if (curSquareType == constants.MOWER_CODE || curSquareType == constants.PUPPY_MOWER_CODE) {
                                    trackMoveResult = "stall";
                                    trackActualMovedStep = i - 1;
                                mowerState.setState(constants.MOWER_STALLED);
                                mowerState.setStallTurn(m_collisionDelay);
                                mower.finishMove(new Move(mowerState.getDirection(), i-1), mowerState.getState(), mowerState.getStallTurn());
                                return;
                            }
                            //Crash into a puppy on grass or on empty
                            else if (curSquareType == constants.PUPPY_GRASS_CODE || curSquareType == constants.PUPPY_EMPTY_CODE) {

                                //if (m_lawnInfo[newSquareX][newSquareY] == constants.PUPPY_GRASS_CODE) {
                                    //decrease grass count;;
                                //}
                                trackMoveResult = "stall";
                                trackActualMovedStep = i;
                                mowerState.setState(constants.MOWER_STALLED);
                                mowerState.setX(newSquareX);
                                mowerState.setY(newSquareY);
                                //mowerState.setDirection(trackNewDirection);
                                lawnInfo.updateGrid(oldX, oldY, constants.EMPTY_CODE);
                                lawnInfo.updateGrid(newSquareX, newSquareY, constants.PUPPY_MOWER_CODE);
                                mower.finishMove(new Move(mowerState.getDirection(), i), mowerState.getState(), mowerState.getStallTurn());
                                return;
                            }
                            //Regular grass or empty
                            else {
                                trackActualMovedStep = i;
                                mowerState.setX(newSquareX);
                                mowerState.setY(newSquareY);
                                //mowerState.setDirection(trackNewDirection);
                                lawnInfo.updateGrid(oldX, oldY, constants.EMPTY_CODE);
                                lawnInfo.updateGrid(newSquareX, newSquareY, constants.MOWER_CODE);
                            }
                        }
                        //Crash into fence or crater
                        else {
                            trackMoveResult = "crash";
                            mowerState.setState(constants.MOWER_CRASHED);
                            lawnInfo.updateGrid(oldX, oldY, constants.EMPTY_CODE);
                            mower.finishMove(new Move(mowerState.getDirection(), i-1), mowerState.getState(), 0);
                            return;
                        }
                    }
                    mowerState.setDirection(trackNewDirection);
                    mower.finishMove(new Move(mowerState.getDirection(), trackMoveDistance), mowerState.getState(), mowerState.getStallTurn());
                }
            } 
        }
    }

    public void getPuppyAction() {
        for (int i=0; i < m_puppies.length; ++i) {
            singlePuppy(m_puppies[i], i);
            displayActionAndResponses();
        }
    }
    
    public void singlePuppy(Puppy puppy, int id) {
    	trackLawnObject = "puppy";
    	trackLawnObjectId = id + 1;  //display ID starts at 1
    	trackMoveResult = "ok";
    	
    	if(puppy.isStaying()) {
    		trackAction = "stay";
    		return;
    	}
    	
        List<Integer> availDir = new ArrayList<Integer>();
        int oldX = puppy.getX();     //original X
	int oldY = puppy.getY();     //original Y
        for(int j = 0; j < 8; ++j){ 
            
            String dir = constants.DIRECTIONS[j];
            int xOrientation = constants.xDIR_MAP.get(dir);
	    int yOrientation = constants.yDIR_MAP.get(dir);
            int newX = oldX + 1 * xOrientation;
	    int newY = oldY + 1 * yOrientation;
            
            if (newX <0 || newX >= lawnInfo.getWidth() || newY < 0 || newY >= lawnInfo.getHeight())
	            continue;
            int newSquareType = lawnInfo.getSquareType(newX, newY);
            if ((newX >= 0 & newX < lawnInfo.getWidth() & newY >= 0 & newY < lawnInfo.getHeight())
                    && (newSquareType != constants.CRATER_CODE)
                    && (newSquareType != constants.PUPPY_MOWER_CODE)
                    && (newSquareType != constants.PUPPY_GRASS_CODE)
                    && (newSquareType != constants.PUPPY_EMPTY_CODE)) {
                
                availDir.add(j);
            }
        }
        
        int availSize = availDir.size();
        if(availSize > 0){
            Integer moveRandomChoice = randGenerator.nextInt(availSize);
            String dir = constants.DIRECTIONS[availDir.get(moveRandomChoice)];
            
            int xOrientation = constants.xDIR_MAP.get(dir);
	    int yOrientation = constants.yDIR_MAP.get(dir);
            int newX = oldX + 1 * xOrientation;
	    int newY = oldY + 1 * yOrientation;
            int newSquareType = lawnInfo.getSquareType(newX, newY);
            int oldSquareType = lawnInfo.getSquareType(oldX, oldY);
            
            if(newSquareType == constants.EMPTY_CODE) {
	        lawnInfo.updateGrid(newX, newY, constants.PUPPY_EMPTY_CODE);
	    }
            else if(newSquareType == constants.GRASS_CODE) {
                lawnInfo.updateGrid(newX, newY, constants.PUPPY_GRASS_CODE);
	    }
            else if(newSquareType == constants.MOWER_CODE) {
                lawnInfo.updateGrid(newX, newY, constants.PUPPY_MOWER_CODE);
	        for(int i = 0; i < m_mowerState.length; i++) {
	            if(m_mowerState[i].getX() == newX && m_mowerState[i].getY() == newY) {
	        	m_mowerState[i].setState(constants.MOWER_STALLED);
	        	break;
	            }
	        }
	    }else{
                System.out.println("Error: "+ puppy.getX() + ", " + puppy.getY());
            }
	        	
	    puppy.setX(newX);
	    puppy.setY(newY);
	    trackAction = "move";
	    trackNewX = newX;
	    trackNewY = newY;
	        
	    if(oldSquareType == constants.PUPPY_EMPTY_CODE) {
                lawnInfo.updateGrid(oldX, oldY, constants.EMPTY_CODE);
	    }
            else if(oldSquareType == constants.PUPPY_GRASS_CODE) {
                lawnInfo.updateGrid(oldX, oldY, constants.GRASS_CODE);
	    }
            else if(oldSquareType == constants.PUPPY_MOWER_CODE) {
                lawnInfo.updateGrid(oldX, oldY, constants.MOWER_CODE);
	        for(int i = 0; i < m_mowerState.length; i++) {
                    if(m_mowerState[i].getX() == oldX && m_mowerState[i].getY() == oldY) {
                        m_mowerState[i].setState(constants.MOWER_ACTIVE);
                        break;
                    }
	        }
	    }
        }
        else{
            //cannot find a valid move, therefore stay
            trackAction = "stay";
        }
        
    }

    public String displayActionAndResponses_UI(int x, int y) {
        
        displayActionAndResponses();

    	StringBuilder sb = new StringBuilder();
        sb.append("<table class='singleLog'>");
        sb.append("<tr><td class='name'>");
    	if(trackLawnObject.equals("mower")) {
            sb.append("Mower #" + trackLawnObjectId +"\n" + "("+x+","+y+")");
            sb.append("</td><td>");
            if (trackAction.equals("turn_off")) {
                sb.append(trackAction + "<br>" + trackMoveResult);
            } 
            else if (trackAction.equals("scan")) {
            	sb.append(trackAction + "<br>" + trackScanResults);
            } 
            else if (trackAction.equals("move")) {
            	
            	if(trackMoveResult.equals("ok") || trackMoveResult.equals("crash")) {
            		sb.append(trackAction + "," + trackMoveDistance+ "," + trackNewDirection + "<br>");
            		sb.append(trackMoveResult);
            	}
            	else if(trackMoveResult.equals("stall")) {
            		sb.append(trackAction + "," + trackMoveDistance+ "," + trackNewDirection + "<br>");
            		sb.append(trackMoveResult + "," + trackActualMovedStep);
            	}
            } 
            else {
                sb.append("action not recognized");
            }   		
    	}

    	if(trackLawnObject.equals("puppy")) {
            sb.append("Puppy #" + trackLawnObjectId+"\n" + "("+x+","+y+")");
            sb.append("</td><td>");
            if (trackAction.equals("stay")) {
                sb.append(trackAction + "<br>" + trackMoveResult);
            } 
            else if (trackAction.equals("move")) {

            	sb.append(trackAction + "," + trackNewX + "," + trackNewY + "<br>");
            	sb.append(trackMoveResult);

            } 
            else {
                sb.append("action not recognized");
            }   		
    	}
        sb.append("</td></tr>");
        sb.append("</table>");
        return sb.toString();
    }
    
    public void displayActionAndResponses() {
    	
    	if(trackLawnObject.equals("mower")) {
            log.append("mower" + ","  + trackLawnObjectId+'\n');
    		
            if (trackAction.equals("turn_off")) {
                log.append(trackAction+'\n');
                log.append(trackMoveResult+'\n');
            } 
            else if (trackAction.equals("scan")) {
            	log.append(trackAction+'\n');
                log.append(trackScanResults+'\n');
            } 
            else if (trackAction.equals("move")) {
            	
            	if(trackMoveResult.equals("ok") || trackMoveResult.equals("crash")) {
                    log.append(trackAction + "," + trackMoveDistance+ "," + trackNewDirection+'\n');
                    log.append(trackMoveResult+'\n');
            	}
            	else if(trackMoveResult.equals("stall")) {
                    log.append(trackAction + "," + trackMoveDistance+ "," + trackNewDirection+'\n');
                    log.append(trackMoveResult + "," + trackActualMovedStep+'\n');
            	}
            } 
            else {
                System.out.println("action not recognized");
            }   		
    	}

    	if(trackLawnObject.equals("puppy")) {
    		log.append("puppy" + ","  + trackLawnObjectId+'\n');
    		
            if (trackAction.equals("stay")) {
                log.append(trackAction+'\n');
                log.append(trackMoveResult+'\n');
            } 
            else if (trackAction.equals("move")) {

            	log.append(trackAction + "," + trackNewX + "," + trackNewY+'\n');
            	log.append(trackMoveResult+'\n');

            } 
            else {
                System.out.println("action not recognized");
            }   		
    	}
    }
    
        public void printFinalReport(){
        int total = 0;
            for (int i = 0; i < lawnInfo.getWidth(); i++) {
                for (int j = 0; j < lawnInfo.getHeight(); j++) {
                    int curSquare = lawnInfo.getSquareType(i,j);
                    if(curSquare == constants.EMPTY_CODE
                        || curSquare == constants.PUPPY_EMPTY_CODE
                        || curSquare == constants.PUPPY_MOWER_CODE
                        || curSquare == constants.MOWER_CODE)
                    {
                        total++;
                    }
                }
            }
        int lawnSize = lawnInfo.getWidth() * lawnInfo.getHeight();
        int numGrass = lawnSize - m_numCrater;
        log.append(lawnSize + "," + numGrass + "," + total + "," + m_turn);
    }
    
    public String printFinalReport_UI(){
        printFinalReport();

        int total = 0;
            for (int i = 0; i < lawnInfo.getWidth(); i++) {
                for (int j = 0; j < lawnInfo.getHeight(); j++) {
                    int curSquare = lawnInfo.getSquareType(i,j);
                    if(curSquare == constants.EMPTY_CODE
                        || curSquare == constants.PUPPY_EMPTY_CODE
                        || curSquare == constants.PUPPY_MOWER_CODE
                        || curSquare == constants.MOWER_CODE)
                    {
                        total++;
                    }
                }
            }
        int lawnSize = lawnInfo.getWidth() * lawnInfo.getHeight();
        int numGrass = lawnSize - m_numCrater;
        return lawnSize + "," + numGrass + "," + total + "," + m_turn;
    }
    
        public String getStatus_UI(){
        int cut = 0;
            for (int i = 0; i < lawnInfo.getWidth(); i++) {
                for (int j = 0; j < lawnInfo.getHeight(); j++) {
                    int curSquare = lawnInfo.getSquareType(i,j);
                    if(curSquare == constants.EMPTY_CODE
                        || curSquare == constants.PUPPY_EMPTY_CODE
                        || curSquare == constants.PUPPY_MOWER_CODE
                        || curSquare == constants.MOWER_CODE)
                    {
                        cut++;
                    }
                }
            }
        int lawnSize = lawnInfo.getWidth() * lawnInfo.getHeight();
        int numGrass = lawnSize - m_numCrater;
        
        DecimalFormat df = new DecimalFormat("#.##");
        double percentage = (double)cut/numGrass;
        percentage *= 100;
        int remain = numGrass-cut;
        
        String html = "<table class='status'><tr bgcolor=\"#eeeeee\"><td>Turn(s)</td><td>Cut</td><td>Grass Remaining</td><td>Percent Done(%)</td></tr><tr><td>#";
        html += m_turn +"</td><td>" + cut +"</td><td>" + remain +"</td><td>" + df.format(percentage) +" %</td></tr></table>";
        
        return html;
    }

    public String getLog(){
        return log.toString();
    }
   
    public String renderLawnForUI(int x, int y) {
        return buildHTML(lawnInfo.renderLawnForUI(), x, y);
    }

    public String buildHTML(String lawn,int x, int y){
        int nowY = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        String[] lines = lawn.split("\n");

        for(String line:lines){
            int nowX = 0;
            ++nowY;
            sb.append("<tr>");
            String[] spots = line.split("\\|");
            for(String spot:spots){
                if(spot.equals(""))
                    continue;
                sb.append("<td>");
                String img = "<img src=\"";
                if(spot.equals("  ")){
                    img += "image/cut.png";
                }else if(spot.equals(" g")){
                    img += "image/grass.png";
                }else if(spot.equals(" c")){
                    img += "image/crater.png";
                }else if(spot.equals(" m")){
                    img += "image/mower.png";
                }else if(spot.equals("p ")){
                    img += "image/cut_puppy.png";
                }else if(spot.equals("pm")){
                    img += "image/mower_puppy.png";
                }else if(spot.equals("pg")){
                    img += "image/grass_puppy.png";
                }
                if(nowX++==x && (m_lawnHeight-nowY) ==y){
                    
                    img += "\" class=\"spot now\" />";
                }else{
                    img += "\" class=\"spot\" />";
                }
                sb.append(img);
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    
    public Integer getMaxTurn() {
        return m_maxTurn;
    }
    
    //TODO: Implement Stop run
    public boolean stopRun() {
        return m_stopRun;
    }
    
    public Mower[] getMowers(){
        return m_mowers;
    }
    
    public Puppy[] getPuppies(){
        return m_puppies;
    }

    public MowerState[] getMowerStates(){
        return m_mowerState;
    }

}
