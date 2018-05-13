package com.louise.udacity.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.louise.udacity.bakingapp.R;
import com.louise.udacity.bakingapp.data.Ingredient;
import com.louise.udacity.bakingapp.data.Recipe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MyWidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    List<Ingredient> mIngredients;

    public MyWidgetRemoteViewFactory(Context context) {
        this.mContext = context;

        // Register broadcast to receive update on recipe list
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, new IntentFilter(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
    }

    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("Receive intent in factory==========================");
            Recipe recipe = intent.getParcelableExtra(MainActivity.EXTRA_RECIPE);
            Timber.d("factory onReceive recipe name " + recipe.getName());
            mIngredients = recipe.getIngredients();
            // Notify factory to update data
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, MyWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetListView);
        }
    };

    // Get called when the appwidget is created for the first time.
    @Override
    public void onCreate() {

    }

    // Get called whenever the appwidget is updated.
    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
    }

    // Number of items that need to be displayed in the widget
    @Override
    public int getCount() {
        if (mIngredients == null)
            return 0;
        return mIngredients.size();
    }

    // Handles all the processing work. It returns a RemoteViews object which in our case is the single list item.
    @Override
    public RemoteViews getViewAt(int position) {

        Timber.d("the ingredient string in getViewAt :"  + Ingredient.getIngredientString(mIngredients.get(position)));
        RemoteViews itemRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.collection_widget_list_item);
        itemRemoteViews.setTextViewText(R.id.widgetItemIngredient, Ingredient.getIngredientString(mIngredients.get(position)));

        return itemRemoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    // Returns the number of types of views we have in ListView. In our case, we have same view types in each ListView item so we return 1 there.
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}
