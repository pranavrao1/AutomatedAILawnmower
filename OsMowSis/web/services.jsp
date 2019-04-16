<%-- 
    Document   : getNext
    Created on : Apr 15, 2019, 11:46:58 AM
    Author     : parby02
--%>
<%@page import="core.*"%>
<%
    String action = request.getParameter("action");
    SimMonitor monitor = SimMonitor.getInstance();
    
    if(action.equals("next")){
        monitor.pollMowerForAction();
        monitor.getPuppyAction();
        out.println(monitor.renderLawnForUI());
    }
%>
