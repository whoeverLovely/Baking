package com.louise.udacity.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.louise.udacity.bakingapp.data.Ingredient;
import com.louise.udacity.bakingapp.data.Recipe;
import com.louise.udacity.bakingapp.data.Step;
import com.louise.udacity.bakingapp.util.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeDetailFragment extends Fragment implements ItemClickListener {

    @BindView(R.id.recyclerView_ingredients)
    RecyclerView ingredientsRecyclerView;
    @BindView(R.id.recyclerView_steps)
    RecyclerView stepsRecyclerView;

    List<Step> steps;

    public static final String BUNDLE_STEPS = "steps";
    public static final String EXTRA_BUNDLE_STEPS = "bundleSteps";
    public static final String EXTRA_INDEX = "index";

    OnStepClickedListener onStepClickedListener;

    public interface OnStepClickedListener{
        public void onStepSelected(int position);
    }

    public RecipeDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onStepClickedListener = (OnStepClickedListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        Recipe recipe = getActivity().getIntent().getParcelableExtra(MainActivity.EXTRA_RECIPE);
        List<Ingredient> ingredients = recipe.getIngredients();
        steps = recipe.getSteps();

        // Setup ingredientsRecyclerView
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(getActivity());
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientsAdapter.swapData(ingredients);

        // Setup stepsRecyclerView
        StepsAdapter stepsAdapter = new StepsAdapter(getActivity(), this);
        stepsRecyclerView.setAdapter(stepsAdapter);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stepsAdapter.swapData(steps);

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {

        Timber.d("the step is clicked.");

        onStepClickedListener.onStepSelected(position);

        /*Intent intent = new Intent(getActivity(), StepDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_STEPS, (ArrayList<Step>) steps);
        intent.putExtra(EXTRA_BUNDLE_STEPS, bundle);
        intent.putExtra(EXTRA_INDEX, position);
        startActivity(intent);*/
    }
}
