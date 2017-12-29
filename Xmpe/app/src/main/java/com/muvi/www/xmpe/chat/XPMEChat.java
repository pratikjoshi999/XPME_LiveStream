package com.muvi.www.xmpe.chat;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by MUVI on 6/8/2017.
 */

public class XPMEChat {


    private static Socket socket;
    private static boolean isInitialized = false;
    private static String TAG = "WorldScopeSocketService";

    // List of listeners listening to the events
    private static ArrayList<OnIdentifyEventListener> identifyEventListeners = new ArrayList<>();
    private static ArrayList<OnJoinEventListener> joinEventListeners = new ArrayList<>();
    private static ArrayList<OnCommentEventListener> commentEventListeners = new ArrayList<>();
    private static ArrayList<OnLeaveEventListener> leaveEventListeners = new ArrayList<>();
    private static ArrayList<OnStickerEventListener> stickerEventListeners = new ArrayList<>();

    // Event names
    private static String EVENT_IDENTIFY = "identify";
    private static String EVENT_JOIN = "join";
    private static String EVENT_COMMENT = "comment";
    private static String EVENT_LEAVE = "leave";
    private static String EVENT_STICKER = "sticker";


    // Connect to Socket.IO in App Server
    public static void initialize() {

        if(socket != null) {
            socket.disconnect();
            stopListening();
        }

        try {
            socket = IO.socket(Constants.CHAT_SERVER_URL);
            socket.connect();
            isInitialized = true;
            // Start listening to events
            startListening();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void startListening() {
        socket.on(EVENT_IDENTIFY, onIdentifyEvent);
        socket.on(EVENT_JOIN, onJoinEvent);
        socket.on(EVENT_COMMENT, onCommentEvent);
        socket.on(EVENT_LEAVE, onLeaveEvent);
        socket.on(EVENT_STICKER, onStickerEvent);
    }

    private static void stopListening() {
        socket.off(EVENT_IDENTIFY, onIdentifyEvent);
        socket.off(EVENT_JOIN, onJoinEvent);
        socket.off(EVENT_COMMENT, onCommentEvent);
        socket.off(EVENT_LEAVE, onLeaveEvent);
        socket.off(EVENT_STICKER, onStickerEvent);
    }

    // Emits an identify event, payload should be the current cookie
    public static void emitIdentify(String data) {
        if(isInitialized) {
            Log.d(TAG, "Emitting identify with: " + data);
            socket.emit(EVENT_IDENTIFY, data);
        }
    }

    // Emits a comment event, payload should be the appInstance/roomId
    public static void emitJoin(String data) {
        if(isInitialized) {
            Log.d(TAG, "Emitting join with: " + data);
            socket.emit(EVENT_JOIN, data);
        }
    }

    // Emits a comment event, payload should be the comment
    public static void emitComment(String data) {
        if(isInitialized) {
            Log.d(TAG, "Emitting comment with: " + data);
            socket.emit(EVENT_COMMENT, data);
        }
    }

    // Emits a leave event, payload should be the appInstance/roomId
    public static void emitLeave(String data) {
        if(isInitialized) {
            Log.d(TAG, "Emitting leave with: " + data);
            socket.emit(EVENT_LEAVE, data);
        }
    }

    // Emits a sticker event, no payload
    public static void emitSticker() {
        if(isInitialized) {
            Log.d(TAG, "Emitting sticker");
            socket.emit(EVENT_STICKER);
        }
    }

    // Adds the object as a listener if it is valid
    public static boolean registerListener(Object listener) {

        Log.d(TAG, "Registering: " + listener);

        if(listener instanceof OnIdentifyEventListener) {
            Log.d(TAG, "Identify listener added");
            identifyEventListeners.add((OnIdentifyEventListener)listener);
            return true;
        }

        if(listener instanceof OnCommentEventListener) {
            Log.d(TAG, "Comment listener added");
            commentEventListeners.add((OnCommentEventListener)listener);
            return true;
        }

        if(listener instanceof OnJoinEventListener) {
            Log.d(TAG, "Join listener added");
            joinEventListeners.add((OnJoinEventListener)listener);
            return true;
        }

        if(listener instanceof OnLeaveEventListener) {
            Log.d(TAG, "Leave listener added");
            leaveEventListeners.add((OnLeaveEventListener)listener);
            return true;
        }

        if(listener instanceof OnStickerEventListener) {
            Log.d(TAG, "Sticker listener added");
            stickerEventListeners.add((OnStickerEventListener)listener);
            return true;
        }


        return false;
    }

    // Removes the object as a listener of socket service
    public static void unregisterListener(Object listener) {
        Log.d(TAG, "Unregistering: " + listener);

        if(listener instanceof OnIdentifyEventListener) {
            Log.d(TAG, "Removing Identify listener");
            identifyEventListeners.remove(listener);
        }

        if(listener instanceof OnJoinEventListener) {
            Log.d(TAG, "Removing Join listener");
            joinEventListeners.remove(listener);
        }

        if(listener instanceof OnCommentEventListener) {
            Log.d(TAG, "Removing Comment listener");
            commentEventListeners.remove(listener);
        }

        if(listener instanceof OnLeaveEventListener) {
            Log.d(TAG, "Removing Leave listener");
            leaveEventListeners.remove(listener);
        }

        if(listener instanceof OnStickerEventListener) {
            Log.d(TAG, "Removing Leave listener");
            stickerEventListeners.remove(listener);
        }
    }

    public interface OnIdentifyEventListener {
        void onIdentifyEventEmitted(String data);
    }

    public interface OnJoinEventListener {
        void onJoinEventEmitted(String data);
    }

    public interface OnCommentEventListener {
        void onCommentEventEmitted(String data);
    }

    public interface OnLeaveEventListener {
        void onLeaveEventEmitted(String data);
    }

    public interface OnStickerEventListener {
        void onStickerEventEmitted();
    }

    // Generate an instance of the Emitter.Listener for identify
    private static Emitter.Listener onIdentifyEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String data = args[0].toString();

            Log.d(TAG, "Socket adapter received identify event: " + data);
            Log.d(TAG, "Passing identify event to " + identifyEventListeners.size() + " listeners");
            for (OnIdentifyEventListener listener: identifyEventListeners) {
                listener.onIdentifyEventEmitted(data);
            }
        }
    };

