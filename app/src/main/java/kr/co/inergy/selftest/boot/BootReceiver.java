package kr.co.inergy.selftest.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
        Log.e("SKY" ,"BootReceiver onReceive " );
		Intent i = new Intent(context, ScreenService.class);
		context.startService(i);

	}

}
