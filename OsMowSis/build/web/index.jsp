<%@page import="core.*"%>
<html>
<head>
    <meta charset="utf-8" />
    <title>A6-60 OsMowSis</title>
    <link rel="stylesheet" href="styles.css">
</head>
<script src="jQuery.js"></script>
<script>
        var play = null;
        $(document).ready(function() {
            $('#play').click(function (e){
                e.preventDefault();
                play = setInterval(function () {document.getElementById("next").click();}, 200);
                document.getElementById("play").style.display = "none";
                document.getElementById("stop").style.display = "inline-block";
                document.getElementById("next").style.display = "none";
               
            });
        });
        $(document).ready(function() {
            $('#stop').click(function (e){
                e.preventDefault();
                clearInterval(play);
                document.getElementById("play").style.display = "inline-block";
                document.getElementById("stop").style.display = "none";
                document.getElementById("next").style.display = "inline-block";
            });
        });
        $(document).ready(function() {
            $('#export').click(function (e){
                e.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "services.jsp", 
                    data: "action=export",
                    error:function(){
                        alert("error occured!!!");
                    },
                    success: function(str){    
                        var link = document.createElement('a');
                        link.download = 'log.txt';
                        var blob = new Blob([str.trim()], {type: 'text/plain'});
                        link.href = window.URL.createObjectURL(blob);
                        document.body.appendChild(link);
                        link.click();
                        document.body.removeChild(link);
                    }
                });
            });
        });
        $(document).ready(function() {
            $('#next').click(function (e){
                e.preventDefault();
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
                            $("#log").append("<table class='finishedLog'><tr><td>ALL DONE!!!!</td></tr></table>");
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
                                $("#status").empty();
                                $("#status").append(json.status);
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
                $("#status").empty();
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
        <form enctype="multipart/form-data" action="" method="post" name="csv" id="csv" style="margin: 0; margin-left: 20px; margin-top: 20px; height:30px;"> 
              <input class="borderline" type="file" name="csvfile" id="csvfile" value="" class="" style="width: 500px;"/>
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
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td>
                    <form ENCTYPE="plain/text" action="" style="margin: 0 auto; display: table;">
                        <button class="bigBtn" type="submit" id="play" disabled="true">Play</button>
                        <button class="bigBtn" type="submit" id="stop" style="display: none;" disabled="true">Stop</button>
                        <button class="bigBtn" type="submit" id="next" disabled="true" style="margin-left: 10px;">Next</button>
                    </form>
                </td>
            </tr>
        </table>
    </td>
    <td>
        <table>
            <tr>
                <td>
                    <div id="status" class="borderline" style="width:100%; height: 60px;"></div>
                </td>
            </tr>
            <tr>
                <td>
                    <div id="log" class="borderline logDiv"></div>
                </td>
            </tr>
            <tr>
                <td>
                     <div style="margin: auto; width: 120px;">
                        <button id="export" class="bigBtn" type="button" disabled="true">EXPORT LOG</button>
                    </div>
                </td>
            </tr>
        </table>
    </td>
</tr>
</table>
</body>
</html>