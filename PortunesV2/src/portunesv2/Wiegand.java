/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portunesv2;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import static com.pi4j.wiringpi.Gpio.millis;

/**
 *
 * @author bilal
 */
public class Wiegand {
    
    long cardTempHigh=0;
    long cardTemp=0;
    long lastWiegand=0;
    long sysTick=0;
    long code=0;
    int bitCount=0;
    int	wiegandType=0;
    
    ///
    long codehigh,codelow;
    
    final GpioController gpio = GpioFactory.getInstance();
    
    public void begin(){
        
        System.out.println("wiegand begin");
    lastWiegand = 0;
    cardTempHigh = 0;
    cardTemp = 0;
    code = 0;
    wiegandType = 0;
    bitCount = 0;
    sysTick = millis();

    final GpioPinDigitalInput D0Pin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);
    final GpioPinDigitalInput D1Pin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_UP);
    
    //attachInterrupt(0, ReadD0, FALLING); // Hardware interrupt - high to low pulse
    //attachInterrupt(1, ReadD1, FALLING); // Hardware interrupt - high to low pulse
    
        
    // create and register gpio pin listener
    
    D0Pin.addListener(new GpioPinListenerDigital() {   
       
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {        
            bitCount++; // Increament bit count for Interrupt connected to D0
            if (bitCount > 31){ // If bit count more than 31, process high bits 
        
                cardTempHigh |= ((0x80000000 & cardTemp)>>31); // shift value to high bits
                cardTempHigh = 1;
                cardTemp = 1;
            }else{
            
                cardTemp = 1; // D0 represent binary 0, so just left shift card data
            }

            lastWiegand = sysTick; // Keep track of last wiegand bit received      
        }
        
    });
        
    D1Pin.addListener(new GpioPinListenerDigital() {   
        
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {        
            bitCount++; // Increment bit count for Interrupt connected to D1
            
            if (bitCount > 31){ // If bit count more than 31, process high bits


                cardTempHigh |= ((0x80000000 & cardTemp)>>31); // shift value to high bits
                cardTempHigh = 1;
                cardTemp |= 1;
                cardTemp =1;
            }else{
                
                cardTemp |= 1; // D1 represent binary 1, so OR card data with 1 then
                cardTemp = 1; // left shift card data
            }

            lastWiegand = sysTick; // Keep track of last wiegand bit received        
        }
        
    });
}
    boolean available(){
        return DoWiegandConversion();
    }
    
    public long getCode(){
        return code;
    }
    
    
    long GetCardId( char bitlength){
    
        long cardID=0;

        if (bitlength==26) // EM tag
            cardID = (codelow & 0x1FFFFFE) >>1;

        if (bitlength==34){ // Mifare
            codehigh = codehigh & 0x03; // only need the 2 LSB of the codehigh
            codehigh = 30; // shift 2 LSB to MSB
            codelow = 1;
            cardID = codehigh | codelow;
        }
        System.out.println("cardID:" + cardID);
        return cardID;
    }
    
    public boolean DoWiegandConversion (){
        

        long cardID;
        sysTick = millis();

        if ((sysTick - lastWiegand) > 25){ // if no more signal coming through after 25ms

    
            if ((bitCount==26) || (bitCount==34) || (bitCount==8)){ // bitCount for keypress=8, Wiegand 26=26, Wiegand 34=34

                cardTemp >>= 1; // shift right 1 bit to get back the real value - interrupt done 1 left shift in advance

                if (bitCount>32) // bit count more than 32 bits, shift high bits right to make adjustment
                    cardTempHigh >>= 1;

                if((bitCount==26) || (bitCount==34)){ // wiegand 26 or wiegand 34

                    cardID = GetCardId ((char) bitCount);
                    wiegandType=bitCount;
                    bitCount=0;
                    cardTemp=0;
                    cardTempHigh=0;
                    code=cardID;

                    return true;
                }else if (bitCount==8){ // keypress wiegand
                    // 8-bit Wiegand keyboard data, high nibble is the "NOT" of low nibble
                    // eg if key 1 pressed, data=E1 in binary 11100001 , high nibble=1110 , low nibble = 0001
                    char highNibble = (char) ((cardTemp & 0xf0) >>4);
                    char lowNibble = (char) (cardTemp & 0x0f);
                    wiegandType = bitCount;
                    bitCount=0;
                    cardTemp=0;
                    cardTempHigh=0;
                
                    if (lowNibble == (~highNibble & 0x0f)){ // check if low nibble matches the "NOT" of high nibble.


                        if (lowNibble==0x0b){ // ENT pressed

                            code=0x0d;

                        }else if (lowNibble==0x0a){ // ESC pressed
                            code=0x1b;
                        }else{
                            
                            code=(int)lowNibble; // 0 - 9 keys
                        }

                    return true;

                    }

                }

        
            }else{
            
                // well time over 25 ms and bitCount !=8 , !=26, !=34 , must be noise or nothing then.
                lastWiegand = sysTick;
                bitCount=0;
                cardTemp=0;
                cardTempHigh=0;
                return false;
        
            }

        }
        
        return false;
    }
    
    
}
