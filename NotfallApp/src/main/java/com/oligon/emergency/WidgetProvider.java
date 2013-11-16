package com.oligon.emergency;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    private final static String UPDATE_CONTENT = "EmergencyAppWidgetUpdateContent";
    private final static String KEY_ITEM = "EmergencyAppWidgetItem";

    private final static String[] CONTENT_TEXT = {"Feuerwehr - 112", "Polizei - 110", "Bereitschaftsarzt - 116 117", "Giftnotrufzentrale BaWü - 0176 19240", "EC-Karte sperren - 116 116", "Personalausweis sperren - 0180 133 33 33", "ADAC Pannendienst - 01802 22 22 22", "Zugfahrpläne - 0800 150 70 90", "RGD Sekretariat - 07162 9226 25"};
    private final static int[] CONTENT_IMAGE = {R.drawable.ic_fire, R.drawable.ic_police, R.drawable.ic_doctor, R.drawable.ic_poison, R.drawable.ic_ec, R.drawable.ic_perso, R.drawable.ic_adac, R.drawable.ic_db, R.drawable.ic_behavior};

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            updateLayout(context, appWidgetManager, appWidgetId, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds)
            updateLayout(context, appWidgetManager, appWidgetId, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (UPDATE_CONTENT.equals(intent.getAction()))
            updateLayout(context, AppWidgetManager.getInstance(context), intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0), intent.getIntExtra(KEY_ITEM, 0));
    }

    private void updateLayout(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int item) {
        int layoutType = 1, layoutId;
        if (appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY) == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD) {
            layoutId = R.layout.widget_layout_lock;
            layoutType = 0;
        } else if (appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) >= 100)
            layoutId = R.layout.widget_layout_large;
        else
            layoutId = R.layout.widget_layout;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutId);
        switch (layoutType) {
            case 0:
                remoteViews.setOnClickPendingIntent(R.id.widget_behavior, PendingIntent.getActivity(context, 4, new Intent(context, ActivityBehavior.class), 0));
                remoteViews.setOnClickPendingIntent(R.id.widget_numbers, PendingIntent.getActivity(context, 5, new Intent(context, ActivityNumbers.class), 0));
                break;
            case 1:
                remoteViews.setTextViewText(R.id.widget_text, CONTENT_TEXT[item]);
                remoteViews.setImageViewResource(R.id.widget_image, CONTENT_IMAGE[item]);
                remoteViews.setOnClickPendingIntent(R.id.widget_home, PendingIntent.getActivity(context, 0, new Intent(context, ActivityMain.class), PendingIntent.FLAG_CANCEL_CURRENT));
                remoteViews.setOnClickPendingIntent(R.id.widget_next, PendingIntent.getBroadcast(context, 1, new Intent(context, getClass()).putExtra(KEY_ITEM, item + 1 > CONTENT_IMAGE.length - 1 ? 0 : item + 1).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId).setAction(UPDATE_CONTENT), PendingIntent.FLAG_UPDATE_CURRENT));
                remoteViews.setOnClickPendingIntent(R.id.widget_previous, PendingIntent.getBroadcast(context, 2, new Intent(context, getClass()).putExtra(KEY_ITEM, item - 1 < 0 ? CONTENT_IMAGE.length - 1 : item - 1).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId).setAction(UPDATE_CONTENT), PendingIntent.FLAG_UPDATE_CURRENT));
                remoteViews.setOnClickPendingIntent(R.id.widget_call, PendingIntent.getActivity(context, 3, new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + CONTENT_TEXT[item].replaceAll("\\D+",""))), PendingIntent.FLAG_UPDATE_CURRENT));
                remoteViews.setProgressBar(R.id.widget_progress, CONTENT_IMAGE.length, item + 1, false);
                break;
        }
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

}
