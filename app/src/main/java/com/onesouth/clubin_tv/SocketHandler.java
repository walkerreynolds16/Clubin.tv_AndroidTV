package com.onesouth.clubin_tv;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class SocketHandler {

    private static final String TAG = "SocketHandler";

    private static Socket socket = null;


    public static synchronized Socket getSocket(String lobbyCode){
        if(socket == null){
            Log.i(TAG, "trying socket connection");
            try {
                socket = IO.socket(Constants.API_LINK);
                socket.connect();
                socket.emit("Event_connection", new JSONObject().put("lobbyCode", lobbyCode));
            } catch (Exception e) {
                Log.e(TAG, "error in connecting");
                e.printStackTrace();
            }
        }

        return socket;
    }
}