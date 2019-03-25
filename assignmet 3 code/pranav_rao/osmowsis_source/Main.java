public class Main {

    public static void main(String[] args) {
        SimDriver monitorSim = new SimDriver();

        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            monitorSim.uploadStartingFile(args[0]);

            // run the simulation for a fixed number of steps
            int turns = 0;
            while(turns < 100) {
                monitorSim.pollMowerForAction();
                monitorSim.validateMowerAction();
                monitorSim.displayActionAndResponses();

                if ("crash".equals(monitorSim.getTrackAction())) {
                    break;
                }
                turns++;
            }
            System.out.println(monitorSim.getMapSize() + "," + monitorSim.numberOfGreenSquares() + "," + monitorSim.numberOfCutGreenSquares() + "," + turns);
        }
    }

}
