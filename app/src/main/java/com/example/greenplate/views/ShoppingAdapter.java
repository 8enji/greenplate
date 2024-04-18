package com.example.greenplate.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenplate.R;
import java.util.ArrayList;

import com.example.greenplate.model.Ingredient;
import com.example.greenplate.viewmodels.ShoppingListViewModel;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder> {
    private ArrayList<Ingredient> ingredients;
    private ShoppingListViewModel viewModel;

    public ShoppingAdapter(ArrayList<Ingredient> ingredients, ShoppingListViewModel viewModel) {
        this.ingredients = ingredients;
        this.viewModel = viewModel;
    }

    @Override
    public ShoppingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.ingredientNameTextView.setText(String.format("%5s%5s%5s",
                ingredient.getName(), ingredient.getQuantity(), ingredient.getUnits()));

        holder.buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.increaseIngredientQuantity(ingredient, new ShoppingListViewModel().IngredientUpdateCallback() {
                    @Override
                    public void onIngredientUpdated(boolean success, String error) {
                        if (success) {
                            ingredient.setQuantity(ingredient.getQuantity() + 1.0);
                            notifyItemChanged(position);
                        }
                    }
                });
            }
        });
        holder.buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.decreaseIngredientQuantity(ingredient, new ShoppingListViewModel.IngredientUpdateCallback() {
                    @Override
                    public void onIngredientUpdated(boolean success, String error) {
                        if (success) {
                            ingredient.setQuantity(Math.max(0, ingredient.getQuantity() - 1.0));
                            notifyItemChanged(position);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class ShoppingViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientNameTextView;
        Button buttonIncrease;
        Button buttonDecrease;

        public ShoppingViewHolder(View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.ingredientNameTextView);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
        }
    }
}
