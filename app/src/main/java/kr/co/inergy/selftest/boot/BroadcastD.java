package kr.co.inergy.selftest.boot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kr.co.inergy.selftest.MainActivity;
import kr.co.inergy.selftest.R;

/**
 * Created by GHKwon on 2016-02-17.
 */
public class BroadcastD extends BroadcastReceiver {
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        Log.e("SKY" , "onReceive Is 알람");

        String Name=intent.getStringExtra("Name");
        String Message=intent.getStringExtra("Message");
        String SMS_Message=intent.getStringExtra("SMS_Message");
        String url=intent.getStringExtra("url");

        if(url == null){
            //문자 발송
            Log.e("SKY" , "onReceive Name :: " + Name);
            Log.e("SKY" , "onReceive Message :: " + Message);
            Log.e("SKY" , "onReceive SMS_Message :: " + SMS_Message);

            //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent it = new Intent(context, MainActivity.class);
            it.putExtra("Name" , Name);
            it.putExtra("Message" , Message);
            it.putExtra("SMS_Message" , SMS_Message);
            it.putExtra("url" , url);
            it.putExtra("arlam" , "arlam");

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher).setTicker(Message).setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle("치매체크").setContentText(Message)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

            notificationmanager.notify(1, builder.build());
        }else{
            //url 이동
            Log.e("SKY" , "onReceive SMS_Message :: " + url);

            //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent it = new Intent(context, MainActivity.class);
            it.putExtra("url" , url);
            it.putExtra("arlam2" , "arlam2");

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher).setTicker(Message).setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle("치매체크").setContentText(Message)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

            notificationmanager.notify(1, builder.build());

        }


    }
}