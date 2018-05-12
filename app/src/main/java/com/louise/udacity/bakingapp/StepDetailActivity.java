package com.louise.udacity.bakingapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.louise.udacity.bakingapp.data.Step;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.louise.udacity.bakingapp.RecipeDetailFragment.BUNDLE_STEPS;

public class StepDetailActivity extends AppCompatActivity {

    StepDetailFragment stepDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(RecipeDetailFragment.EXTRA_BUNDLE_STEPS);
        int index = intent.getIntExtra(RecipeDetailFragment.EXTRA_INDEX, -1);
        ArrayList<Step> steps = bundle.getParcelableArrayList(BUNDLE_STEPS);

        Timber.d("Here is activity onCreate");
        stepDetailFragment = new StepDetailFragment();
        // Add the fragment to its container using a FragmentManager and a Transaction
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            Timber.d("savedInstanceState in activity onCreate is not null");
            //Restore the fragment's instance
            stepDetailFragment = (StepDetailFragment) fragmentManager.getFragment(savedInstanceState, StepDetailFragment.class.getSimpleName());
            stepDetailFragment.setSteps(steps);
        } else {
            Timber.d("savedInstanceState in activity onCreate is null");
            stepDetailFragment.setIndex(index);
            stepDetailFragment.setSteps(steps);
            fragmentManager.beginTransaction()
                    .add(R.id.frameLayout_step_detail, stepDetailFragment)
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, StepDetailFragment.class.getSimpleName(), stepDetailFragment);
    }
}
