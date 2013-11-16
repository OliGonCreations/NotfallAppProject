package com.oligon.emergency;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import co.juliansuarez.libwizardpager.wizard.LocationHelper;

public class ActivityMain extends SherlockActivity {

    private String locationInString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btBehavior).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ActivityBehavior.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        findViewById(R.id.btNumbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ActivityNumbers.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("prefs_notification", false)) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(ActivitySettings.NOTIFICATION_ID);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(ActivitySettings.NOTIFICATION_ID,
                    new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_stat_main)
                            .setContentTitle(getString(R.string.notification_title))
                            .setContentText(getString(R.string.notification_content))
                            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, ActivityMain.class), PendingIntent.FLAG_CANCEL_CURRENT))
                            .addAction(R.drawable.ic_stat_numbers, getString(R.string.numbers), PendingIntent.getActivity(this, 0, new Intent(this, ActivityNumbers.class), PendingIntent.FLAG_CANCEL_CURRENT))
                            .addAction(R.drawable.ic_stat_behavior, getString(R.string.behavior), PendingIntent.getActivity(this, 0, new Intent(this, ActivityBehavior.class), PendingIntent.FLAG_CANCEL_CURRENT))
                            .setPriority(NotificationCompat.PRIORITY_MIN)
                            .setOngoing(true)
                            .build());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog(this);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, ActivitySettings.class));
                return true;
            case R.id.action_alarm:
                showPanicDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void showAboutDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_about, null);
        try {
            ((TextView) view.findViewById(R.id.versionNumber)).setText("Version " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        view.findViewById(R.id.dialog_play_store).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://search?q=OliGon Creations"));
                context.startActivity(intent);
            }
        });
        builder.setView(view).setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public void showPanicDialog(){
        new LocationWorker().execute();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_panic, null);

        builder.setView(view).setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(locationInString.equals("")){
                        }
                        /*SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage("15555215554",null,locationInString,null,null);
                        sms.sendTextMessage("15555215556", null, locationInString, null, null);
                        */
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:123456789" )).putExtra("sms_body", locationInString));
                        locationInString = "";
                    }
                }).start();
            }
        }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        }).show();
    }

    private class LocationWorker extends AsyncTask<Void, Void, String> {

        LocationHelper myLocationHelper = new LocationHelper(getApplicationContext());

        @Override
        protected String doInBackground(Void... params) {
            while (!myLocationHelper.gotLocation()) {
            }
            return "Lat: " + myLocationHelper.getLat() + ", Long: " + myLocationHelper.getLong() + ", auf " + myLocationHelper.getAccuracy() + "m genau.";
        }

        @Override
        protected void onPostExecute(String result) {
            locationInString = result;
        }
    }
}
