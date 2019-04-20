<%@page import="core.*"%>
<%@page import="org.json.*"%>
<%
    String action = request.getParameter("action");
    String contentType = request.getContentType();
    if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
        SimMonitor monitor = new SimMonitor();
        String map = monitor.uploadStartingFileUI(request.getInputStream());
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
                finalReport.put("done",monitor.printFinalReport_UI());
                out.print(finalReport);
                out.flush();
            }else{
                Mower[] mowers = monitor.getMowers();
                MowerState[] mowerStates = monitor.getMowerStates();
                Puppy[] puppies = monitor.getPuppies();

                int index = monitor.indexForNextBtn;
                int totalNumMowerPuppy = mowers.length + puppies.length;
                int curr = index % totalNumMowerPuppy;
                String turnHTML = "";
                if(curr == 0){
                    // increase the turn count per round
                    monitor.beforePollMowersForAction();
                    int turnNum = (index / totalNumMowerPuppy)+1;
                    turnHTML += "<table class='turnLog'><tr><td>Turn #"+ turnNum +  "</td></tr></table>";
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
                        single.put("log", turnHTML+log);
                        String html = monitor.renderLawnForUI();
                        single.put("html", html);
                    }else{
                        String log = monitor.displayActionAndResponses_UI();
                        single.put("log", turnHTML);
                        String html = monitor.renderLawnForUI();
                        single.put("html", html);
                    }
                }else{

                    curr -= mowers.length;

                    monitor.singlePuppy(puppies[curr], curr);
                    String log = monitor.displayActionAndResponses_UI();
                    single.put("log", turnHTML+log);
                    String html = monitor.renderLawnForUI();
                    single.put("html", html);
                }
                single.put("status", monitor.getStatus_UI());
                monitor.indexForNextBtn += 1;
                out.print(single);
                out.flush();
            }
        }else if(action.equals("export")){
            String log = monitor.getLog();
            out.print(log);
            out.flush();
        }
    }
%>
