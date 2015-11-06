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
   String user_id = "";
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
    
   private final String db_domain = config.db_domain;   
   private final String db_url = "jdbc:mysql://" + config.db_domain + "/"+config.db_name;                 //"jdbc:mysql://" + db_domain + "/saglamdis"; // 192.241.172.225:3306
   private final String db_user = config.db_user;           
   private final String db_password = "123456";
    
   private final String db_table = "users";
   
   
   public boolean Select(String search_card_no){
       
       int customized_id;
        
        if(!"".equals(search_card_no)){
            
        System.out.println(db_url);
        try {
                    con = (Connection) DriverManager.getConnection(db_url, db_user, db_password);
                    st = (Statement) con.createStatement();
                    rs = st.executeQuery("SELECT customized_id FROM minered.custom_values where value = '" + search_card_no + "' AND custom_field_id = 3 ");


                    if (rs.next()) {
                            customized_id = rs.getInt("customized_id");
                            System.out.println("custom id " + customized_id);
                    }else{// cant find any record!
                        return false;
                    }
                  
                    rs = st.executeQuery("SELECT * FROM users WHERE id = " + customized_id + " ");
                    
                    if (rs.next()) {
                            login = rs.getString("login");
                            firstname = rs.getString("firstname");
                            lastname = rs.getString("lastname");
                            created_on = rs.getDate("created_on");
                            type = rs.getString("type");
                            System.out.println("user " + login + " " + firstname + " " + lastname);
                    }else{// cant find any record!
                        return false;
                    }
                    
                    rs = st.executeQuery("SELECT value FROM custom_values WHERE customized_id = " + customized_id + " AND custom_field_id = 1 ");
                    
                    if (rs.next()) {
                            user_id = rs.getString("value");
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
                
        }
        
          return false;     
}
}

