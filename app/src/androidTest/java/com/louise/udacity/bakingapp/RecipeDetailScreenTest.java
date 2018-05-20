package com.louise.udacity.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.louise.udacity.bakingapp.data.Ingredient;
import com.louise.udacity.bakingapp.data.Recipe;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class RecipeDetailScreenTest {

    @Rule
    public ActivityTestRule<RecipeDetailActivity> mActivityRule = new ActivityTestRule<>(
            RecipeDetailActivity.class, false, false);

    Context appContext = InstrumentationRegistry.getTargetContext();
    Recipe recipe;

    @Before
    public void launchTargetRecipeDetail() {
        recipe = MainActivityScreenTest.readRecipesLocally(appContext).get(1);

        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_RECIPE, recipe);
        mActivityRule.launchActivity(intent);

    }

    @Test
    public void checkStepDetailLaunched() {
        Timber.d("tablet in bool file: " + appContext.getResources().getBoolean(R.bool.tablet));

        List<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            onView(withId(R.id.recyclerView_ingredients)).perform(RecyclerViewActions.scrollToPosition(i));
            onView(withText(ingredient.getIngredient())).check(matches(isDisplayed()));
        }

        // Test for tables
        if (appContext.getResources().getBoolean(R.bool.tablet)) {
            onView(withId(R.id.playerView_step_video)).check(matches(isDisplayed()));
            onView(withId(R.id.textView_step_description)).check(matches(isDisplayed()));

        }

        // Test for phones
        else {
            onView(withId(R.id.playerView_step_video)).check(doesNotExist());
            onView(withText(R.id.textView_step_description)).check(doesNotExist());
        }
    }

    @Test
    public void clickStepDetail() {

        onView(withId(R.id.recyclerView_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        String step = "1. Preheat the oven to 350�F. Butter the bottom and sides of a 9\"x13\" pan.";
        onView(withId(R.id.textView_step_description)).check(matches(withText(step)));

        // Test for tables
        if (appContext.getResources().getBoolean(R.bool.tablet)) {
            onView(withId(R.id.recyclerView_ingredients)).check(matches(isDisplayed()));

        }

        // Test for phones
        else {
            onView(withId(R.id.recyclerView_ingredients)).check(doesNotExist());
        }
    }

}
