package Backend;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Simulator {

	private Lawn lawn;
        private static Simulator instance;
	private Constants code;
	// private Mower mower;
	private int absX;
	private int absY;
	private int lawnWidth;
	private int lawnHeight;
	private boolean isTerminated;
	private MowingService ms;
	private boolean submit = true;
        boolean fileUploaded = false;
        
        public static synchronized Simulator getInstance(){
            if(instance == null){
                instance = new Simulator();
            }
            return instance;
        }
        
	public Simulator() {
            lawn = new Lawn();
            code = new Constants();
            isTerminated = false;
            ms = new MowingService();
            instance = this;
            System.out.println("started");
	}
            
	public String uploadStartingFile(InputStream is){

            final String DELIMITER = ",";
            

            try {
                BufferedReader takeCommand = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String[] tokens;
                int k;

                // read in the lawn information
                while(takeCommand.readLine().length()>1);
                
                tokens = takeCommand.readLine().split(DELIMITER);

                int w = Integer.parseInt(tokens[0]);
                lawn.setLawnWidth(w);

                tokens = takeCommand.readLine().split(DELIMITER);
                int h = Integer.parseInt(tokens[0]);
                lawn.setLawnHeight(h);

                lawnWidth = w;
                lawnHeight = h;

                // generate the lawn information
                lawn.initLawnInfo();

                // read in the lawnmower starting information
                tokens = takeCommand.readLine().split(DELIMITER);

                // Number of mower will be always 1 for this assignment.
                int numMowers = Integer.parseInt(tokens[0]);

                for (k = 0; k < numMowers; k++) {
                        tokens = takeCommand.readLine().split(DELIMITER);
                        // Only Simulator knows the init position of the mower
                        absX = Integer.valueOf(tokens[0]);
                        absY = Integer.valueOf(tokens[1]);
                        ms.setMowerDirec(tokens[2]);

                        // mow the grass at the initial location
                        ms.setMowerAbs(absX, absY);
                        lawn.setLawnInfo(absX, absY, code.EMPTY_CODE);
                }

                // read in the crater information
                tokens = takeCommand.readLine().split(DELIMITER);
                int numCraters = Integer.parseInt(tokens[0]);
                lawn.initTotals(numCraters);

                for (k = 0; k < numCraters; k++) {
                        tokens = takeCommand.readLine().split(DELIMITER);
                        lawn.setLawnInfo(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), code.CRATER_CODE);
                }

                takeCommand.close();

                

            } catch (Exception e) {
                    e.printStackTrace();
            }
            return createLanwHTML(lawn.renderLawn(ms.getMower()));
	}
        
        public String createLanwHTML(String lawn){
            
            StringBuilder sb = new StringBuilder();
            sb.append("<table>");
            String[] lines = lawn.split("\n");
            
            for(String line:lines){
                sb.append("<tr>");
                String[] spots = line.split("|");
                for(String spot:spots){
                    if(spot.equals("|"))
                        continue;
                    sb.append("<td>");
                    
                    String img = "<img src=\"";
                    if(spot.equals("M")){
                        img += "image/mower_n.png";
                    }else if(spot.equals("e")){
                        img += "image/cut.png";
                    }else if(spot.equals("g")){
                        img += "image/grass.png";
                    }else if(spot.equals("c")){
                        img += "image/crater.png";
                    }
                    img += "\" class=\"spot\" />";
                    sb.append(img);
                    sb.append("</td>");
                }
                sb.append("</tr>");
            }
            sb.append("</table>");
            return sb.toString();
        }
	public String pollMowerForAction() {
            
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
                return createLanwHTML(lawn.renderLawn(ms.getMower()));
	}

	public void validateMowerActionAndUpdateLawn(Mower mower, String[] newMowerAction) {
    	
        String action = newMowerAction[0];
        String printAction = "";
        String printResult = "";
        
        if (action.equals("scan")) {
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

	public void printFinalReport() {
		System.out.println(lawn.print());
	}

}