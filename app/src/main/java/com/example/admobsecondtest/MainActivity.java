package com.example.admobsecondtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onBackPressed() {
        //moves mainactivity to the previous activity of the stack
        //meaning when you click back, it will revert to this activity
        //when user clicks back button after ad, it will go straight to this
        moveTaskToBack(true);
    }
}