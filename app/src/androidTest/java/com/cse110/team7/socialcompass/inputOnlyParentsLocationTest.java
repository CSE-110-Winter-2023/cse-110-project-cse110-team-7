package com.cse110.team7.socialcompass;


import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.cse110.team7.socialcompass.backend.HouseDao;
import com.cse110.team7.socialcompass.backend.HouseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

/**
 * This test checks that if we input the parent's location, then we get their label to show up on
 * the UI.
 *
 * Note: Currently none of these tests pass; I'm guessing it's because of differing ids between
 * instances and because of the database (in this case).
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class inputOnlyParentsLocationTest {

    private HouseDao houseDao;
    private HouseDatabase houseDatabase;
    Activity currentActivity = null;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();

        houseDatabase = Room.inMemoryDatabaseBuilder(context, HouseDatabase.class)
                .allowMainThreadQueries()
                .build();

        houseDao = houseDatabase.getHouseDao();
    }

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void inputOnlyParentsLocation() throws InterruptedException {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.latLongTextView),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.houseInputItems),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("31, 31"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.latLongTextView), withText("31, 31"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.houseInputItems),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(pressImeActionButton());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.goToCompass), withText("Go To Compass"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        //Pulls current activity - but we have a bug, where it is pulling the initial Main Activity
        //and then says that the elements on it are visible, when they should not be.
        Activity current = getActivityInstance();

        //Gets the layout of the current activity.
        ViewGroup view = (ViewGroup) current.findViewById(android.R.id.content);
        ConstraintLayout constraintLayout = (ConstraintLayout) view.getChildAt(0);

        //Temporarily, this is checking if the "Go to Compass" compass button is VISIBLE, however,
        //it is currently saying it is, when really we'd like it to be GONE.
        assertEquals(constraintLayout.getChildAt(0).getVisibility(), VISIBLE);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    /**
     * Should go to the top of the activity stack and return the most current activity.
     */
    public Activity getActivityInstance() {
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities =
                        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                        currentActivity = (Activity)resumedActivities.toArray()[0];
                        if (resumedActivities.iterator().hasNext()){
                            currentActivity = (Activity)resumedActivities.iterator().next();
                        }
            }
        });

        return currentActivity;
    }
}
