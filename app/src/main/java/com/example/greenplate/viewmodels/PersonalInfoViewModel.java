package com.example.greenplate.viewmodels;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class PersonalInfoViewModel extends ViewModel {
    private FirebaseAuth mAuth;

    public PersonalInfoViewModel() {mAuth = FirebaseAuth.getInstance(); }


}
