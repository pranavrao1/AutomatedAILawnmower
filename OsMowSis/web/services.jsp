<%-- 
    Document   : getNext
    Created on : Apr 15, 2019, 11:46:58 AM
    Author     : parby02
--%>
<%@page import="java.io.PrintWriter"%>
<%@page import="core.*"%>
<%@page import="org.json.*"%>

<%
    String action = request.getParameter("action");
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
%>
