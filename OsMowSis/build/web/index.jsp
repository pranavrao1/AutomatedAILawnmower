
<%@page import="sun.misc.IOUtils"%>
<%@page import="java.nio.file.StandardCopyOption"%>
<%@page import="core.*"%>
<%@ page import="java.io.*"%>
<%        
    String saveFile = "";
    String contentType = request.getContentType();
    String map = "";
    if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
        SimMonitor monitor = new SimMonitor();
        map = monitor.uploadStartingFileUI(request.getInputStream());
    }
%>
<html>
<head>
    <meta charset="utf-8" />
<title>A6-60 OsMowSis</title>
<link rel="stylesheet" href="styles.css">
<script src="jQuery.js"></script>
</head>
<body bgcolor=white>
<script>
        $(document).ready(function() {
            $('#next').click(function (){
                $.ajax({
                    type: "POST",
                    url: "services.jsp", 
                    data: "action=next",
                    enctype: "text/plain",
                    error:function(){
                        alert("error occured!!!");
                    },
                    success: function(json){    
                        
                        if(json.done!=null){
                            $("#log").append(json.done);
                            var result = json.done;
                            var s =  result.split(",");
                            var msg = "WE ARE DONE!!!!" + "\n";
                            msg = "Lawn size: " + s[0] + "\n";
                            msg += "Grass area: "+ s[1] + "\n";
                            msg += "Total cut: "+ s[2] + "\n";
                            msg += "Number of turns: "+ s[3] + "\n";
                            alert(msg);
                        }else{
                            var logHTML = "<div class='oneRound'>";
                            $.each(json, function(){
                                $("#lawnMap").empty();
                                $("#lawnMap").append(this['html']);
                                logHTML += this['log'];
                            });
                            logHTML += "</div>";
                            $("#log").append(logHTML);
                        }
                        
                    }
                });
            });
        });
</script>
<table class="borderline centered" style="width: 1200px; height: 800px;">
<tr style="height: 40px;">
    <td colspan="2">
        <div>
            <img src="image/client-logo-gt.jpg" style="width: 80px;margin-top: -30px; margin-left: -20px;"> <span style="font-size: 22px;">CS6310 Group A6-60 OsMowSis</span>
        </div>
    </td>
</tr>
<tr>
    <td>
            <form ENCTYPE="multipart/form-data" action="#" method="post" style="margin: 0; margin-left: 20px; margin-top: 20px; height:30px;">
                <input name="path" style="width:600px" class="borderline" type ="file" size = "50"/>
                <input id="sendMailBtn" type = "submit" value = "Import"/>
            </form>
    </td>
    <td>
        <span style="position: absolute;">Output:</span>
    </td>
</tr>
<tr>
    <td style="width: 800px;">
        <table>
            <tr>
                <td>
                    <table style="width:780px; height:700px; border: 1px solid #aaaaaa;">
                        <tr>
                            <td style="vertical-align: text-top;">
                                <div style="float: top" id="lawnMap">
                                    <% out.print(map); %>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td>
                    <form ENCTYPE="plain/text" action="#" style="margin: 0 auto; display: table;">
                        <button class="bigBtn" type="submit" id="next" value="next">NEXT</button>
                    </form>
                </td>
            </tr>
        </table>
    </td>
    <td>
        <table>
            <tr>
                <td>
                    <div id="log" class="borderline logDiv"></div>
                </td>
            </tr>
            <tr>
                <td>
                     <div style="margin: auto; width: 120px;">
                        <button class="bigBtn" type="button" onclick="">EXPORT LOG</button>
                    </div>
                </td>
            </tr>
        </table>
    </td>
</tr>
</table>

</body>
</html>
