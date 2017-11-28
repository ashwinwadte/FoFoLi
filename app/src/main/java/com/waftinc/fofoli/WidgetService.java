package com.waftinc.fofoli;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.waftinc.fofoli.adapters.WidgetDataProviderFactory;

public class WidgetService extends RemoteViewsService {

    // So pretty simple just defining the Adapter of the ListView.
    // Here Adapter is WidgetDataProviderFactory
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProviderFactory(this.getApplicationContext());
    }
}