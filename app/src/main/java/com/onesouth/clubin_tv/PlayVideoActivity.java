package com.onesouth.clubin_tv;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.media.tv.companionlibrary.TvPlayer;
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
        lobby = (Lobby) getIntent().getSerializableExtra("lobby");

        socket = lobby.getSocket();

        mPlayer = (findViewById(R.id.player_youtube));

        setupPlayerCallbacks();
    }

    public void setupPlayerCallbacks(){

        mPlayer.registerCallback(new TvPlayer.Callback() {
            @Override
            public void onCompleted() {
                super.onCompleted();

                try{
                    socket.emit("Event_endVideo", new JSONObject().put("lobbyCode", lobby.getLobbyCode()).put("currentVideo", lobby.getCurrentVideo()));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


        });
    }


}
