package com.waftinc.fofoli;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

public class FoFoLiIntentService extends IntentService {
    public static final String TAG = FoFoLiIntentService.class.getSimpleName();


    public static final String ACTION_UPDATE_WIDGETS = "com.waftinc.fofoli.action.update_widgets";
    public static final String ACTION_NAVIGATE_TO_MAP = "com.waftinc.fofoli.action.navigate_to_map";
    public static final String EXTRA_ADDRESS = "com.waftinc.fofoli.string.address";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FoFoLiIntentService() {
        super(TAG);
    }

    /**
     * Starts this service to perform UpdateWidget action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, FoFoLiIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_UPDATE_WIDGETS.equals(action)) {
                handleActionUpdateWidgets();
            } else if (ACTION_NAVIGATE_TO_MAP.equals(action)) {
                final String address = intent.getStringExtra(EXTRA_ADDRESS);
                navigateToMap(address);
            }
        }
    }

    private void navigateToMap(String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mapIntent);
    }

    private void handleActionUpdateWidgets() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FoFoLiAppWidgetProvider.class));

        //trigger data update and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

        //Now update all widgets
        FoFoLiAppWidgetProvider.updateAllAppWidgets(this, appWidgetManager, appWidgetIds);
    }
}
