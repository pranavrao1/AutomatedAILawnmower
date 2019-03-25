import java.io.File;
import java.util.Scanner;

public class Simulator {

	private Lawn lawn;
	private Constants code;
	// private Mower mower;
	private int absX;
	private int absY;
	private int lawnWidth;
	private int lawnHeight;
	private boolean isTerminated;
	private MowingService ms;
	private boolean submit = true;

	public Simulator() {
		lawn = new Lawn();
		code = new Constants();
		isTerminated = false;
		ms = new MowingService();
	}

	public void uploadStartingFile(String testFileName) {

		final String DELIMITER = ",";

		try {
			Scanner takeCommand = new Scanner(new File(testFileName));
			String[] tokens;
			int k;

			// read in the lawn information
			tokens = takeCommand.nextLine().split(DELIMITER);
			int w = Integer.parseInt(tokens[0]);
			lawn.setLawnWidth(w);

			tokens = takeCommand.nextLine().split(DELIMITER);
			int h = Integer.parseInt(tokens[0]);
			lawn.setLawnHeight(h);

			lawnWidth = w;
			lawnHeight = h;

			// generate the lawn information
			lawn.initLawnInfo();

			// read in the lawnmower starting information
			tokens = takeCommand.nextLine().split(DELIMITER);

			// Number of mower will be always 1 for this assignment.
			int numMowers = Integer.parseInt(tokens[0]);

			for (k = 0; k < numMowers; k++) {
				tokens = takeCommand.nextLine().split(DELIMITER);
				// Only Simulator knows the init position of the mower
				absX = Integer.valueOf(tokens[0]);
				absY = Integer.valueOf(tokens[1]);
				ms.setMowerDirec(tokens[2]);

				// mow the grass at the initial location
				ms.setMowerAbs(absX, absY);
				lawn.setLawnInfo(absX, absY, code.EMPTY_CODE);
			}

			// read in the crater information
			tokens = takeCommand.nextLine().split(DELIMITER);
			int numCraters = Integer.parseInt(tokens[0]);
			lawn.initTotals(numCraters);

			for (k = 0; k < numCraters; k++) {
				tokens = takeCommand.nextLine().split(DELIMITER);
				lawn.setLawnInfo(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), code.CRATER_CODE);
			}

			takeCommand.close();

			// lawn.renderLawn(ms.getMower());

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
		}
	}

	public void pollMowerForAction() {
		String[] newMowerAction = ms.findNextAction(); // {action,steps,direction}
		if (newMowerAction[0].equals("done")) {
			if (submit) {
				System.out.println("turn_off");
				System.out.println("ok");
			}

			isTerminated = true;
		} else {
			validateMowerActionAndUpdateLawn(ms.getMower(), newMowerAction);

			if (!newMowerAction[0].equals("scan"))
				ms.setMowerDirec(newMowerAction[2]);

			lawn.increaseTotalAction();
		}

	}

	public void validateMowerActionAndUpdateLawn(Mower mower, String[] newMowerAction) {
    	
        String action = newMowerAction[0];
        String printAction = "";
        String printResult = "";
        
        if (action.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation
        	printAction = "scan";
        	if(submit){
        		System.out.println("scan");
            	System.out.println(scan(mower));
        	}else{
        		scan(mower);
        	}
        	
        } else if (action.equals("move")) {
			 String mowerDirection = mower.getDirec();
			 int steps = Integer.parseInt(newMowerAction[1]);
			 String newDirec = newMowerAction[2];
			 
			 if(steps==0){
				printAction = "ok";
             	printResult = "move," + steps +"," + newDirec;
			 }
			 else{
				 int mowerX = mower.getX() + code.xDIR_MAP.get(mowerDirection) + mower.getAbsX();
	             int mowerY = mower.getY() + code.yDIR_MAP.get(mowerDirection) + mower.getAbsY();
	             
	             if (mowerX >= 0 & mowerX < lawnWidth & mowerY >= 0 & mowerY < lawnHeight) {
	        		if(lawn.getLawnInfo(mowerX,mowerY)==code.CRATER_CODE){
	        			//hit the crater.
	                	printAction = "crash";
	                	isTerminated = true;
	                }else{
	                	printAction = "ok";
	                	printResult = "move," + steps +"," + newDirec;
	                	
	                	lawn.setLawnInfo(mowerX,mowerY,code.EMPTY_CODE);
	                	mower.setX(mower.getX() + code.xDIR_MAP.get(mowerDirection));
	                	mower.setY(mower.getY() + code.yDIR_MAP.get(mowerDirection));
	                }
	             }else {
	        		//hit the fence.
	            	printAction = "crash";
	            	isTerminated = true;
	             }

	             if(steps==2 && printAction.equals("ok")){
	        		mowerX += code.xDIR_MAP.get(mowerDirection);
	        		mowerY += code.yDIR_MAP.get(mowerDirection);
	        		
	        		if (mowerX >= 0 & mowerX < lawnWidth & mowerY >= 0 & mowerY < lawnHeight) {
	            		if(lawn.getLawnInfo(mowerX,mowerY)==code.CRATER_CODE){
	            			//hit the crater.
	                    	printAction = "crash";
	                    	isTerminated = true;
	                    }else{
	                    	printAction = "ok";
	                    	printResult = "move," + steps +"," + newDirec;
	                    	
	                    	lawn.setLawnInfo(mowerX,mowerY,code.EMPTY_CODE);
	                    	mower.setX(mower.getX() + code.xDIR_MAP.get(mowerDirection));
	                    	mower.setY(mower.getY() + code.yDIR_MAP.get(mowerDirection));
	                    }
	            	}else {
	            		//hit the fence.
	                	printAction = "crash";
	                	isTerminated = true;
	                }
	        	}
			 }
             
			 if(submit){
				 if(printResult.length()>0)
	            	System.out.println(printResult);
	            System.out.println(printAction);
			 }
            
            
        } else if (action.equals("turn_off")) {
        	printAction = "ok";
        	if(submit){
	        	System.out.println(printAction);
	        	isTerminated = true;
        	}
        }
//        if(submit)
//        	lawn.renderLawn(mower);
    }

	public String scan(Mower mower) {

		StringBuilder sb = new StringBuilder();
		String[] resultSet = new String[8];
		int index = 0;
		// Need to add myLawn update method
		for (String direc : code.DIRECTIONS) {

			int x = mower.getX() + code.xDIR_MAP.get(direc) + mower.getAbsX();
			int y = mower.getY() + code.yDIR_MAP.get(direc) + mower.getAbsY();

			if (x < lawn.getLawnWidth() && x >= 0 && y < lawn.getLawnHeight() && y >= 0) {
				int code = lawn.getLawnInfo(x, y);
				sb.append(codeToString(code));
				resultSet[index] = codeToString(code);
			} else {
				resultSet[index] = "fence";
				sb.append("fence");
			}
			sb.append(",");
			index++;
		}

		ms.updateMap(resultSet);
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	public String codeToString(int code) {
		if (code == 0) {
			return "empty";
		} else if (code == 1) {
			return "grass";
		} else {
			return "crater";
		}
	}

	public boolean getIsTerminated() {
		if (ms.isDone()) {
			if(submit){
				System.out.println("turn_off");
				System.out.println("ok");
			}
			return true;
		}
		return isTerminated;
	}

	// public void displayActionAndResponses() {
	// // display the mower's actions
	// System.out.print(trackAction);
	// if (trackAction.equals("move")) {
	// System.out.println("," + trackMoveDistance + "," + trackNewDirection);
	// } else {
	// System.out.println();
	// }
	//
	// // display the simulation checks and/or responses
	// if (trackAction.equals("move") | trackAction.equals("turn_off")) {
	// System.out.println(trackMoveCheck);
	// } else if (trackAction.equals("scan")) {
	// System.out.println(trackScanResults);
	// } else {
	// System.out.println("action not recognized");
	// }
	// }

	public void printFinalReport() {
		System.out.println(lawn.print());
	}

}