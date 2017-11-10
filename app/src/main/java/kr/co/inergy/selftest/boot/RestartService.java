package kr.co.inergy.selftest.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartService extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        Log.e("SKY" , "RestartService :: " +intent.getAction());

        if(intent.getAction().equals("RestartService.ACTION_RESTART_SCREENSERVICE"))
		{
            Log.e("SKY" , "RestartService :: " );

            Intent i = new Intent(context, ScreenService.class);
			context.startService(i);
		}

	}

}
