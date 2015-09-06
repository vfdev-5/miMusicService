package com.example.vfdev.musicapp.test;

import android.os.Bundle;

import android.support.test.runner.MonitoringInstrumentation;

import cucumber.api.android.CucumberInstrumentationCore;

/**
 * Created by vfomin on 8/9/15.
 */
public class CucuRunner extends MonitoringInstrumentation {

    private final CucumberInstrumentationCore helper = new CucumberInstrumentationCore(this);

    @Override
    public void onCreate(Bundle arguments) {
        helper.create(arguments);
        super.onCreate(arguments);
        start();
    }

    @Override
    public void onStart() {
        waitForIdleSync();
        helper.start();
    }
}
