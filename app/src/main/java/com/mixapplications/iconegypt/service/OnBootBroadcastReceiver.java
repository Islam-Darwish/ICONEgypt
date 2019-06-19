package com.mixapplications.iconegypt.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent("com.mixapplications.iconegypt.service.MyFirebaseMessagingService");
        i.setClass(context, MyFirebaseMessagingService.class);
        context.startService(i);
    }
}