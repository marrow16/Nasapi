# NasApi

##### Proof of Concept / Prototype

A Javascript (RESTful) webservices container using Nashorn - incorporating ```require``` capabilities (using [nashorn-commonjs-modules](https://github.com/coveo/nashorn-commonjs-modules)).

Uses:-
* SpringBoot
* Embedded Jetty server
* Nashorn
* MongoDb

Use Maven to build (produces full fat jar)
<br/>

To run server:-

In path where Javascript sources exist (uses ```index.js``` in current path):-
```
java -jar [path-to-jar]/Nasapi-0.1.jar
```
<br/>

To specify a path to load Javascript sources (looks for ```index.js``` in specified path):-
```
java -jar Nasapi-0.1.jar myServers/test
```
<br/>

To specify a specific start-up root Javascript file:-
```
java -jar Nasapi-0.1.jar myServers/test/main.js
```
<br/>
<br/>

Working example - see [ServerTest](./serverTest/index.jsl)