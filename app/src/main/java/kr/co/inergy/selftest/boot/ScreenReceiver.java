package kr.co.inergy.selftest.boot;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

public class ScreenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
		if(tm.getCallState() == TelephonyManager.CALL_STATE_RINGING)
		{
		}
		else if(tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK)
		{
		}
		else
		{
			ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(10);
			
			for (int i = 0; i < taskInfo.size(); i++) {
				
			   }

			String currentTask = taskInfo.get(0).topActivity.getPackageName();
	
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

			if(currentTask.equalsIgnoreCase("com.sec.android.app.clockpackage"))
			{
			}else if ((currentTask.equalsIgnoreCase("com.lge.clock"))) {
				
			}
			else
			{
                Log.e("SKY" , "ScreenService :: " );

                //ApplicationClass.fromReceiver = 1;
				try {
                    Intent i = new Intent(context, ScreenService.class);
                    context.startService(i);
//					Intent i = new Intent();
//					i.setClassName("sjy.policenewproject", "sjy.policenewproject.LockScreenActivity");
//					//i.setAction(Intent.ACTION_MAIN);
//					//i.addCategory(Intent.CATEGORY_HOME);
//					//i.addCategory(Intent.CATEGORY_DEFAULT);
//					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					context.startActivity(i);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			
		}

	}

}
