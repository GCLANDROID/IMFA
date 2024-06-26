package com.genius.imfa.Utility;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalTime;

public class GreetingGenerator {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getGreeting() {
        LocalTime time = LocalTime.now();
        // Define the thresholds for morning, noon, evening, and night
        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime noonStart = LocalTime.of(12, 0);
        LocalTime eveningStart = LocalTime.of(18, 0);
        LocalTime nightStart = LocalTime.of(21, 0);

        // Determine the appropriate greeting based on the time
        if (time.isAfter(morningStart) && time.isBefore(noonStart)) {
            return "Good Morning";
        } else if (time.isAfter(noonStart) && time.isBefore(eveningStart)) {
            return "Good Noon";
        } else if (time.isAfter(eveningStart) && time.isBefore(nightStart)) {
            return "Good Evening";
        } else {
            return "Good Night";
        }
    }
}
