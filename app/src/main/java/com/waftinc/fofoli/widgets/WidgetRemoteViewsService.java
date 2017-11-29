package com.waftinc.fofoli.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetRemoteViewsService extends RemoteViewsService {

    // So pretty simple just defining the Adapter of the ListView.
    // Here Adapter is WidgetDataProviderFactory
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProviderFactory(this.getApplicationContext());
    }
}