package com.louise.udacity.bakingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.opengl.Visibility;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.louise.udacity.bakingapp.data.Recipe;
import com.louise.udacity.bakingapp.data.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.louise.udacity.bakingapp.RecipeDetailFragment.BUNDLE_STEPS;
import static com.louise.udacity.bakingapp.RecipeDetailFragment.EXTRA_BUNDLE_STEPS;
import static com.louise.udacity.bakingapp.RecipeDetailFragment.EXTRA_INDEX;
import static com.louise.udacity.bakingapp.StepDetailFragment.BUNDLE_INDEX;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickedListener {

    List<Step> steps;
    private boolean mTwoPane;
    StepDetailFragment stepDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);


        Recipe recipe = getIntent().getParcelableExtra(MainActivity.EXTRA_RECIPE);
        steps = recipe.getSteps();

        if (findViewById(R.id.frameLayout_step_detail) != null) {
            mTwoPane = true;
            Timber.d("twoPane enabled");

            FragmentManager fragmentManager = getSupportFragmentManager();

            if (savedInstanceState != null) {
                Timber.d("savedInstanceState in activity onCreate is not null");
                int newIndex = savedInstanceState.getInt(BUNDLE_INDEX);

                Timber.d("index in activity onCreate savedInstance is %s", newIndex);
                //Restore the fragment's instance
                stepDetailFragment = (StepDetailFragment) fragmentManager.getFragment(savedInstanceState, StepDetailFragment.class.getSimpleName());
                stepDetailFragment.setSteps(steps);

            } else {
                stepDetailFragment = new StepDetailFragment();
                stepDetailFragment.setSteps(steps);
                stepDetailFragment.setIndex(0);
                fragmentManager.beginTransaction()
                        .add(R.id.frameLayout_step_detail, stepDetailFragment)
                        .commit();
            }


        } else {
            mTwoPane = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                throw new RuntimeException("The menu item selected is unknown.");
        }
    }

    @Override
    public void onStepSelected(int position) {
        Timber.d("mTwoPane in onStepSelected %s", mTwoPane);

        if (mTwoPane) {
            Timber.d("the fragment shoule be updated");
            stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setIndex(position);
            stepDetailFragment.setSteps(steps);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_step_detail, stepDetailFragment).commit();

        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_STEPS, (ArrayList<Step>) steps);
            intent.putExtra(EXTRA_BUNDLE_STEPS, bundle);
            intent.putExtra(EXTRA_INDEX, position);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mTwoPane) {
            getSupportFragmentManager().putFragment(outState, StepDetailFragment.class.getSimpleName(), stepDetailFragment);
        }
    }
}
