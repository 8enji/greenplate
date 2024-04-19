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
                .inflate(R.layout.shopping_item, parent, false);
        return new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.ingredientSNameTextView.setText(String.format("%5s%5s%5s",
                ingredient.getName(), ingredient.getQuantity(), ingredient.getUnits()));

        holder.buttonIncrease2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.increaseIngredientQuantity(ingredient, new ShoppingListViewModel.IngredientUpdateCallback() {
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
        holder.buttonDecrease2.setOnClickListener(new View.OnClickListener() {
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
        TextView ingredientSNameTextView;
        Button buttonIncrease2;
        Button buttonDecrease2;

        public ShoppingViewHolder(View itemView) {
            super(itemView);
            ingredientSNameTextView = itemView.findViewById(R.id.ingredientSNameTextView);
            buttonIncrease2 = itemView.findViewById(R.id.buttonIncrease2);
            buttonDecrease2 = itemView.findViewById(R.id.buttonDecrease2);
        }
    }
}