# booking-java-websockets
booking example using java websockets

First of all, if you want to import this project to yourr IDE like eclipse or IDEA, you need to install de lombok plugin,
because this project use it. For more information www.projectlombok.org or search in google 'project lombok'.

You need:

Java 6 or 7
Tomcat 7
Maven 3.2.x

To run this app...

1. clone this repo
2. inside the repo dir: mvn clean install
3. deploy in tomcat 7
4. open in browser: http://localhost:8080/booking-java-websockets

This is the same application like my other repo: booking-express-socket.io
but whit quite diferences using HTML5 WebSockets (no nodejs), so the implementation of the app.js is more or less different.

The web socket server is in java.

Hope it helps.
