package com.example.hrant.alarmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

/**
 * Created by Hrant on 01.06.2017.
 */

public class AlarmReceiverVibrate extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long[] pattern =  {200, 200, 200, 200, 200, 100, 100, 100, 100, 100, 300, 100, 100, 100, 100, 100, 100, 100,
                300, 100, 100, 100};
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
    }
}
