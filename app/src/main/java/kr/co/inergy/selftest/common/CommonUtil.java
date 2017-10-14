package kr.co.inergy.selftest.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import kr.co.inergy.selftest.boot.BroadcastD;

public class CommonUtil {
	private static CommonUtil _instance;

    private static final String sTagAlarms = ":alarms";

	public String PHONE;
	public String PHONE_ID;
	public String REG_ID;
	public double latitude = 0;
	public double longitude=0;



	
	static {
		_instance = new CommonUtil();
		try {
			_instance.PHONE = 	   		"";
			_instance.REG_ID = 	   		"";
			_instance.PHONE_ID = 	   		"";
			_instance.latitude = 	   		0;
			_instance.longitude = 	   		0;

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static CommonUtil getInstance() {
		return _instance;
	}

	
	public ArrayList<String> Token_String(String url , String token){
		ArrayList<String> Obj = new ArrayList<String>();

		StringTokenizer st1 = new StringTokenizer( url , token);
		while(st1.hasMoreTokens()){
			Obj.add(st1.nextToken());
		}
		return Obj;
	}


    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        removeAlarmId(context, notificationId);
    }
    private static void removeAlarmId(Context context, int id) {
        List<Integer> idsAlarms = getAlarmIds(context);

        for (int i = 0; i < idsAlarms.size(); i++) {
            if (idsAlarms.get(i) == id)
                idsAlarms.remove(i);
        }

        saveIdsInPreferences(context, idsAlarms);
    }
    private static void saveIdsInPreferences(Context context, List<Integer> lstIds) {
        JSONArray jsonArray = new JSONArray();
        for (Integer idAlarm : lstIds) {
            jsonArray.put(idAlarm);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getPackageName() + sTagAlarms, jsonArray.toString());

        editor.apply();
    }
    private static void saveAlarmId(Context context, int id) {
        List<Integer> idsAlarms = getAlarmIds(context);

        if (idsAlarms.contains(id)) {
            return;
        }

        idsAlarms.add(id);

        saveIdsInPreferences(context, idsAlarms);
    }
    public static void cancelAllAlarms(Context context, Intent intent) {
        Log.e("SKY" , "cancelAllAlarms");

        for (int idAlarm : getAlarmIds(context)) {
            Log.e("SKY" , "idAlarm :: " + idAlarm);
            cancelAlarm(context, intent, idAlarm);
        }
    }
    private static List<Integer> getAlarmIds(Context context) {
        List<Integer> ids = new ArrayList<>();
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            JSONArray jsonArray2 = new JSONArray(prefs.getString(context.getPackageName() + sTagAlarms, "[]"));

            for (int i = 0; i < jsonArray2.length(); i++) {
                ids.add(jsonArray2.getInt(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    public void startAlram(Context context,int index ,String Name ,String Message , String SMS_Message , int year , int month , int day , int hour , int minute) {
        Log.e("SKY" ,"********startAlram********");

        saveAlarmId(context , index);
        Intent alarmIntent = new Intent(context, BroadcastD.class);
        alarmIntent.putExtra("Name" , Name);
        alarmIntent.putExtra("Message" , Message);
        alarmIntent.putExtra("SMS_Message" , SMS_Message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, index, alarmIntent, 0);

        Calendar calendar = Calendar.getInstance();
        //알람시간 calendar에 set해주기

        Log.e("SKY" , ""+year);
        Log.e("SKY" , ""+month);
        Log.e("SKY" , ""+day);
        Log.e("SKY" , ""+hour);
        Log.e("SKY" , ""+minute);

        calendar.set(Calendar.YEAR,  year);
        calendar.set(Calendar.MONTH,  month);
        calendar.set(Calendar.DATE,  day);
        calendar.set(Calendar.HOUR_OF_DAY,  hour);
        calendar.set(Calendar.MINUTE,  minute);

        // AlarmManager 호출
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1분뒤에 AlarmOneMinuteBroadcastReceiver 호출 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            //manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        }
    }
    public void startAlram2(Context context,int index ,String Name ,String Message , String SMS_Message , int year , int month , int day , int hour , int minute) {
        Log.e("SKY" ,"********startAlram2********");
        Log.e("SKY" , "startAlram2 url :: "+SMS_Message);

        saveAlarmId(context , index);
        Intent alarmIntent = new Intent(context, BroadcastD.class);
        alarmIntent.putExtra("url" , SMS_Message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, index, alarmIntent, 0);

        Calendar calendar = Calendar.getInstance();
        //알람시간 calendar에 set해주기

        Log.e("SKY" , ""+year);
        Log.e("SKY" , ""+month);
        Log.e("SKY" , ""+day);
        Log.e("SKY" , ""+hour);
        Log.e("SKY" , ""+minute);

        calendar.set(Calendar.YEAR,  year);
        calendar.set(Calendar.MONTH,  month);
        calendar.set(Calendar.DATE,  day);
        calendar.set(Calendar.HOUR_OF_DAY,  hour);
        calendar.set(Calendar.MINUTE,  minute);

        // AlarmManager 호출
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1분뒤에 AlarmOneMinuteBroadcastReceiver 호출 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            //manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        }
    }
}
