package com.oligon.emergency;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NFCProvider extends Activity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private String mTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String path = getIntent().getData().getEncodedPath();
        Intent intent = null;
        if (path.contains("/map")) {
            intent = new Intent(this, ActivityMap.class);
            if (path.length() > 4) {
                int i = -1;
                Location location = new Location("own");
                while ((i = path.indexOf("lat", i + 1)) > -1) {
                    location.setLatitude(Double.parseDouble(path.substring(Math.max(0, 9), Math.min(i + 4 + 9, path.length()))));
                }
                while ((i = path.indexOf("long", i + 1)) > -1) {
                    location.setLongitude(Double.parseDouble(path.substring(Math.max(0, 23), Math.min(i + 4 + 10, path.length()))));
                }
                intent.putExtra("location", location);
            }
        } else if (path.equals("/numbers")) {
            intent = new Intent(this, ActivityNumbers.class);
        } else if (path.equals("/behavior")) {
            intent = new Intent(this, ActivityBehavior.class);
        } else if (path.contains("/sms")) {
            int i = -1;
            String nmb = "", body = "";
            Pattern pattern = Pattern.compile("nmb:(.*?)body:");
            Matcher matcher = pattern.matcher(path);
            while (matcher.find()) {
                nmb = matcher.group(1);
            }
            while ((i = path.indexOf("body:", i + 1)) > -1)
                body = path.substring(i + 5, path.length());
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + nmb)).putExtra("sms_body", body);
        } else if (path.contains("/tts")) {
            int i = -1;
            while ((i = path.indexOf("text:", i + 1)) > -1) {
                mTTS = path.substring(10, path.length());
            }
            tts = new TextToSpeech(this, this);
        } else {
            intent = new Intent(this, ActivityMain.class);
        }
        if (intent != null)
            startActivity(intent);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.GERMAN);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                tts.speak(mTTS, TextToSpeech.QUEUE_FLUSH, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (tts.isSpeaking()) {
                        }
                        finish();
                    }
                }).start();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
