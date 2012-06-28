package com.magnux.iobahn;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.magnux.iobahn.SocketIOConnection.EventMeta;

import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketMessage;
import de.tavendo.autobahn.WebSocketOptions;
import de.tavendo.autobahn.WebSocketReader;

public class SocketIOReader extends WebSocketReader {

    private static final boolean DEBUG = true;
    private static final String TAG = SocketIOReader.class.getName();

    // / Jackson JSON-to-object mapper.
    private final ObjectMapper mJsonMapper;

    // / Jackson JSON factory from which we create JSON parsers.
    private final JsonFactory mJsonFactory;

    // / Holds reference to event subscription map created on master.
    private final ConcurrentHashMap<String, EventMeta> mEvents;

    /**
     * A reader object is created in SocketIOConnection.
     * 
     * @param events
     *            The event subscription map created on master.
     * @param master
     *            Message handler of master (used by us to notify the master).
     * @param socket
     *            The TCP socket.
     * @param options
     *            WebSockets connection options.
     * @param threadName
     *            The thread name we announce.
     */
    public SocketIOReader(ConcurrentHashMap<String, EventMeta> events, Handler master, SocketChannel socket,
            WebSocketOptions options, String threadName) {
        super(master, socket, options, threadName);
        mEvents = events;

        mJsonMapper = new ObjectMapper();
        mJsonMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mJsonFactory = mJsonMapper.getJsonFactory();

        if (DEBUG)
            Log.d(TAG, "created");
    }

    protected void onTextMessage(String payload) {
        // TODO: make error propagation consistent
        notify(new WebSocketMessage.Error(new WebSocketException("non-raw receive of text message")));
    }

    protected void onBinaryMessage(byte[] payload) {
        // TODO: make error propagation consistent
        notify(new WebSocketMessage.Error(new WebSocketException("received binary message")));
    }

    /**
     * Unwraps a SocketIO message which is a WebSockets text message with JSON
     * payload conforming to SocketIO.
     */
    protected void onRawTextMessage(byte[] payload) {

        try {
            String message = new String(payload, "UTF8");
            String[] parts = message.split(":", 4);
            int msgType = Integer.parseInt(parts[0]);

            switch (msgType) {

            case SocketIOMessage.MESSAGE_TYPE_CONNECT:
                notify(new SocketIOMessage.Connect("", ""));
                break;
            case SocketIOMessage.MESSAGE_TYPE_HEARTBEAT:
                break;
            case SocketIOMessage.MESSAGE_TYPE_TEXT_MESSAGE:
                if (DEBUG)
                    Log.d(TAG, "text message currently unsupported");
                break;
            case SocketIOMessage.MESSAGE_TYPE_JSON_MESSAGE:
                if (DEBUG)
                    Log.d(TAG, "json message currently unsupported");
                break;
            case SocketIOMessage.MESSAGE_TYPE_EVENT:
                final String id = parts[1];
                final String endpoint = parts[2];
                final String dataString = parts[3];
                JSONObject data = new JSONObject(dataString);
                String name = data.getString("name");
                JSONArray args = data.getJSONArray("args");
                String arg = args.getString(0); // This only supports
                                                // sending one argument!!!
                JsonParser parser = mJsonFactory.createJsonParser(arg);
                Object event = null;

                if (mEvents.containsKey(name)) {

                    EventMeta meta = mEvents.get(name);
                    if (meta.mEventClass != null) {
                        event = parser.readValueAs(meta.mEventClass);
                    } else if (meta.mEventTypeRef != null) {
                        event = parser.readValueAs(meta.mEventTypeRef);
                    } else {
                    }
                    notify(new SocketIOMessage.Event(id, endpoint, name, event));

                } else {

                    if (DEBUG)
                        Log.d(TAG, "SocketIO event for not-subscribed topic received");
                }

                parser.close();
                break;
            case SocketIOMessage.MESSAGE_TYPE_ACK:
                break;
            case SocketIOMessage.MESSAGE_TYPE_ERROR:
                if (DEBUG)
                    Log.d(TAG, "Error, message: " + message);
                break;
            case SocketIOMessage.MESSAGE_TYPE_NOOP:
                break;
            default:
                if (DEBUG)
                    Log.d(TAG, "unknown code");
            }

        } catch (JSONException e) {
            if (DEBUG)
                e.printStackTrace();

        } catch (JsonParseException e) {

            if (DEBUG)
                e.printStackTrace();

        } catch (IOException e) {

            if (DEBUG)
                e.printStackTrace();

        }
    }

}
