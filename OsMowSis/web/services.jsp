<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="core.*"%>
<%@page import="org.json.*"%>

<%
    String action = request.getParameter("action");
    String contentType = request.getContentType();
    if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
        SimMonitor monitor = new SimMonitor();
        InputStream is = request.getInputStream();
        String map = monitor.uploadStartingFileUI(is);
        out.print(map);
        out.flush();
    }
    else {
        SimMonitor monitor = SimMonitor.getInstance();
        Constants constants = new Constants();
        
        if(action.equals("next")){

            response.setContentType("application/json");
            if(monitor.stopRun()){
                JSONObject finalReport = new JSONObject();
                finalReport.put("done",monitor.printFinalReport());
                out.print(finalReport);
                out.flush();
            }else{

                Mower[] mowers = monitor.getMowers();
                MowerState[] mowerStates = monitor.getMowerStates();
                Puppy[] puppies = monitor.getPuppies();

                int index = monitor.indexForNextBtn;
                int totalNumMowerPuppy = mowers.length + puppies.length;
                int curr = index % totalNumMowerPuppy;
                if(curr == 0){
                    // increase the turn count per round
                    monitor.beforePollMowersForAction();
                }
                JSONObject single = new JSONObject();

                if(curr<mowers.length){

                    boolean skipDisplay = false;

                    if(mowerStates[curr].getState() == constants.MOWER_OFF 
                        || mowerStates[curr].getState() == constants.MOWER_CRASHED
                            || mowerStates[curr].getState() == constants.MOWER_STALLED)
                    {
                            skipDisplay = true;
                    }

                    monitor.singleMower(mowers[curr], mowerStates[curr], curr);

                    if(!skipDisplay) {
                        String log = monitor.displayActionAndResponses_UI();
                        single.put("log", log);
                        String html = monitor.renderLawnForUI();
                        single.put("html", html);
                    }
                }else{

                    curr -= mowers.length;

                    monitor.singlePuppy(puppies[curr], curr);
                    String log = monitor.displayActionAndResponses_UI();
                    single.put("log", log);
                    String html = monitor.renderLawnForUI();
                    single.put("html", html);
                }

                monitor.indexForNextBtn += 1;
                out.print(single);
                out.flush();
            }
        }else if(action.equals("forward")){

            response.setContentType("application/json");
            if(monitor.stopRun()){
                JSONObject finalReport = new JSONObject();
                finalReport.put("done",monitor.printFinalReport());
                out.print(finalReport);
                out.flush();
            }else{
                JSONArray singleRound =  new JSONArray();

                monitor.beforePollMowersForAction();

                Mower[] mowers = monitor.getMowers();
                MowerState[] mowerStates = monitor.getMowerStates();

                for (int i=0; i < mowers.length; ++i) {

                    JSONObject single = new JSONObject();
                    boolean skipDisplay = false;

                    if(mowerStates[i].getState() == constants.MOWER_OFF 
                        || mowerStates[i].getState() == constants.MOWER_CRASHED
                            || mowerStates[i].getState() == constants.MOWER_STALLED)
                    {
                            skipDisplay = true;
                    }

                    monitor.singleMower(mowers[i], mowerStates[i], i);

                    if(!skipDisplay) {
                        String log = monitor.displayActionAndResponses_UI();
                        single.put("log", log);
                        String html = monitor.renderLawnForUI();
                        single.put("html", html);
                        singleRound.put(single);
                    }
                }

                Puppy[] puppies = monitor.getPuppies();
                for(int i=0; i< puppies.length;i++){
                    JSONObject single = new JSONObject();
                    monitor.singlePuppy(puppies[i], i);
                    String log = monitor.displayActionAndResponses_UI();
                    single.put("log", log);
                    String html = monitor.renderLawnForUI();
                    single.put("html", html);
                    singleRound.put(single);
                }
                out.print(singleRound);
                out.flush();
            }
        }
    }
%>
