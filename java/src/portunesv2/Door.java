/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portunesv2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author bilal
 */
public class Door {
    
    String cardNo = "";
     Date date = new Date( );
     SimpleDateFormat ft;
    
    public Door(){
    
        ft  = new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");

        System.out.println("Datetime: " + ft.format(date));
        
    }
    
    public boolean create(){        
        
        
        return false;
    }
    
    public boolean update(){
        
        
        return false;
    }
    
    public boolean delete(){
        
        return false;
    }
    
}
