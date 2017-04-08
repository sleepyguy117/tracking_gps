package com.example.hanswee.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by hanswee on 4/8/17.
 */

public class MyBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "HELLO THIS WORKED", Toast.LENGTH_LONG).show();
        Intent serviceIntent = new Intent(context, TrackService.class);
        context.startService(serviceIntent);
    }
}
