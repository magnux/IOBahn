package com.magnux.iobahn;

import de.tavendo.autobahn.WebSocketMessage;

public class SocketIOMessage {

    public static final int MESSAGE_TYPE_DISCONNECT = 0;
    public static final int MESSAGE_TYPE_CONNECT = 1;
    public static final int MESSAGE_TYPE_HEARTBEAT = 2;
    public static final int MESSAGE_TYPE_TEXT_MESSAGE = 3;
    public static final int MESSAGE_TYPE_JSON_MESSAGE = 4;
    public static final int MESSAGE_TYPE_EVENT = 5;
    public static final int MESSAGE_TYPE_ACK = 6;
    public static final int MESSAGE_TYPE_ERROR = 7;
    public static final int MESSAGE_TYPE_NOOP = 8;

    // / Base message class.
    public static class Message extends WebSocketMessage.Message {
    }

    /**
     * Define Disconnect message. Server-to-client and client-to-server message.
     */
    public static class Disconnect extends Message {
        public String mEndpoint;

        public Disconnect(String endpoint) {
            mEndpoint = endpoint;
        }
    }

    /**
     * Define Connect message. Server-to-client and client-to-server message.
     */
    public static class Connect extends Message {
        public String mEndpoint;
        public String mParams;

        public Connect(String endpoint, String params) {
            mEndpoint = endpoint;
            mParams = params;
        }
    }

    /**
     * Define Heartbeat message. Server-to-client and client-to-server message.
     */
    public static class Heartbeat extends Message {
    }

    /**
     * Define Text message. Server-to-client and client-to-server message.
     */
    public static class TextMessage extends Message {
        public String mId;
        public String mEndpoint;
        public String mData;

        public TextMessage(String id, String endpoint, String data) {
            mId = id;
            mEndpoint = endpoint;
            mData = data;
        }
    }

    /**
     * Define JsonMessage. Server-to-client and client-to-server message.
     */
    public static class JsonMessage extends Message {
        public String mId;
        public String mEndpoint;
        public String mJson;

        public JsonMessage(String id, String endpoint, String json) {
            mId = id;
            mEndpoint = endpoint;
            mJson = json;
        }
    }

    /**
     * Define Event. Server-to-client message.
     */
    public static class Event extends Message {
        public String mId;
        public String mEndpoint;
        public String mName;
        public Object mEvent;

        public Event(String id, String endpoint, String name, Object event) {
            mId = id;
            mEndpoint = endpoint;
            mName = name;
            mEvent = event;
        }
    }

    /**
     * Define ACK. Server-to-client and client-to-server message.
     */
    public static class ACK extends Message {
        public String mId;
        public String mData;

        public ACK(String id, String data) {
            mId = id;
            mData = data;
        }
    }

    /**
     * Define Error. Server-to-client and client-to-server message.
     */
    public static class Error extends Message {
        public String mEndpoint;
        public String mReason;
        public String mAdvice;

        public Error(String endpoint, String reason, String advice) {
            mEndpoint = endpoint;
            mReason = reason;
            mAdvice = advice;
        }
    }

    /**
     * Define Noop. Server-to-client message.
     */
    public static class Noop extends Message {
    }
    
    
    /**
     * Define Event. Client-to-server message.
     */
    public static class Emit extends Message {
        public String mName;
        public Object mEvent;

        public Emit(String name, Object event) {
            mName = name;
            mEvent = event;
        }
    }
    

}
