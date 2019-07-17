package com.itcrusaders.staffaccesssystems;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.VideoView;

public class SplashScreen extends AppCompatActivity {

    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activitysplash);
            VideoView videoView = findViewById(R.id.videosplash);



            Uri video = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.splash);

            videoView.setVideoURI(video);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //jump();
                }
            });
            videoView.start();
        }catch(Exception ex){
            Log.d("exception",ex.toString());
            jump();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        jump();
        return true;
    }

    private void jump() {
        if(isFinishing())
            return;
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }
}
