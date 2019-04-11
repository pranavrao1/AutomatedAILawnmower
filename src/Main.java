public class Main {

    public static void main(String[] args) {
        SimMonitor monitor = new SimMonitor();

        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            monitor.uploadStartingFile(args[0]);
            //monitorSim.renderLawn();

            // run the simulation for a fixed number of steps
            int turns = 0;
            for(; turns < 100 & !monitor.stopRun(); turns++) {
                monitor.pollMowerForAction();
                monitor.validateMowerAction();
                monitor.getPuppyAction();
                
                monitor.displayActionAndResponses();

                // REMEMBER to delete or comment out the rendering before submission
                // monitorSim.renderLawn();
                // monitorSim.renderKnownLawn();

                // pause after each event for a given number of seconds
                // pausing is completely optional
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //monitorSim.renderLawn();
            monitor.printFinal(turns);
        }
    }

}

