/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osmowsis;

import java.util.HashMap;

/**
 *
 * @author oscarc
 */
public class Mower {
    
    private String m_currentDir;
    private String m_nextAction;
    private String m_lastAction;
    private Move   m_nextMove;
    private int    m_totalTurn;
    private Boolean m_bRedirect;   
    private Integer m_scanResults[];
    private HashMap<Integer, String> SCAN_MAP;
    private HashMap<String, Integer> DIR_MAP;
    
    private final int EMPTY_CODE = 0;
    private final int GRASS_CODE = 1;
    private final int CRATER_CODE = 2;
    private final int FENCE_CODE = 3;
    
    public Mower(String direction){
        
        m_currentDir = direction;
        m_nextAction = "scan";
        m_lastAction = "scan";
        m_scanResults = new Integer[8];
        m_nextMove = new Move();
        m_totalTurn = 0;
        m_bRedirect = false;
        
        SCAN_MAP = new HashMap<>();
        SCAN_MAP.put(0, "North");
        SCAN_MAP.put(1, "Northeast");
        SCAN_MAP.put(2, "East");
        SCAN_MAP.put(3, "Southeast");
        SCAN_MAP.put(4, "South");
        SCAN_MAP.put(5, "Southwest");
        SCAN_MAP.put(6, "West");
        SCAN_MAP.put(7, "Northwest");
        
        DIR_MAP = new HashMap<>();
        DIR_MAP.put("North",0);
        DIR_MAP.put("Northeast",1);
        DIR_MAP.put("East",2);
        DIR_MAP.put("Southeast",3);
        DIR_MAP.put("South",4);
        DIR_MAP.put("Southwest",5);
        DIR_MAP.put("West",6);
        DIR_MAP.put("Northwest",7);
    }
    
    public String getNextAction(){
        return m_nextAction;
    }
    
    public Move getMove(){
        m_currentDir = m_nextMove.direction;
        return m_nextMove;
    }
    
    public void finishMove(){
        determineNextMove();
//        if(m_bRedirect)
//        {
//            //m_nextMove.direction = m_currentDir;
//            m_nextMove.step = 1;
//            m_nextAction = "move";
//        }
//        else
//        {
//            determineNextMove();
//        }
    }
   
    public void provideScanResult(Integer[] scan){
        m_scanResults = scan;
        determineNextMove();
    }
    
    private void determineNextMove(){
        
        if(m_nextAction.equals("scan"))
        {
            if(m_scanResults[DIR_MAP.get(m_currentDir)] == GRASS_CODE)
            {
                m_nextMove.direction = m_currentDir;
                m_nextMove.step = 1;
                m_nextAction = "move";
                return;
            }
            else
            {
                Boolean bFoundGrass = false;
                Integer empty = null; 
                for(int i=0; i<8; ++i)
                {
                    if(m_scanResults[i] == GRASS_CODE)
                    {
                        if(!bFoundGrass)
                        {
                            m_nextMove.direction = SCAN_MAP.get(i);
                        }
                        m_nextMove.step = 0;
                        m_nextAction = "move";
                        m_bRedirect = true;
                        bFoundGrass = true;
                        //return;
                    }
                    if(m_scanResults[i] == EMPTY_CODE)
                    {
                        if(empty == null)
                        {
                            empty = i;
                        }
                    }
                    
                }
                
                if(!bFoundGrass && empty != null)
                {
                    m_nextMove.direction = SCAN_MAP.get(empty);
                    m_nextMove.step = 0;
                    m_nextAction = "move";
                    m_bRedirect = true;
                }
                else if(!bFoundGrass && empty == null)
                { 
                    //no more move can be made
                    m_nextAction = "turn_off";        
                }
            }
        }
        else if(m_nextAction.equals("move"))
        {
            if(m_bRedirect)
            {
                m_nextAction = "move";
                m_nextMove.step = 1;
                m_bRedirect = false;
            }
            else
            {
                m_nextAction = "scan";
            }
            return;
        }
        
    }
    
    
}
