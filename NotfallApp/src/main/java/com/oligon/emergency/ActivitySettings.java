package com.oligon.emergency;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.oligon.emergency.model.DynamicListView;
import com.oligon.emergency.model.StableArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActivitySettings extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, AdapterView.OnItemSelectedListener {

    public final static int NOTIFICATION_ID = 1787299834;
    private static SharedPreferences sp;

    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private AlertDialog dialogWriteNFC;
    private boolean mNumbersDialog = false, mNFCDialog = false, mSMSDialog = false;

    private String ndefMessage = "http://emergency.oligon.com";

    private EditTextPreference mUserName;
    private EditText mText1, mText2;
    private Spinner mSelection1;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        updateNotif(findPreference("prefs_notification"));

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        mUserName = (EditTextPreference) findPreference("prefs_user_name");
        mUserName.setSummary(PreferenceManager.getDefaultSharedPreferences(this).getString("prefs_user_name", ""));

        sp = getSharedPreferences("preferences", MODE_PRIVATE);
        if (sp.getInt("prefs_numbers_count", 0) == 0) {
            String[] numbers, titles;
            Resources res = getResources();
            numbers = res.getStringArray(R.array.numbers_number);
            titles = res.getStringArray(R.array.numbers_title);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("prefs_numbers_count", numbers.length);
            for (int i = 0; i < numbers.length; i++) {
                editor.putString("prefs_numbers_number_" + i, numbers[i]);
                editor.putString("prefs_numbers_titles_" + i, titles[i]);
            }
            editor.commit();
        }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("numbers_dialog", mNumbersDialog);
        outState.putBoolean("nfc_dialog", mNFCDialog);
        outState.putBoolean("sms_dialog", mSMSDialog);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("numbers_dialog"))
            createNumbersDialog();
        if (savedInstanceState.getBoolean("nfc_dialog"))
            createNFCDialog();
        if (savedInstanceState.getBoolean("sms_dialog"))
            createSMSDialog();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("prefs_alarm_numbers")) {
            createNumbersDialog();
        } else if (preference.getKey().equals("prefs_nfc_write")) {
            createNFCDialog();
        }
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

    private void createNumbersDialog() {
        mNumbersDialog = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_prefs_numbers, null);

        ArrayList<String> mItemList = new ArrayList<String>();
        for (int i = 0; i < sp.getInt("prefs_numbers_count", 0); i++)
            mItemList.add(sp.getString("prefs_numbers_titles_" + i, "") + ": " + sp.getString("prefs_numbers_number_" + i, ""));

        StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.text_view, R.id.list_text, mItemList);
        final DynamicListView listView = (DynamicListView) view.findViewById(R.id.listview);

        listView.setCheeseList(mItemList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        builder.setView(view).setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sp.edit();
                ArrayList<String> list = listView.getItemList();
                editor.putInt("prefs_numbers_count", list.size());
                for (int i = 0; i < list.size(); i++) {
                    String[] temp = list.get(i).split(": ");
                    editor.putString("prefs_numbers_titles_" + i, temp[0]);
                    editor.putString("prefs_numbers_number_" + i, temp[1]);
                }
                editor.commit();
                dialog.cancel();
                mNumbersDialog = false;
            }
        }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mNumbersDialog = false;
            }
        }).show();
    }

    private void createSMSDialog() {
        mNumbersDialog = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_prefs_sms, null);

        builder.setView(view)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mNumbersDialog = false;
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mNumbersDialog = false;
                    }
                })
                .show();
    }

    private void createNFCDialog() {
        mNFCDialog = true;
        dialogWriteNFC = new AlertDialog.Builder(this).setTitle("Touch tag to write").setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                disableTagWriteMode();
                ndefMessage = "http://emergency.oligon.com";
            }

        }).create();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_nfc_write, null);
        final Spinner type = (Spinner) view.findViewById(R.id.dialog_nfc_write_type);
        type.setSelection(0);
        mText1 = (EditText) view.findViewById(R.id.dialog_nfc_write_text1);
        mText2 = (EditText) view.findViewById(R.id.dialog_nfc_write_text2);
        mSelection1 = (Spinner) view.findViewById(R.id.dialog_nfc_write_selection1);
        type.setOnItemSelectedListener(this);
        builder.setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (type.getSelectedItemPosition()) {
                            case 0:
                                ndefMessage += "/sms/nmb:" + mText1.getText().toString() + "body:" + mText2.getText().toString();
                                break;
                            case 1:
                                ndefMessage += "/map";
                                break;
                            case 2:
                                switch(mSelection1.getSelectedItemPosition()){
                                    case 0:
                                        ndefMessage += "/behavior";
                                        break;
                                    case 1:
                                        ndefMessage += "/numbers";
                                        break;
                                }
                                break;
                            case 3:
                                ndefMessage += "/tts/text:" + mText1.getText().toString();
                                break;

                        }
                        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
                        mNfcPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                new Intent(getApplicationContext(), ActivitySettings.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                        dialogWriteNFC.show();
                        enableTagWriteMode();
                        dialog.dismiss();
                        mNFCDialog = false;
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mNFCDialog = false;
                    }
                })
                .show();

    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] mWriteTagFilters = new IntentFilter[]{tagDetected};
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
        dialogWriteNFC.cancel();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NdefRecord record = NdefRecord.createUri(ndefMessage);
            NdefMessage message = new NdefMessage(new NdefRecord[]{record});
            if (writeTag(message, detectedTag)) {
                Toast.makeText(this, R.string.dialog_nfc_write_success, Toast.LENGTH_LONG).show();
                dialogWriteNFC.cancel();
            }
        }
    }

    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, R.string.dialog_nfc_write_error_writable, Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(this, R.string.dialog_nfc_write_error_small, Toast.LENGTH_SHORT).show();
                    return false;
                }
                ndef.writeNdefMessage(message);
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("prefs_user_name"))
            mUserName.setSummary(mUserName.getText());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mText1.setVisibility(View.VISIBLE);
                mText2.setVisibility(View.VISIBLE);
                mSelection1.setVisibility(View.GONE);
                mText1.setHint("Nummer");
                mText2.setHint("Text");
                break;
            case 1:
                mText1.setVisibility(View.GONE);
                mText2.setVisibility(View.GONE);
                mSelection1.setVisibility(View.GONE);
                break;
            case 2:
                mText1.setVisibility(View.GONE);
                mText2.setVisibility(View.GONE);
                mSelection1.setVisibility(View.VISIBLE);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.dialog_nfc_write_quick_access, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSelection1.setAdapter(adapter);
                break;
            case 3:
                mText1.setVisibility(View.VISIBLE);
                mText1.setHint("Text");
                mText2.setVisibility(View.GONE);
                mSelection1.setVisibility(View.GONE);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
