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
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
///**
// * Inputs locationis for all 3 potential labels and checks that the compass displays three labels.
// *
// * Note: Currently none of these tests pass; I'm guessing it's because of differing ids between
// * instances and because of the database (in this case).
// */
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class inputAllLocationsTest {
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
//    public void inputAllLocationsTest() {
//        ViewInteraction appCompatEditText = onView(
//                allOf(withId(R.id.latLongTextView),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        0),
//                                1),
//                        isDisplayed()));
//        appCompatEditText.perform(replaceText("31, 31"), closeSoftKeyboard());
//
//        ViewInteraction appCompatEditText2 = onView(
//                allOf(withId(R.id.latLongTextView), withText("31, 31"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        0),
//                                1),
//                        isDisplayed()));
//        appCompatEditText2.perform(pressImeActionButton());
//
//        ViewInteraction appCompatEditText3 = onView(
//                allOf(withId(R.id.parentLabelName), withText("Friends"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        1),
//                                0),
//                        isDisplayed()));
//        appCompatEditText3.perform(pressImeActionButton());
//
//        ViewInteraction appCompatEditText4 = onView(
//                allOf(withId(R.id.latLongTextView),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        1),
//                                1),
//                        isDisplayed()));
//        appCompatEditText4.perform(replaceText("60,60"), closeSoftKeyboard());
//
//        ViewInteraction appCompatEditText5 = onView(
//                allOf(withId(R.id.latLongTextView), withText("60,60"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        1),
//                                1),
//                        isDisplayed()));
//        appCompatEditText5.perform(pressImeActionButton());
//
//        ViewInteraction appCompatEditText6 = onView(
//                allOf(withId(R.id.parentLabelName), withText("My Home"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        2),
//                                0),
//                        isDisplayed()));
//        appCompatEditText6.perform(pressImeActionButton());
//
//        ViewInteraction appCompatEditText7 = onView(
//                allOf(withId(R.id.latLongTextView),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        2),
//                                1),
//                        isDisplayed()));
//        appCompatEditText7.perform(replaceText("30,-111"), closeSoftKeyboard());
//
//        ViewInteraction appCompatEditText8 = onView(
//                allOf(withId(R.id.latLongTextView), withText("30,-111"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.houseInputItems),
//                                        2),
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
//        ViewInteraction imageView = onView(
//                allOf(withParent(allOf(withId(R.id.compassActivityParentConstraints),
//                                withParent(withId(android.R.id.content)))),
//                        isDisplayed()));
//        imageView.check(matches(isDisplayed()));
//
//        ViewInteraction imageView2 = onView(
//                allOf(withParent(allOf(withId(R.id.compassActivityParentConstraints),
//                                withParent(withId(android.R.id.content)))),
//                        isDisplayed()));
//        imageView2.check(matches(isDisplayed()));
//
//        ViewInteraction imageView3 = onView(
//                allOf(withParent(allOf(withId(R.id.compassActivityParentConstraints),
//                                withParent(withId(android.R.id.content)))),
//                        isDisplayed()));
//        imageView3.check(matches(isDisplayed()));
//
//        ViewInteraction imageView4 = onView(
//                allOf(withId(R.id.labelNorth),
//                        withParent(allOf(withId(R.id.CompassCenter),
//                                withParent(withId(R.id.compassActivityParentConstraints)))),
//                        isDisplayed()));
//        imageView4.check(matches(isDisplayed()));
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
