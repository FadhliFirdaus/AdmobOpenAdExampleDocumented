package com.example.admobsecondtest;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    //how many seconds we want the splash screen to show
    private static final long howManySecondsForSplashScreen = 10;
    //how many seconds left until the splash screen closes
    private long secondsRemaining;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        //start timer
        createTimer(howManySecondsForSplashScreen);
    }

    private void createTimer(long howManySecondsForSplashScreen) {
        //textview to show the time remaining till splash closes
        //make a reference to it
        final TextView counter = findViewById(R.id.timer);

        CountDownTimer countDownTimer = new CountDownTimer(howManySecondsForSplashScreen * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //each tick is per countDownInterval, 1000 = 1 secs
                secondsRemaining = ((millisUntilFinished / 1000) + 1);
                counter.setText("" + secondsRemaining + " seconds remaining");
            }

            @Override
            public void onFinish() {
                //handles what happens when the timer finishes
                //set seconds remaining to default
                secondsRemaining = 0;
                counter.setText("Done");
                //gets the application that owns this activity
                Application application = getApplication();

                //if the application that owns this activity is not
                //our open ad activity, then just go straight
                //to main activity
                if(!(application instanceof MyApplication)){
                    startMainActivity();
                    return;
                }

                //else, show the ad
                ((MyApplication) application).showAdIfAvailable(SplashActivity.this, new MyApplication.OnShowAdCompleteListener() {
                    @Override
                    public void onShowAdComplete() {
                        //after showing the ad, then go to mainActivity
                        startMainActivity();
                    }
                });
            }

        };
        //start the timer
        countDownTimer.start();
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }
}
