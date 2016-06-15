package com.asuper.playview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.asuper.widget.PlayView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private PlayView mPlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayView = (PlayView) findViewById(R.id.play_view);

        mPlayView.setOnControlStatusChangeListener(new PlayView.OnControlStatusChangeListener() {
           @Override
           public void onStatusChange(View view, boolean state) {
               if (state) {
                   Log.i(TAG, "" + state);
               } else {
                   Log.i(TAG, "" + state);
               }
           }
       });
    }
}
