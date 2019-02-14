
package com.onesouth.clubin_tv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createLobby();
    }

    public void sendTestRequest(View view){
//        GetRequest req = new GetRequest();
//        String url = "https://clubin-tv-backend.herokuapp.com/getVersion";
//
//        String result;
//        TextView version = findViewById(R.id.version);
//
//
//        try {
//            result = req.execute(url).get();
//            version.setText(result);
//        }catch (Exception e){
//            version.setText("Error in http request");
//        }


    }

    public void createLobby(){
        PostRequest req = new PostRequest();
        String url = "https://clubin-tv-backend.herokuapp.com/createLobby";
        JSONObject data = new JSONObject();
        String[] taskInput = new String[]{url, data.toString()};

        TextView lobbyCodeView = findViewById(R.id.lobbyCode);

        String result;
        try {
            result = req.execute(taskInput).get();
            lobbyCodeView.setText(result);
            Log.i(TAG, result);
        }catch (Exception e){
        }


    }
}
