package core;

public abstract class LawnmowerShared {
    public static Lawn grid_observed = null;
    public static int knownHeight = 1;
    public static int knownWidth = 1;
    public Constants c = new Constants();

    private boolean fencesFound(int knownBaseX, int knownBaseY){
        int [][] knowledgeMap = grid_observed.getGrid();
        //check columns
        for(int j =0; j< knownHeight; j++){
            if (knowledgeMap[knownBaseX][knownBaseY+j] != c.FENCE_CODE || knowledgeMap[knownBaseX+knownWidth-1][knownBaseY+j] != c.FENCE_CODE){
                return false;
            }
        }

        //check rows
        for(int j =0; j< knownWidth; j++){
            if (knowledgeMap[knownBaseX+j][knownBaseY] != c.FENCE_CODE || knowledgeMap[knownBaseX+j][knownBaseY+knownHeight-1] != c.FENCE_CODE){
                return false;
            }
        }
        return true;
    }

    public boolean surroundedByFence(int knownBaseX, int knownBaseY){
        int [][] knowledgeMap = grid_observed.getGrid();
        // check if all the blocks inside are empty
        for(int j =1; j< knownWidth-1; j++){
            for(int i =1; i< knownHeight-1; i++){
                int squareType = knowledgeMap[knownBaseX+j][knownBaseY+i];
                if (squareType == c.GRASS_CODE || squareType == c.PUPPY_GRASS_CODE || squareType == c.UNKNOWN_CODE){
                    return false;
                }
            }
        }

        //check if fence exist and it makes an enclosed area
        return fencesFound(knownBaseX, knownBaseY);
    }
}
