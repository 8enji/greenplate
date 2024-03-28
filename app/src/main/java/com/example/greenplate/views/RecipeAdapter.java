package com.example.greenplate.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenplate.R;
import java.util.ArrayList;

import com.example.greenplate.model.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private ArrayList<Recipe> recipes;
    private ArrayList<String> cookable;

    public RecipeAdapter(ArrayList<Recipe> recipes, ArrayList<String> cookable) {
        this.recipes = recipes;
        this.cookable = cookable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        String canCook = cookable.get(position);
        holder.nameTextView.setText(recipe.getName());
        holder.additionalTextView.setText(canCook);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView additionalTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            additionalTextView = itemView.findViewById(R.id.additionalTextView);
        }
    }
}