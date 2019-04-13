

/**
 *  Used by main monitor to keep track of Mower
 * @author oscar 
 */
public class MowerState {
	
    private int           m_state;
    private int           m_stallTurn;
    private int           m_x;
    private int           m_y;
    private String        m_direction;
    private Constants    code;
    
    public MowerState(int x, int y, String dir) {
    	code = new Constants();
        m_state = code.MOWER_ACTIVE;
        m_stallTurn = 0;
        m_x = x;
        m_y = y;
        m_direction = dir;
    }
    
    public int getX(){    	
    	return m_x;
    }
    public int getY(){    	
    	return m_y;
    }
    
    public String getDirection(){    	
    	return m_direction;
    }
    public void setDirection(String dir){    	
    	m_direction = dir;
    }
    
    public void setX(int x){    	
    	m_x = x;
    }
    public void setY(int y){    	
    	m_y = y;
    }
    
    public int getState(){    	
    	return m_state;
    }
    public int getStallTurn(){    	
    	return m_stallTurn;
    }
    public void setState(int newState){    	
    	m_state = newState;
    }
    public void setStallTurn(int turn){    	
    	m_stallTurn = turn;
    }
}
