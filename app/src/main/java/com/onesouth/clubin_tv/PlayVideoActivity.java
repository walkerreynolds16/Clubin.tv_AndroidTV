package com.onesouth.clubin_tv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.navigation.NavigationView;
import com.google.android.media.tv.companionlibrary.TvPlayer;
import com.google.gson.Gson;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import news.androidtv.libs.player.YouTubePlayerView;

import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
import static android.view.KeyEvent.KEYCODE_MENU;

public class PlayVideoActivity extends Activity {

    private static final String TAG = "PlayVideo";

    private Socket socket;

    private YouTubePlayerView mPlayer;

    private Lobby lobby;


    @SuppressLint("SetJavaScriptEnabled")
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, item.toString());

                switch(item.getItemId()){
                    case(R.id.menu_item1):
                        //Skip video
                        onPlayerFinishVideo();
                }


                return false;
            }
        });

        TextView navViewLobbyCode = navigationView.getHeaderView(0).findViewById(R.id.nav_view_lobby_code);
        navViewLobbyCode.setText(lobby.getLobbyCode());



    }

    public void startCurrentVideo(){
        lobby.setCurrentVideo(lobby.getVideoQueue().get(0));

        lobby.setCurrentDJ(lobby.getCurrentVideo().getMemberName());
        mPlayer.loadVideo(lobby.getCurrentVideo().getVideoId());
        lobby.setSomeoneDJing(true);

        socket.emit("Event_startingVideo", lobby.getLobbyCode());
    }

    public void onPlayerFinishVideo(){
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

    public void setupPlayerCallbacks(){

        mPlayer.registerCallback(new TvPlayer.Callback() {
            @Override
            public void onCompleted() {
                super.onCompleted();

                onPlayerFinishVideo();
            }


        });

        mPlayer.registerCallback(new TvPlayer.Callback() {
            @Override
            public void onStarted() {
                super.onStarted();

                Toast.makeText(PlayVideoActivity.this, "Press the Menu button on your remote to access lobby functions", Toast.LENGTH_LONG).show();
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



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        switch(event.getKeyCode()){
            case(KEYCODE_DPAD_UP):
                return super.dispatchKeyEvent(event);

            case(KEYCODE_DPAD_DOWN):
                return super.dispatchKeyEvent(event);

            case(KEYCODE_DPAD_RIGHT):
                return super.dispatchKeyEvent(event);

            case(KEYCODE_DPAD_LEFT):
                return super.dispatchKeyEvent(event);

            case(KEYCODE_DPAD_CENTER):
                return super.dispatchKeyEvent(event);

            case(KEYCODE_MEDIA_PLAY_PAUSE):
                break;


            case(KEYCODE_MENU):
                if(!drawerLayout.isDrawerOpen(GravityCompat.END)){
                    drawerLayout.openDrawer(GravityCompat.END);
                    navigationView.requestFocus();

                }else {
                    drawerLayout.closeDrawers();
                }

                break;

            case(KEYCODE_BACK):
                if(drawerLayout.isDrawerOpen(GravityCompat.END)){
                    drawerLayout.closeDrawers();
                }

                break;
        }


        Log.i(TAG, "KeyEvent = " + event.toString());

//        Toast.makeText(this, event.toString(), Toast.LENGTH_LONG).show();
        return true;
//        return super.dispatchKeyEvent(event);
    }
}
