package com.onesouth.clubin_tv;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.media.tv.companionlibrary.TvPlayer;
import com.google.gson.Gson;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import news.androidtv.libs.player.YouTubePlayerView;

public class PlayVideoActivity extends AppCompatActivity {

    private static final String TAG = "PlayVideo";

    private Socket socket;

    private YouTubePlayerView mPlayer;

    private Lobby lobby;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        // Get passed values from MainActivity

        lobby = LobbyHandler.getLobby(getIntent().getStringExtra("lobbyCode"));

        lobby.setInPlaybackActivity(true);

        socket = lobby.getSocket();

        mPlayer = (findViewById(R.id.player_youtube));

        setupSocketListeners();

        setupPlayerCallbacks();

        startCurrentVideo();

        //View sideBarLayout = findViewById(R.id.sideBarLayout);
//        sideBarLayout.requestFocus();




    }

    public void startCurrentVideo(){
        lobby.setCurrentDJ(lobby.getCurrentVideo().getMemberName());
        mPlayer.loadVideo(lobby.getCurrentVideo().getVideoId());
        lobby.setSomeoneDJing(true);
    }

    public void setupPlayerCallbacks(){

        mPlayer.registerCallback(new TvPlayer.Callback() {
            @Override
            public void onCompleted() {
                super.onCompleted();

                try{
                    JSONObject data = new JSONObject();
                    data.put("lobbyCode", lobby.getLobbyCode());
                    data.put("currentVideo", lobby.getCurrentVideo());

                    Log.i(TAG, "CurrentVideo: " + lobby.getCurrentVideo());
                    Log.i(TAG, "Data: " + data.toString());


                    socket.emit("Event_endVideo", data);
                }catch(Exception e){
                    e.printStackTrace();
                }

                lobby.setSomeoneDJing(false);
                lobby.setCurrentDJ("");
                lobby.setCurrentVideo(null);
            }


        });

    }

    public void setupSocketListeners(){
        socket.on("Event_startVideo", args -> {
            JSONObject data = (JSONObject) args[0];

            try{
                Video currentVideo = new Gson().fromJson(data.get("currentVideo").toString(), Video.class);
                Log.i(TAG, "CurrentVideo: " + currentVideo.toString());

                lobby.setSomeoneDJing(true);
                lobby.setCurrentDJ(currentVideo.getMemberName());
                lobby.setCurrentVideo(currentVideo);

                mPlayer.loadVideo(currentVideo.getVideoId());

            }catch (Exception e){
                Log.i(TAG, "Error in start video");
                e.printStackTrace();
            }
        });
    }

//    public void showUsersLayout(View view){
//
//
//        runOnUiThread(() -> {
//            View showUsersLayout = findViewById(R.id.showUsersLayout);
//            View watchVideoHomeLayout = findViewById(R.id.watchVideoHomeLayout);
//            ListView usersListView = findViewById(R.id.showUsersListView);
//
//            watchVideoHomeLayout.setVisibility(View.INVISIBLE);
//            showUsersLayout.setVisibility(View.VISIBLE);
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(PlayVideoActivity.this, R.layout.joined_users_listview, lobby.getMemberList());
//            usersListView.setAdapter(adapter);
//
//        });
//    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Back pressed");

//        View showUsersLayout = findViewById(R.id.showUsersLayout);
//        View watchVideoHomeLayout = findViewById(R.id.watchVideoHomeLayout);
//        View sideBarLayout = findViewById(R.id.sideBarLayout);
//
//        if(showUsersLayout.getVisibility() == View.VISIBLE){
//            showUsersLayout.setVisibility(View.INVISIBLE);
//            watchVideoHomeLayout.setVisibility(View.VISIBLE);
//            sideBarLayout.requestFocus();
//        }else {
//            super.onBackPressed();
//        }

        super.onBackPressed();
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
