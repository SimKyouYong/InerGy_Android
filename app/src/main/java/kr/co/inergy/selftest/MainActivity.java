package kr.co.inergy.selftest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import co.kr.sky.AccumThread;
import co.kr.sky.webview.SKYWebview;
import kr.co.inergy.selftest.boot.ScreenService;
import kr.co.inergy.selftest.common.Check_Preferences;
import kr.co.inergy.selftest.common.CommonUtil;
import kr.co.inergy.selftest.common.DEFINE;

public class MainActivity extends Activity {
    private SKYWebview mWebView;             //웹뷰
    WebView pWebView;

    public static TextToSpeech myTTS;
    public static LocationManager myLocationManager;
    CommonUtil dataSet = CommonUtil.getInstance();

    public static Intent intent;
    SKYWebview wc;
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 42;
    private ValueCallback<Uri[]> mFilePathCallback;
    private static final String TYPE_IMAGE = "image/*";
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 40;
    private final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 41;
    private ValueCallback<Uri[]> filePathCallbackLollipop;


    private int count;
    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            count++;
        }
    };


    //Post 보내기 위한 SKY 라이브러리
    AccumThread mThread;
    Map<String, String> map = new HashMap<String, String>();
    public static String return_fun = "";


    public static String TTS_str = "문단을 말해주세요";

    public static MediaPlayer music;
    public static Button setting_btn , unlock_btn;
    boolean popup = false;

    public RelativeLayout view001;
    public Button reflash;
    public ImageView img1;
    public TextView txt1;


    int first_view = 0;
    public Context mContext;
    @Override
    public void onResume() {
        super.onResume();
        if (!isServiceRunningCheck()){
            if (Check_Preferences.getAppPreferencesboolean(MainActivity.this , "BACKGROUND")){
                //서비스 실행
                intent = new Intent(this, ScreenService.class);
                startService(MainActivity.intent);
            }
        }
    }
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ((service.service.getClassName()).matches(".*ScreenService.*")) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Log.e("SKY" , "onNewIntent");
        if (getIntent().getStringExtra("arlam") != null){
            String Name=intent.getStringExtra("Name");
            String Message=intent.getStringExtra("Message");
            String SMS_Message=intent.getStringExtra("SMS_Message");

            Log.e("SKY" , "onNewIntent Name :: " + Name);
            Log.e("SKY" , "onNewIntent Message :: " + Message);
            Log.e("SKY" , "onNewIntent SMS_Message :: " + SMS_Message);

            //문자 발송 화면 이동

        }else{
            if(getIntent().getStringExtra("url") == null || getIntent().getStringExtra("url").equals("")){
                mWebView.loadUrl(DEFINE.DEFAULT_URL);
            }else{
                mWebView.loadUrl(getIntent().getStringExtra("rul"));
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        setting_btn = (Button)findViewById(R.id.setting_btn);
        unlock_btn = (Button)findViewById(R.id.unlock_btn);
        view001 = (RelativeLayout)findViewById(R.id.view001);
        reflash = (Button)findViewById(R.id.reflash);
        img1 = (ImageView)findViewById(R.id.img1);
        txt1 = (TextView)findViewById(R.id.txt1);

        findViewById(R.id.setting_btn).setOnClickListener(btnListener);
        findViewById(R.id.unlock_btn).setOnClickListener(btnListener);
        findViewById(R.id.home_btn).setOnClickListener(btnListener);
        findViewById(R.id.back_btn).setOnClickListener(btnListener);
        findViewById(R.id.reflash).setOnClickListener(btnListener);

        //인터넷 환경 체크
        if (!checkNetwordState()) {
            //인터넷 끊김!!
            first_view = 1;
            view001.setVisibility(View.VISIBLE);
            reflash.setVisibility(View.VISIBLE);
            img1.setVisibility(View.VISIBLE);
            txt1.setVisibility(View.VISIBLE);
            return;
        }else{
            setInit();
        }


        if (getIntent().getStringExtra("arlam") != null){
            Log.e("SKY" , "onCreate arlam");

            String Name=getIntent().getStringExtra("Name");
            String Message=getIntent().getStringExtra("Message");
            String SMS_Message=getIntent().getStringExtra("SMS_Message");

            Log.e("SKY" , "onNewIntent Name :: " + Name);
            Log.e("SKY" , "onNewIntent Message :: " + Message);
            Log.e("SKY" , "onNewIntent SMS_Message :: " + SMS_Message);

            //문자 발송 화면 이동
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", SMS_Message); // 보낼 문자
            sendIntent.putExtra("address", ""); // 받는사람 번호
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
        }else if (getIntent().getStringExtra("arlam2") != null){
            Log.e("SKY" , "onCreate arlam2");

            String url=getIntent().getStringExtra("url");

            Log.e("SKY" , "onNewIntent url :: " + url);

            if(getIntent().getStringExtra("url") == null || getIntent().getStringExtra("url").equals("")){
                mWebView.loadUrl(DEFINE.DEFAULT_URL);
            }else{
                mWebView.loadUrl(getIntent().getStringExtra("url"));
            }
        }
    }
    private void setInit(){
        Log.e("SKY" ,"********onCreate********");
        //버전 체크
        map.clear();
        map.put("url", DEFINE.VERSION_CHECK);
        mThread = new AccumThread(this, mAfterAccum, map, 2, 0, null);
        mThread.start(); // 스레드 시작!!

        intent = new Intent(this, ScreenService.class);
        startActivity(new Intent(MainActivity.this, IntroActivity.class));
        if (myLocationManager == null) {
            myLocationManager = (LocationManager)getSystemService(this.LOCATION_SERVICE);
        }

        myTTS=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    myTTS.setLanguage(Locale.KOREA);
                }
            }
        });

        settingGPS();

        //웹뷰 셋팅
        mWebView = (SKYWebview) findViewById(R.id.webview);
        mWebView.Setting(this , mAfterAccum , mWebView , "kr.co.inergy.selftest.common.FunNative" , null , pWebView);
        //Log.e("SKY" , "url22 :: " + getIntent().getStringExtra("url"));
        if(getIntent().getStringExtra("url") == null || getIntent().getStringExtra("url").equals("")){
            mWebView.loadUrl(DEFINE.DEFAULT_URL);
        }else{
            mWebView.loadUrl(getIntent().getStringExtra("url"));
        }
    }
    public boolean checkNetwordState() {
        ConnectivityManager connManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo state_3g = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo state_wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo state_blue = connManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);

        return state_3g.isConnected() || state_wifi.isConnected()|| state_blue.isConnected();
    }
    //버튼 리스너 구현 부분
    View.OnClickListener btnListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reflash:
                    if (first_view == 1){
                        setInit();
                    }else{
                        mWebView.reload();
                    }
                    break;
                case R.id.setting_btn:
                    mWebView.loadUrl(DEFINE.SETTING_002);
                    break;
                case R.id.unlock_btn:
                    mWebView.loadUrl(DEFINE.SETTING_001);
                    break;
                case R.id.home_btn:     //ok
                    if (Check_Preferences.getAppPreferences(MainActivity.this , "BACK_KEY").equals("true")){
                        Log.e("SKY" , "1");
                        mWebView.goBack();
                    }else if (Check_Preferences.getAppPreferences(MainActivity.this , "BACK_KEY").equals("false")){
                        Log.e("SKY" , "2");
                        mWebView.loadUrl("javascript:pressBackKey()");
                    }else{
                        Log.e("SKY" , "3");
                        mWebView.goBack();
                    }
                    break;
                case R.id.back_btn:     //ok
                    //Intent alarmIntent = new Intent(getApplicationContext(), BroadcastD.class);
                    //dataSet.cancelAllAlarms(mContext , alarmIntent);

                    mWebView.goBack();
                    break;
            }
        }
    };
    Handler mAfterAccum = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                String res = (String) msg.obj;

                Log.e("SKY" , "VERSION :: " + res);
                String version;
                try {
                    PackageInfo i = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0);
                    version = i.versionName.trim();

                    float i_version = Float.parseFloat(version);
                    float i_result = Float.parseFloat(res);
                    Log.e("CHECK", "version  -> " + i_version);
                    Log.e("CHECK", "RESULT  -> " + i_result);

                    if (i_result != i_version){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        builder.setMessage("업데이트 되었습니다. 구글플레이어 스토어로 이동합니다");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //어플 다운로드 페이지 이동(구글 스토어 이동
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();




                    }

                } catch(PackageManager.NameNotFoundException e) { }
            }else if(msg.arg1 == 300){
                mUploadMessage = (ValueCallback<Uri>) msg.obj;
            }else if(msg.arg1 == 301){
                mFilePathCallback = (ValueCallback<Uri[]>) msg.obj;
            }else if(msg.arg1 == 8002){
                //Webvie Start
                first_view = 2;
                //인터넷 환경 체크
                if (!checkNetwordState()) {
                    //인터넷 끊김!!
                    mWebView.goBack();
                    view001.setVisibility(View.VISIBLE);
                    reflash.setVisibility(View.VISIBLE);
                    img1.setVisibility(View.VISIBLE);
                    txt1.setVisibility(View.VISIBLE);
                }else{
                    reflash.setVisibility(View.GONE);
                    img1.setVisibility(View.GONE);
                    txt1.setVisibility(View.GONE);

                    mHandler.postDelayed(r, 1000);
                }
            }

        }
    };
    Handler mHandler = new Handler();
    Runnable r= new Runnable() {
        @Override
        public void run() {
            view001.setVisibility(View.GONE);
        }
    };
    public void alertCheckGPS() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("원활한 서비스를 위해\nGPS를 활성화를 부탁 드립니다.");
        builder.setCancelable(false);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveConfigGPS();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // GPS 설정화면으로 이동
    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(gpsOptionsIntent , 1);
    }

    private void settingGPS() {
        // Acquire a reference to the system Location Manager

        // GPS 프로바이더 사용가능여부
        Boolean isGPSEnabled = myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        Boolean isNetworkEnabled = myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.e("Main", "isGPSEnabled=" + isGPSEnabled);
        Log.e("Main", "isNetworkEnabled=" + isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                dataSet.latitude = location.getLatitude();
                dataSet.longitude = location.getLongitude();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }
        myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        myLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = myLocationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLatitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("SKY" , "RESULT :: " + requestCode);
        Log.e("SKY" , "resultCode :: " + resultCode);
        Log.e("SKY" , "data :: " + data);
        if (data == null) {
            Log.e("SKY" , "data null:: ");
            if (mUploadMessage != null){
                mUploadMessage.onReceiveValue(null);
            }
            if (filePathCallbackLollipop != null){
                filePathCallbackLollipop.onReceiveValue(null);

            }
            if (mFilePathCallback != null){
                mFilePathCallback.onReceiveValue(null);
            }

            mUploadMessage = null;
            mFilePathCallback = null;
            filePathCallbackLollipop = null;

            return;
        }
        switch (requestCode) {
            case 1:
                settingGPS();
                break;
            case 999:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.e("SKY" , "RESULT :: " + result.get(0).trim());
                    Log.e("SKY" , "return_fun :: " + return_fun);
                    mWebView.loadUrl("javascript:"+return_fun + "('" + result.get(0).trim() + "')");
                }
                break;

        }
        if(requestCode == FILECHOOSER_RESULTCODE){
            if(null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;



        }else  if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri[] results = new Uri[]{getResultUri(data)};

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            } else {
                if (mUploadMessage == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri result = getResultUri(data);

                Log.d(getClass().getName(), "openFileChooser : "+result);
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }else if (requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE) {
            if (filePathCallbackLollipop == null) return ;
            filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            filePathCallbackLollipop = null;
        }

    }
    @Override
    @SuppressLint("NewApi")
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
            //mWebView.loadUrl("javascript:pressBackKey()");

            if (Check_Preferences.getAppPreferences(MainActivity.this , "BACK_KEY").equals("true")){
                Log.e("SKY" , "1");
                mWebView.goBack();
            }else if (Check_Preferences.getAppPreferences(MainActivity.this , "BACK_KEY").equals("false")){
                Log.e("SKY" , "2");
                mWebView.loadUrl("javascript:pressBackKey()");

                return true;
            }else{
                Log.e("SKY" , "3");
                mWebView.goBack();
            }

            return true;
        }else if(keyCode == KeyEvent.KEYCODE_BACK){
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setMessage("종료 하시겠습니까?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private Uri getResultUri(Intent data) {
        Uri result = null;
        if(data == null || TextUtils.isEmpty(data.getDataString())) {
            // If there is not data, then we may have taken a photo
            if(mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath);
            }
        } else {
            String filePath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                filePath = data.getDataString();
            } else {
                //filePath = "file:" + RealPathUtil.getRealPath(this, data.getData());
            }
            result = Uri.parse(filePath);
        }

        return result;
    }

}