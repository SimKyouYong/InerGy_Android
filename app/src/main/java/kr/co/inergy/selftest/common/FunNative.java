package kr.co.inergy.selftest.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.util.Locale;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.co.inergy.selftest.MainActivity;
import kr.co.inergy.selftest.boot.BroadcastD;
import kr.co.inergy.selftest.boot.ScreenService;


public class FunNative  {

	CommonUtil dataSet = CommonUtil.getInstance();
	private WebView Webview_copy;

	//로컬 사파리 브라우져 이동
    public void WebLoadUrl(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
        Log.e("SKY" , "--WebLoadUrl-- :: ");
        String val[] = url.split(",");
        for (int i = 0; i < val.length; i++) {
            Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(val[0]));
        ac.startActivity(intent);

        Log.e("SKY" , "javascript:"+return_fun + "('" + "true" +  "')");
        vc.loadUrl("javascript:"+return_fun + "('" + "true" +  "')");
    }

    //백그라운드 실행(ing) 여부
    public void BackGroundING(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
        Log.e("SKY" , "--BackGroundING-- :: ");
        String val[] = url.split(",");
        for (int i = 0; i < val.length; i++) {
            Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
        }

        Log.e("SKY" , "javascript:"+return_fun + "('" + Check_Preferences.getAppPreferencesboolean(ac , "BACKGROUND") +  "')");
        vc.loadUrl("javascript:"+return_fun + "('" + Check_Preferences.getAppPreferencesboolean(ac , "BACKGROUND") +  "')");
    }



	public void SendStartGPS(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--SendStartGPS-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		Check_Preferences.setAppPreferences(ac , "LOCATION_INTERVAL" , val[0].trim().replace(" " , ""));
		//dataSet.LOCATION_INTERVAL = Integer.parseInt(val[0].trim().replace(" " , ""));




        ac.stopService(MainActivity.intent);
        if(!Check_Preferences.getAppPreferencesboolean(ac , "ING")){
            ac.startService(MainActivity.intent);
            Check_Preferences.setAppPreferences(ac , "BACKGROUND" , true);
            Check_Preferences.setAppPreferences(ac , "ING" , true);
        }


        Log.e("SKY" , "javascript:"+return_fun + "('" + "true" +  "')");
        vc.loadUrl("javascript:"+return_fun + "('" + "true" +  "')");
	}

	public void SendStopGPS(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--SendStopGPS-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}

		Intent intent = new Intent(ac, ScreenService.class);
		ac.stopService(intent);

        Check_Preferences.setAppPreferences(ac , "BACKGROUND" , false);
        Check_Preferences.setAppPreferences(ac , "ING" , false);

