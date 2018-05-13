package com.louise.udacity.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.louise.udacity.bakingapp.data.Recipe;
import com.louise.udacity.bakingapp.util.ItemClickListener;
import com.louise.udacity.bakingapp.util.MySingletonVolley;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recipeRecyclerView;
    @BindView(R.id.main_progress_bar)
    ProgressBar progressBar;

    RecipeAdapter recipeAdapter;
    List<Recipe> recipes;

    public static final String EXTRA_RECIPE= "recipe";

    public static final String URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeRecyclerView.setHasFixedSize(true);
        recipeAdapter = new RecipeAdapter(this, this);
        recipeRecyclerView.setAdapter(recipeAdapter);

        setRecipeData();
    }

    private void setRecipeData() {

        Timber.d("Started fetch data...");
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Timber.d("recipe json received: " + response.toString());
                recipes = new Gson().fromJson(response.toString(), new TypeToken<List<Recipe>>() {
                }.getType());
                recipeAdapter.swapData(recipes);
                progressBar.setVisibility(View.INVISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }
        });

        // Access the RequestQueue through your singleton class.
        MySingletonVolley.getInstance(this).addToRequestQueue(jsonArrayRequest);

    }

    @Override
    public void onItemClick(View view, int position) {
        Recipe recipe = recipes.get(position);

        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE, recipe);
        startActivity(intent);

        // Update widget
        IngredientsAppWidget.updateIngredientsWidgets(this, recipe);

    }
}
