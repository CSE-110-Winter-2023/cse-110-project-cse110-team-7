//
//package com.cse110.team7.socialcompass;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//
//import static org.junit.Assert.*;
//
//import android.hardware.SensorManager;
//import com.cse110.team7.socialcompass.services.OrientationService;
//
//import androidx.lifecycle.Lifecycle;
//import androidx.test.core.app.ActivityScenario;
//
//@RunWith(RobolectricTestRunner.class)
//public class OrientationServiceUnitTests   {
//   @Test
//   public void testOrientationService(){
//      var scenario = ActivityScenario.launch(CompassActivity.class);
//      scenario.moveToState(Lifecycle.State.CREATED);
//      scenario.moveToState(Lifecycle.State.STARTED);
//
//      scenario.onActivity(activity -> {
//         // check that if location manager is null, a nullpointer exception is thrown
//         OrientationService orientationService = OrientationService.getInstance();
//         SensorManager sensorManager = null;
//         NullPointerException thrown = assertThrows(
//                 NullPointerException.class,
//                 () -> {
//                    orientationService.setSensorManager(sensorManager);
//                    orientationService.registerSensorEventListener();
//                 });
//
//         NullPointerException thrown2 = assertThrows(
//                 NullPointerException.class,
//                 () -> {
//                    orientationService.setSensorManager(sensorManager);
//                    orientationService.unregisterSensorEventListener();
//                 });
//      });
//   }
//
//}
//
//
