package team.diamond.kaizer.models;

import android.webkit.JavascriptInterface;


import team.diamond.kaizer.OnCallRandom;

public class InterfaceJava {

    OnCallRandom callActivity;


    public InterfaceJava(OnCallRandom callActivity) {
        this.callActivity = callActivity;
    }


    @JavascriptInterface
    public void  onPeerConnected(){
        callActivity.onPeerConnected();


    }



}
