package com.example.rahulvansh.androidthings_pi_led_blinking;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * In this example I have connected "BCM4" pin in "Raspberry pi", you can change according to your convince.
 * Referenced from: https://github.com/androidthings/sample-simplepio
 */
public class Led_Blink extends Activity {

    //Decide delay and GPIO pin number
    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;
    private static final String LED = "BCM4";

    private Handler mHandler = new Handler();
    private Gpio mLedGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led__blink);

        PeripheralManagerService service = new PeripheralManagerService();

        try {
            //Define pin number
            mLedGpio = service.openGpio(LED);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            //Call LBlinkRunnable
            mHandler.post(LBlinkRunnable);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove pending blink Runnable from the handler.
        mHandler.removeCallbacks(LBlinkRunnable);
        try {
            mLedGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpio = null;
        }
    }

    private Runnable LBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            //In case LED GPIO pin didn't initialize
            if (mLedGpio == null) {
                return;
            }
            try {
                // Toggle the GPIO state
                mLedGpio.setValue(!mLedGpio.getValue());
                //Put Delay
                mHandler.postDelayed(LBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };
}
