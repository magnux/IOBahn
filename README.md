IOBahn
======

[Socket.IO protocol](https://github.com/LearnBoost/socket.io-spec) implementation on top of [AutobahnAndroid](https://github.com/tavendo/AutobahnAndroid) WebSockets library.
This works with the lattest Socket.IO protocol.

Many thanks to the [Socket.IO](http://socket.io/) and [Autobahn](http://autobahn.ws/) projects.

## Why?

Because we like to develop servers with Socket.IO, and clients on Android.
And also because Autobahn provide a robust library for the transport layer, the websockets.

## Overview

It works a lot like the Autobahn library for the WAMP protocol; but this extension implements the Socket.IO protocol.

## How to use it? 

First instantiate a Socket.IO connection:
```java
private final SocketIO mConnection = new SocketIOConnection();
```

Then initiate the connection, implementing the handler:
```java
mConnection.connect(wsuri, new SocketIO.ConnectionHandler() {
  public void onOpen() {
  //What to do on WebSocket openning
  }
  
  public void onClose() {
   //What to do on WebSocket closing
  }
});
```

And finally, subscribe to the desired events, declaring their handlers and the types of the incomming objects:
```java
mConnection.on("myevent", MyEvent.class, new SocketIO.EventHandler() {
  //What to do on event
});
```

Don't worry I'll documment more soon, and also will post a Simple Events Demo.
