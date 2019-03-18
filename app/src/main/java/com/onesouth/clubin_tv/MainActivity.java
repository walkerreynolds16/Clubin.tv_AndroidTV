
package com.onesouth.clubin_tv;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


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
        lobby = LobbyHandler.getLobby(lc);

        Socket socket = lobby.getSocket();

        VideoView backgroundVideo = findViewById(R.id.backgroundVideo);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.blurry_party3_compressed);

        backgroundVideo.setVideoURI(uri);
        backgroundVideo.start();

        backgroundVideo.setOnPreparedListener(mp -> mp.setLooping(true));

        View layout = findViewById(R.id.mainLayout);
        layout.requestFocus();

        socket.on("Event_lobbyUpdate", args -> {
            JSONObject data = (JSONObject) args[0];

            String lobbyCode;
            ArrayList<String> memberList = new ArrayList<>();
            ArrayList<Video> videoQueue;
            Video currentVideo;

            Type listType = new TypeToken<ArrayList<Video>>(){}.getType();

            Log.i(TAG + "/update", "UpdateJSON: " + data.toString());


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

                lobby.setVideoQueue(videoQueue);
                lobby.setCurrentVideo(currentVideo);
                lobby.setMemberList(memberList);

            } catch (JSONException e) {
                Log.i(TAG, "Error in parsing lobby update");
                e.printStackTrace();
            }


        });

    }

    public void startWatching(View view){
        if(lobby.getCurrentVideo() != null && lobby.getCurrentVideo().getVideoId() != null && !lobby.isInPlaybackActivity()){
            Intent myIntent = new Intent(MainActivity.this, PlayVideoActivity.class);
            myIntent.putExtra("lobbyCode", lobby.getLobbyCode());
            startActivity(myIntent);

            didSendIntent = true;
            finish();
        }else {

            if(lobby.getCurrentVideo() != null && lobby.getCurrentVideo().getVideoId() == null){
                Toast.makeText(this, "No videos in the Queue", Toast.LENGTH_LONG).show();

            } else if(lobby.getMemberList().size() == 0){
                Toast.makeText(this, "No one has joined yet", Toast.LENGTH_LONG).show();

            }

        }

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "KeyCode = " + keyCode);
        Log.i(TAG, "KeyEvent = " + event.toString());


        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i(TAG, "KeyCode = " + keyCode);
        Log.i(TAG, "KeyEvent = " + event.toString());



        return true;
    }

}
