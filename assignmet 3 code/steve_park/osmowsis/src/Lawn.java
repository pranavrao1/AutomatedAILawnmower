
public class Lawn {
	
	private Constants code;
	private int lawnHeight;
	private int lawnWidth;
	private int[][] lawnInfo;
	private int percentageOfDone;
	private int totalNumSquares;
	private int totalNumGrass;
	private int totalNumCut;
	private int totalAction;
	
    public Lawn(){
        lawnHeight = 0;
        lawnWidth = 0;
        code = new Constants();
    }
    
    public void initLawnInfo(){
    	lawnInfo = new int[lawnWidth][lawnHeight];
    	for (int i = 0; i < lawnWidth; i++) {
            for (int j = 0; j < lawnHeight; j++) {
                lawnInfo[i][j] = code.GRASS_CODE;
            }
        }
    }
    
    public void setLawnHeight(int height){
    	lawnHeight = height;
    }
    
    public int getLawnHeight(){
    	return lawnHeight;
    }
    
    public void setLawnWidth(int width){
    	lawnWidth = width;
    }
    
    public int getLawnWidth(){
    	return lawnWidth;
    }
    
    public void setLawnInfo(int x,int y, int status){
    	
    	if(status==code.EMPTY_CODE && lawnInfo[x][y]==code.GRASS_CODE )
    		totalNumCut++;
    	
    	lawnInfo[x][y] = status;
    }
    public int getLawnInfo(int x, int y){
    	return lawnInfo[x][y];
    }
    
    public int[][] getLawnInfo(){
    	return lawnInfo;
    }
    
    public void increaseTotalAction(){
    	totalAction++;
    }
    
    public void initTotals(int numCraters){
    	totalNumSquares = lawnWidth * lawnHeight;
    	totalNumGrass = totalNumSquares - numCraters;
    	totalNumCut = 1; //Mower starts on grass.
    	totalAction = 0;
    }
    
    public void calPercentageOfDone(){
    	
    }
    
    public int getPercentageOfDone(){
    	return percentageOfDone;
    }
    
    public String print(){
//    	System.out.println(lawnWidth +","+lawnHeight+","+numCraters);
//    	float percent = (totalNumCut * 100.0f) / totalNumGrass;
//    	return percent + "%  "+ totalNumSquares+","+ totalNumGrass +"," + totalNumCut +"," + totalAction;
    	return totalNumSquares+","+ totalNumGrass +"," + totalNumCut +"," + totalAction;
    }
    
    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    
    public void renderLawn(Mower mower) {
        int i, j;
        int charWidth = 2 * lawnWidth + 2;
        // display the rows of the lawn from top to bottom
        int mowerX = mower.getX()+mower.getAbsX();
        int mowerY = mower.getY()+mower.getAbsY();
        
    	
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
                        case 0:
                            System.out.print(" ");
                            break;
                        case 1:
                            System.out.print("g");
                            break;
                        case 2:
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

//        // display the mower's direction
//        System.out.println("dir: " + mowerDirection);
        System.out.println("");
    }
}