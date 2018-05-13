package com.louise.udacity.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.louise.udacity.bakingapp.data.Ingredient;
import com.louise.udacity.bakingapp.data.Recipe;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {

        if(recipe == null) {


        }else {
            // Create an Intent to launch RecipeDetail activity
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECIPE, recipe);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_app_widget);
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
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, IngredientsAppWidget.class));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId, );
        }
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

