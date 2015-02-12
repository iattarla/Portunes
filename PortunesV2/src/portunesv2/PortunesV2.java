/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portunesv2;

/**
 *
 * @author bilal
 */
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;


public class PortunesV2 {


    public static char[] s = new char[10000];
    static int bits = 0;

    public static void main(String[] args) {
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down
        // resistor enabled
        final GpioPinDigitalInput pin0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);

        final GpioPinDigitalInput pin1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_UP);

        System.out.println("PINs ready");
        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {

                    if (pin0.isLow()) { // D1 on ground?
                        s[bits++] = '0';
                        while (pin0.isLow()) {

                        }

                    }

                    if (pin1.isLow()) { // D1 on ground?
                        s[bits++] = '1';
                        while (pin1.isLow()) {
                        }

                    }

                    if (bits == 26) {
                        bits=0;

                        Print();

                    }

                }

            }
        });
        th.setPriority(Thread.MAX_PRIORITY);
        th.start();
        System.out.println("Thread start");

        for (;;) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    protected static void Print() {

        for (int i = 0; i < 26; i++) {
            System.out.write(s[i]);

        }
        System.out.println();
        bits = 0;

    }

}