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
    
    public Puppy(Integer stayPercent) {
        m_randGenerator = new Random();
        m_stayPercent = stayPercent;
    }
    
    public Boolean willStay(){
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
