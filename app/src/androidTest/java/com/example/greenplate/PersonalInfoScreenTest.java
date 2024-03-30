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

    /**
     * JUNIT TESTS
     * NOTE: YOU CAN'T RUN BOTH TESTS AT THE SAME TIME, AS YOU NEED TO MAKE DIFFERENT ACCOUNTS
     */
    @Test
    public void savePersonalInfo_VerifyDefaultValues() {
        ActivityScenario.launch(PersonalInfoScreen.class);

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPersonalInfo", Context.MODE_PRIVATE);
        String defaultHeight = sharedPreferences.getString("height", "0");
        String defaultWeight = sharedPreferences.getString("weight", "0");
        String defaultGender = sharedPreferences.getString("gender", "Not specified");

        assertEquals("Default height value incorrect.", "0", defaultHeight);
        assertEquals("Default weight value incorrect.", "0", defaultWeight);
        assertEquals("Default gender value incorrect.", "Not specified", defaultGender);
    }

//    @Test
//    public void savePersonalInfo_VerifyStandardValues() {
//        ActivityScenario.launch(PersonalInfoScreen.class);
//
//        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
//
//        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPersonalInfo", Context.MODE_PRIVATE);
//        String standardHeight = sharedPreferences.getString("height", "72");
//        String standardWeight = sharedPreferences.getString("weight", "175");
//        String standardGender = sharedPreferences.getString("gender", "Male");
//
//        assertEquals("Default height value incorrect.", "72", standardHeight);
//        assertEquals("Default weight value incorrect.", "175", standardWeight);
//        assertEquals("Default gender value incorrect.", "Male", standardGender);
//    }

//    @Test
//    public void savePersonalInfo_VerifyPersistenceAcrossSessions() {
//        String testHeight = "68";
//        String testWeight = "145";
//        String testGender = "Female";
//
//        ActivityScenario.launch(PersonalInfoScreen.class);
//        onView(withId(R.id.editTextHeight)).perform(typeText(testHeight), closeSoftKeyboard());
//        onView(withId(R.id.editTextWeight)).perform(typeText(testWeight), closeSoftKeyboard());
//        onView(withId(R.id.editTextGender)).perform(typeText(testGender), closeSoftKeyboard());
//        onView(withId(R.id.buttonSave)).perform(click());
//
//        ActivityScenario.launch(PersonalInfoScreen.class);
//
//        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
//
//        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPersonalInfo", Context.MODE_PRIVATE);
//        String savedHeight = sharedPreferences.getString("height", "");
//        String savedWeight = sharedPreferences.getString("weight", "");
//        String savedGender = sharedPreferences.getString("gender", "");
//
//        assertEquals("Height not persisted across sessions.", testHeight, savedHeight);
//        assertEquals("Weight not persisted across sessions.", testWeight, savedWeight);
//        assertEquals("Gender not persisted across sessions.", testGender, savedGender);
//    }

    @Test
    public void savePersonalInfo_VerifyPersistenceAcrossSessions_2() {
        String testHeight = "65";
        String testWeight = "140";
        String testGender = "Male";

        ActivityScenario.launch(PersonalInfoScreen.class);
        onView(withId(R.id.editTextHeight)).perform(typeText(testHeight), closeSoftKeyboard());
        onView(withId(R.id.editTextWeight)).perform(typeText(testWeight), closeSoftKeyboard());
        onView(withId(R.id.editTextGender)).perform(typeText(testGender), closeSoftKeyboard());
        onView(withId(R.id.buttonSave)).perform(click());

        ActivityScenario.launch(PersonalInfoScreen.class);

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPersonalInfo", Context.MODE_PRIVATE);
        String savedHeight = sharedPreferences.getString("height", "");
        String savedWeight = sharedPreferences.getString("weight", "");
        String savedGender = sharedPreferences.getString("gender", "");

        assertEquals("Height not persisted across sessions.", testHeight, savedHeight);
        assertEquals("Weight not persisted across sessions.", testWeight, savedWeight);
        assertEquals("Gender not persisted across sessions.", testGender, savedGender);
    }
}
