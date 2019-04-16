package Backend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class MowingService {
	
	private HashMap<String,String> map;
	private Constants code;
	private Mower mower;
	private int[] endPoints; //north,east,south,west
	private int area;
	private int cut;
	private boolean gotArea;
	private int crater;
	private Stack<String[]> history;
	private int preStep;
	
	public MowingService(){
		map = new HashMap<>();
		code = new Constants();
		mower = new Mower();
		endPoints = new int[4];
		Arrays.fill(endPoints, -1);
		area = -1;
		cut = 0;
		gotArea = false;
		crater = 0;
		history = new Stack<>();
		preStep = 0;
	}
	public String[] findNextAction(){
		String[] action = new String[3]; //action,steps,direction;
		System.out.println("mowers " + mower==null);
		String direc = mower.getDirec();
		
		String location = (mower.getX() + code.xDIR_MAP.get(direc))+","+ (mower.getY() + code.yDIR_MAP.get(direc));
		
		int step = 0;
		
		if(map.containsKey(location)){
			if(map.get(location).equals("crater") || map.get(location).equals("fence") || map.get(location).equals("empty")){
				String nextDirec = findGrass(mower.getX(),mower.getY());
				if(nextDirec.equals("none")){
					return goBack();
				}else if(nextDirec.equals("scan")){
					String[] scan = {"scan"};
					return scan;
				}else{
					direc = nextDirec;
				}
			}else{
				step++;
				location = (mower.getX() + code.xDIR_MAP.get(direc)*2)+","+ (mower.getY() + code.yDIR_MAP.get(direc)*2);
				if(map.containsKey(location)){
					if(map.get(location).equals("crater") || map.get(location).equals("fence") || map.get(location).equals("empty")){
						String nextDirec = findGrass(mower.getX() + code.xDIR_MAP.get(direc),mower.getY() + code.yDIR_MAP.get(direc));
						if(!nextDirec.equals("scan") && !nextDirec.equals("none"))
							 direc = nextDirec;
					}else{
						step++;
					}
				}
			}
			action[0]="move";
			action[1]= String.valueOf(step);
			action[2]= direc;
			if(step!=0){
				String[] s = {String.valueOf(step),mower.getDirec()};
				history.push(s);
			}
			
			updateCut(step);
		}else{
			action[0]="scan";
		}
		
		return action;
	}
	public void updateCut(int step){
		if(step!=0){
			String xy = (mower.getX() + code.xDIR_MAP.get(mower.getDirec())) +","+(mower.getY() + code.yDIR_MAP.get(mower.getDirec()));
			if(map.get(xy).equals("grass")){
				map.put(xy, "empty");
				cut++;
			}
				
			if(step==2){
				xy = (mower.getX() + code.xDIR_MAP.get(mower.getDirec())*2)+","+ (mower.getY() + code.yDIR_MAP.get(mower.getDirec())*2);
				if(map.get(xy).equals("grass")){
					map.put(xy, "empty");
					cut++;
				}
			}
		}
	}
	public void updateMap(String[] data){
		int index = 0;
		for(String direc:code.DIRECTIONS){
    		String xy = (mower.getX() + code.xDIR_MAP.get(direc)) +","+(mower.getY() + code.yDIR_MAP.get(direc));
    		if(!map.containsKey(xy)){
    			map.put(xy, data[index]);
        		if(data[index].equals("crater")){
        			crater++;
        		}
    		}
    		index++;
    	}
		
		if(data[0].equals("fence") && data[7].equals("fence") && data[1].equals("fence"))
			endPoints[0] = Math.abs(mower.getY());
		else if(data[1].equals("fence") && data[2].equals("fence") && data[3].equals("fence"))
			endPoints[1] = Math.abs(mower.getX());
		else if(data[5].equals("fence") && data[4].equals("fence") && data[3].equals("fence"))
			endPoints[2] = Math.abs(mower.getY());
		else if(data[5].equals("fence") && data[6].equals("fence") && data[7].equals("fence"))
			endPoints[3] = Math.abs(mower.getX());
		else if(data[0].equals("fence") && data[1].equals("fence") && data[2].equals("fence")){
			endPoints[0] = Math.abs(mower.getY());
			endPoints[1] = Math.abs(mower.getX());
		}else if(data[2].equals("fence") && data[3].equals("fence") && data[4].equals("fence")){
			endPoints[1] = Math.abs(mower.getX());
			endPoints[2] = Math.abs(mower.getY());
		}else if(data[4].equals("fence") && data[5].equals("fence") && data[6].equals("fence")){
			endPoints[2] = Math.abs(mower.getY());
			endPoints[3] = Math.abs(mower.getX());
		}else if(data[6].equals("fence") && data[7].equals("fence") && data[0].equals("fence")){
			endPoints[0] = Math.abs(mower.getY());
			endPoints[3] = Math.abs(mower.getX());
		}
		verify();
	}
	
	public void verify(){
//		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$"+ endPoints[0]+", "+endPoints[1]+", "+endPoints[2]+", "+endPoints[3]);
		if(endPoints[0]>=0 && endPoints[2]>=0 && endPoints[1]>=0 && endPoints[3]>=0){
			int width = endPoints[1]+endPoints[3]+1;
			int height = endPoints[0]+endPoints[2]+1;
			area = width*height;
			
//			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$"+ map.size()+": "+area+"-"+cut+"-"+crater);
			if(!map.containsValue("grass")){
				gotArea = true;
			}
		}
//		for(String key : map.keySet()){
//			System.out.println(key+"    "+map.get(key));
//		}
	}
	
	public boolean isDone(){
		if(gotArea){
//			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$"+ map.size()+": "+area+"-"+cut+"-"+crater);
			if(area - cut - crater == 1){
//				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$"+ map.size()+": "+area+"-"+cut+"-"+crater);
				return true;
			}
		}
//		for(String key : map.keySet()){
//			System.out.println(key+"    "+map.get(key));
//		}
		return false;
	}
	public void setMowerDirec(String direc){
		mower.setDirec(direc);
	}
	public void setMowerAbs(int absX, int absY){
		mower.setAbs(absX,absY);
	}
	public Mower getMower(){
		return mower;
	}
	private String findGrass(int x, int y){
		for(String direc:code.DIRECTIONS){
    		String xy = (mower.getX() + code.xDIR_MAP.get(direc)) +","+(mower.getY() + code.yDIR_MAP.get(direc));
    		if(map.containsKey(xy) ){
    			if(map.get(xy).equals("grass"))
    				return direc;
    		}else{
    			return "scan";
    		}
    	}
		return "none";
	}
	
	private String[] goBack(){
		
		String[] result = new String[3];
		
		if(preStep==0){ //change direction only to go back.
			if(history.isEmpty()){
				result[0] = "done";
				return result;
			}
			String[] pre = history.pop();
			preStep = Integer.valueOf(pre[0]);
			String direc = pre[1];
			direc = getOppositeDirec(direc);
			result[0]="move";
			result[1]= "0";
			result[2]= direc;
		}else{
			int x = mower.getX() + code.xDIR_MAP.get(mower.getDirec())*preStep;
			int y = mower.getY() + code.yDIR_MAP.get(mower.getDirec())*preStep;
			String direc = mower.getDirec();
			for(String d:code.DIRECTIONS){
	    		String xy = (x + code.xDIR_MAP.get(d)) +","+(y + code.yDIR_MAP.get(d));
	    		if(map.containsKey(xy) && map.get(xy).equals("grass")){
	    			direc = d;
	    			break;
	    		}
	    	}
			result[0]="move";
			result[1]= String.valueOf(preStep);
			result[2]= direc;
			preStep =0;
		}
		verify();
		return result;
	}
	private String getOppositeDirec(String direc){
		
		if(direc.equals("North"))
			return "South";
		else if(direc.equals("Northeast"))
			return "Southwest";
		else if(direc.equals("East"))
			return "West";
		else if(direc.equals("Southeast"))
			return "Northwest";
		else if(direc.equals("South"))
			return "North";
		else if(direc.equals("Southwest"))
			return "Northeast";
		else if(direc.equals("West"))
			return "East";
		else
			return "Southeast";
			
	}
}
