package com.oligon.emergency;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class ActivitySettings extends SherlockPreferenceActivity {

    public final static int NOTIFICATION_ID = 1787299834;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        updateNotif(findPreference("prefs_notification"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        updateNotif(preference);
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateNotif(Preference preference) {
        if (preference.getKey().equals("prefs_notification")) {
            if (((CheckBoxPreference) preference).isChecked()) {
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID,
                        new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_stat_main)
                                .setContentTitle(getString(R.string.notification_title))
                                .setContentText(getString(R.string.notification_content))
                                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, ActivityMain.class), PendingIntent.FLAG_CANCEL_CURRENT))
                                .addAction(R.drawable.ic_stat_numbers, getString(R.string.numbers), PendingIntent.getActivity(this, 0, new Intent(this, ActivityNumbers.class), PendingIntent.FLAG_CANCEL_CURRENT))
                                .addAction(R.drawable.ic_stat_behavior, getString(R.string.behavior), PendingIntent.getActivity(this, 0, new Intent(this, ActivityBehavior.class), PendingIntent.FLAG_CANCEL_CURRENT))
                                .setPriority(NotificationCompat.PRIORITY_MIN)
                                .setOngoing(true)
                                .build());
            } else {
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
            }
        }
    }

}
