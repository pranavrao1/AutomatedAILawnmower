import java.util.HashMap;

public final class Constants {

	public final int EMPTY_CODE = 0;
	public final int GRASS_CODE = 1;
	public final int MOWER_CODE = 2;
	public final int CRATER_CODE = 3;
	public final int FENCE_CODE = 4;
	public final int PUPPY_EMPTY_CODE = 5;
	public final int PUPPY_GRASS_CODE = 6;
	public final int PUPPY_MOWER_CODE = 7;
    public final int UNKNOWN_CODE = 8;
    public final String[] SQUARES = {"empty","grass","mower","crater","fence","puppy_empty","puppy_grass","puppy_mower","unknown"};
	
	public final int MOWER_ACTIVE = 0;
	public final int MOWER_STALLED = 1;
	public final int MOWER_CRASHED = 2;
	public final int MOWER_OFF = 3;
    public final String[] MOWER_STATE = {"ok", "stall", "crash", "off"};

    public final int DEFAULT_WIDTH = 15;
    public final int DEFAULT_HEIGHT = 10;
    
    public final String[] DIRECTIONS = {"north","northeast","east","southeast","south","southwest","west","northwest"};
    public final HashMap<String, Integer> xDIR_MAP;
    public final HashMap<String, Integer> yDIR_MAP;
    
    
    public Constants(){
    	xDIR_MAP = new HashMap<>();
        xDIR_MAP.put("north", 0);
        xDIR_MAP.put("northeast", 1);
        xDIR_MAP.put("east", 1);
        xDIR_MAP.put("southeast", 1);
        xDIR_MAP.put("south", 0);
        xDIR_MAP.put("southwest", -1);
        xDIR_MAP.put("west", -1);
        xDIR_MAP.put("northwest", -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put("north", 1);
        yDIR_MAP.put("northeast", 1);
        yDIR_MAP.put("east", 0);
        yDIR_MAP.put("southeast", -1);
        yDIR_MAP.put("south", -1);
        yDIR_MAP.put("southwest", -1);
        yDIR_MAP.put("west", 0);
        yDIR_MAP.put("northwest", 1);
    }
    
}