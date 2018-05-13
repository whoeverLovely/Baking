package com.louise.udacity.bakingapp;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.louise.udacity.bakingapp.data.Ingredient;

import java.util.ArrayList;

public class MyWidgetRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewFactory(getApplicationContext());
    }
}
