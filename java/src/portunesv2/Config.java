/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portunesv2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gunebakan
 */
public class Config {
    
    String db_name = "";
    String db_user = "";
    String db_domain = "";
    
    
    Properties prop = new Properties();
    
    public Config(){
        try {
            prop.load(new FileInputStream("portunes.conf"));
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Sistem ayar dosyası bulunamadı.yanlışlıkla silmiş olabilirsiniz\n lütfen sistem yöneticinizle irtibata geçin.");
        }
        
        db_name = prop.getProperty("db_name");
        db_user = prop.getProperty("db_user");
        db_domain = prop.getProperty("db_domain");
        
        System.out.println("user:"+db_user+"@"+db_name+db_domain);
    }
    public void Update(){
        
 
            try {
    		
                //set the properties value  
                
                prop.setProperty("db_name",db_name);
                prop.setProperty("db_user",db_user);
                prop.setProperty("db_domain",db_domain);
                
    		//save properties to project root folder
    		prop.store(new FileOutputStream("portunes.conf"), null);
 
            } catch (IOException ex) {
                System.out.println(ex);
            }
    
    }
    
    public String windowsMac(){
 
	InetAddress ip;
        
        StringBuilder sb = new StringBuilder();
        
	try {
 
		ip = InetAddress.getLocalHost();
		
                System.out.println("Current IP address : " + ip.getHostAddress());
                
		NetworkInterface network = NetworkInterface.getByInetAddress(ip);
 
		byte[] mac = network.getHardwareAddress();
 
		System.out.print("Current MAC address : ");
 
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
                
	} catch (UnknownHostException | SocketException e) {
            System.out.println(e);
	}
        System.out.println(sb.toString());
    return sb.toString();
   }
    public String getMac(){
        
        if("".equals(linuxMac())){
            return windowsMac();
        }else{
            return linuxMac();
        }
        
    }
    private String linuxMac(){
        
        String macAdd = "";
        
        try {
  
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            
            while(networkInterfaces.hasMoreElements()){
               
                NetworkInterface network = networkInterfaces.nextElement();
                //System.out.println("network : " + network);
            
                byte[] mac = network.getHardwareAddress();
                
                if(mac == null){
                    System.out.println("null mac");             
                }else{
                  //  System.out.print("MAC address : ");

                    StringBuilder sb = new StringBuilder();
                    
                    for (int i = 0; i < mac.length; i++){
                        
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
                    }
                
                    //System.out.println(sb.toString());  
                    macAdd = sb.toString();
                
                    break;
                }
        
            }
    
        } catch (SocketException e){
            Exception ex = new Exception(e,this.getClass().toString());
        }
        
        return macAdd;
    }
}
