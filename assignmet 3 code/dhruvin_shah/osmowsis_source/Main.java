public class Main {

    public static void main(String[] args) {
        SimDriver monitorSim = new SimDriver();

        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            monitorSim.uploadStartingFile(args[0]);
            //monitorSim.renderLawn();

            // run the simulation for a fixed number of steps
            int turns = 0;
            for(; turns < 100 & !monitorSim.allGrassMowed(); turns++) {
                monitorSim.pollMowerForAction();
                monitorSim.validateMowerAction();
                monitorSim.displayActionAndResponses();

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
            monitorSim.printFinal(turns);
        }
    }

}

