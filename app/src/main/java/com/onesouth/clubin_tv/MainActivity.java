
package com.onesouth.clubin_tv;

import android.app.Activity;
import android.content.Intent;
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

    private boolean didSendIntent = false;

    private Lobby lobby;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String lc = createLobby();
        lobby = new Lobby(lc);

        Socket socket = lobby.getSocket();


        socket.on("Event_lobbyUpdate", args -> {
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
                Log.i(TAG, "Error in parsing lobby update");
                e.printStackTrace();
            }


        });

        socket.on("Event_videoQueued", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//                JSONObject data = (JSONObject) args[0];

                Log.i(TAG , "Video Queued");

                try{
                    Intent myIntent = new Intent(MainActivity.this, PlayVideoActivity.class);
                    myIntent.putExtra("lobby", lobby);
                    startActivity(myIntent);

                    didSendIntent = true;
                    finish();

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

        if(!didSendIntent){
            lobby.disconnectSocket();
        }
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

            return result;
        }catch (Exception e){
            Log.i(TAG, "Error in making lobby");
            e.printStackTrace();
        }

        return null;
    }



}