    // Generate an instance of the Emitter.Listener for join
    private static Emitter.Listener onJoinEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String data = args[0].toString();

            Log.d(TAG, "Socket adapter received join event: " + data);
            Log.d(TAG, "Passing join event to " + joinEventListeners.size() + " listeners");
            for (OnJoinEventListener listener: joinEventListeners) {
                listener.onJoinEventEmitted(data);
            }
        }
    };


    // Generate an instance of the Emitter.Listener for comment
    private static Emitter.Listener onCommentEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String data = args[0].toString();
            Log.d(TAG, "Socket adapter received comment event: " + data);
            Log.d(TAG, "Passing comment event to " + commentEventListeners.size() + " listeners");
            for (OnCommentEventListener listener: commentEventListeners) {
                listener.onCommentEventEmitted(data);
            }
        }
    };

    // Generate an instance of the Emitter.Listener for leave
    private static Emitter.Listener onLeaveEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String data = args[0].toString();
            Log.d(TAG, "Socket adapter received leave event: " + data);
            Log.d(TAG, "Passing leave event to " + leaveEventListeners.size() + " listeners");
            for (OnLeaveEventListener listener: leaveEventListeners) {
                listener.onLeaveEventEmitted(data);
            }
        }
    };

    // Generate an instance of the Emitter.Listener for sticker
    private static Emitter.Listener onStickerEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (OnStickerEventListener listener: stickerEventListeners) {
                listener.onStickerEventEmitted();
            }
        }
    };
}

