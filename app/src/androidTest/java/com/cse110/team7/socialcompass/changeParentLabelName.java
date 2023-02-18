//package com.cse110.team7.socialcompass;
//
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
//import static androidx.test.espresso.action.ViewActions.replaceText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withParent;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.allOf;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import androidx.room.Room;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.espresso.ViewInteraction;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.filters.LargeTest;
//import androidx.test.rule.GrantPermissionRule;
//
//import com.cse110.team7.socialcompass.backend.HouseDao;
//import com.cse110.team7.socialcompass.backend.HouseDatabase;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
///**
// * This test changes the label name of "Parents" to "My Parents" and gives them a location of
// * (31, 31). Following this, it goes to the compass view and checks that there is a label with the
// * proper text in it.
// *
// * Note: Currently none of these tests pass; I'm guessing it's because of differing ids between
// * instances.
// */
//
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class changeParentLabelName {
//
//    private HouseDao houseDao;
//    private HouseDatabase houseDatabase;
//
//    @Before
//    public void createDatabase() {
//        Context context = ApplicationProvider.getApplicationContext();
//
//        houseDatabase = Room.inMemoryDatabaseBuilder(context, HouseDatabase.class)
//                .allowMainThreadQueries()
//                .build();
//
//        houseDao = houseDatabase.getHouseDao();
//    }
//
//    @After
//    public void closeDatabase() {
//        houseDatabase.close();
//    }
//
//    @Rule
//    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
//            new ActivityScenarioRule<>(MainActivity.class);
//
//    @Rule
//    public GrantPermissionRule mGrantPermissionRule =
//            GrantPermissionRule.grant(
//                    "android.permission.ACCESS_FINE_LOCATION");
//
//    @Test
//    public void changeParentLabelName() {
//
//
//        ViewInteraction appCompatEditText = onView(
//                allOf(withId(R.id.parentLabelName), withText("Parents"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        0),
//                                0),
//                        isDisplayed()));
//        appCompatEditText.perform(replaceText("My Parents"));
//
//        ViewInteraction appCompatEditText6 = onView(
//                allOf(withId(R.id.parentLabelName), withText("My Parents"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        0),
//                                0),
//                        isDisplayed()));
//        appCompatEditText6.perform(pressImeActionButton());
//
//        ViewInteraction appCompatEditText7 = onView(
//                allOf(withId(R.id.latLongTextView),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        0),
//                                1),
//                        isDisplayed()));
//        appCompatEditText7.perform(replaceText("31, 31"), closeSoftKeyboard());
//
//        ViewInteraction appCompatEditText8 = onView(
//                allOf(withId(R.id.latLongTextView), withText("31, 31"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        0),
//                                1),
//                        isDisplayed()));
//        appCompatEditText8.perform(pressImeActionButton());
//
//        ViewInteraction materialButton = onView(
//                allOf(withId(R.id.goToCompass), withText("Go To Compass"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                0),
//                        isDisplayed()));
//        materialButton.perform(click());
//
//        ViewInteraction textView = onView(
//                allOf(withText("My Parents"),
//                        withParent(allOf(withId(R.id.compassActivityParentConstraints),
//                                withParent(withId(android.R.id.content)))),
//                        isDisplayed()));
//        textView.check(matches(withText("My Parents")));
//
//        ViewInteraction textView2 = onView(
//                allOf(withText("My Parents"),
//                        withParent(allOf(withId(R.id.compassActivityParentConstraints),
//                                withParent(withId(android.R.id.content)))),
//                        isDisplayed()));
//        textView2.check(matches(isDisplayed()));
//    }
//
//    private static Matcher<View> childAtPosition(
//            final Matcher<View> parentMatcher, final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                        && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
//}
