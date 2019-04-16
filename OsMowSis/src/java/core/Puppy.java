package core;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package osmowsis;
import java.util.Random;
/**
 *
 * @author oscarc
 */
public class Puppy {
    private static Random m_randGenerator;
    private final Integer m_stayPercent;
    private int           m_x;
    private int           m_y;
    
    public Puppy(Integer stayPercent, int x, int y) {
        m_randGenerator = new Random();
        m_stayPercent = stayPercent;
        m_x = x;
        m_y = y;
    }
    
    public int getX(){    	
    	return m_x;
    }
    public int getY(){    	
    	return m_y;
    }
    public void setX(int x){    	
    	m_x = x;
    }
    public void setY(int y){    	
    	m_y = y;
    }
    
    public Boolean isStaying(){
        Integer moveRandomChoice = m_randGenerator.nextInt(100);
        if(moveRandomChoice < m_stayPercent )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public Integer getMove(){
        Integer moveRandomChoice = m_randGenerator.nextInt(8);
        return moveRandomChoice;
    }
}
