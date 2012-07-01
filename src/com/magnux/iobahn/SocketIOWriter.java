package com.magnux.iobahn;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.MappingJsonFactory;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import de.tavendo.autobahn.NoCopyByteArrayOutputStream;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;
import de.tavendo.autobahn.WebSocketWriter;

public class SocketIOWriter extends WebSocketWriter {

    private static final boolean DEBUG = true;
    private static final String TAG = SocketIOWriter.class.getName();

    /**
     * This is the Jackson JSON factory we use to create JSON generators.
     */
    private final JsonFactory mJsonFactory;

    /**
     * This is where we buffer the JSON serialization of SocketIO messages.
     */
    private final NoCopyByteArrayOutputStream mPayload;

    /**
     * A writer object is created in SocketIOConnection.
     * 
     * @param looper
     *            The message looper associated with the thread running this
     *            object.
     * @param master
     *            The message handler associated with the master thread (running
     *            SocketIOConnection).
     * @param socket
     *            The TCP socket (channel) the WebSocket connection runs over.
     * @param options
     *            WebSockets options for the underlying WebSockets connection.
     */
    public SocketIOWriter(Looper looper, Handler master, SocketChannel socket, WebSocketOptions options) {

        super(looper, master, socket, options);

        mJsonFactory = new MappingJsonFactory();
        mPayload = new NoCopyByteArrayOutputStream();

        if (DEBUG)
            Log.d(TAG, "created");
    }

    /**
     * Called from WebSocketWriter when it receives a message in it's message
     * loop it does not recognize.
     */
    protected void processAppMessage(Object msg) throws WebSocketException, IOException {

        mPayload.reset();

        // creating a JSON generator is supposed to be a light-weight operation
        JsonGenerator generator = mJsonFactory.createJsonGenerator(mPayload);

        try {

            if (msg instanceof SocketIOMessage.Disconnect) {

                generator.writeNumber(SocketIOMessage.MESSAGE_TYPE_DISCONNECT);
                SocketIOMessage.Disconnect dis = (SocketIOMessage.Disconnect) msg;
                
                if (dis.mEndpoint != null){
                	generator.writeRaw("::/");
                	generator.writeRaw(dis.mEndpoint);
                }

            } else if (msg instanceof SocketIOMessage.Heartbeat) {

                generator.writeNumber(SocketIOMessage.MESSAGE_TYPE_HEARTBEAT);
                generator.writeRaw(":::");

            } else if (msg instanceof SocketIOMessage.Emit) {

                SocketIOMessage.Emit emit = (SocketIOMessage.Emit) msg;
                
                generator.writeNumber(SocketIOMessage.MESSAGE_TYPE_EVENT);
                generator.writeRaw(":::");
                generator.writeStartObject();
                generator.writeFieldName("name");
                generator.writeString(emit.mName);
                generator.writeFieldName("args");
                generator.writeStartArray();
                generator.writeObject(emit.mEvent);
                generator.writeEndArray();
                generator.writeEndObject();

            } else if (msg instanceof SocketIOMessage.ACK) {

                SocketIOMessage.ACK ack = (SocketIOMessage.ACK) msg;

                generator.writeNumber(SocketIOMessage.MESSAGE_TYPE_ACK);
                generator.writeRaw(":::");
                generator.writeRaw(ack.mId);

            } else {

                // this should not happen, but to be sure
                throw new WebSocketException("invalid message received by SocketIOWriter");
            }
        } catch (JsonGenerationException e) {

            // this may happen, and we need to wrap the error
            throw new WebSocketException("JSON serialization error (" + e.toString() + ")");

        } catch (JsonMappingException e) {

            // this may happen, and we need to wrap the error
            throw new WebSocketException("JSON serialization error (" + e.toString() + ")");
        }

        // make sure the JSON generator has spit out everything
        generator.flush();

        if (DEBUG)
            Log.d(TAG, new String(mPayload.getByteArray(), "UTF8"));

        // Jackson's JSON generator produces UTF-8 directly, so we send
        // a text message frame using the raw sendFrame() method
        sendFrame(1, true, mPayload.getByteArray(), 0, mPayload.size());

        // cleanup generators resources
        generator.close();
    }

}
