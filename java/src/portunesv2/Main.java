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

import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    public final static int LCD_ROWS = 4;
    public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;
    public final static int LCD_ROW_3 = 2;
    public final static int LCD_ROW_4 = 3;
    public final static int LCD_COLUMNS = 20;
    public final static int LCD_BITS = 4;
    
    
    
    Main(){
        
        
        
        
    }
    
    
    public static void main(String args[]) throws InterruptedException {

        Date today_date = new Date( );            
        SimpleDateFormat justdate_format;
        SimpleDateFormat datetime_format;
        
        justdate_format  = new SimpleDateFormat ("yyyy-MM-dd");
        datetime_format  = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        
        final GpioController gpio = GpioFactory.getInstance();
        // initialize LCD
        final GpioLcdDisplay lcd = new GpioLcdDisplay(LCD_ROWS,LCD_COLUMNS, // number of columns supported by LCD
        RaspiPin.GPIO_02, // LCD RS pin
        RaspiPin.GPIO_03, // LCD strobe pin
        RaspiPin.GPIO_06, // LCD data bit 1
        RaspiPin.GPIO_05, // LCD data bit 2
        RaspiPin.GPIO_04, // LCD data bit 3
        RaspiPin.GPIO_01); // LCD data bit 4
        
        final GpioPinDigitalInput button1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput button2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, PinPullResistance.PULL_DOWN);
        
        /////button listeners
        // create and register gpio pin listener
        button1.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                
                if(event.getState() == PinState.HIGH){
                    System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                }
            }
            
        });
        // create and register gpio pin listener
        button2.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                if(event.getState() == PinState.HIGH){
                    System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                }
            }
            
        });
        //////////////////////777777
        
        
        lcd.clear();
        
        lcd.write(LCD_ROW_2,"Portunes V2 by TARLA");
        try {        
            Thread.sleep(1000);            
        } catch (InterruptedException ex) {        
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        lcd.clear();
        
        lcd.write(LCD_ROW_1,"Welcome,Card please.");
        Thread.sleep(1000);
        
        
        new Thread("date"){        
             
            public void run(){     
                while(true){   
                    Date date = new Date( );            
                    SimpleDateFormat ft;
                    ft  = new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");

                    lcd.write(LCD_ROW_4,ft.format(date));
                    lcd.write(LCD_ROW_1,"Welcome,Card please.");
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                }
                
            }      
        }.start();
        
        
        while(true){
            
                try {
                    
                    Process p = Runtime.getRuntime().exec("portunes-read"); // portunes read wiedgard command
                    BufferedReader stdInput = new BufferedReader(new 
                    InputStreamReader(p.getInputStream()));
                    
                    int exitVal = p.waitFor();
                    String cardnoFull = null;
                    String cardnoBin = "";
                    
                    while ((cardnoFull = stdInput.readLine()) != null) {
                        //s = s.replace("\n", "").replace("\r", "");
                        
                        for(int a=1;a<25;a++){ // get data without checksum
                            cardnoBin = cardnoBin + cardnoFull.charAt(a);
                        } 
                        
                        long card_no = Long.parseLong(cardnoBin, 2);
                        String cardNo = String.valueOf(card_no);
                        
                        Personnel personel = new Personnel();
                        Door door = new Door();
                        
                        if(personel.Select(cardNo, "")){
                           
                            if(door.Select(personel.tarla_id, (String) justdate_format.format(today_date) ) ) {
                                System.out.println("shift bitis");
                                lcd.write(LCD_ROW_2, "gulegule " + personel.firstname);
                                door.ext_time = datetime_format.format(today_date);
                                
                                door.Update();
                                Thread.sleep(2000);
                                lcd.clear();
                            
                            }else{
                                System.out.println("shift baslangic");
                                lcd.write(LCD_ROW_2, "Merhaba " + personel.firstname);
                                door.tarla_id = personel.tarla_id;
                                door.ent_time = datetime_format.format(today_date);
                                door.ext_time = "0000-00-00 00:00:00";
                                door.Create();
                                Thread.sleep(2000);
                                lcd.clear();
                                
                            }  
                        
                            
                        }else{
                             lcd.write(LCD_ROW_2,"                    ");
                            System.out.println("yanlış kart " + cardNo);
                            lcd.clear(LCD_ROW_2);
                            lcd.write(LCD_ROW_2, "yanlis kart!");
                            lcd.clear();
                            
                        }
                        
                        
                        //send card_No to the server
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    
                }
            
        }
        
        
    
    }
    
    
  
}
