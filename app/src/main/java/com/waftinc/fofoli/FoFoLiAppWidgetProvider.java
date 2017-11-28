package com.waftinc.fofoli;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class FoFoLiAppWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = getListRemoteView(context);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static RemoteViews getListRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.donate_app_widget);

        // set WidgetService intent to act as a adapter
        Intent intent = new Intent(context, WidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_view, intent);

        // Create an Intent to launch MainActivity when donate button clicked
        Intent donateIntent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.SHOW_DONATE_DIALOG, true);
        PendingIntent donatePendingIntent = PendingIntent
                .getActivity(context, 0, donateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.bDonate, donatePendingIntent);

        // Create an intent to listen to distribute food clicks
        Intent navigateIntent = new Intent(context, FoFoLiIntentService.class);
        navigateIntent.setAction(FoFoLiIntentService.ACTION_NAVIGATE_TO_MAP);
        PendingIntent navigatePendingIntent = PendingIntent
                .getService(context, 0, navigateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, navigatePendingIntent);

        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);

        return views;
    }

    /**
     * Updates all widget instances given the widget Ids and display information
     *
     * @param context          The calling context
     * @param appWidgetManager The widget manager
     * @param appWidgetIds     Array of widget Ids to be updated
     */
    public static void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        FoFoLiIntentService.startActionUpdateWidgets(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle
            newOptions) {
        FoFoLiIntentService.startActionUpdateWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

