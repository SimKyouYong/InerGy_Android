package kr.co.inergy.selftest;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import kr.co.inergy.selftest.common.Check_Preferences;
import kr.co.inergy.selftest.common.CommonUtil;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    CommonUtil dataSet = CommonUtil.getInstance();

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        Check_Preferences.setAppPreferences(getApplicationContext() , "REG_ID" , token);
        //dataSet.REG_ID = token;
    }

}
