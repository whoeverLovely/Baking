package com.louise.udacity.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.louise.udacity.bakingapp.widget.MyWidgetRemoteViewFactory;

public class MyWidgetRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewFactory(getApplicationContext());
    }
}
