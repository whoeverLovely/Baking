package com.louise.udacity.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.louise.udacity.bakingapp.data.Recipe;
import com.louise.udacity.bakingapp.util.MySingletonVolley;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import timber.log.Timber;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    private Context appContext = InstrumentationRegistry.getTargetContext();

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityRule.getActivity().getCountingIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);

    }

    @Test
    public void checkRecipeDisplayed() {
        /*onView(withId(R.id.recipe_recycler_view)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withText(recipes.get(i).getName())).check(matches(isDisplayed()));*/
        /*onView(withId(R.id.recipe_recycler_view)).check(matches(withText("Nutella Pie")));*/

        /*onView(withId(R.id.recipe_recycler_view)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withText("Nutella Pie")).check(matches(isDisplayed()));*/

        /*onView(withId(R.id.recipe_recycler_view)).check(matches(isDisplayed()));*/

        // click on the first item in the list:
        /*onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));*/

        /*onView(withId(R.id.recipe_recycler_view)).check(ViewAssertions.matches(isDisplayed()));*/

       /* try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        /*onView(withId(R.id.recipe_recycler_view)).perform(RecyclerViewActions.scrollToPosition(0));*/
        List<Recipe> recipes = readRecipesLocally(appContext);
        for(int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            onView(withId(R.id.recipe_recycler_view)).perform(RecyclerViewActions.scrollToPosition(i));
            onView(withText(recipe.getName())).check(matches(isDisplayed()));
        }

    }

    @Test
    public void checkRecipeDetailLaunched() {
        onView(withId(R.id.recipe_recycler_view)).
                perform(RecyclerViewActions.<RecipeAdapter.ViewHolder>actionOnItem(hasDescendant(withText("Nutella Pie")), click()));
       /* onView(ViewMatchers.withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));*/

        onView(withId(R.id.cardView_ingredients)).check(matches(isDisplayed()));
        onView(withId(R.id.cardView_steps)).check(matches(isDisplayed()));

        onView(withText("Graham Cracker crumbs")).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

    public static List<Recipe> readRecipesLocally(Context context) {
            InputStream is = context.getResources().openRawResource(R.raw.baking);
            String s = null;
        try {
            s = IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IOUtils.closeQuietly(is);
        return new Gson().fromJson(s, new TypeToken<List<Recipe>>() {}.getType());
    }

}
