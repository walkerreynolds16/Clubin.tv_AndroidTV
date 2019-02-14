
package com.onesouth.clubin_tv;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendTestRequest(View view){
        SendHTTPRequest req = new SendHTTPRequest();
        String url = "https://clubin-tv-backend.herokuapp.com/getVersion";

        String result;
        TextView version = findViewById(R.id.version);


        try {
            result = req.execute(url).get();
            version.setText(result);
        }catch (Exception e){
            version.setText("Error in http request");
        }


    }
}
