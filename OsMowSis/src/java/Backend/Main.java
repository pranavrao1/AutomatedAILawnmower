package Backend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Main {
        public Simulator monitorSim;
        public Main(){};
        
        public Simulator getSimulator(){
            return monitorSim;
        }
        
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {

//		
//		if (args.length == 0) {
//			System.out.println("ERROR: Test scenario file name not found.");
//		} else {
//			monitorSim.uploadStartingFile(args[0]);
//			// run the simulation for a fixed number of steps
//			for (int turns = 0; turns < 100; turns++) {
//				monitorSim.pollMowerForAction();
//				if (monitorSim.getIsTerminated()) {
//					break;
//				}
//			}
//			monitorSim.printFinalReport();
//		}

//		Simulator monitorSim = new Simulator();
//		String path = "/Users/parby02/temp/scenario1.csv";

//                final File initialFile = new File(path);
//                final InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
//      BufferedReader takeCommand = new BufferedReader(new InputStreamReader(targetStream,"UTF-8"));
//      String line = takeCommand.readLine();
//      while(line!=null){
//          System.out.println(line);
//      }
//		monitorSim.uploadStartingFile();
//
//		for (int turns = 0; turns < 3000; turns++) {
//			monitorSim.pollMowerForAction();
//			if (monitorSim.getIsTerminated())
//				break;
//		}
////		System.out.print(path + " ");
//		monitorSim.printFinalReport();

		// for(int i=0;i<1000;i++){
		// Simulator monitorSim = new Simulator();
		// String path = "";
		// if(i<10)
		// path = "/Users/parby02/temp/testScenarios/testScenario00"+i+".csv";
		// else if(i<100)
		// path = "/Users/parby02/temp/testScenarios/testScenario0"+i+".csv";
		// else
		// path = "/Users/parby02/temp/testScenarios/testScenario"+i+".csv";
		//
		// monitorSim.uploadStartingFile(path);
		//
		// for (int turns = 0; turns < 3000; turns++) {
		// monitorSim.pollMowerForAction();
		// if (monitorSim.getIsTerminated())
		// break;
		// }
		// System.out.print(path+ " ");
		// monitorSim.printFinalReport();
		// }
	}

}
