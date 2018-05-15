package com.louise.udacity.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.louise.udacity.bakingapp.MainActivity;
import com.louise.udacity.bakingapp.R;
import com.louise.udacity.bakingapp.RecipeDetailActivity;
import com.louise.udacity.bakingapp.data.Recipe;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidgetProvider extends AppWidgetProvider {



    /*static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {

        if(recipe == null) {


        }else {
            // Create an Intent to launch RecipeDetail activity
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECIPE, recipe);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
            // Update ingredients textView
            views.setTextViewText(R.id.widget_recipe_title, recipe.getName() + " Ingredients");
            views.setTextViewText(R.id.appwidget_recipe_content, Ingredient.getIngredientsString(recipe.getIngredients()));
            // Widgets allow click handlers to only launch pending intents
            views.setOnClickPendingIntent(R.id.appwidget_recipe_content, pendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateIngredientsWidgets(Context context, Recipe recipe) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyWidgetProvider.class));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }


    }*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("===============================widgetProvider onUpdate start");
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
            Intent intent = new Intent(context, MyWidgetRemoteViewService.class);
            views.setRemoteAdapter(R.id.widgetListView, intent);

            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, 0);
            views.setOnClickPendingIntent(R.id.widgetTitleLabel, clickPI);

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
        Timber.d("===============================widgetProvider onUpdate end");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            if (intent.getParcelableExtra(MainActivity.EXTRA_RECIPE) != null) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyWidgetProvider.class));
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
                Recipe recipe = intent.getParcelableExtra(MainActivity.EXTRA_RECIPE);
                views.setTextViewText(R.id.widgetTitleLabel, recipe.getName());

                Intent clickIntent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(MainActivity.EXTRA_RECIPE, recipe);
                PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.widget_list_view_frame, clickPI);

                appWidgetManager.updateAppWidget(appWidgetIds, views);
            }

        }
        super.onReceive(context, intent);
    }
}

