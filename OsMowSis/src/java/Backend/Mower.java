package Backend;

public class Mower {
	private String mowerDirection;
	private int mowerX, mowerY;
    private int absX, absY;
    
    private Constants code;
    
    public Mower(){
    	code = new Constants();
    	mowerX = 0;
        mowerY = 0;
    }
    
    public void update(String[] action){
    	mowerX += code.xDIR_MAP.get(mowerDirection)*Integer.parseInt(action[1]);
    	mowerY += code.yDIR_MAP.get(mowerDirection)*Integer.parseInt(action[1]);
    	mowerDirection = action[2];
    }
    
    public void setAbs(int x, int y){
        absX = x;
        absY = y;
    }
    
    public void setDirec(String direc){
    	mowerDirection = direc;
    }
    public String getDirec(){
    	return mowerDirection;
    }
    
    public void setX(int x){
    	mowerX = x;
    }
    public int getX(){
    	return mowerX;
    }
    public void setY(int y){
    	mowerY = y;
    }
    public int getY(){
    	return mowerY;
    }
    public int getAbsX(){
    	return absX;
    }
    public int getAbsY(){
    	return absY;
    }
    
}
