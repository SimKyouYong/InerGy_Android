package kr.co.inergy.selftest.boot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import co.kr.sky.AccumThread;
import kr.co.inergy.selftest.common.Check_Preferences;
import kr.co.inergy.selftest.common.CommonUtil;

public class ScreenService extends Service {

	private static final String TAG = "MyLocationService";
	private LocationManager mLocationManager = null;
	private static final float LOCATION_DISTANCE = 10f;
	CommonUtil dataSet = CommonUtil.getInstance();
    private Handler mHandler;
    private Runnable mRunnable;
    public double latitude = 0;
    public double longitude=0;
    int LOCATION_INTERVAL;

	//Post 보내기 위한 SKY 라이브러리
	AccumThread mThread;
	Map<String, String> map = new HashMap<String, String>();
	
	private class LocationListener implements android.location.LocationListener {
		Location mLastLocation;

		public LocationListener(String provider) {
			Log.e(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.e(TAG, "onLocationChanged: " + location);
			mLastLocation.set(location);

            latitude = location.getLatitude();
            longitude = location.getLongitude();
			Log.e("Main", "latitude=" + location.getLatitude());
			Log.e("Main", "longitude=" + location.getLongitude());
			//Post 발송

		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.e(TAG, "onProviderDisabled: " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.e(TAG, "onProviderEnabled: " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e(TAG, "onStatusChanged: " + provider);
		}
	}



	Handler mAfterAccum = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 0) {
				String res = (String) msg.obj;
				Log.e("CHECK", "RESULT  -> " + res);
			}
		}
	};
    /*
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    */

	LocationListener[] mLocationListeners = new LocationListener[]{
			new LocationListener(LocationManager.PASSIVE_PROVIDER)
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onCreate() {

		Log.e(TAG, "onCreate");
        LOCATION_INTERVAL = Integer.parseInt(Check_Preferences.getAppPreferences(getApplicationContext() , "LOCATION_INTERVAL"));

		initializeLocationManager();

		try {
			mLocationManager.requestLocationUpdates(
					LocationManager.PASSIVE_PROVIDER,
					LOCATION_INTERVAL,
					LOCATION_DISTANCE,
					mLocationListeners[0]
			);
		} catch (SecurityException ex) {
			Log.e(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.e(TAG, "network provider does not exist, " + ex.getMessage());
		}

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }


            mRunnable = new Runnable() {
                @Override
                public void run() {
                    if (Check_Preferences.getAppPreferencesboolean(getApplicationContext() , "BACKGROUND" )) {
                        Log.e("SKY" , "전송 되나......");
                        Log.e("SKY" , "latitude : " + latitude);
                        Log.e("SKY" , "longitude : " + longitude);
                        map.clear();

                        map.put("url", "http://app.nid.or.kr/loitering/dementia_gps.aspx");
                        map.put("id", "" + Check_Preferences.getAppPreferences(getApplicationContext() , "LOGIN_UDID"));
                        map.put("la2", "" + latitude);
                        map.put("lo2", "" + longitude);
                        //snap40 cafe24
////			map.put("url", "http://snap40.cafe24.com/Test/gps.php");
////			map.put("w", "" + location.getLatitude());
////			map.put("g", "" + location.getLongitude());
                        // 스레드 생성
                        mThread = new AccumThread(getApplicationContext(), mAfterAccum, map, 0, 1000, null);
                        mThread.start(); // 스레드 시작!!

                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, LOCATION_INTERVAL);
                    }

                }
            };

            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, LOCATION_INTERVAL);




	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						return;
					}
					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
					Log.e(TAG, "fail to remove location listener, ignore", ex);
				}
			}
		}
	}

	private void initializeLocationManager() {
		Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}

}
