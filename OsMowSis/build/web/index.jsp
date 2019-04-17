
<%@page import="sun.misc.IOUtils"%>
<%@page import="java.nio.file.StandardCopyOption"%>
<%@page import="core.*"%>
<%@ page import="java.io.*"%>
<html>
<head>
    <meta charset="utf-8" />
    <title>A6-60 OsMowSis</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body bgcolor=white>
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
<!--            <form name="import" id="import" enctype="multipart/form-data" action="" method="post"  style="margin: 0; margin-left: 20px; margin-top: 20px; height:30px;">
                <input value="" id="file" style="width:600px" class="borderline"  value=""type ="file"/>
                <input type="submit" id="submit" />
            </form>-->
            <form enctype="multipart/form-data" action="" method="post" name="csv" id="csv" style="margin: 0; margin-left: 20px; margin-top: 20px; height:30px;"> 
                  <input class="borderline" type="file" name="csvfile" id="csvfile" value="" class=""/>
                  <input type="hidden" name="MAX_FILE_SIZE" value="10000000" />
                  <input type="submit" name="uploadCSV" id="upload" value="Upload" class="btn btn-primary pull-right"/>
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
                                    <% // out.print(map); %>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td>
                    <form ENCTYPE="plain/text" action="#" style="margin: 0 auto; display: table;">
                        <button class="bigBtn" type="submit" id="play" disabled="true">Forward</button>
                        <button class="bigBtn" type="submit" id="stop" style="display: none;" disabled="true">Stop</button>
                        <button class="bigBtn" type="submit" id="next" disabled="true">Next</button>
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
                        <button id="export" class="bigBtn" type="button" onclick="" disabled="true">EXPORT LOG</button>
                    </div>
                </td>
            </tr>
        </table>
    </td>
</tr>
</table>

</body>
</html>
<script src="jQuery.js"></script>
<script>
        var play = null;
        $(document).ready(function() {
            $('#play').click(function (){
               play = setInterval(function () {document.getElementById("next").click();}, 200);
               document.getElementById("play").style.display = "none";
               document.getElementById("stop").style.display = "inline-block";
               document.getElementById("next").style.display = "none";
               
            });
        });
        $(document).ready(function() {
            $('#stop').click(function (){
                clearInterval(play);
                document.getElementById("play").style.display = "inline-block";
                document.getElementById("stop").style.display = "none";
                document.getElementById("next").style.display = "inline-block";
            });
        });
        $(function () {
            $('#import').submit(function (){
                var data = new FormData(this);
                $.ajax({
                    url: "services.jsp",
                    type: "POST",
                    data:  data,
                    processData: false,
                    contentType: false,
                    dataType: "json"
                  }).done(function(resp) {
                    // CHECK WE HAVE DATA
                    if (resp) {
                        alert(resp);
                    }
                  });  
            });
        });
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
                            clearInterval(play);
                            var result = json.done;
                            var s =  result.split(",");
                            var msg = "WE ARE DONE!!!!" + "\n";
                            msg = "Lawn size: " + s[0] + "\n";
                            msg += "Grass area: "+ s[1] + "\n";
                            msg += "Total cut: "+ s[2] + "\n";
                            msg += "Number of turns: "+ s[3] + "\n";
                            alert(msg);
                            document.getElementById('play').setAttribute('disabled', 'disabled');
                            document.getElementById('next').setAttribute('disabled', 'disabled');
                            document.getElementById('stop').setAttribute('disabled', 'disabled');
                        }else{
                            if(json.log!=null){
                                $("#lawnMap").empty();
                                $("#lawnMap").append(json.html);
                                $("#log").append(json.log);
                                var div = document.getElementById("log");
                                div.scrollTop = div.scrollHeight - div.clientHeight;
                            }
                        }
                    }
                });
            });
        });
        
    $(function () {
        $('#csv').submit(function (e) {
            e.preventDefault();
            var data = new FormData(this);
            $.ajax({
                url: "services.jsp",
                type: "POST",
                data:  data,
                processData: false,
                contentType: false
            }).done(function(resp) {
                $("#log").empty();
                $("#lawnMap").empty();
                $("#lawnMap").append(resp);
                document.getElementById("play").removeAttribute('disabled');
                document.getElementById("next").removeAttribute('disabled');
                document.getElementById("stop").removeAttribute('disabled');
                document.getElementById("export").removeAttribute('disabled');
            });  
        });
    });
</script>