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
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;


public class PortunesV2 {

    public static char[] s = new char[10000];
    static int bits = 0;
    
    public static void main(String args[]) throws InterruptedException {
        System.out.println("<--Pi4J--> GPIO Listen Example ... started.");
        
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput data0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);

        final GpioPinDigitalInput data1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_UP);
        
        // create and register gpio pin listener
        data0.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
               
                if (bits == 26) {
                        bits=0;
                        Print();
                    }

                if (data0.isLow() && bits <= 26) { // D1 on ground?
                        s[bits] = '1';
                        bits++;
                        System.out.println(0);
                      
                }
            }
            
        });
        
        data1.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                
                if (bits == 26) {
                        bits=0;
                        Print();
                }
                if (data1.isLow() && bits<=26) { // D1 on ground?
                        s[bits] = '0';
                        //s[bits] |= 1;
                        bits++;
                        System.out.println(1);
                       

                }
            }
            
        });
        
        
        //System.out.println(" ... complete the GPIO #02 circuit and see the listener feedback here in the console.");
        
        // keep program running until user aborts (CTRL-C)
        for (;;) {
            Thread.sleep(500);
        }
        
        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller        
    }
    
  protected static void Print() {

        for (int i = 0; i < 26; i++) {
            System.out.write(s[i]);

        }
        System.out.println();
        bits = 0;

    }

}