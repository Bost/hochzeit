// <html>
// <head>
//   <meta charset="utf-8">
//   <title>WebSocket echo server test</title>
//   <script type="text/javascript">
var serverUrl = "ws://localhost:8080";
var ws = new WebSocket(serverUrl);

ws.onopen = function(e) {
    console.log("ws.onopen() {");
    console.log("Connected to "+serverUrl);
    var msgName = "Jim";
    ws.send(msgName);
    console.log("Message send: '"+msgName+"'");
    var msgTxt = "Text";
    ws.send(msgTxt);
    console.log("Message send: '"+msgTxt+"'");
    console.log("ws.onopen() }");
};

ws.onerror = function(e) {
    alert("webws error!");
};

ws.onclose = function(e) {
    alert("webws closed!");
};

ws.onmessage = function(e) {
    console.log("Got message: ", e.data);
    var object = JSON.parse(e.data);
    console.log("JSON.parse():", object);
    // var object = BSON.deserialize(e.data);
    // console.log("Deserialised message:", object);
    // var chatlog = document.getElementById("chatlog");
    // chatlog.innerHTML += e.data + "<br>\n";
    // console.log("theBar: ", theBar);
    bar(object);
    return false;
};

var connect = function() {
    //alert("Sending nick!");
    //var nick = document.getElementById("nick").value;
    var nick = "Jim";
    ws.send(nick);
    console.log("connect() executed");
    return false;
};

var sendMsg = function() {
    var msg = document.getElementById("netmsg").value;
    ws.send(msg);
    return false;
};

// window.onload = function() {
//     document.getElementById("connect").onclick = connect;
//     document.getElementById("connectform").onsubmit = connect;

//     document.getElementById("send").onclick = sendMsg;
//     document.getElementById("sendform").onsubmit = sendMsg;
// }
//   </script>
// </head>
// <body>
//   <form id="connectform">
//     <input type="text" id="nick" name="nick" value="">
//     <input type="button" id="connect" name="connect" value="Connect">
//   </form>
//   <form id="sendform">
//     <input type="text" id="netmsg" name="netmsg" value="">
//     <input type="button" id="send" name="send" value="Send">
//   </form>
//   <div id="chatlog">
//   </div>
// </body>
// </html>
