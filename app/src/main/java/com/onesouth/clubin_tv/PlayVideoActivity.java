package com.onesouth.clubin_tv;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.media.tv.companionlibrary.TvPlayer;
import com.google.gson.Gson;

import org.json.JSONObject;

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


}
