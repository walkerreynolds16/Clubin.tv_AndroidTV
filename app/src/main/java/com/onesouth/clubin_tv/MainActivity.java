
package com.onesouth.clubin_tv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Socket mSocket;

    private String lobbyCode;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lobbyCode = createLobby();

        mSocket.on("Event_lobbyUpdate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                String lobbyCode;
                ArrayList<String> memberList = new ArrayList<>();
                ArrayList<Video> videoQueue = new ArrayList<>();
                Video currentVideo;

                Type listType = new TypeToken<ArrayList<Video>>(){}.getType();

                Log.i(TAG + "/update", data.toString());

                try {
                    lobbyCode = data.getString("lobbyCode");
                    Log.i(TAG + "/update", "LobbyCode: " + lobbyCode);

                    videoQueue = new Gson().fromJson(data.get("videoQueue").toString(), listType);
                    Log.i(TAG + "/update", "VideoQueue: " + videoQueue.toString());

                    JSONArray arr = data.optJSONArray("memberList");
                    for(int i = 0; i < arr.length(); i++){
                        try {
                            memberList.add(arr.getString(i));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.i(TAG + "/update", "MemberList: " + memberList.toString());

                    currentVideo = new Gson().fromJson(data.get("currentVideo").toString(), Video.class);
                    Log.i(TAG + "/update", "CurrentVideo: " + currentVideo.toString());


                } catch (JSONException e) {
                    return;
                }


            }
        });

        mSocket.on("Event_startVideo", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                Log.i(TAG + "/startVideo", data.toString());

                try{
//                    Log.i(TAG + "/startVideo", data.get("currentVideo").toString());

                    Video currentVideo = new Gson().fromJson(data.get("currentVideo").toString(), Video.class);
                    Log.i(TAG, "CurrentVideo: " + currentVideo.toString());


                }catch (Exception e){
                    Log.i(TAG, "Error in start video");
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        try{
            mSocket.emit("Event_disconnection", new JSONObject().put("lobbyCode", lobbyCode));
        }catch(Exception e){
            Log.e(TAG, "error in disconnecting");
        }

        PostRequest req = new PostRequest();
        String url = Constants.API_LINK + "/deleteLobby";


        try {
            JSONObject data = new JSONObject().put("lobbyCode", lobbyCode);
            String[] taskInput = new String[]{url, data.toString()};

            String result = req.execute(taskInput).get();
            Log.i(TAG + "/delLobby", result);
        }catch (Exception e){
            Log.i(TAG, "error in deleting lobby");
            e.printStackTrace();
        }

        mSocket.disconnect();
        mSocket.off();
    }

    public String createLobby(){
        PostRequest req = new PostRequest();
        String url = Constants.API_LINK + "/createLobby";
        JSONObject data = new JSONObject();
        String[] taskInput = new String[]{url, data.toString()};

        TextView lobbyCodeView = findViewById(R.id.lobbyCode);

        String result;
        try {
            result = req.execute(taskInput).get();
            lobbyCodeView.setText(result);
            Log.i(TAG, result);

            createSocketConnection(result);

            return result;
        }catch (Exception e){}

        return "-1";
    }

    public void createSocketConnection(String lobbyCode){
        Log.i(TAG, "trying socket connection");
        try {
            mSocket = IO.socket(Constants.API_LINK);
            mSocket.connect();
            mSocket.emit("Event_connection", new JSONObject().put("lobbyCode", lobbyCode));
        } catch (Exception e) {
            Log.e(TAG, "error in connecting");
            e.printStackTrace();
        }
    }


}
