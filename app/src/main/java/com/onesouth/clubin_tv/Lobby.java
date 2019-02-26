package com.onesouth.clubin_tv;

import android.util.Log;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONObject;
import java.util.ArrayList;

public class Lobby {

    private boolean isSomeoneDJing;
    private ArrayList<Video> videoQueue;
    private String currentDJ;
    private Video currentVideo;
    private String lobbyCode;
    private ArrayList<String> memberList;
    private boolean inPlaybackActivity;

    private Socket socket;

    private static String TAG;

    public Lobby(String lobbyCode) {
        isSomeoneDJing = false;
        videoQueue = new ArrayList<>();
        memberList = new ArrayList<>();
        currentDJ = "";
        currentVideo = null;
        inPlaybackActivity = false;

        this.lobbyCode = lobbyCode;
        TAG = "Lobby" + lobbyCode;

        createSocketConnection();
    }

    private void createSocketConnection() {
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

    public void disconnectSocket() {
        try {
            socket.emit("Event_disconnection", new JSONObject().put("lobbyCode", lobbyCode));
        } catch (Exception e) {
            Log.e(TAG, "error in disconnecting");
        }

        PostRequest req = new PostRequest();
        String url = Constants.API_LINK + "/deleteLobby";

        try {
            JSONObject data = new JSONObject().put("lobbyCode", lobbyCode);
            String[] taskInput = new String[]{url, data.toString()};

            String result = req.execute(taskInput).get();
            Log.i(TAG + "/delLobby", result);
        } catch (Exception e) {
            Log.i(TAG, "error in deleting lobby");
            e.printStackTrace();
        }

        socket.disconnect();
        socket.off();
    }


    public Socket getSocket() {
        return socket;
    }

    public ArrayList<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<String> memberList) {
        this.memberList = memberList;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public boolean isSomeoneDJing() {
        return isSomeoneDJing;
    }

    public void setSomeoneDJing(boolean someoneDJing) {
        isSomeoneDJing = someoneDJing;
    }

    public ArrayList<Video> getVideoQueue() {
        return videoQueue;
    }

    public void setVideoQueue(ArrayList<Video> videoQueue) {
        this.videoQueue = videoQueue;
    }

    public String getCurrentDJ() {
        return currentDJ;
    }

    public void setCurrentDJ(String currentDJ) {
        this.currentDJ = currentDJ;
    }

    public Video getCurrentVideo() {
        return currentVideo;
    }

    public void setCurrentVideo(Video currentVideo) {
        this.currentVideo = currentVideo;
    }

    public boolean isInPlaybackActivity() {
        return inPlaybackActivity;
    }

    public void setInPlaybackActivity(boolean inPlaybackActivity) {
        this.inPlaybackActivity = inPlaybackActivity;
    }
}
