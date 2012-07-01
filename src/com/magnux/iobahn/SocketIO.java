package com.magnux.iobahn;

import org.codehaus.jackson.type.TypeReference;

public interface SocketIO {

    /**
     * Session handler for SocketIO sessions.
     */
    public interface ConnectionHandler {

        /**
         * Fired upon successful establishment of connection to SocketIO server.
         */
        public void onOpen();

        /**
         * Fired upon unsuccessful connection attempt or when connection was
         * closed normally, or abnormally.
         * 
         * @param code
         *            The close code, which provides information about why the
         *            connection was closed.
         * @param reason
         *            A humand readable description of the reason of connection
         *            close.
         */
        public void onClose(int code, String reason);
    }

    /**
     * Connect to SocketIO server.
     * 
     * @param wsUri
     *            The WebSockets URI of the server.
     * @param sessionHandler
     *            The handler for the session.
     */
    public void connect(String wsUri, ConnectionHandler sessionHandler);

    /**
     * Connect to SocketIO server.
     * 
     * @param wsUri
     *            The WebSockets URI of the server.
     * @param sessionHandler
     *            The handler for the session.
     * @param options
     *            WebSockets and SocketIO option.s
     */
    public void connect(String wsUri, ConnectionHandler sessionHandler, SocketIOOptions options);

    /**
     * Disconnect from SocketIO server.
     */
    public void disconnect();

    /**
     * Disconnect from SocketIO endpoint.
     * 
     * @param endpoint
     *            The URI or CURIE of the topic to unsubscribe from.
     */
    public void disconnect(String endpoint);

    /**
     * Check if currently connected to server.
     * 
     * @return True, iff connected.
     */
    public boolean isConnected();

    /**
     * Handler for PubSub events.
     */
    public interface EventHandler{

        /**
         * Fired when an event is received.
         * 
         * @param name
         *            The name of the event.
         * @param event
         *            The event, transformed into the type that was specified
         *            when subscribing.
         */
        public void onEvent(Object event);
    }

    /**
     * Subscribe to an event. When already subscribed, overwrite the event
     * handler.
     * 
     * @param name
     *            The name of the event.
     * @param eventType
     *            The type that event get transformed into.
     * @param eventHandler
     *            The event handler.
     */
    public void on(String name, Class<?> eventType, EventHandler eventHandler);

    /**
     * Subscribe to an event. When already subscribed, overwrite the event
     * handler.
     * 
     * @param name
     *            The name of the event.
     * @param eventType
     *            The type that event get transformed into.
     * @param eventHandler
     *            The event handler.
     */
    public void on(String name, TypeReference<?> eventType, EventHandler eventHandler);

    /**
     * Emit an event.
     * 
     * @param name
     *            The name of the event.
     * @param event
     *            The event to be published.
     */
    public void emit(String name, Object event);

}
