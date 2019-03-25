/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osmowsis;

/**
 *
 * @author oscarc
 */
public class Osmowsis {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Controller monitorSim = new Controller();

        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            monitorSim.uploadStartingFile(args[0]);

            // run the simulation for a fixed number of steps
            for(int turns = 0; turns < 100; turns++) {
                monitorSim.pollMowerForAction();

                monitorSim.validateMowerAction();
                monitorSim.displayActionAndResponses();

                // REMEMBER to delete or comment out the rendering before submission
                //monitorSim.renderLawn();

                // pause after each event for a given number of seconds
                // pausing is completely optional
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            monitorSim.printFinalReport();
        }
    }
    
}
