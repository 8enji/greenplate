package com.example.greenplate;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import com.example.greenplate.views.PersonalInfoScreen;

@RunWith(AndroidJUnit4.class)
public class PersonalInfoScreenTest {

    @Test
    public void savePersonalInfo_VerifySharedPreferences() {
        String testHeight = "72";
        String testWeight = "160";
        String testGender = "Male";

        // Start PersonalInfoScreen activity
        ActivityScenario.launch(PersonalInfoScreen.class);

        // Input test data into EditText fields and click the save button
        onView(withId(R.id.editTextHeight)).perform(typeText(testHeight), closeSoftKeyboard());
        onView(withId(R.id.editTextWeight)).perform(typeText(testWeight), closeSoftKeyboard());
        onView(withId(R.id.editTextGender)).perform(typeText(testGender), closeSoftKeyboard());
        onView(withId(R.id.buttonSave)).perform(click());

        // Fetch the context
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Retrieve saved data from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPersonalInfo", Context.MODE_PRIVATE);
        String savedHeight = sharedPreferences.getString("height", "");
        String savedWeight = sharedPreferences.getString("weight", "");
        String savedGender = sharedPreferences.getString("gender", "");

        // Verify the saved data matches the input
        assertEquals("Failed to save or retrieve height correctly.", testHeight, savedHeight);
        assertEquals("Failed to save or retrieve weight correctly.", testWeight, savedWeight);
        assertEquals("Failed to save or retrieve gender correctly.", testGender, savedGender);
    }
}
