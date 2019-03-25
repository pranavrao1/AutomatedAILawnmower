import java.util.HashMap;

public final class Constants {
    public final int EMPTY_CODE = 0;
    public final int GRASS_CODE = 1;
    public final int CRATER_CODE = 2;
    public final String[] DIRECTIONS = {"North","Northeast","East","Southeast","South","Southwest","West","Northwest"};
    public final HashMap<String, Integer> xDIR_MAP;
    public final HashMap<String, Integer> yDIR_MAP;
    
    
    public Constants(){
    	xDIR_MAP = new HashMap<>();
        xDIR_MAP.put("North", 0);
        xDIR_MAP.put("Northeast", 1);
        xDIR_MAP.put("East", 1);
        xDIR_MAP.put("Southeast", 1);
        xDIR_MAP.put("South", 0);
        xDIR_MAP.put("Southwest", -1);
        xDIR_MAP.put("West", -1);
        xDIR_MAP.put("Northwest", -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put("North", 1);
        yDIR_MAP.put("Northeast", 1);
        yDIR_MAP.put("East", 0);
        yDIR_MAP.put("Southeast", -1);
        yDIR_MAP.put("South", -1);
        yDIR_MAP.put("Southwest", -1);
        yDIR_MAP.put("West", 0);
        yDIR_MAP.put("Northwest", 1);
    }
    
}