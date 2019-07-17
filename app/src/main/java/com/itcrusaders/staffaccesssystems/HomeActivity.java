package com.itcrusaders.staffaccesssystems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.VideoView;

public class HomeActivity extends AppCompatActivity {

    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;
    VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("com.itcrusaders.staffaccesssystems.Admin",
                MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()
                        +"/"
                        +R.raw.fandf);
        videoview.setVideoURI(uri);
        videoview.start();

        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    mp.setVolume(0f, 0f);
                    mp.setLooping(true);
                    if(mCurrentVideoPosition!=0){
                        mMediaPlayer.seekTo(mCurrentVideoPosition);
                        mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                        mMediaPlayer.start();
                    }
                    mp.setVolume(0f,0f);
                } catch (Exception e) {
                    e.printStackTrace();
                }    mMediaPlayer=mp;
                //mMediaPlayer.setLooping(true);
            }
        });

        // TODO: 5/30/2019 change Dashboard to StaffLogin 
        findViewById(R.id.btn_staff_portal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,StaffLogin.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_visitors_portal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,VisitorLogin.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMediaPlayer!=null) {
            mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
            videoview.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoview.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mMediaPlayer.reset();
            //mMediaPlayer= null;
        }catch (IllegalStateException e){
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_login, menu);
        if(sharedPreferences.getString("clearanceLevel","")==null){
            menu.removeItem(R.id.action_admin_login);
        }else{
            menu.removeItem(R.id.action_admin_logout);
        }
        return true;
    }
    SharedPreferences sharedPreferences;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_admin_login) {
            Intent intent = new Intent(this,AdminLogin.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_admin_logout){
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(this,SplashScreen.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
