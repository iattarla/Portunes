/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portunesv2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author bilal
 */
public class Personnel {
 
   String tc_no = "";
   String tarla_id = "";
   String card_no = "";
   String login = "";
   String firstname = "";
   String lastname = "";
   String mail = "";
   Date created_on = new Date();
   String type = "";
  
   
   
   Connection con = null;
   Statement st = null;
   ResultSet rs = null;

   Config config = new Config();
    
   private final String db_domain = config.db_domain;        //"localhost:3307";
   private final String db_url = "jdbc:mysql://" + config.db_domain + "/"+config.db_name;                 //"jdbc:mysql://" + db_domain + "/saglamdis"; // 192.241.172.225:3306
   private final String db_user = config.db_user;           //"saglamdisuser";
   private final String db_password = "super_251";
    
   private final String db_table = "users";
   
   
   public boolean Select(String search_card_no,String search_tarla_id){
       
        
        if(!"".equals(search_card_no)){
            
        
        try {
                    con = (Connection) DriverManager.getConnection(db_url, db_user, db_password);
                    st = (Statement) con.createStatement();
                    rs = st.executeQuery("SELECT * FROM " + db_table + " where card_no = '" + search_card_no + "'");


                    if (rs.next()) {
                        
                            tc_no = rs.getString("tc_no");
                            tarla_id = rs.getString("tarla_id");
                            card_no = rs.getString("card_no");
                            login = rs.getString("login");
                            firstname = rs.getString("firstname");
                            lastname = rs.getString("lastname");
                            mail = rs.getString("mail");
                            created_on = rs.getDate("created_on");
                            type = rs.getString("type");
                            
                            return true;
                           
                    }else{// cant find any record!
                        return false;
                    }
                  
                } catch (SQLException ex) {
                    System.out.println(ex);
                    Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi
                } finally {
                    
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                           st.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        System.out.println(ex);    
                        Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi
                    }
                }   
                
        }else{
            
            try {
                    con = (Connection) DriverManager.getConnection(db_url, db_user, db_password);
                    st = (Statement) con.createStatement();
                    rs = st.executeQuery("SELECT * FROM " + db_table + " where tarla_id = '"+ search_tarla_id + "' ;");


                    if (rs.next()) {
                        
                            tc_no = rs.getString("tc_no");
                            tarla_id = rs.getString("tarla_id");
                            login = rs.getString("login");
                            firstname = rs.getString("firstname");
                            lastname = rs.getString("lastname");
                            mail = rs.getString("mail");
                            created_on = rs.getDate("created_on");
                            type = rs.getString("type");
                            
                            return true;
                           
                    }else{ // cant find any record!
                        return false;
                    }
                  
                } catch (SQLException ex) {
                    System.out.println(ex);
                    Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi
                } finally {
                    
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                           st.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        System.out.println(ex);    
                        Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi
                    }
                } 
            
            
            
            
            
        }      
          return false;     
}
}

