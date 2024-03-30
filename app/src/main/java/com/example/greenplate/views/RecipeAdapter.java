package com.example.greenplate.views;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        holder.additionalButton.setText(canCook);
        holder.additionalTextView.setText(canCook);
        if (canCook.equals("Yes")) {
            holder.additionalTextView.setVisibility(View.GONE);
            holder.additionalButton.setVisibility(View.VISIBLE);
            holder.additionalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click event
                    // You can use 'position' here to know which item was clicked, if needed
                    Intent intent = new Intent(v.getContext(), RecipeDetailsScreen.class);
                    intent.putExtra("RECIPE_NAME", recipes.get(position).getName());
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            holder.additionalTextView.setVisibility(View.VISIBLE);
            holder.additionalButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView additionalTextView;
        public Button additionalButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            additionalTextView = itemView.findViewById(R.id.additionalTextView);
            additionalButton = itemView.findViewById(R.id.additionalButton);
        }
    }
}