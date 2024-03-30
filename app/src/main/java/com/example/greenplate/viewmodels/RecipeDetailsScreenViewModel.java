package com.example.greenplate.viewmodels;

import android.widget.Toast;

import com.example.greenplate.model.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.Recipe;

import org.w3c.dom.Document;

public class RecipeDetailsScreenViewModel extends ViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;
    //private ArrayList<Ingredient> globalPantry;

    public RecipeDetailsScreenViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("users").document(email);
    }

    public void loadRecipe(String recipeNameString, LoadRecipeCallback callback) {
        db.collection("cookbook").document(recipeNameString).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot recipeDocument = task.getResult();
                Map<String, Object> r = recipeDocument.getData();
                ArrayList<HashMap<String, Object>> rI = (ArrayList<HashMap<String, Object>>) r.get("ingredients");
                ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
                for (int i = 0; i < rI.size(); i++) {
                    Ingredient ingr = new Ingredient(rI.get(i).get("name").toString(), (double) rI.get(i).get("quantity"), rI.get(i).get("units").toString(), 0);
                    ingredients.add(ingr);
                }
                callback.onSuccess(ingredients);

            } else {
                callback.onFailure("recipe cannot be accessed");
            }
        });
    }

    public interface LoadRecipeCallback {
        void onSuccess(ArrayList<Ingredient> ingredients);
        void onFailure(String error);
    }



}
