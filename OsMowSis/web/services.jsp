<%-- 
    Document   : getNext
    Created on : Apr 15, 2019, 11:46:58 AM
    Author     : parby02
--%>
<%@page import="Backend.*"%>
<%
    String action = request.getParameter("action");
    Simulator sim = Simulator.getInstance();
    
    if(action.equals("next")){
        out.println(sim.pollMowerForAction());
    }
%>
