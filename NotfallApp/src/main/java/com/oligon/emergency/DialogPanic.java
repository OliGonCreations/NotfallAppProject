package com.oligon.emergency;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.SherlockDialogFragment;

import co.juliansuarez.libwizardpager.wizard.LocationHelper;

public class DialogPanic extends SherlockDialogFragment {

    public String locationInString = "";
    private LocationWorker locationWorker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        locationWorker = new LocationWorker();
        locationWorker.execute();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = ((LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_panic, null);
        builder.setView(view)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (locationInString.equals("")) {
                                }
                        /*SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage("5554",null,locationInString,null,null);
                        sms.sendTextMessage("5556", null, locationInString, null, null);
                        */
                                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:123456789")).putExtra("sms_body", locationInString));
                                locationInString = "";
                            }
                        }).start();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        locationWorker.cancel(true);
    }

    private class LocationWorker extends AsyncTask<Void, Void, String> {

        LocationHelper myLocationHelper = new LocationHelper(getActivity());

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

        @Override
        protected void onCancelled() {
            super.onCancelled();
            myLocationHelper.killLocationServices();
        }
    }

}
