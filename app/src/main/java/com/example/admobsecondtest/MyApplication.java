package com.example.admobsecondtest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Date;
import java.util.logging.Logger;

//it extends the application class
//this is so that we can initialize the MobileAds SDK
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {


    //private Object of the AppOpenAdManager to be instantiated
    private AppOpenAdManager appOpenAdManager;
    //reference of the currentactivity when this ad was called
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        //initialize the mobile ads SDK ehre
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                //handles initialization statuses
                //either NOT_READY or READY for initializationStatus
            }
        });
        //this class provides full access to the lifecycle of an application
        //this method puts an observer - that observes what happens during the lifecycle -
        //observer enables us to handle things that happen during certain parts of the lifecycle
        //like when app is closed or switched to another
        //the ProcessLifecyclerOwner is deprecated
        registerActivityLifecycleCallbacks(this);
        //instantiate an instance of the appOpenAdManager so we can use all the functions available in that class
        appOpenAdManager = new AppOpenAdManager();
    }




    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        //handles what happens when activity is built
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        //handles what happens when activity is started
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        //Handles what happens when app is resumed from pause
        //show ad when activity is resumed from foreground
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        //handles what happens when activity is paused
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        //handles what happens when activity is stopped
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        //handles states of the activity
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        //handles what happens when activity is destroyed
    }


    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener){
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    public interface OnShowAdCompleteListener {
        //interface to make sure function exists where it needs
        //optional not mandatory
        void onShowAdComplete();
    }

    //Manager class of the ad
    //to handle ad requests, ad showing, ad failures, ad clicks, everything in one class
    //so every methods are already here that's why we make a manager class
    private class AppOpenAdManager{

        //just for log purposes, as the tag so we can easily reference to the debug error codes/messages
        private static final String LOG_TAG = "AppOpenAdManager";

        //ad test unit id, replace with real ad unit ID
        private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";

        //instance of the ad that we're going to show
        //so we instantiate it as null first
        //so the procedure will be to load it
        //when it loads, then this instance will be instantiated
        private AppOpenAd appOpenAd = null;

        //boolean to refer if the ad is currently loading
        //loading as in being requested
        //while adrequest is being requested, then it will be true
        //when it's done requesting, as in you get an ad from the servers, then this will be false again
        private boolean isLoadingAd = false;

        //boolean to refer to if ad is showing on screen
        //we put it on default false
        //so after request is done, ad will be shown
        //ad is shown using the
        //appOpenAd.show(activity)
        //in the showAdIfAvailable() method
        //so when it is shown = true
        //after the ad is shown, or if error occurs to show the ad = false
        private boolean isShowingAd = false;

        private long loadTime = 0;

        public AppOpenAdManager(){}


        //same as fetchAd() in the main documentations
        //function for getting the ad
        //this is the method that's actually doing the job
        private void loadAd(Context context){

            //clause to stop the ad load
            //isLoadingAd means it's currently requesting, so return as in stop the clause no need to load the ad as it's already requesting
            //isAdAvailable means the ad is already requested and available to be shown, so no need to request again
            //just go
            if(isLoadingAd||isAdAvailable()){
                //ad available and age is less than 4 hours so no need to request another ad
                //if ad not available or available but older than 4 hours then does not go through this clause instead loadAd() is called
                //to request new ad
                return;
            }

            //set the isLoadingAd to be true so that other methods that we've boolean checked doesn't instantiate or should instantiate
            isLoadingAd = true;
            //get a request build
            AdRequest request = new AdRequest.Builder().build();
            //request an ad using the .load() function
            //takes params of : context, ad unit id string, the request build just before, specification of orientation whether portrait or landscape, and a callback
            //callback is for handling what happens during certain parts of the ad process
            AppOpenAd.load(context, AD_UNIT_ID, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    //handle what happens when ad fails to load
                    //set to false because it means the ad load is finished when there is error
                    isLoadingAd = false;
                    //log the error message
                    Log.d(LOG_TAG, "onadfailed" + loadAdError.getMessage());

                }

                @Override
                public void onAdLoaded(@NonNull AppOpenAd ad) {
                    //handle what happens when ad is loaded
                    //this does not show the ad, just sets the appOpenAd from null to instantiated
                    //the param @nonnull is the loaded ad, so assign a temp to it to use the loaded ad
                    appOpenAd = ad;
                    //set to false because ad is already received
                    isLoadingAd = false;
                    //get time of the ad receival because we want to compare later with how many hours it had been
                    //if it's after 4 hours we'll load a new ad
                    loadTime = (new Date()).getTime();
                    Log.d(LOG_TAG, "onAdLoaded");
                }
            });
        }

        //just a method to count the time between the last ad loaded and current time
        //is a method with param n = number of hours
        //currently in beta version so it's set to 4 but subjected so that it can be used when this
        //changes
        private boolean wasLoadTimeLessThanNHoursAgo(long n){
            //loadtime = the time when the last ad was loaded
            //get difference of hours between (new date) = current date and load time = last ad time
            long dateDifference = new Date().getTime() - loadTime;
            //conversion from millis to secs
            long numMillisInSecs = 3600000;
            //return a boolean that tells whether it's bigger than 4 hours or not
            //if less than 4 then return true
            //if more then false
            return (dateDifference < (numMillisInSecs * numMillisInSecs));
        }

        private boolean isAdAvailable() {
            //boolean check for if ad is already available & load time is < 4
            //if ad is already set  and last load is < 4 then return true
            //this means the ad is available & already 4 hours old then we should renew with a new ad
            //if it's available but fresh under 4 hours or it's not available yet, then return false
            //we return false so that
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
            // if it's available and ad age < 4 = true =
            //if it's not available or ad age > 4 = false
        }

        //one method to use but is not used in this example
        private void showAdIfAvailable(@NonNull final Activity activity){
            showAdIfAvailable(activity, () -> {

            });
        }

        //method used to show ad if available
        //takes activity instance with a listener for when the ad is shown
        private void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener){
            //when ad is already showing then just exit this clause
            //no need to go through this function if there's already an ad showing
            if(isShowingAd) {
                return;
            }

            //go through this clause if there's no ad available
            //how to show ad if you don't have an ad to show
            if(!isAdAvailable()){
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
                return;
            }

            //if ad is available and ad is yet to be shown, set callback first
            //to handle what happens during the ad process
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    //handles what happens when user clicks the ad
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    //handles what happens when user dismisses/closes the ad
                    super.onAdDismissedFullScreenContent();
                    //nullify the ad because it's already shown
                    //request another ad so that we don't show an expired ad/same ad to same user
                    appOpenAd = null;
                    //ad is dismissed so set it to false
                    isShowingAd = false;
                    onShowAdCompleteListener.onShowAdComplete();
                    //request another ad as we're done with this one
                    loadAd(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    //handles what happens when error occurs when showing the ad
                    super.onAdFailedToShowFullScreenContent(adError);
                    //get error message adError for debugging use
                    //current ad might be faulty so set it to null
                    //request another ad
                    appOpenAd = null;
                    //ad failed to show so for safety purposes go false
                    isShowingAd = false;
                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                }

                @Override
                public void onAdImpression() {
                    //handles what happens when ad impression is recorded
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    //handles what happens when ad is successfully show
                    super.onAdShowedFullScreenContent();
                    //ad is shown so set this to true
                    isShowingAd = true;
                }
            });
            //show the ad
            appOpenAd.show(activity);
        }
        //don't need else clause here because we handled the elses in the if-return clauses already
    }

}
