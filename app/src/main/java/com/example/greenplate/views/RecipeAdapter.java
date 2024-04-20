package com.example.greenplate.views;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenplate.R;
import java.util.ArrayList;

import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.Recipe;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.example.greenplate.viewmodels.RecipeDetailsScreenViewModel;
import com.example.greenplate.viewmodels.RecipeScreenViewModel;
import com.example.greenplate.viewmodels.ShoppingListViewModel;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private ArrayList<Recipe> recipes;
    private ArrayList<String> cookable;
    private ShoppingListViewModel viewModel;

    public RecipeAdapter(ArrayList<Recipe> recipes, ArrayList<String> cookable) {
        this.recipes = recipes;
        this.cookable = cookable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) parent.getContext()).get(ShoppingListViewModel.class);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        String canCook = cookable.get(position);
        holder.nameTextView.setText(recipe.getName());
        holder.additionalButton.setText(canCook);
        holder.additionalButton2.setText(canCook);
        if (canCook.equals("Yes")) {
            holder.additionalButton2.setVisibility(View.GONE);
            holder.additionalButton.setVisibility(View.VISIBLE);
            holder.additionalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click event
                    Intent intent = new Intent(v.getContext(), RecipeDetailsScreen.class);
                    intent.putExtra("RECIPE_NAME", recipes.get(position).getName());
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            holder.additionalButton2.setVisibility(View.VISIBLE);
            holder.additionalButton.setVisibility(View.GONE);
            holder.additionalButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click event
                    viewModel.getPantry(recipes.get(position).getName(), new ShoppingListViewModel.GetPantryCallBack() {
                        @Override
                        public void onSuccess(ArrayList<Ingredient> pantry) {
                            addIngredients(v, pantry, recipes, position);
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(v.getContext(),
                                    "Failed to access pantry: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void addIngredients(View v, ArrayList<Ingredient> pantry, ArrayList<Recipe> recipes, int recipePosition) {
        viewModel.addIngredientsRecipe(pantry, recipes, recipePosition, new ShoppingListViewModel.AddCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(v.getContext(),
                        "Added needed Ingredients to shopping list", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(v.getContext(),
                        "Failed to add to Shopping list: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public Button additionalButton2;
        public Button additionalButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            additionalButton2 = itemView.findViewById(R.id.additionalButton2);
            additionalButton = itemView.findViewById(R.id.additionalButton);
        }
    }
}