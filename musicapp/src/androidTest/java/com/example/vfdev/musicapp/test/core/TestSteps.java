//package com.example.vfdev.musicapp.test;
//
//import android.app.Instrumentation;
//import android.test.ActivityInstrumentationTestCase2;
//
//import com.example.vfdev.musicapp.SimplePlayer;
//
//import cucumber.api.CucumberOptions;
//import cucumber.api.java.Before;
//import cucumber.api.java.en.Given;
//import cucumber.api.java.en.Then;
//import cucumber.api.java.en.When;
//
///**
// * Created by vfomin on 8/15/15.
// */
//@CucumberOptions(features = "features")
//public class TestSteps extends ActivityInstrumentationTestCase2<SimplePlayer> {
//
//    public TestSteps() {
//        super(SimplePlayer.class);
//    }
//
//    @Before
//    public void before() {
//        Instrumentation instrumentation = getInstrumentation();
//        assertNotNull(instrumentation);
//        assertNotNull(getActivity());
//        String testPackageName = instrumentation.getContext().getPackageName();
//        String targetPackageName = instrumentation.getContext().getPackageName();
//        assertEquals(testPackageName, targetPackageName);
//    }
//
//    @Given("^Initial state$")
//    public void givenInitialState() {
//        assertTrue(true);
//    }
//
//    @When("^Started$")
//    public void whenStarted() {
//        assertTrue(true);
//    }
//
//    @Then("^Everything is ok$")
//    public void everythingIsOk() {
//        assertTrue(true);
//    }
//
//
//}
