package com.example.greenplate.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenplate.R;
import com.example.greenplate.model.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<Ingredient> ingredients = new ArrayList<>();

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.nameTextView.setText(ingredient.getName());
        // Set other properties of your ingredient as needed
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.ingredientName);
            // Initialize other views from your layout if needed
        }
    }
}
