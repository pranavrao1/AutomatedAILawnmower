<html>
<head>
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
            <form action = "UploadServlet" method = "post" enctype = "multipart/form-data" style="margin: 0; margin-left: 20px; margin-top: 20px; height:30px;">
                <input style="width:600px" class="borderline" type = "file" name = "file" size = "50"/>
                <input type = "submit" value = "Import" />
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
                            <td>
                                
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td>
                    <div style="margin: auto; width: 120px; display:flex;">
                        <button type="button" onclick="">START</button>
                        <button type="button" onclick=")">STOP</button>
                    </div>
                    
                </td>
            </tr>
        </table>
    </td>
    <td>
        <table>
            <tr>
                <td>
                    <div class="borderline" style="width:370px; height:700px;"></div>
                </td>
            </tr>
            <tr>
                <td>
                     <div style="margin: auto; width: 120px;">
                        <button type="button" onclick="">EXPORT LOG</button>
                    </div>
                </td>
            </tr>
        </table>
    </td>
</tr>
</table>

</body>
</html>