        Log.e("SKY" , "javascript:"+return_fun + "('" + "true" +  "')");
        vc.loadUrl("javascript:"+return_fun + "('" + "true" +  "')");

	}
	
	/* 1.(푸시키 요청하기)
	 * param 
	 * url :: 안씀 
	 * name :: 안씀
	 * return :: JavaScript 함수명
	 * window.location.href = "js2ios://GetPushToken?param1=null&param2=null&return=WGetPushToken";
	 * */
	public void GetPushToken(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--GetPushToken-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		
		Log.e("SKY" , "javascript:"+return_fun + "('" + Check_Preferences.getAppPreferences(ac , "REG_ID") +  "')");
		vc.loadUrl("javascript:"+return_fun + "('" + Check_Preferences.getAppPreferences(ac , "REG_ID") +  "')");

	}
	/* 2.(Speach to Text STT)
         * param 
         * url :: 안씀 
         * name :: 안씀
         * return :: JavaScript 함수명
         * window.location.href = "js2ios://StartSTT?param1=null&param2=null&return=WStartSTT";
         * */
	public void StartSTT(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--SearchSst-- :: ");
		MainActivity.myTTS.stop();
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, MainActivity.TTS_str);
		intent.putExtra("return_fun", return_fun);
		MainActivity.return_fun = return_fun;
		try {
			ac.startActivityForResult(intent, 999);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(ac,"다시 시도해주세요.",
					Toast.LENGTH_SHORT).show();
		}
	}

	/* 3.(MP3 실행)
         * param 
         * url :: 안씀 
         * name :: 안씀
         * return :: JavaScript 함수명
         * window.location.href = "js2ios://MusicStart?param1=null&param2=null&return=WMusicStart";
         * */
	public void MusicStart(String url , Activity ac , final WebView vc , final String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--MusicStart1-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}

        Log.e("SKY" , "--MusicSㅇㅁㅇtart1-- :: ");

		int sound_id = ac.getResources().getIdentifier(val[0], "raw",
				ac.getPackageName());
        Log.e("SKY" , "sound_id : " + sound_id);

        if(sound_id != 0) {

            try {
                if (MainActivity.music == null){

                    MainActivity.music = new MediaPlayer();
                    MainActivity.music = MediaPlayer.create(ac, sound_id);
                    MainActivity.music.setLooping(false);
                    MainActivity.music.start();
                    MainActivity.music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            Log.e("SKY" , "The Playing music is Completed.");
                            Log.e("SKY" , "javascript:"+return_fun + "('" + "true" +  "')");
                            vc.loadUrl("javascript:"+return_fun + "('" + "true" +  "')");
                            MainActivity.music.release();
                            MainActivity.music = null;


                        }
                    });
                }else if(MainActivity.music.isPlaying()){
                    MainActivity.music.stop();
                    MainActivity.music.release();
                    MainActivity.music = null;
                    MainActivity.music = new MediaPlayer();
                    MainActivity.music = MediaPlayer.create(ac, sound_id);
                    MainActivity.music.setLooping(false);
                    MainActivity.music.start();
                    MainActivity.music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            Log.e("SKY" , "The Playing music is Completed.");
                            Log.e("SKY" , "javascript:"+return_fun + "('" + "true" +  "')");
                            vc.loadUrl("javascript:"+return_fun + "('" + "true" +  "')");
                            MainActivity.music.release();
                            MainActivity.music = null;


                        }
                    });
                }else{
                    MainActivity.music.start();
                    MainActivity.music.stop();
                    MainActivity.music.release();
                    MainActivity.music = null;
                }
            }catch (Exception e){
                Log.e("SKY" , "Error : " + e.toString());
                MainActivity.music = null;

            }

		}


	}

	public void MusicPause(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--MusicPause-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}

		MainActivity.music.pause();


	}
	

	/* 4.(MP3 멈춤)
         * param 
         * url :: 안씀 
         * name :: 안씀
         * return :: 안씀
         * window.location.href = "js2ios://MusicStop?param1=null&param2=null&return=null";
         * */
	public void MusicStop(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--MusicStart-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		MainActivity.music.stop();
		MainActivity.music = null;
	}

	/* 5.(gpt on / off 정보 가져오기)
         * param 
         * url :: 안씀 
         * name :: 안씀
         * return :: 안씀
         * window.location.href = "js2ios://MusicStop?param1=null&param2=null&return=null";
         * */
	public void GetGpsStatus(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--GetGpsStatus-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}

		Boolean isGpsEnabled = MainActivity.myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		Log.e("SKY" , "javascript:"+return_fun + "('" + isGpsEnabled +  "')");
		vc.loadUrl("javascript:"+return_fun + "('" + isGpsEnabled +  "')");

	}

    public void SetBackKey(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
        Log.e("SKY" , "--SetBackKey-- :: ");
        String val[] = url.split(",");
        for (int i = 0; i < val.length; i++) {
            Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
        }
        Check_Preferences.setAppPreferences(ac , "BACK_KEY" , val[0]);
        Log.e("SKY" , "javascript:"+return_fun + "('" + val[0] +  "')");
        vc.loadUrl("javascript:"+return_fun + "('" + val[0] +  "')");
    }
	/* 6.(로그인 정보 넘기기)
         * param 
         * url :: 안씀 
         * name :: 안씀
         * return :: 안씀
         * window.location.href = "js2ios://MusicStop?param1=null&param2=null&return=null";
         * */
	public void SetGuid(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--SetGuid-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		Log.e("SKY" , "LOGIN_UDID : " + val[0]);
		Check_Preferences.setAppPreferences(ac , "LOGIN_UDID" , val[0]);
		//Toast.makeText(ac, "저장되었습니다(" + val[0] + ")- 추후 삭제 예정", Toast.LENGTH_SHORT).show();
        Log.e("SKY" , "javascript:"+return_fun + "('" + "true" +  "')");
        vc.loadUrl("javascript:"+return_fun + "('" + "true" +  "')");
	}

	public void GetLocation(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--GetLocation-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		//gps 체크
        Boolean isGpsEnabled = MainActivity.myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.e("SKY" , "isGpsEnabled :: " + isGpsEnabled);
        if (!isGpsEnabled) {
            alertCheckGPS(ac);
        }
		Log.e("SKY" , "javascript:"+return_fun + "('" + dataSet.latitude +  "','" + dataSet.longitude + "')");
		vc.loadUrl("javascript:"+return_fun + "('" + dataSet.latitude +  "','" + dataSet.longitude + "')");

	}


    /* 5.볼륩 제어하기
         * param
         * param1 :: 0 : up  , 1 :down
         * param2 :: 1~10(숫자 사용
         * return :: 반환할 함수명(true)
         * window.location.href = "js2ios://SetVolume?param1=null&param2=null&return=WSetVolume";
         * */
    public void SetVolume(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
        Log.e("SKY" , "--GetLocation-- :: ");
        String val[] = url.split(",");
        for (int i = 0; i < val.length; i++) {
            Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
        }
        AudioManager audioManager = (AudioManager) ac.getSystemService(Context.AUDIO_SERVICE);

        int power = Integer.parseInt(val[1]);
        int volumeUp = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + power;
        int volumeUp2 = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) + power;
        int volumeDown = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - power;
        int volumeDown2 = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) - power;

        if (val[0].equals("0")){
            //up
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeUp, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volumeUp2, 0);
        }else{
            //down
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeDown, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volumeDown2, 0);
        }

        Log.e("SKY" , "javascript:"+return_fun + "('" + "true" + "')");
        vc.loadUrl("javascript:"+return_fun + "('" + "true" + "')");

    }
    /* .볼륩 값 반환
         * param
         * param1 :: null
         * param2 :: null
         * return :: 반환할 함수명(사운드값)
         * window.location.href = "js2ios://GetValVolume?param1=null&param2=null&return=WGetValVolume";
         * */
    public void GetValVolume(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
        Log.e("SKY" , "--GetValVolume-- :: ");
        String val[] = url.split(",");
        for (int i = 0; i < val.length; i++) {
            Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
        }
        AudioManager audioManager = (AudioManager) ac.getSystemService(Context.AUDIO_SERVICE);

        int power = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        Log.e("SKY" , "javascript:"+return_fun + "('" + power + "')");
        vc.loadUrl("javascript:"+return_fun + "('" + power + "')");

    }
    /* 6.Loing Success
         * param1 :: 0 : 로그인 성공 , 1 : 로그아웃
         * param2 :: null
         * return :: 반환할 함수명(true)
         * window.location.href = "js2ios://SetLogin?param1=null&param2=null&return=WSetLogin";
         * */
    public void SetLogin(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
        Log.e("SKY" , "--SetLogin-- :: ");
        String val[] = url.split(",");
        for (int i = 0; i < val.length; i++) {
            Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
        }

        if (val[0].equals("0")){
            //로그인 성공
            MainActivity.setting_btn.setVisibility(View.VISIBLE);
            MainActivity.unlock_btn.setVisibility(View.GONE);
        }else{
            //로그아웃
            MainActivity.setting_btn.setVisibility(View.GONE);
            MainActivity.unlock_btn.setVisibility(View.VISIBLE);
        }

        Log.e("SKY" , "javascript:"+return_fun + "('" + "true" + "')");
        vc.loadUrl("javascript:"+return_fun + "('" + "true" + "')");

    }
    private void alertCheckGPS(final Activity ac) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ac , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("원활한 서비스를 위해\nGPS를 활성화를 부탁 드립니다.");
        builder.setCancelable(false);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveConfigGPS(ac);
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
    private void moveConfigGPS(Activity ac) {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ac.startActivityForResult(gpsOptionsIntent , 1);
    }
	

	/* 6.(로그인 정보 넘기기)
         * param 
         * url :: 안씀 
         * name :: 안씀
         * return :: 안씀
         * window.location.href = "js2ios://MusicStop?param1=null&param2=null&return=null";
         * */
	public void ShareKaKao(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
		Log.e("SKY" , "--ShareKaKao-- :: ");
		String val[] = url.split(",");
		for (int i = 0; i < val.length; i++) {
			Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
		}
		Log.e("SKY" , "LOGIN_UDID : " + val[0]);



		try {
			final KakaoLink kakaoLink = KakaoLink.getKakaoLink(ac);
			final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            /*메시지 추가*/
			kakaoBuilder.addText(val[0]);

            /*이미지 가로/세로 사이즈는 80px 보다 커야하며, 이미지 용량은 500kb 이하로 제한된다.*/
//			String url = "https://developers.kakao.com/assets/img/main/icon_account_login.png";
//			kakaoBuilder.addImage(url);

            /*앱 실행버튼 추가*/
			kakaoBuilder.addAppButton(val[1]);

            /*메시지 발송*/
			kakaoLink.sendMessage(kakaoBuilder, ac);
		} catch (KakaoParameterException e){
			e.printStackTrace();
		}
	}
    /* 7.알람 설정 및 삭제
     * param1 : 추가>0  , 삭제>1 , 모두 삭제>2
     * param2 : 시퀀스
     * param3 : 약이름
     * param4 : 시작일
     * param5 : 종료일
     * param6 : 알람시간
     * param7 : 푸쉬 메시지
     * param8 : 문자메시지
     * return :: 리턴 함수
     * window.location.href = "js2ios://SetNoti?param1=1&param2=2&param3=3&param4=4&param5=5&param6=6&param7=7&param8=8&return=WSetNoti";
     * */
    public void SetNoti(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
        Log.e("SKY" , "--SetNoti-- :: ");
        String val[] = url.split(",");
        for (int i = 0; i < val.length; i++) {
            Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
        }

        if(val[0].equals("0")){
            //추가
            String[] time = val[5].split(":");
            dateDD(ac , Integer.parseInt(val[1]) , val[2] , val[6], val[7], val[3].replace("-" , ""), val[4].replace("-" , ""), Integer.parseInt(time[0]) , Integer.parseInt(time[1]));
        }else if(val[0].equals("2")){
            //모두 삭제
            Intent alarmIntent = new Intent(ac, BroadcastD.class);
            dataSet.cancelAllAlarms(ac , alarmIntent);
        }else if(val[0].equals("1")){
            //하나만 삭제
            Intent alarmIntent = new Intent(ac, BroadcastD.class);
            dataSet.cancelAlarm(ac , alarmIntent , Integer.parseInt(val[1]));


        }
        Log.e("SKY" , "javascript:"+return_fun + "('" + "true" + "')");
        vc.loadUrl("javascript:"+return_fun + "('" + "true" + "')");

    }
    /* 8.알람 설정 및 삭제(문자 안보내고 특정 유알엘로 이동)
     * param1 : 추가>0  , 삭제>1 , 모두 삭제>2
     * param2 : 시퀀스
     * param3 : 약이름
     * param4 : 시작일
     * param5 : 종료일
     * param6 : 알람시간
     * param7 : 푸쉬 메시지
     * param8 : 문자메시지
     * return :: 리턴 함수
     * window.location.href = "js2ios://SetNoti?param1=1&param2=2&param3=3&param4=4&param5=5&param6=6&param7=7&param8=8&return=WSetNoti";
     * */
    public void SetNoti2(String url , Activity ac , WebView vc , String return_fun , Handler mAfterAccum){
        Log.e("SKY" , "--SetNoti2-- :: ");
        String val[] = url.split(",");
        for (int i = 0; i < val.length; i++) {
            Log.e("SKY" , "VAL["+i + "]  :: " + i + " --> " + val[i]);
        }

        if(val[0].equals("0")){
            //추가
            String[] time = val[5].split(":");
            dateDD2(ac , Integer.parseInt(val[1]) , val[2] , val[6], val[7], val[3].replace("-" , ""), val[4].replace("-" , ""), Integer.parseInt(time[0]) , Integer.parseInt(time[1]));
        }else if(val[0].equals("2")){
            //모두 삭제
            Intent alarmIntent = new Intent(ac, BroadcastD.class);
            dataSet.cancelAllAlarms(ac , alarmIntent);
        }else if(val[0].equals("1")){
            //하나만 삭제
            Intent alarmIntent = new Intent(ac, BroadcastD.class);
            dataSet.cancelAlarm(ac , alarmIntent , Integer.parseInt(val[1]));


        }
        Log.e("SKY" , "javascript:"+return_fun + "('" + "true" + "')");
        vc.loadUrl("javascript:"+return_fun + "('" + "true" + "')");

    }
    //StartDate : 20170531  , EndDate : 20170622
    private void dateDD(Activity mContext , int index ,String Name ,String Message , String SMS_Message ,  String StartDate , String EndDate , int hour , int minute){
        Log.e("SKY" , "index :: " +index);
        Log.e("SKY" , "Name :: " +Name);
        Log.e("SKY" , "Message :: " +Message);
        Log.e("SKY" , "SMS_Message :: " +SMS_Message);
        Log.e("SKY" , "StartDate :: " +StartDate);
        Log.e("SKY" , "EndDate :: " +EndDate);
        Log.e("SKY" , "hour :: " +hour);
        Log.e("SKY" , "minute :: " +minute);
        String s1=StartDate;
        String s2=EndDate;
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        //Date타입으로 변경
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse( s1 );
            d2 = df.parse( s2 );
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("SKY" , "EndDate :: " +e.toString());

        }
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        //Calendar 타입으로 변경 add()메소드로 1일씩 추가해 주기위해 변경
        c1.setTime( d1 );
        c2.setTime( d2 );
        Log.e("SKY" , "c2");

        //시작날짜와 끝 날짜를 비교해, 시작날짜가 작거나 같은 경우 출력
        while( c1.compareTo( c2 ) !=1 ){
            //출력
            Log.e("SKY" , "c1.getTime() :: " +c1.get(Calendar.YEAR) + c1.get(Calendar.MONTH) + c1.get(Calendar.DATE) + c1.get(Calendar.HOUR) + c1.get(Calendar.MINUTE));
            dataSet.startAlram(mContext , index , Name , Message , SMS_Message , c1.get(Calendar.YEAR) , c1.get(Calendar.MONTH) , c1.get(Calendar.DATE) , hour , minute);

            //시작날짜 + 1 일
            c1.add(Calendar.DATE, 1);
        }
    }
    private void dateDD2(Activity mContext , int index ,String Name ,String Message , String SMS_Message ,  String StartDate , String EndDate , int hour , int minute){
        Log.e("SKY" , "2index :: " +index);
        Log.e("SKY" , "2Name :: " +Name);
        Log.e("SKY" , "2Message :: " +Message);
        Log.e("SKY" , "2SMS_Message :: " +SMS_Message);
        Log.e("SKY" , "2StartDate :: " +StartDate);
        Log.e("SKY" , "2EndDate :: " +EndDate);
        Log.e("SKY" , "2hour :: " +hour);
        Log.e("SKY" , "2minute :: " +minute);
        String s1=StartDate;
        String s2=EndDate;
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        //Date타입으로 변경
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse( s1 );
            d2 = df.parse( s2 );
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("SKY" , "EndDate :: " +e.toString());

        }
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        //Calendar 타입으로 변경 add()메소드로 1일씩 추가해 주기위해 변경
        c1.setTime( d1 );
        c2.setTime( d2 );
        Log.e("SKY" , "c2");

        //시작날짜와 끝 날짜를 비교해, 시작날짜가 작거나 같은 경우 출력
        while( c1.compareTo( c2 ) !=1 ){
            //출력
            Log.e("SKY" , "c1.getTime() :: " +c1.get(Calendar.YEAR) + c1.get(Calendar.MONTH) + c1.get(Calendar.DATE) + c1.get(Calendar.HOUR) + c1.get(Calendar.MINUTE));
            dataSet.startAlram2(mContext , index , Name , Message , SMS_Message , c1.get(Calendar.YEAR) , c1.get(Calendar.MONTH) , c1.get(Calendar.DATE) , hour , minute);

            //시작날짜 + 1 일
            c1.add(Calendar.DATE, 1);
        }
    }
}
