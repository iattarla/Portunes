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
import com.pi4j.io.gpio.RaspiPin;
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
                    
                    //lcd.clear();
                    
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
                            lcd.write(LCD_ROW_2,"                    ");
                            
                            //System.out.println("card_no: " + cardNo);
                            //System.out.println("tarla_id: " + personel.tarla_id);
                            
                            ///////////7
                           
                            //System.out.println(justdate_format.format(today_date));
                        
                        
                            if(door.Select(personel.tarla_id, (String) justdate_format.format(today_date) ) ) {
                                System.out.println("shift bitis");
                                lcd.write(LCD_ROW_2, "gulegule " + personel.firstname);
                                door.ext_time = datetime_format.format(today_date);
                                
                                door.Update();
                            
                            }else{
                                System.out.println("shift baslangic");
                                lcd.write(LCD_ROW_2, "merhaba " + personel.firstname);
                                door.tarla_id = personel.tarla_id;
                                door.ent_time = datetime_format.format(today_date);
                                door.ext_time = "0000-00-00 00:00:00";
                                door.Create();
                            }  
                        
                            
                        }else{
                             lcd.write(LCD_ROW_2,"                    ");
                            System.out.println("yanlış kart " + cardNo);
                            lcd.clear(LCD_ROW_2);
                            lcd.write(LCD_ROW_2, "yanlis kart!");
                        }
                        
                        
                        //send card_No to the server
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    
                }
            
        }
        
        
    
    }
    
    
  
}
